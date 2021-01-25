package nbbrd.console.picocli.text;

import org.junit.Test;
import picocli.CommandLine.Model.CommandSpec;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class ObsFormatOptionsTest {

    @Test
    public void testDatetimePattern() {
        String datetimePattern = CommandSpec.forAnnotatedObject(new ObsFormatOptions())
                .findOption('S')
                .defaultValue();

        LocalDateTime dt1 = LocalDateTime.of(2010, 11, 3, 12, 15, 22);
        assertThat(DateTimeFormatter.ofPattern(datetimePattern).format(dt1))
                .isEqualTo(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dt1));

        LocalDateTime dt2 = LocalDate.of(2010, 11, 3).atStartOfDay();
        assertThat(DateTimeFormatter.ofPattern(datetimePattern).format(dt2))
                .isEqualTo(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dt2));
    }
}
