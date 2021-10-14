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
import nbbrd.service.ServiceProvider;

import java.nio.charset.Charset;
import java.util.function.UnaryOperator;

/**
 * @author Philippe Charles
 */
@ServiceProvider(ConsoleProperties.Spi.class)
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MingwXterm implements ConsoleProperties.Spi {

    @lombok.NonNull
    private final UnaryOperator<String> sys;

    @lombok.NonNull
    private final UnaryOperator<String> env;

    @lombok.NonNull
    private final Utils.ExternalCommand cmd;

    public MingwXterm() {
        this(System::getProperty, System::getenv, Utils.ExternalCommand.getDefault());
    }

    @Override
    public boolean isAvailable() {
        return Utils.isMingwXterm(sys, env);
    }

    @Override
    public int getRank() {
        return 20;
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
        return cmd.exec("tput", "cols").map(Integer::valueOf).orElse(UNKNOWN_COLUMNS);
    }

    @Override
    public int getRows() {
        return cmd.exec("tput", "lines").map(Integer::valueOf).orElse(UNKNOWN_ROWS);
    }

    private Charset getLocaleEncodingOrNull() {
        return cmd.exec("locale", "charmap").map(Charset::forName).orElse(null);
    }
}
