package nbbrd.console.picocli;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class LocaleConverterTest {

    @Test
    public void test() {
        LocaleConverter converter = new LocaleConverter();
        assertThat(converter.convert("en")).isEqualTo(Locale.ENGLISH);
        assertThat(converter.convert("en_US")).isEqualTo(Locale.US);
        assertThat(converter.convert("en-US")).isEqualTo(Locale.US);
        assertThat(converter.convert("")).isEqualTo(Locale.ROOT);
        assertThatIllegalArgumentException().isThrownBy(() -> converter.convert("$"));
    }
}
