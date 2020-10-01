package nbbrd.console.picocli;

import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static nbbrd.console.picocli.SystemProperties.PATH_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class JarPathHelperTest {

    @Test
    public void testGetJarPath() {
        Properties context = new Properties();
        context.put(PATH_SEPARATOR, File.pathSeparator);

        JarPathHelper helper = JarPathHelper.of(SystemProperties.of(context));

        assertThatNullPointerException()
                .isThrownBy(() -> helper.getJarPath(null, path -> true));

        assertThatNullPointerException()
                .isThrownBy(() -> helper.getJarPath(JarPathHelperTest.class, null));

        assertThat(helper.getJarPath(JarPathHelperTest.class, path -> true))
                .isNull();

        context.put(SystemProperties.JAVA_CLASS_PATH, "hello.jar");
        assertThat(helper.getJarPath(JarPathHelperTest.class, path -> true))
                .hasFileName("hello.jar");

        context.put(SystemProperties.JAVA_CLASS_PATH, "hello.jar" + File.pathSeparator + "world.jar");
        assertThat(helper.getJarPath(JarPathHelperTest.class, path -> true))
                .hasFileName("hello.jar");

        context.put(SystemProperties.JAVA_CLASS_PATH, "hello.jar" + File.pathSeparator + "world.jar");
        assertThat(helper.getJarPath(JarPathHelperTest.class, path -> path.toString().contains("world")))
                .hasFileName("world.jar");
    }
}
