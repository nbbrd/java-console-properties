package nbbrd.console.picocli;

import nbbrd.io.sys.SystemProperties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Function;

import static nbbrd.io.sys.SystemProperties.*;
import static org.assertj.core.api.Assertions.*;

public class ConfigHelperTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testFactories() {
        assertThatNullPointerException()
                .isThrownBy(() -> ConfigHelper.of(null));
    }

    @Test
    public void testLoad() throws IOException {
        Properties context = new Properties();
        context.put(PATH_SEPARATOR, File.pathSeparator);

        ConfigHelper helper = ConfigHelper
                .builder()
                .appName("abc")
                .system(SystemProperties.of(context, FileSystems.getDefault()))
                .build();

        assertThatNullPointerException()
                .isThrownBy(() -> helper.load(null, ConfigHelper.Scope.SYSTEM));

        assertThatNullPointerException()
                .isThrownBy(() -> helper.load(new Properties(), null));

        Path systemCfg = write("systemLoad", "abc.properties", "keySystem=valueSystem");
        Path globalCfg = write("globalLoad", "abc.properties", "keyGlobal=valueGlobal");
        Path localCfg = write("localLoad", "abc.properties", "keyLocal=valueLocal");

        Function<ConfigHelper.Scope, Properties> loader = scope -> {
            Properties result = new Properties();
            helper.load(result, scope);
            return result;
        };

        assertThat(loader.apply(ConfigHelper.Scope.SYSTEM))
                .isEmpty();

        context.put(JAVA_CLASS_PATH, systemCfg.resolveSibling("abc.jar").toString());
        assertThat(loader.apply(ConfigHelper.Scope.SYSTEM))
                .hasSize(1)
                .containsEntry("keySystem", "valueSystem");

        context.put(JAVA_CLASS_PATH, systemCfg.resolveSibling("abc.jar") + File.pathSeparator + globalCfg.resolveSibling("zzz.jar"));
        assertThat(loader.apply(ConfigHelper.Scope.SYSTEM))
                .hasSize(1)
                .containsEntry("keySystem", "valueSystem");

        assertThat(loader.apply(ConfigHelper.Scope.GLOBAL))
                .isEmpty();

        context.put(USER_HOME, globalCfg.getParent().toString());
        assertThat(loader.apply(ConfigHelper.Scope.GLOBAL))
                .hasSize(1)
                .containsEntry("keyGlobal", "valueGlobal");

        assertThat(loader.apply(ConfigHelper.Scope.LOCAL))
                .isEmpty();

        context.put(USER_DIR, localCfg.getParent().toString());
        assertThat(loader.apply(ConfigHelper.Scope.LOCAL))
                .hasSize(1)
                .containsEntry("keyLocal", "valueLocal");
    }

    private Path write(String folderName, String fileName, String content) throws IOException {
        Path result = temp.newFolder(folderName).toPath().resolve(fileName);
        Files.write(result, Collections.singleton(content));
        return result;
    }

    @Test
    public void testLoadAll() {
        Properties context = new Properties();
        context.put(PATH_SEPARATOR, File.pathSeparator);

        ConfigHelper helper = ConfigHelper
                .builder()
                .appName("abc")
                .system(SystemProperties.of(context, FileSystems.getDefault()))
                .build();

        assertThatNullPointerException()
                .isThrownBy(() -> helper.loadAll(null));
    }

    @Test
    public void testLoadFile() {
        assertThatNullPointerException()
                .isThrownBy(() -> ConfigHelper.of("abc").loadFile(null, Paths.get("")));

        assertThatCode(() -> ConfigHelper.of("abc").loadFile(new Properties(), null))
                .doesNotThrowAnyException();
    }
}
