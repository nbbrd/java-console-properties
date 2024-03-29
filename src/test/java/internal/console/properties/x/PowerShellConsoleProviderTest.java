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

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Philippe Charles
 */
public class PowerShellConsoleProviderTest {

    @Test
    public void testRegistration() {
        assertThat(ServiceLoader.load(ConsoleProperties.Spi.class))
                .anyMatch(PowerShellConsoleProvider.class::isInstance);
    }

    @Test
    public void testIfUnavailable() {
        AtomicInteger errors = new AtomicInteger();
        PowerShellConsoleProvider x = new PowerShellConsoleProvider((ex, cmd) -> errors.incrementAndGet());
        if (x.isAvailable() && x.getExecutable() != null) {
            assertThat(x.getStdInEncodingOrNull()).isNotNull();
            assertThat(x.getStdOutEncodingOrNull()).isNotNull();
            assertThat(x.getColumns()).isGreaterThan(0);
            assertThat(x.getRows()).isGreaterThan(0);
            assertThat(errors).hasValue(0);
        } else {
//            assertThat(x.getStdInEncodingOrNull()).isNull();
//            assertThat(x.getStdOutEncodingOrNull()).isNull();
//            assertThat(x.getColumns()).isEqualTo(UNKNOWN_COLUMNS);
//            assertThat(x.getRows()).isEqualTo(UNKNOWN_ROWS);
//            assertThat(errors).hasValue(4);
        }
    }
}
