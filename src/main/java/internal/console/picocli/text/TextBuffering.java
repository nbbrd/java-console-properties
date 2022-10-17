package internal.console.picocli.text;

import nbbrd.console.picocli.text.TextInput2;
import nbbrd.console.picocli.text.TextOutput2;
import nbbrd.io.text.TextBuffers;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

@FunctionalInterface
public interface TextBuffering<X, Y> {

    Y wrap(X closeable, int bufferSize) throws IOException;

    static <T> T of(TextInput2 input, Path file, TextBuffering<Reader, T> wrapper) throws IOException {
        if (input.isStdInFile(file)) {
            return wrapper.wrap(input.getStdinSource().newReader(), TextBuffers.DEFAULT_CHAR_BUFFER_SIZE);
        }
        CharsetDecoder decoder = input.getEncoding().newDecoder();
        TextBuffers buffers = TextBuffers.of(file, decoder);
        ReadableByteChannel channel = input.getFileSource().newByteChannel(file, READ);
        return wrapper.wrap(buffers.newCharReader(channel, decoder), buffers.getCharBufferSize());
    }

    static <T> T of(TextOutput2 output, Path file, TextBuffering<Writer, T> wrapper) throws IOException {
        if (output.isStdoutFile(file)) {
            return wrapper.wrap(output.getStdoutSink().newWriter(), TextBuffers.DEFAULT_CHAR_BUFFER_SIZE);
        }
        CharsetEncoder encoder = output.getEncoding().newEncoder();
        TextBuffers buffers = TextBuffers.of(file, encoder);
        WritableByteChannel channel = output.getFileSink().newByteChannel(file, WRITE, CREATE, output.isAppend() ? APPEND : TRUNCATE_EXISTING);
        return wrapper.wrap(buffers.newCharWriter(channel, encoder), buffers.getCharBufferSize());
    }
}
