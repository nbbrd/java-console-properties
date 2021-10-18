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
package _test;

import nbbrd.console.properties.ConsoleProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Philippe Charles
 */
@lombok.Value
public final class Sample implements ConsoleProperties.Spi {

    public static final Sample UNKNOWN = new Sample(true, UNKNOWN_RANK, null, null, UNKNOWN_COLUMNS, UNKNOWN_ROWS);
    public static final Sample FIRST = new Sample(true, 1, StandardCharsets.US_ASCII, StandardCharsets.UTF_8, 120, 30);
    public static final Sample SECOND = new Sample(true, 2, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_16, 40, 70);

    boolean available;
    int rank;
    Charset stdInEncodingOrNull;
    Charset stdOutEncodingOrNull;
    int columns;
    int rows;
}
