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
public class TextDemo2 implements Callable<Void> {

    static class FileTextInput {
        @Mixin
        FileInputOptions file;

        @Mixin
        TextInputOptions2 text;

        @Mixin
        GzipInputOptions gzip;

        String load() throws IOException {
            text.setFileSource(gzip.asFileSource());
            return text.readString(file.getFile());
        }
    }

    static class FileTextOutput {
        @Mixin
        FileOutputOptions file;

        @Mixin
        TextOutputOptions2 text;

        @Mixin
        GzipOutputOptions gzip;

        void store(String str) throws IOException {
            text.setFileSink(gzip.asFileSink());
            text.writeString(file.getFile(), str);
        }
    }

    @Mixin
    FileTextInput input;

    @Mixin
    FileTextOutput output;

    @Mixin
    TextOperationOptions operation;

    @Override
    public Void call() throws Exception {
        output.store(operation.transform(input.load()));
        return null;
    }

    public static void main(String[] args) {
        new CommandLine(new TextDemo2()).execute("--help");
    }
}
