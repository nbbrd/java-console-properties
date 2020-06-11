package nbbrd.console.picocli;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Properties;

import static nbbrd.console.picocli.SystemProperties.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class SystemPropertiesTest {

    @Test
    public void testFactories() {
        assertThatNullPointerException()
                .isThrownBy(() -> of(null));

        assertThat(ofDefault())
                .isNotNull();
    }

    @Test
    public void testGetUserHome() {
        Properties p = new Properties();
        SystemProperties x = of(p);

        assertThat(x.getUserHome())
                .isNull();

        p.put(USER_HOME, "C:\\Temp");
        assertThat(x.getUserHome())
                .isEqualByComparingTo(Paths.get("C:\\Temp"));
    }

    @Test
    public void testGetUserDir() {
        Properties p = new Properties();
        SystemProperties x = of(p);

        assertThat(x.getUserDir())
                .isNull();

        p.put(USER_DIR, "C:\\Temp");
        assertThat(x.getUserDir())
                .isEqualByComparingTo(Paths.get("C:\\Temp"));
    }

    @Test
    public void testGetClassPath() {
        Properties p = new Properties();
        SystemProperties x = of(p);

        assertThat(x.getClassPath())
                .isEmpty();

        p.put(PATH_SEPARATOR, ";");
        assertThat(x.getClassPath())
                .isEmpty();

        p.put(JAVA_CLASS_PATH, "C:\\Temp\\x.jar");
        assertThat(x.getClassPath())
                .containsExactly(Paths.get("C:\\Temp\\x.jar"));

        p.put(JAVA_CLASS_PATH, "C:\\Temp\\x.jar;C:\\Temp\\y.jar");
        assertThat(x.getClassPath())
                .containsExactly(
                        Paths.get("C:\\Temp\\x.jar"),
                        Paths.get("C:\\Temp\\y.jar")
                );
    }

    @Test
    public void testGetPathSeparator() {
        Properties p = new Properties();
        SystemProperties x = of(p);

        assertThat(x.getPathSeparator())
                .isEqualTo('\0');

        p.put(PATH_SEPARATOR, "");
        assertThat(x.getPathSeparator())
                .isEqualTo('\0');

        p.put(PATH_SEPARATOR, "; ");
        assertThat(x.getPathSeparator())
                .isEqualTo('\0');

        p.put(PATH_SEPARATOR, ";");
        assertThat(x.getPathSeparator())
                .isEqualTo(';');
    }
}
