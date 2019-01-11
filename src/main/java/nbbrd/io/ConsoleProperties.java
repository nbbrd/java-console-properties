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
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class allows to retrieve some console properties such as size and
 * encoding.
 *
 * @author Philippe Charles
 */
@ThreadSafe
@lombok.Builder(builderClassName = "Builder", toBuilder = true)
public final class ConsoleProperties {

    /**
     * Creates a new instance by getting resources from ServiceLoader.
     *
     * @return a new instance
     */
    @Nonnull
    public static ConsoleProperties ofServiceLoader() {
        return of(
                ServiceLoader.load(Spi.class),
                new UnexpectedErrorLogger(Logger.getLogger(ConsoleProperties.class.getName()), Level.WARNING)
        );
    }

    private static ConsoleProperties of(Iterable<? extends Spi> list, BiConsumer<? super String, ? super RuntimeException> onUnexpectedError) {
        return ConsoleProperties
                .builder()
                .providers(getProviders(list, onUnexpectedError))
                .onUnexpectedError(onUnexpectedError)
                .build();
    }

    @lombok.Singular
    private final List<Spi> providers;

    @lombok.NonNull
    private final BiConsumer<? super String, ? super RuntimeException> onUnexpectedError;

    /**
     * Gets the encoding that the console uses to read input.
     *
     * @return an optional encoding
     */
    @Nonnull
    public Optional<Charset> getStdInEncoding() {
        return providers
                .stream()
                .map(this::tryGetStdInEncoding)
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Gets the encoding that the console uses to write output.
     *
     * @return an optional encoding
     */
    @Nonnull
    public Optional<Charset> getStdOutEncoding() {
        return providers
                .stream()
                .map(this::tryGetStdOutEncoding)
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Gets the number of columns in the console.
     *
     * @return an optional number of columns
     */
    @Nonnull
    public OptionalInt getColumns() {
        return providers
                .stream()
                .mapToInt(this::tryGetColumns)
                .filter(this::isNotNegative)
                .findFirst();
    }

    /**
     * Gets the number of rows in the console.
     *
     * @return an optional number of rows
     */
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

    @lombok.AllArgsConstructor
    private static final class UnexpectedErrorLogger implements BiConsumer<String, RuntimeException> {

        @lombok.NonNull
        private final Logger log;

        @lombok.NonNull
        private final Level level;

        @Override
        public void accept(String msg, RuntimeException ex) {
            if (log.isLoggable(level)) {
                log.log(level, msg, ex);
            }
        }
    }

    private static int tryGetRank(Spi o, BiConsumer<? super String, ? super RuntimeException> onUnexpectedError) {
        try {
            return o.getRank();
        } catch (RuntimeException ex) {
            onUnexpectedError.accept("While calling 'getRank' on '" + o + "'", ex);
            return Spi.UNKNOWN_RANK;
        }
    }

    static List<Spi> getProviders(Iterable<? extends Spi> list, BiConsumer<? super String, ? super RuntimeException> onUnexpectedError) {
        List<Spi> providers = new ArrayList<>();
        for (Spi o : list) {
            if (o != null) {
                providers.add(o);
            }
        }
        providers.sort(Comparator.comparingInt((Spi o) -> tryGetRank(o, onUnexpectedError)).thenComparing(o -> o.getClass().getName()));
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
