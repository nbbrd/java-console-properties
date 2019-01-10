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
import nbbrd.io.ConsoleProperties;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ConsoleProperties.Spi.class)
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JdkProperty implements ConsoleProperties.Spi {

    private final UnaryOperator<String> sys;

    public JdkProperty() {
        this(System::getProperty);
    }

    @Override
    public int getRank() {
        return 10;
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        String result = sys.apply("sun.stdout.encoding");
        return result != null ? Charset.forName(result) : null;
    }

    @Override
    public int getColumns() {
        return UNKNOWN_COLUMNS;
    }

    @Override
    public int getRows() {
        return UNKNOWN_ROWS;
    }
}
