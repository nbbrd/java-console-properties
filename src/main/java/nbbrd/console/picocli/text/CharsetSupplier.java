package nbbrd.console.picocli.text;

import nbbrd.console.properties.ConsoleProperties;

import java.nio.charset.Charset;

@FunctionalInterface
public interface CharsetSupplier {

    Charset getCharset();

    static CharsetSupplier getDefault() {
        return ofName(DEFAULT_ENCODING);
    }

    static CharsetSupplier of(Charset charset) {
        return () -> charset;
    }

    static CharsetSupplier ofName(String charsetName) {
        return () -> Charset.forName(charsetName);
    }

    static CharsetSupplier ofStdin() {
        return () -> ConsoleProperties
                .ofServiceLoader()
                .getStdInEncoding()
                .orElseGet(() -> Charset.forName(DEFAULT_ENCODING));
    }

    static CharsetSupplier ofStdout() {
        return () -> ConsoleProperties
                .ofServiceLoader()
                .getStdOutEncoding()
                .orElseGet(() -> Charset.forName(DEFAULT_ENCODING));
    }

    String DEFAULT_ENCODING = "UTF-8";
}
