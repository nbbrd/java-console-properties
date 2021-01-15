package internal.console.picocli.csv;

import lombok.AccessLevel;
import nbbrd.design.ThreadSafe;
import nbbrd.design.VisibleForTesting;
import nbbrd.picocsv.Csv;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormatSymbols;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * CSV produced by Excel is system-dependent. Its configuration is available at runtime.
 * https://superuser.com/questions/606272/how-to-get-excel-to-interpret-the-comma-as-a-default-delimiter-in-csv-files
 */
@ThreadSafe
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelCsv {

    public static ExcelCsv INSTANCE = new ExcelCsv(Spi.get());

    @lombok.NonNull
    private final Spi provider;

    public Csv.@NonNull NewLine getSeparator() {
        return Csv.NewLine.WINDOWS;
    }

    public char getDelimiter() {
        Character result = provider.getDelimiterOrNull(getDecimalSeparator());
        return result != null ? result : Spi.COMMA;
    }

    public char getQuote() {
        return '"';
    }

    /**
     * Gets the system-dependent encoding for Excel.
     *
     * @return a non-null encoding
     */
    public @NonNull Charset getEncoding() {
        return Charset.defaultCharset();
    }

    /**
     * Gets the system-dependent locale for Excel.
     *
     * @return a non-null locale
     */
    public @NonNull Locale getLocale() {
        return Locale.getDefault(Locale.Category.FORMAT);
    }

    public @NonNull String getDatePattern() {
        String result = provider.getDatePatternOrNull();
        return result != null ? result : getLocalizedDateTimePattern(FormatStyle.SHORT, null);
    }

    public @NonNull String getTimePattern() {
        String result = provider.getTimePatternOrNull();
        return result != null ? result : getLocalizedDateTimePattern(null, FormatStyle.SHORT);
    }

    public @NonNull String getDateTimePattern() {
        return getDatePattern() + " " + getTimePattern();
    }

    private char getDecimalSeparator() {
        return new DecimalFormatSymbols(getLocale()).getDecimalSeparator();
    }

    private String getLocalizedDateTimePattern(FormatStyle dateStyle, FormatStyle timeStyle) {
        return DateTimeFormatterBuilder.getLocalizedDateTimePattern(dateStyle, timeStyle, IsoChronology.INSTANCE, getLocale());
    }

    @VisibleForTesting
    @lombok.extern.java.Log
    enum Spi {
        WIN {
            @Override
            public Character getDelimiterOrNull(char decimalSeparator) {
                if (decimalSeparator == COMMA) return SEMICOLON;
                String result = get("sList");
                return result != null && result.length() == 1 ? result.charAt(0) : null;
            }

            @Override
            public String getDatePatternOrNull() {
                return get("sShortDate");
            }

            @Override
            public String getTimePatternOrNull() {
                return get("sShortTime");
            }

            private String get(String key) {
                try {
                    return regQuery("HKCU\\Control Panel\\International", key);
                } catch (IOException ex) {
                    log.log(Level.WARNING, "While querying", ex);
                    return null;
                }
            }

            private String regQuery(String path, String key) throws IOException {
                String response = execToString("reg query \"" + path + "\" /v " + key);
                String anchor = "    " + key + "    REG_SZ    ";
                int anchorIdx = response.lastIndexOf(anchor);
                if (anchorIdx == -1) return null;
                int lineIdx = response.indexOf(System.lineSeparator(), anchorIdx);
                if (lineIdx == -1) return null;
                return response.substring(anchorIdx + anchor.length(), lineIdx);
            }

            private String execToString(String command) throws IOException {
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

            private String readerToString(java.io.Reader reader) throws IOException {
                StringBuilder result = new StringBuilder();
                char[] buffer = new char[8 * 1024];
                int read = 0;
                while ((read = reader.read(buffer)) != -1) result.append(buffer, 0, read);
                return result.toString();
            }
        },
        MAC {
            @Override
            public Character getDelimiterOrNull(char decimalSeparator) {
                return decimalSeparator == COMMA ? SEMICOLON : COMMA;
            }

            @Override
            public String getDatePatternOrNull() {
                return null;
            }

            @Override
            public String getTimePatternOrNull() {
                return null;
            }
        },
        UNKNOWN {
            @Override
            public Character getDelimiterOrNull(char decimalSeparator) {
                return null;
            }

            @Override
            public String getDatePatternOrNull() {
                return null;
            }

            @Override
            public String getTimePatternOrNull() {
                return null;
            }
        };

        public abstract @Nullable Character getDelimiterOrNull(char decimalSeparator);

        public abstract @Nullable String getDatePatternOrNull();

        public abstract @Nullable String getTimePatternOrNull();

        private static final char COMMA = ',';
        private static final char SEMICOLON = ';';

        public static @NonNull Spi get() {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                return WIN;
            }
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                return MAC;
            }
            return UNKNOWN;
        }
    }
}
