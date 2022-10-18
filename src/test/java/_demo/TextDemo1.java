package _demo;

import nbbrd.console.picocli.FileInputOptions;
import nbbrd.console.picocli.FileOutputOptions;
import nbbrd.console.picocli.GzipInputOptions;
import nbbrd.console.picocli.GzipOutputOptions;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.io.IOException;
import java.util.concurrent.Callable;

import static nbbrd.console.picocli.text.TextInputSupport.newTextInputSupport;
import static nbbrd.console.picocli.text.TextOutputSupport.newTextOutputSupport;

@Command(name = "TextDemo", sortOptions = false, mixinStandardHelpOptions = true)
public class TextDemo1 implements Callable<Void> {

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

    private String load() throws IOException {
        return newTextInputSupport(inputGzip)
                .readString(inputFile.getFile());
    }

    private void store(String text) throws IOException {
        newTextOutputSupport(outputGzip)
                .writeString(outputFile.getFile(), text);
    }

    @Override
    public Void call() throws Exception {
        store(operation.transform(load()));
        return null;
    }

    public static void main(String[] args) {
        new CommandLine(new TextDemo1()).execute("--help");
    }
}
