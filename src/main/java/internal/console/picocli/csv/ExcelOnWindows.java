package internal.console.picocli.csv;

import nbbrd.console.picocli.csv.ExcelCsv;
import nbbrd.service.ServiceProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@ServiceProvider(ExcelCsv.Spi.class)
public final class ExcelOnWindows implements ExcelCsv.Spi {

    private final UnaryOperator<String> regionalSettings = getWindowsRegionalSettings(Objects::requireNonNull);

    @Override
    public boolean isAvailable() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Override
    public Character getDelimiterOrNull(char decimalSeparator) {
        if (decimalSeparator == COMMA) return SEMICOLON;
        String result = regionalSettings.apply("sList");
        return result != null && result.length() == 1 ? result.charAt(0) : null;
    }

    @Override
    public String getDatePatternOrNull() {
        return regionalSettings.apply("sShortDate");
    }

    @Override
    public String getTimePatternOrNull() {
        return regionalSettings.apply("sShortTime");
    }

    private static UnaryOperator<String> getWindowsRegionalSettings(Consumer<? super IOException> onError) {
        return key -> {
            try {
                return regQuery("HKCU\\Control Panel\\International", key);
            } catch (IOException ex) {
                onError.accept(ex);
                return null;
            }
        };
    }

    private static String regQuery(String path, String key) throws IOException {
        String response = execToString("reg query \"" + path + "\" /v " + key);
        String anchor = "    " + key + "    REG_SZ    ";
        int anchorIdx = response.lastIndexOf(anchor);
        if (anchorIdx == -1) return null;
        int lineIdx = response.indexOf(System.lineSeparator(), anchorIdx);
        if (lineIdx == -1) return null;
        return response.substring(anchorIdx + anchor.length(), lineIdx);
    }

    private static String execToString(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        try (java.io.Reader reader = new InputStreamReader(process.getInputStream(), Charset.defaultCharset())) {
            return readerToString(reader);
        } finally {
            try {
                process.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }
    }

    private static String readerToString(java.io.Reader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[8 * 1024];
        int read = 0;
        while ((read = reader.read(buffer)) != -1) result.append(buffer, 0, read);
        return result.toString();
    }
}
