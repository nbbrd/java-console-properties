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
public final class MingwXterm implements ConsoleProperties.Spi {

    private final UnaryOperator<String> sys;
    private final UnaryOperator<String> env;
    private final Utils.ExternalCommand cmd;

    public MingwXterm() {
        this(System::getProperty, System::getenv, Utils.ExternalCommand.getDefault());
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
        return Utils.isMingwXterm(sys, env)
                ? cmd.exec("tput", "cols").map(Integer::valueOf).orElse(UNKNOWN_COLUMNS)
                : UNKNOWN_COLUMNS;
    }

    @Override
    public int getRows() {
        return Utils.isMingwXterm(sys, env)
                ? cmd.exec("tput", "lines").map(Integer::valueOf).orElse(UNKNOWN_ROWS)
                : UNKNOWN_ROWS;
    }

    private Charset getLocaleEncodingOrNull() {
        return Utils.isMingwXterm(sys, env)
                ? cmd.exec("locale", "charmap").map(Charset::forName).orElse(null)
                : null;
    }
}
