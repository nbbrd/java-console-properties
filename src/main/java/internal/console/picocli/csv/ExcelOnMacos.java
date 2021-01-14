package internal.console.picocli.csv;

import nbbrd.console.picocli.csv.ExcelCsv;
import nbbrd.service.ServiceProvider;

@ServiceProvider(ExcelCsv.Spi.class)
public final class ExcelOnMacos implements ExcelCsv.Spi {

    @Override
    public boolean isAvailable() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

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
}
