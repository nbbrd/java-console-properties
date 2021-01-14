package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.ObsFormatOptions;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormatSymbols;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * CSV produced by Excel is system-dependent. Its configuration is available at runtime.
 * https://superuser.com/questions/606272/how-to-get-excel-to-interpret-the-comma-as-a-default-delimiter-in-csv-files
 */
public final class ExcelCsv {

    public static final ExcelCsv of() {
        return new ExcelCsv(isWindows() ? getWindowsRegionalSettings(Objects::requireNonNull) : key -> null);
    }

    private final UnaryOperator<String> windowsRegionalSettings;

    private ExcelCsv(UnaryOperator<String> windowsRegionalSettings) {
        this.windowsRegionalSettings = Objects.requireNonNull(windowsRegionalSettings);
    }

    public static Csv.NewLine getSeparator() {
        return Csv.NewLine.WINDOWS;
    }

    public char getDelimiter() {
        if (isWindows()) return getDecimalSeparator() == COMMA ? SEMICOLON : getWindowsListSeparator();
        if (isMac()) return getDecimalSeparator() == COMMA ? SEMICOLON : COMMA;
        return COMMA;
    }

    public char getQuote() {
        return '"';
    }

    /**
     * Gets the system-dependent encoding for Excel.
     *
     * @return a non-null encoding
     */
    public Charset getEncoding() {
        return Charset.defaultCharset();
    }

    /**
     * Gets the system-dependent locale for Excel.
     *
     * @return a non-null locale
     */
    public Locale getLocale() {
        return Locale.getDefault(Locale.Category.FORMAT);
    }

    public String getDatePattern() {
        String result = isWindows() ? windowsRegionalSettings.apply("sShortDate") : null;
        return !isNullOrEmpty(result) ? result : getLocalizedDateTimePattern(FormatStyle.SHORT, null);
    }

    public String getTimePattern() {
        String result = isWindows() ? windowsRegionalSettings.apply("sShortTime") : null;
        return !isNullOrEmpty(result) ? result : getLocalizedDateTimePattern(null, FormatStyle.SHORT);
    }

    public String getDateTimePattern() {
        return getDatePattern() + " " + getTimePattern();
    }

    public void apply(CsvInputOptions options) {
        options.setDelimiter(getDelimiter());
        options.setQuote(getQuote());
        options.setSeparator(getSeparator());
        options.setEncoding(getEncoding());
    }

    public void apply(CsvOutputOptions options) {
        options.setDelimiter(getDelimiter());
        options.setQuote(getQuote());
        options.setSeparator(getSeparator());
        options.setEncoding(getEncoding());
    }

    public void apply(ObsFormatOptions options) {
        options.setLocale(getLocale());
        options.setDatePattern(getDatePattern());
        options.setDatetimePattern(getDateTimePattern());
        options.setIgnoreNumberGrouping(false);
    }

    private char getDecimalSeparator() {
        return new DecimalFormatSymbols(getLocale()).getDecimalSeparator();
    }

    private char getWindowsListSeparator() {
        String result = windowsRegionalSettings.apply("sList");
        return !isNullOrEmpty(result) ? result.charAt(0) : COMMA;
    }

    private String getLocalizedDateTimePattern(FormatStyle dateStyle, FormatStyle timeStyle) {
        return DateTimeFormatterBuilder.getLocalizedDateTimePattern(dateStyle, timeStyle, IsoChronology.INSTANCE, getLocale());
    }

    private static final char COMMA = ',';
    private static final char SEMICOLON = ';';

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
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

    // @VisibleForTesting
    static final boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    // @VisibleForTesting
    static final boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
