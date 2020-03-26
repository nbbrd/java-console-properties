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
package nbbrd.console.properties;

import _test.Sample;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class ConsolePropertiesTest {

    @Test
    public void testGetStdInEncoding() {
        assertThat(empty.getStdInEncoding()).isEmpty();
        assertThat(unknown.getStdInEncoding()).isEmpty();
        assertThat(first.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(unknownFirst.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(firstSecond.getStdInEncoding()).contains(StandardCharsets.US_ASCII);
        assertThat(secondFirst.getStdInEncoding()).contains(StandardCharsets.ISO_8859_1);
    }

    @Test
    public void testGetStdOutEncoding() {
        assertThat(empty.getStdOutEncoding()).isEmpty();
        assertThat(unknown.getStdOutEncoding()).isEmpty();
        assertThat(first.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(unknownFirst.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(firstSecond.getStdOutEncoding()).contains(StandardCharsets.UTF_8);
        assertThat(secondFirst.getStdOutEncoding()).contains(StandardCharsets.UTF_16);
    }

    @Test
    public void testGetRows() {
        assertThat(empty.getRows()).isEmpty();
        assertThat(unknown.getRows()).isEmpty();
        assertThat(first.getRows()).hasValue(30);
        assertThat(unknownFirst.getRows()).hasValue(30);
        assertThat(firstSecond.getRows()).hasValue(30);
        assertThat(secondFirst.getRows()).hasValue(70);
    }

    @Test
    public void testGetColumns() {
        assertThat(empty.getColumns()).isEmpty();
        assertThat(unknown.getColumns()).isEmpty();
        assertThat(first.getColumns()).hasValue(120);
        assertThat(unknownFirst.getColumns()).hasValue(120);
        assertThat(firstSecond.getColumns()).hasValue(120);
        assertThat(secondFirst.getColumns()).hasValue(40);
    }

    private final ConsoleProperties empty = ConsoleProperties
            .builder()
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
}
