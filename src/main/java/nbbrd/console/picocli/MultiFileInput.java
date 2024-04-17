/*
 * Copyright 2020 National Bank of Belgium
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
package nbbrd.console.picocli;

import internal.console.picocli.RecursiveFiles;
import nbbrd.io.function.IOConsumer;
import nbbrd.io.function.IOFunction;
import nbbrd.io.function.IOPredicate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Philippe Charles
 */
public interface MultiFileInput {

    List<Path> getFiles();

    boolean isRecursive();

    boolean isSkipErrors();

    default boolean isSingleFile() {
        return getFiles().size() == 1 && Files.isRegularFile(getFiles().get(0));
    }

    default Path getSingleFile() {
        return getFiles().get(0);
    }

    default List<Path> getAllFiles(IOPredicate<? super Path> filter) throws IOException {
        List<Path> result = new ArrayList<>();
        for (Path item : getFiles()) {
            try (Stream<Path> files = RecursiveFiles.walk(item, isRecursive(), filter)) {
                files.forEach(result::add);
            } catch (UncheckedIOException ex) {
                throw ex.getCause();
            }
        }
        Collections.sort(result);
        return result;
    }

    default <T> Function<Path, Optional<T>> asFunction(IOFunction<Path, T> delegate, BiConsumer<Exception, Path> report) {
        return isSkipErrors()
                ? RecursiveFiles.applyOrReport(delegate, report).andThen(Optional::ofNullable)
                : delegate.andThen(Optional::ofNullable).asUnchecked();
    }

    default Consumer<Path> asConsumer(IOConsumer<Path> delegate, BiConsumer<Exception, Path> report) {
        return isSkipErrors()
                ? RecursiveFiles.acceptOrReport(delegate, report)
                : delegate.asUnchecked();
    }
}
