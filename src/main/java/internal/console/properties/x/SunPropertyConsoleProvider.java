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
import nbbrd.service.ServiceProvider;

import java.nio.charset.Charset;

import static internal.console.properties.x.Utils.QUICK_RANK;

/**
 * @author Philippe Charles
 */
@ServiceProvider(ConsoleProperties.Spi.class)
public final class SunPropertyConsoleProvider implements ConsoleProperties.Spi {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getRank() {
        return QUICK_RANK;
    }

    @Override
    public Charset getStdInEncodingOrNull() {
        return getPropertyEncodingOrNull(UNSUPPORTED_STDOUT_ENCODING);
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        return getPropertyEncodingOrNull(UNSUPPORTED_STDOUT_ENCODING);
    }

    @Override
    public int getColumns() {
        return UNKNOWN_COLUMNS;
    }

    @Override
    public int getRows() {
        return UNKNOWN_ROWS;
    }

    private static Charset getPropertyEncodingOrNull(String property) {
        String result = System.getProperty(property);
        return result != null ? Charset.forName(result) : null;
    }

    static final String UNSUPPORTED_STDOUT_ENCODING = "sun.stdout.encoding";
}
