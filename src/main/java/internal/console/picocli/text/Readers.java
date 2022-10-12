package internal.console.picocli.text;

import java.io.IOException;
import java.io.Reader;

@lombok.experimental.UtilityClass
public class Readers {

    public static String readString(Reader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[8 * 1024];
        int readCount = 0;
        while ((readCount = reader.read(buffer)) != -1) {
            result.append(buffer, 0, readCount);
        }
        return result.toString();
    }
}
