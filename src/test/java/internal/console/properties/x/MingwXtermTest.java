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
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ServiceLoader;

import static internal.console.properties.x.Utils.MSYSTEM_ENV;
import static internal.console.properties.x.Utils.TERM_ENV;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Philippe Charles
 */
public class MingwXtermTest {

    @Test
    public void testRegistration() {
        assertThat(ServiceLoader.load(ConsoleProperties.Spi.class))
                .anyMatch(MingwXterm.class::isInstance);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = MSYSTEM_ENV, matches = "^MINGW(32)|(64)$")
    @EnabledIfEnvironmentVariable(named = TERM_ENV, matches = "^xterm$")
    public void testIfAvailable() {
        MingwXterm x = new MingwXterm();
        assertThat(x.isAvailable()).isTrue();
        assertThat(x.getStdInEncodingOrNull()).isNotNull();
        assertThat(x.getStdOutEncodingOrNull()).isNotNull();
        assertThat(x.getColumns()).isGreaterThan(0);
        assertThat(x.getRows()).isGreaterThan(0);
    }

    @Test
    @DisabledIfEnvironmentVariable(named = MSYSTEM_ENV, matches = "^MINGW(32)|(64)$")
    @DisabledIfEnvironmentVariable(named = TERM_ENV, matches = "^xterm$")
    public void testIfUnavailable() {
        MingwXterm x = new MingwXterm();
        assertThat(x.isAvailable()).isFalse();
    }
}
