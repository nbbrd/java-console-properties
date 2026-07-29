package internal.console.picocli.csv;

import com.github.tuupertunut.powershelllibjava.PowerShell;
import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import nbbrd.picocsv.Csv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

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

public class ExcelCsvTest {

    private final ExcelCsv xl = ExcelCsv.INSTANCE;

    @Test
    public void testGetSeparator() {
        assertThat(xl.getSeparator()).isNotNull();
    }

    @Test
    public void testGetDelimiter() {
        assertThat(xl.getDelimiter()).isNotEqualTo('\0');
    }

    @Test
    public void testGetQuote() {
        assertThat(xl.getQuote()).isNotEqualTo('\0');
    }

    @Test
    public void testGetEncoding() {
        assertThat(xl.getEncoding()).isNotNull();
    }

    @Test
    public void testGetLocale() {
        assertThat(xl.getLocale()).isNotNull();
    }

    @Test
    public void testGetDatePattern() {
        assertThat(xl.getDatePattern()).isNotEmpty();
    }

    @Test
    public void testGetDateTimePattern() {
        assertThat(xl.getDateTimePattern()).isNotEmpty();
    }

    @Test
    public void testGetTimePattern() {
        assertThat(xl.getTimePattern()).isNotNull();
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @EnabledIf("isExcelInstalled")
    public void testExcelApplication() throws IOException, PowerShellExecutionException {
        File source = createSource(xl);
        File target = createTarget(source);

        assertThat(target).hasSameTextualContentAs(source, xl.getEncoding());
    }

    private static boolean isExcelInstalled() throws IOException, PowerShellExecutionException {
        try (PowerShell ps = PowerShell.open()) {
            return ps.executeCommands("Test-Path HKLM:SOFTWARE\\Classes\\Excel.Application").toLowerCase(Locale.ROOT).startsWith("true");
        }
    }

    private static File createSource(ExcelCsv excel) throws IOException {
        File result = Files.createTempFile("source", ".csv").toFile();
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

        Csv.Format format = Csv.Format.builder().separator(excel.getSeparator().getSeparator()).delimiter(excel.getDelimiter()).quote(excel.getQuote()).build();
        Charset encoding = excel.getEncoding();

        try (Csv.Writer writer = Csv.Writer.of(format, Csv.WriterOptions.DEFAULT, Files.newBufferedWriter(result.toPath(), encoding), Csv.DEFAULT_CHAR_BUFFER_SIZE)) {
            writer.writeField(A1);
            writer.writeField(C1.format(dateFormatter));
            writer.writeField(D1.format(timeFormatter));
            writer.writeField(E1.format(dateTimeFormatter));
        }

        return result;
    }

    private static File createTarget(File source) throws IOException, PowerShellExecutionException {
        File result = Files.createTempFile("target", ".csv").toFile();
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
