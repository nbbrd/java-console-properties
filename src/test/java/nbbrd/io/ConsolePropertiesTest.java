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
package nbbrd.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;
import static nbbrd.io.ConsoleProperties.Spi.UNKNOWN_COLUMNS;
import static nbbrd.io.ConsoleProperties.Spi.UNKNOWN_ROWS;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class ConsolePropertiesTest {

    @Test
    public void testGetProviders() {
        assertThat(ConsoleProperties.getProviders(Arrays.asList()))
                .isEmpty();

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Sample.UNKNOWN)))
                .containsExactly(Sample.UNKNOWN);

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Sample.FIRST)))
                .containsExactly(Sample.FIRST);

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Sample.UNKNOWN, Sample.FIRST)))
                .containsExactly(Sample.FIRST, Sample.UNKNOWN);

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Sample.FIRST, Sample.SECOND)))
                .containsExactly(Sample.FIRST, Sample.SECOND);

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Sample.SECOND, Sample.FIRST)))
                .containsExactly(Sample.FIRST, Sample.SECOND);

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Sample.FIRST, Failing.NPE)))
                .containsExactly(Sample.FIRST, Failing.NPE);

        assertThat(ConsoleProperties.getProviders(Arrays.asList(Failing.NPE, Sample.FIRST)))
                .containsExactly(Sample.FIRST, Failing.NPE);
    }

    @Test
    public void testGetStdInEncoding() {
        errorStack.clear();

        assertThat(empty.getStdInEncoding()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(unknown.getStdInEncoding()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(first.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(errorStack).isEmpty();

        assertThat(unknownFirst.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(errorStack).isEmpty();

        assertThat(firstSecond.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(errorStack).isEmpty();

        assertThat(secondFirst.getStdInEncoding()).contains(StandardCharsets.ISO_8859_1);
        assertThat(errorStack).isEmpty();

        assertThat(firstNpe.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(errorStack).isEmpty();

        assertThat(npeFirst.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(errorStack).hasSize(1).first().isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetStdOutEncoding() {
        errorStack.clear();

        assertThat(empty.getStdOutEncoding()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(unknown.getStdOutEncoding()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(first.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(errorStack).isEmpty();

        assertThat(unknownFirst.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(errorStack).isEmpty();

        assertThat(firstSecond.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(errorStack).isEmpty();

        assertThat(secondFirst.getStdOutEncoding()).contains(StandardCharsets.UTF_16);
        assertThat(errorStack).isEmpty();

        assertThat(firstNpe.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(errorStack).isEmpty();

        assertThat(npeFirst.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(errorStack).hasSize(1).first().isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetRows() {
        errorStack.clear();

        assertThat(empty.getRows()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(unknown.getRows()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(first.getRows()).hasValue(30);
        assertThat(errorStack).isEmpty();

        assertThat(unknownFirst.getRows()).hasValue(30);
        assertThat(errorStack).isEmpty();

        assertThat(firstSecond.getRows()).hasValue(30);
        assertThat(errorStack).isEmpty();

        assertThat(secondFirst.getRows()).hasValue(70);
        assertThat(errorStack).isEmpty();

        assertThat(firstNpe.getRows()).hasValue(30);
        assertThat(errorStack).isEmpty();

        assertThat(npeFirst.getRows()).hasValue(30);
        assertThat(errorStack).hasSize(1).first().isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetColumns() {
        errorStack.clear();

        assertThat(empty.getColumns()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(unknown.getColumns()).isEmpty();
        assertThat(errorStack).isEmpty();

        assertThat(first.getColumns()).hasValue(120);
        assertThat(errorStack).isEmpty();

        assertThat(unknownFirst.getColumns()).hasValue(120);
        assertThat(errorStack).isEmpty();

        assertThat(firstSecond.getColumns()).hasValue(120);
        assertThat(errorStack).isEmpty();

        assertThat(secondFirst.getColumns()).hasValue(40);
        assertThat(errorStack).isEmpty();

        assertThat(firstNpe.getColumns()).hasValue(120);
        assertThat(errorStack).isEmpty();

        assertThat(npeFirst.getColumns()).hasValue(120);
        assertThat(errorStack).hasSize(1).first().isInstanceOf(NullPointerException.class);
    }

    private final Queue<Exception> errorStack = new LinkedList<>();

    private final ConsoleProperties empty = ConsoleProperties
            .builder()
            .onUnexpectedError((m, e) -> errorStack.add(e))
            .build();

    private final ConsoleProperties unknown = empty
            .toBuilder()
            .provider(Sample.UNKNOWN)
            .build();

    private final ConsoleProperties first = empty
            .toBuilder()
            .provider(Sample.FIRST)
            .build();

    private final ConsoleProperties unknownFirst = empty
            .toBuilder()
            .provider(Sample.UNKNOWN)
            .provider(Sample.FIRST)
            .build();

    private final ConsoleProperties firstSecond = empty
            .toBuilder()
            .provider(Sample.FIRST)
            .provider(Sample.SECOND)
            .build();

    private final ConsoleProperties secondFirst = empty
            .toBuilder()
            .provider(Sample.SECOND)
            .provider(Sample.FIRST)
            .build();

    private final ConsoleProperties firstNpe = empty
            .toBuilder()
            .provider(Sample.FIRST)
            .provider(Failing.NPE)
            .build();

    private final ConsoleProperties npeFirst = empty
            .toBuilder()
            .provider(Failing.NPE)
            .provider(Sample.FIRST)
            .build();

    @lombok.Value
    private static final class Sample implements ConsoleProperties.Spi {

        static final Sample UNKNOWN = new Sample(UNKNOWN_RANK, null, null, UNKNOWN_COLUMNS, UNKNOWN_ROWS);
        static final Sample FIRST = new Sample(1, StandardCharsets.US_ASCII, StandardCharsets.UTF_8, 120, 30);
        static final Sample SECOND = new Sample(2, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_16, 40, 70);

        private int rank;
        private Charset stdInEncodingOrNull;
        private Charset stdOutEncodingOrNull;
        private int columns;
        private int rows;
    }

    @lombok.AllArgsConstructor
    private static final class Failing implements ConsoleProperties.Spi {

        static final Failing NPE = new Failing(NullPointerException::new);

        private final Supplier<? extends RuntimeException> exception;

        @Override
        public int getRank() {
            throw exception.get();
        }

        @Override
        public Charset getStdInEncodingOrNull() {
            throw exception.get();
        }

        @Override
        public Charset getStdOutEncodingOrNull() {
            throw exception.get();
        }

        @Override
        public int getColumns() {
            throw exception.get();
        }

        @Override
        public int getRows() {
            throw exception.get();
        }
    }
}
