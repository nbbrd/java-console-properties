/*
 * Copyright 2018 National Bank of Belgium
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
package nbbrd.io;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 *
 * @author Philippe Charles
 */
@ThreadSafe
@lombok.extern.java.Log
@lombok.Builder(builderClassName = "Builder", toBuilder = true)
public final class ConsoleProperties {

    @Nonnull
    public static ConsoleProperties ofServiceLoader() {
        return ConsoleProperties
                .builder()
                .providers(getProviders(ServiceLoader.load(Spi.class)))
                .onUnexpectedError(ConsoleProperties::logUnexpectedError)
                .build();
    }

    @lombok.Singular
    private final List<Spi> providers;

    @lombok.NonNull
    private final BiConsumer<? super String, ? super RuntimeException> onUnexpectedError;

    @Nonnull
    public Optional<Charset> getStdInEncoding() {
        return providers
                .stream()
                .map(this::tryGetStdInEncoding)
                .filter(Objects::nonNull)
                .findFirst();
    }

    @Nonnull
    public Optional<Charset> getStdOutEncoding() {
        return providers
                .stream()
                .map(this::tryGetStdOutEncoding)
                .filter(Objects::nonNull)
                .findFirst();
    }

    @Nonnull
    public OptionalInt getColumns() {
        return providers
                .stream()
                .mapToInt(this::tryGetColumns)
                .filter(this::isNotNegative)
                .findFirst();
    }

    @Nonnull
    public OptionalInt getRows() {
        return providers
                .stream()
                .mapToInt(this::tryGetRows)
                .filter(this::isNotNegative)
                .findFirst();
    }

    private Charset tryGetStdInEncoding(Spi o) {
        try {
            return o.getStdInEncodingOrNull();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getStdInEncodingOrNull' on '" + o + "'", ex);
            return null;
        }
    }

    private Charset tryGetStdOutEncoding(Spi o) {
        try {
            return o.getStdOutEncodingOrNull();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getStdOutEncodingOrNull' on '" + o + "'", ex);
            return null;
        }
    }

    private int tryGetColumns(Spi o) {
        try {
            return o.getColumns();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getColumns' on '" + o + "'", ex);
            return Spi.UNKNOWN_COLUMNS;
        }
    }

    private int tryGetRows(Spi o) {
        try {
            return o.getRows();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getRows' on '" + o + "'", ex);
            return Spi.UNKNOWN_ROWS;
        }
    }

    private boolean isNotNegative(int value) {
        return value >= 0;
    }

    private static void logUnexpectedError(String msg, RuntimeException ex) {
        if (log.isLoggable(Level.WARNING)) {
            log.log(Level.WARNING, msg, ex);
        }
    }

    private static int tryGetRank(Spi o) {
        try {
            return o.getRank();
        } catch (RuntimeException ex) {
            logUnexpectedError("While calling 'getRank' on '" + o + "'", ex);
            return Spi.UNKNOWN_RANK;
        }
    }

    static List<Spi> getProviders(Iterable<? extends Spi> list) {
        List<Spi> providers = new ArrayList<>();
        for (Spi o : list) {
            if (o != null) {
                providers.add(o);
            }
        }
        providers.sort(Comparator.comparingInt(ConsoleProperties::tryGetRank).thenComparing(o -> o.getClass().getName()));
        return providers;
    }

    @ThreadSafe
    public interface Spi {

        int getRank();

        @Nullable
        Charset getStdInEncodingOrNull();

        @Nullable
        Charset getStdOutEncodingOrNull();

        int getColumns();

        int getRows();

        static int UNKNOWN_RANK = Integer.MAX_VALUE;
        static int UNKNOWN_COLUMNS = -1;
        static int UNKNOWN_ROWS = -1;
    }
}
