package nbbrd.console.picocli;

import _test.Values;
import lombok.NonNull;
import nbbrd.io.function.IOPredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class MultiFileInputTest {

    @Test
    public void testGetAllFiles(@TempDir Path tmp) throws IOException {
        Path f2 = tmp.resolve("f2.txt");
        Values.write(f2, UTF_8, false, "world");

        Path f1 = tmp.resolve("f1.txt");
        Values.write(f1, UTF_8, false, "hello");

        Path folder = tmp.resolve("folder");
        Files.createDirectory(folder);

        Path f3 = folder.resolve("f3.txt");
        Values.write(f3, UTF_8, false, "other");

        assertThat(MockedInput.builder().file(tmp).recursive(false).build().getAllFiles(ALL_FILES))
                .isSorted()
                .containsExactly(f1, f2, folder);

        assertThat(MockedInput.builder().file(tmp).recursive(true).build().getAllFiles(ALL_FILES))
                .isSorted()
                .containsExactly(tmp, f1, f2, folder, f3);

        assertThat(MockedInput.builder().file(f1).recursive(false).build().getAllFiles(ALL_FILES))
                .isSorted()
                .containsExactly(f1);

        assertThat(MockedInput.builder().file(f1).recursive(true).build().getAllFiles(ALL_FILES))
                .isSorted()
                .containsExactly(f1);
    }

    private static final @NonNull IOPredicate<Path> ALL_FILES = IOPredicate.of(true);

    @lombok.Value
    @lombok.Builder
    private static class MockedInput implements MultiFileInput {

        @lombok.Singular
        List<Path> files;

        boolean recursive;

        boolean skipErrors;
    }
}
