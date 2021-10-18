/*
 * Copyright 2019 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package internal.console.properties.x;

import nbbrd.console.properties.ConsoleProperties;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

import static nbbrd.console.properties.ConsoleProperties.Spi.UNKNOWN_COLUMNS;
import static nbbrd.console.properties.ConsoleProperties.Spi.UNKNOWN_ROWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Philippe Charles
 */
public class ChcpConsoleProviderTest {

    @Test
    public void testRegistration() {
        assertThat(ServiceLoader.load(ConsoleProperties.Spi.class))
                .anyMatch(ChcpConsoleProvider.class::isInstance);
    }

    @Test
    public void testParseChcp() {
        assertThat(ChcpConsoleProvider.parseChcp("Active code page: 437")).isEqualTo(Charset.forName("IBM437"));
        assertThatIllegalArgumentException().isThrownBy(() -> ChcpConsoleProvider.parseChcp(""));
        assertThatIllegalArgumentException().isThrownBy(() -> ChcpConsoleProvider.parseChcp("Active code page: "));
    }

    @Test
    public void testAll() {
        AtomicInteger errors = new AtomicInteger();
        ChcpConsoleProvider x = new ChcpConsoleProvider((ex, cmd) -> errors.incrementAndGet());
        if (x.isAvailable()) {
            assertThat(x.getStdInEncodingOrNull()).isNotNull();
            assertThat(x.getStdOutEncodingOrNull()).isNotNull();
            assertThat(errors).hasValue(0);
        } else {
//            assertThat(x.getStdInEncodingOrNull()).isNull();
//            assertThat(x.getStdOutEncodingOrNull()).isNull();
//            assertThat(errors).hasValue(2);
        }
        assertThat(x.getColumns()).isEqualTo(UNKNOWN_COLUMNS);
        assertThat(x.getRows()).isEqualTo(UNKNOWN_ROWS);
    }
}
