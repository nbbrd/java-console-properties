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
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class ConsolePropertiesSpiProcTest {

    @Test
    public void test() {
        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of()))
                .isEmpty();

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Sample.UNKNOWN)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.UNKNOWN);

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Sample.FIRST)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.FIRST);

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Sample.UNKNOWN, Sample.FIRST)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.FIRST, Sample.UNKNOWN);

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Sample.FIRST, Sample.SECOND)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.FIRST, Sample.SECOND);

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Sample.SECOND, Sample.FIRST)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.FIRST, Sample.SECOND);

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Sample.FIRST, Failing.NPE)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.FIRST, Failing.NPE);

        assertThat(ConsolePropertiesSpiProc.INSTANCE.apply(Stream.of(Failing.NPE, Sample.FIRST)))
                .extracting(FailsafeConsolePropertiesSpi::unwrap)
                .containsExactly(Sample.FIRST, Failing.NPE);
    }
}
