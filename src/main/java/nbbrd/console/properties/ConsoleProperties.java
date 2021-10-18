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
package nbbrd.console.properties;

import internal.console.properties.ConsolePropertiesSpiLoader;
import internal.console.properties.FailsafeConsolePropertiesSpi;
import nbbrd.design.ThreadSafe;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import nbbrd.service.ServiceFilter;
import nbbrd.service.ServiceSorter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * This class allows to retrieve some console properties such as size and
 * encoding.
 *
 * @author Philippe Charles
 */
@ThreadSafe
@lombok.Builder(toBuilder = true)
public final class ConsoleProperties {

    /**
     * Creates a new instance by getting resources from ServiceLoader.
     *
     * @return a new instance
     */
    @NonNull
    public static ConsoleProperties ofServiceLoader() {
        return ConsoleProperties
                .builder()
                .providers(new ConsolePropertiesSpiLoader().get())
                .build();
    }

    @lombok.Singular
    private final List<Spi> providers;

    /**
     * Gets the encoding that the console uses to read input.
     *
     * @return an optional encoding
     */
    @NonNull
    public Optional<Charset> getStdInEncoding() {
        return providers
                .stream()
                .map(Spi::getStdInEncodingOrNull)
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Gets the encoding that the console uses to write output.
     *
     * @return an optional encoding
     */
    @NonNull
    public Optional<Charset> getStdOutEncoding() {
        return providers
                .stream()
                .map(Spi::getStdOutEncodingOrNull)
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Gets the number of columns in the console.
     *
     * @return an optional number of columns
     */
    @NonNull
    public OptionalInt getColumns() {
        return providers
                .stream()
                .mapToInt(Spi::getColumns)
                .filter(this::isNotNegative)
                .findFirst();
    }

    /**
     * Gets the number of rows in the console.
     *
     * @return an optional number of rows
     */
    @NonNull
    public OptionalInt getRows() {
        return providers
                .stream()
                .mapToInt(Spi::getRows)
                .filter(this::isNotNegative)
                .findFirst();
    }

    private boolean isNotNegative(int value) {
        return value >= 0;
    }

    @ThreadSafe
    @ServiceDefinition(
            loaderName = "internal.console.properties.ConsolePropertiesSpiLoader",
            quantifier = Quantifier.MULTIPLE,
            wrapper = FailsafeConsolePropertiesSpi.class
    )
    public interface Spi {

        @ServiceFilter
        boolean isAvailable();

        @ServiceSorter
        int getRank();

        @Nullable
        Charset getStdInEncodingOrNull();

        @Nullable
        Charset getStdOutEncodingOrNull();

        int getColumns();

        int getRows();

        int UNKNOWN_RANK = Integer.MAX_VALUE;
        int UNKNOWN_COLUMNS = -1;
        int UNKNOWN_ROWS = -1;
    }
}
