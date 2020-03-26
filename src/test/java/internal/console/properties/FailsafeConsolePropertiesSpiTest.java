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
package internal.console.properties;

import _test.Failing;
import _test.Sample;
import _test.CustomRuntimeException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;
import static nbbrd.console.properties.ConsoleProperties.Spi.UNKNOWN_COLUMNS;
import static nbbrd.console.properties.ConsoleProperties.Spi.UNKNOWN_RANK;
import static nbbrd.console.properties.ConsoleProperties.Spi.UNKNOWN_ROWS;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class FailsafeConsolePropertiesSpiTest {

    @Test
    public void testFactories() {
        assertThatNullPointerException()
                .isThrownBy(() -> new FailsafeConsolePropertiesSpi(null, this::doNothing));

        assertThatNullPointerException()
                .isThrownBy(() -> new FailsafeConsolePropertiesSpi(Sample.FIRST, null));

        assertThatNullPointerException()
                .isThrownBy(() -> FailsafeConsolePropertiesSpi.wrap(null));
    }

    @Test
    public void testGetRank() {
        errorStack.clear();

        assertThat(unknown.getRank()).isEqualTo(UNKNOWN_RANK);
        assertThat(errorStack).isEmpty();

        assertThat(first.getRank()).isEqualTo(1);
        assertThat(errorStack).isEmpty();

        assertThat(npe.getRank()).isEqualTo(UNKNOWN_RANK);
        assertThat(errorStack).hasSize(1).last().isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    public void testGetStdInEncoding() {
        errorStack.clear();

        assertThat(unknown.getStdInEncodingOrNull()).isNull();
        assertThat(errorStack).isEmpty();

        assertThat(first.getStdInEncodingOrNull()).isEqualTo(StandardCharsets.US_ASCII);
        assertThat(errorStack).isEmpty();

        assertThat(npe.getStdInEncodingOrNull()).isNull();
        assertThat(errorStack).hasSize(1).last().isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    public void testGetStdOutEncoding() {
        errorStack.clear();

        assertThat(unknown.getStdOutEncodingOrNull()).isNull();
        assertThat(errorStack).isEmpty();

        assertThat(first.getStdOutEncodingOrNull()).isEqualTo(StandardCharsets.UTF_8);
        assertThat(errorStack).isEmpty();

        assertThat(npe.getStdOutEncodingOrNull()).isNull();
        assertThat(errorStack).hasSize(1).last().isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    public void testGetRows() {
        errorStack.clear();

        assertThat(unknown.getRows()).isEqualTo(UNKNOWN_ROWS);
        assertThat(errorStack).isEmpty();

        assertThat(first.getRows()).isEqualTo(30);
        assertThat(errorStack).isEmpty();

        assertThat(npe.getRows()).isEqualTo(UNKNOWN_ROWS);
        assertThat(errorStack).hasSize(1).last().isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    public void testGetColumns() {
        errorStack.clear();

        assertThat(unknown.getColumns()).isEqualTo(UNKNOWN_COLUMNS);
        assertThat(errorStack).isEmpty();

        assertThat(first.getColumns()).isEqualTo(120);
        assertThat(errorStack).isEmpty();

        assertThat(npe.getRows()).isEqualTo(UNKNOWN_COLUMNS);
        assertThat(errorStack).hasSize(1).last().isInstanceOf(CustomRuntimeException.class);
    }

    private final Queue<Exception> errorStack = new LinkedList<>();
    private final BiConsumer<? super String, ? super RuntimeException> errorStacker = (m, e) -> errorStack.add(e);

    private final FailsafeConsolePropertiesSpi unknown = new FailsafeConsolePropertiesSpi(Sample.UNKNOWN, errorStacker);
    private final FailsafeConsolePropertiesSpi first = new FailsafeConsolePropertiesSpi(Sample.FIRST, errorStacker);
    private final FailsafeConsolePropertiesSpi npe = new FailsafeConsolePropertiesSpi(Failing.NPE, errorStacker);

    private void doNothing(String msg, Exception ex) {
    }
}
