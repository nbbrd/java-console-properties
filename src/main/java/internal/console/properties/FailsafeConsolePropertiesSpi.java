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

import java.nio.charset.Charset;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import nbbrd.console.properties.ConsoleProperties;

/**
 *
 * @author Philippe Charles
 */
@lombok.extern.java.Log
@lombok.AllArgsConstructor
public final class FailsafeConsolePropertiesSpi implements ConsoleProperties.Spi {

    public static ConsoleProperties.Spi wrap(ConsoleProperties.Spi delegate) {
        return new FailsafeConsolePropertiesSpi(delegate, FailsafeConsolePropertiesSpi::logUnexpectedError);
    }

    @lombok.NonNull
    private final ConsoleProperties.Spi delegate;

    @lombok.NonNull
    private final BiConsumer<? super String, ? super RuntimeException> onUnexpectedError;

    @Override
    public int getRank() {
        try {
            return delegate.getRank();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getRank' on '" + delegate + "'", ex);
            return ConsoleProperties.Spi.UNKNOWN_RANK;
        }
    }

    @Override
    public Charset getStdInEncodingOrNull() {
        try {
            return delegate.getStdInEncodingOrNull();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getStdInEncodingOrNull' on '" + delegate + "'", ex);
            return null;
        }
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        try {
            return delegate.getStdOutEncodingOrNull();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getStdOutEncodingOrNull' on '" + delegate + "'", ex);
            return null;
        }
    }

    @Override
    public int getColumns() {
        try {
            return delegate.getColumns();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getColumns' on '" + delegate + "'", ex);
            return ConsoleProperties.Spi.UNKNOWN_COLUMNS;
        }
    }

    @Override
    public int getRows() {
        try {
            return delegate.getRows();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getRows' on '" + delegate + "'", ex);
            return ConsoleProperties.Spi.UNKNOWN_ROWS;
        }
    }

    static void logUnexpectedError(String msg, RuntimeException ex) {
        if (log.isLoggable(Level.WARNING)) {
            log.log(Level.WARNING, msg, ex);
        }
    }
}
