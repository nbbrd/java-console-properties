package _demo;

import nbbrd.console.picocli.FileInputOptions;
import nbbrd.console.picocli.FileOutputOptions;
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
public class CsvDemo1 implements Callable<Void> {

    @Mixin
    FileInputOptions inputFile;

    @Mixin
    GzipInputOptions inputGzip;

    @Mixin
    FileOutputOptions outputFile;

    @Mixin
    GzipOutputOptions outputGzip;

    @Mixin
    TextOperationOptions operation;

    private Csv.Reader newCsvReader() throws IOException {
        return newPicocsvInputSupport(inputGzip)
                .newCsvReader(inputFile.getFile());
    }

    private Csv.Writer newCsvWriter() throws IOException {
        return newPicocsvOutputSupport(outputGzip)
                .newCsvWriter(outputFile.getFile());
    }

    @Override
    public Void call() throws Exception {
        try (Csv.Reader reader = newCsvReader()) {
            try (Csv.Writer writer = newCsvWriter()) {
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
        new CommandLine(new CsvDemo1()).execute("--help");
    }
}
