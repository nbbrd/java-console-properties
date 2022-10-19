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
public class TextDemo2 implements Callable<Void> {

    static class FileTextInput {
        @Mixin
        FileInputOptions file;

        @Mixin
        GzipInputOptions gzip;

        String load() throws IOException {
            return newTextInputSupport(gzip)
                    .readString(file.getFile());
        }
    }

    static class FileTextOutput {
        @Mixin
        FileOutputOptions file;

        @Mixin
        GzipOutputOptions gzip;

        void store(String text) throws IOException {
            newTextOutputSupport(gzip)
                    .writeString(file.getFile(), text);
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
