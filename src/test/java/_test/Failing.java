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

import java.nio.charset.Charset;
import java.util.function.Supplier;
import nbbrd.console.properties.ConsoleProperties;

/**
 *
 * @author Philippe Charles
 */
@lombok.AllArgsConstructor
public final class Failing implements ConsoleProperties.Spi {

    public static final Failing NPE = new Failing(CustomRuntimeException::new);

    @lombok.NonNull
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
