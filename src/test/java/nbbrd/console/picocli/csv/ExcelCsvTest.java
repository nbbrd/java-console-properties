package nbbrd.console.picocli.csv;

import com.github.tuupertunut.powershelllibjava.PowerShell;
import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import nbbrd.picocsv.Csv;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

public class ExcelCsvTest {

    private final ExcelCsv excel = ExcelCsv.ofServiceLoader();

    @Test
    public void testGetSeparator() {
        assertThat(excel.getSeparator()).isNotNull();
    }

    @Test
    public void testGetDelimiter() {
        assertThat(excel.getDelimiter()).isNotEqualTo('\0');
    }

    @Test
    public void testGetQuote() {
        assertThat(excel.getQuote()).isNotEqualTo('\0');
    }

    @Test
    public void testGetEncoding() {
        assertThat(excel.getEncoding()).isNotNull();
    }

    @Test
    public void testGetLocale() {
        assertThat(excel.getLocale()).isNotNull();
    }

    @Test
    public void testGetDatePattern() {
        assertThat(excel.getDatePattern()).isNotEmpty();
    }

    @Test
    public void testGetDateTimePattern() {
        assertThat(excel.getDateTimePattern()).isNotEmpty();
    }

    @Test
    public void testGetTimePattern() {
        assertThat(excel.getTimePattern()).isNotNull();
    }

    @Test
    public void testExcelApplication() throws IOException, PowerShellExecutionException {
        assumeThat(isWindows()).isTrue();
        assumeThat(isExcelInstalled()).isTrue();

        File source = createSource(excel);
        File target = createTarget(source);

        assertThat(target).hasSameTextualContentAs(source, excel.getEncoding());
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static boolean isExcelInstalled() throws IOException, PowerShellExecutionException {
        try (PowerShell ps = PowerShell.open()) {
            return ps.executeCommands("Test-Path HKLM:SOFTWARE\\Classes\\Excel.Application").toLowerCase(Locale.ROOT).startsWith("true");
        }
    }

    private static File createSource(ExcelCsv excel) throws IOException {
        File result = File.createTempFile("source", ".csv");
        result.deleteOnExit();

        String A1 = "Some text";
        double B1 = 1234.5;
        LocalDate C1 = LocalDate.of(2010, 05, 15);
        LocalTime D1 = LocalTime.of(10, 30, 12);
        LocalDateTime E1 = LocalDateTime.of(C1, D1);

        Locale locale = excel.getLocale();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(excel.getDatePattern(), locale);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(excel.getTimePattern(), locale);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(excel.getDateTimePattern(), locale);

        Csv.Format format = Csv.Format.builder().separator(excel.getSeparator()).delimiter(excel.getDelimiter()).quote(excel.getQuote()).build();
        Charset encoding = excel.getEncoding();

        try (Csv.Writer writer = Csv.Writer.of(Files.newBufferedWriter(result.toPath(), encoding), format)) {
            writer.writeField(A1);
            writer.writeField(C1.format(dateFormatter));
            writer.writeField(D1.format(timeFormatter));
            writer.writeField(E1.format(dateTimeFormatter));
        }

        return result;
    }

    private static File createTarget(File source) throws IOException, PowerShellExecutionException {
        File result = File.createTempFile("target", ".csv");
        result.deleteOnExit();

        try (PowerShell ps = PowerShell.open()) {
            ps.executeCommands(loadCode());
            ps.executeCommands("SaveAsCsv -Source " + source.getAbsolutePath() + " -Target " + result.getAbsolutePath());
        }

        return result;
    }

    private static String[] loadCode() throws IOException {
        try (InputStream stream = ExcelCsvTest.class.getResourceAsStream("/SaveAsCsv.ps1")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                return reader.lines().toArray(String[]::new);
            }
        }
    }
}
