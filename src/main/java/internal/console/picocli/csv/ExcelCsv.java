package internal.console.picocli.csv;

import lombok.AccessLevel;
import nbbrd.design.ThreadSafe;
import nbbrd.design.VisibleForTesting;
import nbbrd.io.sys.OS;
import nbbrd.io.win.RegWrapper;
import nbbrd.picocsv.Csv;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormatSymbols;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Locale;
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
                    return RegWrapper.query("HKCU\\Control Panel\\International", false)
                            .values()
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(o -> o.getName().equals(key))
                            .map(RegWrapper.RegValue::getValue)
                            .findFirst()
                            .orElse(null);
                } catch (IOException ex) {
                    log.log(Level.WARNING, "While querying", ex);
                    return null;
                }
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
            switch (OS.NAME) {
                case WINDOWS:
                    return WIN;
                case MACOS:
                    return MAC;
                default:
                    return UNKNOWN;
            }
        }
    }
}
