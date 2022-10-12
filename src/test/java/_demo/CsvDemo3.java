package _demo;

import nbbrd.console.picocli.*;
import nbbrd.console.picocli.csv.PicocsvInputOptions;
import nbbrd.console.picocli.csv.PicocsvOutputOptions;
import nbbrd.picocsv.Csv;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "CsvDemo", sortOptions = false, mixinStandardHelpOptions = true)
public class CsvDemo3 implements Callable<Void> {

    static class FileCsvInput {
        @Mixin
        FileInputParameters file;

        @Mixin
        PicocsvInputOptions csv;

        @Mixin
        GzipInputOptions gzip;

        public Csv.Reader newCsvReader() throws IOException {
            csv.setFileSource(gzip.asFileSource());
            return csv.newCsvReader(file.getFile());
        }
    }

    static class FileCsvOutput {
        @Mixin
        FileOutputParameters file;

        @Mixin
        PicocsvOutputOptions csv;

        @Mixin
        GzipOutputOptions gzip;

        public Csv.Writer newCsvWriter() throws IOException {
            csv.setFileSink(gzip.asFileSink());
            return csv.newCsvWriter(file.getFile());
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
