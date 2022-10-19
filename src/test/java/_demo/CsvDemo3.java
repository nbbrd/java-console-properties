package _demo;

import nbbrd.console.picocli.FileInputParameters;
import nbbrd.console.picocli.FileOutputParameters;
import nbbrd.console.picocli.GzipInputOptions;
import nbbrd.console.picocli.GzipOutputOptions;
import nbbrd.picocsv.Csv;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.io.IOException;
import java.util.concurrent.Callable;

import static nbbrd.console.picocli.csv.PicocsvInputSupport.newPicocsvInputSupport;
import static nbbrd.console.picocli.csv.PicocsvOutputSupport.newPicocsvOutputSupport;

@Command(name = "CsvDemo", sortOptions = false, mixinStandardHelpOptions = true)
public class CsvDemo3 implements Callable<Void> {

    static class FileCsvInput {
        @Mixin
        FileInputParameters file;

        @Mixin
        GzipInputOptions gzip;

        public Csv.Reader newCsvReader() throws IOException {
            return newPicocsvInputSupport(gzip)
                    .newCsvReader(file.getFile());
        }
    }

    static class FileCsvOutput {
        @Mixin
        FileOutputParameters file;

        @Mixin
        GzipOutputOptions gzip;

        public Csv.Writer newCsvWriter() throws IOException {
            return newPicocsvOutputSupport(gzip)
                    .newCsvWriter(file.getFile());
        }
    }

    @Mixin
    FileCsvInput input;

    @Mixin
    FileCsvOutput output;

    @Mixin
    TextOperationOptions operation;

    @Override
    public Void call() throws Exception {
        try (Csv.Reader reader = input.newCsvReader()) {
            try (Csv.Writer writer = output.newCsvWriter()) {
                while (reader.readLine()) {
                    if (reader.isComment()) {
                        writer.writeComment(reader);
                    } else {
                        while (reader.readField()) {
                            writer.writeField(operation.transform(reader.toString()));
                        }
                        writer.writeEndOfLine();
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new CommandLine(new CsvDemo3()).execute("--help");
    }
}
