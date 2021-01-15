package internal.console.picocli.csv;

import nbbrd.console.picocli.Profilable;
import nbbrd.console.picocli.Profile;
import nbbrd.console.picocli.csv.CsvInputOptions;
import nbbrd.console.picocli.csv.CsvOutputOptions;
import nbbrd.console.picocli.text.ObsFormatOptions;
import nbbrd.service.ServiceProvider;

@ServiceProvider(Profile.Spi.class)
public final class ExcelProfile implements Profile.Spi {

    private final ExcelCsv xl = ExcelCsv.INSTANCE;

    @Override
    public String getId() {
        return "excel";
    }

    @Override
    public void apply(Profilable profilable) {
        if (profilable instanceof CsvInputOptions) {
            applyOnCsvInput((CsvInputOptions) profilable);
        }

        if (profilable instanceof CsvOutputOptions) {
            applyOnCsvOutput((CsvOutputOptions) profilable);
        }

        if (profilable instanceof ObsFormatOptions) {
            applyOnObsFormat((ObsFormatOptions) profilable);
        }
    }

    private void applyOnObsFormat(ObsFormatOptions o) {
        o.setLocale(xl.getLocale());
        o.setDatePattern(xl.getDatePattern());
        o.setDatetimePattern(xl.getDateTimePattern());
        o.setIgnoreNumberGrouping(false);
    }

    private void applyOnCsvOutput(CsvOutputOptions o) {
        o.setDelimiter(xl.getDelimiter());
        o.setQuote(xl.getQuote());
        o.setSeparator(xl.getSeparator());
        o.setEncoding(xl.getEncoding());
    }

    private void applyOnCsvInput(CsvInputOptions o) {
        o.setDelimiter(xl.getDelimiter());
        o.setQuote(xl.getQuote());
        o.setSeparator(xl.getSeparator());
        o.setEncoding(xl.getEncoding());
    }
}
