package nbbrd.console.picocli.csv;

import internal.nbbrd.console.picocli.csv.ExcelCsvLoader;
import nbbrd.console.picocli.text.ObsFormatOptions;
import nbbrd.design.ThreadSafe;
import nbbrd.picocsv.Csv;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import nbbrd.service.ServiceFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.charset.Charset;
import java.text.DecimalFormatSymbols;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * CSV produced by Excel is system-dependent. Its configuration is available at runtime.
 * https://superuser.com/questions/606272/how-to-get-excel-to-interpret-the-comma-as-a-default-delimiter-in-csv-files
 */
@ThreadSafe
@lombok.Builder(builderClassName = "Builder", toBuilder = true)
public final class ExcelCsv {

    /**
     * Creates a new instance by getting resources from ServiceLoader.
     *
     * @return a new instance
     */
    @NonNull
    public static ExcelCsv ofServiceLoader() {
        return ExcelCsv.builder().providers(ExcelCsvLoader.load()).build();
    }

    @lombok.Singular
    private final List<Spi> providers;

    public Csv.NewLine getSeparator() {
        return Csv.NewLine.WINDOWS;
    }

    public char getDelimiter() {
        return providers
                .stream()
                .map(o -> o.getDelimiterOrNull(getDecimalSeparator()))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Spi.COMMA);
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
        return providers
                .stream()
                .map(Spi::getDatePatternOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> getLocalizedDateTimePattern(FormatStyle.SHORT, null));
    }

    public String getTimePattern() {
        return providers
                .stream()
                .map(Spi::getTimePatternOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> getLocalizedDateTimePattern(null, FormatStyle.SHORT));
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

    private String getLocalizedDateTimePattern(FormatStyle dateStyle, FormatStyle timeStyle) {
        return DateTimeFormatterBuilder.getLocalizedDateTimePattern(dateStyle, timeStyle, IsoChronology.INSTANCE, getLocale());
    }

    @ThreadSafe
    @ServiceDefinition(
            loaderName = "internal.nbbrd.console.picocli.csv.ExcelCsvLoader",
            quantifier = Quantifier.MULTIPLE
    )
    public interface Spi {

        @ServiceFilter
        boolean isAvailable();

        @Nullable
        Character getDelimiterOrNull(char decimalSeparator);

        @Nullable
        String getDatePatternOrNull();

        @Nullable
        String getTimePatternOrNull();

        char COMMA = ',';
        char SEMICOLON = ';';
    }
}
