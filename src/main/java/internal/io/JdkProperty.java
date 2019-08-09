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
package internal.io;

import java.nio.charset.Charset;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import nbbrd.console.properties.ConsoleProperties;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(ConsoleProperties.Spi.class)
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JdkProperty implements ConsoleProperties.Spi {

    @lombok.NonNull
    private final UnaryOperator<String> sys;

    public JdkProperty() {
        this(System::getProperty);
    }

    @Override
    public int getRank() {
        return 10;
    }

    @Override
    public Charset getStdInEncodingOrNull() {
        return getPropertyEncodingOrNull("sun.stdout.encoding");
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        return getPropertyEncodingOrNull("sun.stdout.encoding");
    }

    @Override
    public int getColumns() {
        return UNKNOWN_COLUMNS;
    }

    @Override
    public int getRows() {
        return UNKNOWN_ROWS;
    }

    private Charset getPropertyEncodingOrNull(String property) {
        String result = sys.apply(property);
        return result != null ? Charset.forName(result) : null;
    }
}
