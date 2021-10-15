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

import lombok.AccessLevel;
import nbbrd.console.properties.ConsoleProperties;
import nbbrd.io.sys.OS;
import nbbrd.service.ServiceProvider;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.BiConsumer;

import static internal.console.properties.x.Utils.NORMAL_RANK;

/**
 * @author Philippe Charles
 */
@ServiceProvider(ConsoleProperties.Spi.class)
@lombok.AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class LocaleConsoleProvider implements ConsoleProperties.Spi {

    @lombok.NonNull
    private final BiConsumer<IOException, String[]> onError;

    public LocaleConsoleProvider() {
        this(Utils::logCommandException);
    }

    @Override
    public boolean isAvailable() {
        switch (OS.NAME) {
            case MACOS:
            case LINUX:
            case SOLARIS:
                return true;
            case WINDOWS:
                return Utils.isMingwXterm(System::getenv);
            default:
                return false;
        }
    }

    @Override
    public int getRank() {
        return NORMAL_RANK;
    }

    @Override
    public Charset getStdInEncodingOrNull() {
        return getLocaleEncodingOrNull();
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        return getLocaleEncodingOrNull();
    }

    @Override
    public int getColumns() {
        return UNKNOWN_COLUMNS;
    }

    @Override
    public int getRows() {
        return UNKNOWN_ROWS;
    }

    private Charset getLocaleEncodingOrNull() {
        return Utils.execToString(onError, "locale", "charmap")
                .map(Charset::forName)
                .orElse(null);
    }
}
