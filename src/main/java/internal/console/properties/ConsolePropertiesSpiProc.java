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
package internal.console.properties;

import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import nbbrd.console.properties.ConsoleProperties;

/**
 *
 * @author Philippe Charles
 */
public enum ConsolePropertiesSpiProc implements UnaryOperator<Stream<ConsoleProperties.Spi>> {

    INSTANCE;

    @Override
    public Stream<ConsoleProperties.Spi> apply(Stream<ConsoleProperties.Spi> t) {
        return t.map(FailsafeConsolePropertiesSpi::wrap)
                .sorted(
                        Comparator
                                .comparingInt(ConsoleProperties.Spi::getRank)
                                .thenComparing(o -> o.getClass().getName())
                );
    }
}
