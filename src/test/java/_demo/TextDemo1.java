package _demo;

import nbbrd.console.picocli.FileInputOptions;
import nbbrd.console.picocli.FileOutputOptions;
import nbbrd.console.picocli.GzipInputOptions;
import nbbrd.console.picocli.GzipOutputOptions;
import nbbrd.console.picocli.text.TextInputOptions2;
import nbbrd.console.picocli.text.TextOutputOptions2;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "TextDemo", sortOptions = false, mixinStandardHelpOptions = true)
public class TextDemo1 implements Callable<Void> {

    @Mixin
    FileInputOptions inputFile;

    @Mixin
    TextInputOptions2 inputText;

    @Mixin
    GzipInputOptions inputGzip;

    @Mixin
    FileOutputOptions outputFile;

    @Mixin
    TextOutputOptions2 outputText;

    @Mixin
    GzipOutputOptions outputGzip;

    @Mixin
    TextOperationOptions operation;

    private String load() throws IOException {
        inputText.setFileSource(inputGzip.asFileSource());
        return inputText.readString(inputFile.getFile());
    }

    private void store(String text) throws IOException {
        outputText.setFileSink(outputGzip.asFileSink());
        outputText.writeString(outputFile.getFile(), text);
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
