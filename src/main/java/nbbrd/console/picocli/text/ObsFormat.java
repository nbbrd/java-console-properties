package nbbrd.console.picocli.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface ObsFormat {

    Locale getLocale();

    String getDatePattern();

    String getDatetimePattern();

    String getNumberPattern();

    boolean isIgnoreNumberGrouping();

    default DateTimeFormatter newDateTimeFormatter(boolean includeTime) throws IllegalArgumentException {
        String pattern = includeTime ? getDatetimePattern() : getDatePattern();
        return DateTimeFormatter.ofPattern(pattern, getLocale());
    }

    default NumberFormat newNumberFormat() throws IllegalArgumentException {
        NumberFormat result = new DecimalFormat(getNumberPattern(), DecimalFormatSymbols.getInstance(getLocale()));
        if (isIgnoreNumberGrouping()) {
            result.setGroupingUsed(false);
        }
        return result;
    }
}
