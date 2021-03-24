package nbbrd.console.picocli.csv;

import nbbrd.picocsv.Csv;

import java.util.stream.Stream;

@lombok.AllArgsConstructor
public enum CsvNewLine {

    WINDOWS(Csv.Format.WINDOWS_SEPARATOR),
    UNIX(Csv.Format.UNIX_SEPARATOR),
    MACINTOSH(Csv.Format.MACINTOSH_SEPARATOR);

    @lombok.Getter
    private final String separator;

    public static CsvNewLine parse(String separator) {
        return Stream.of(CsvNewLine.values())
                .filter(o -> o.getSeparator().equals(separator))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
