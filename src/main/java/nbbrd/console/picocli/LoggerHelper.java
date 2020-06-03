package nbbrd.console.picocli;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

@lombok.experimental.UtilityClass
public class LoggerHelper {

    public void disableDefaultConsoleLogger() {
        if (System.getProperty("java.util.logging.config.file") == null) {
            Logger global = Logger.getLogger("");
            for (Handler o : global.getHandlers()) {
                if (o instanceof ConsoleHandler) {
                    global.removeHandler(o);
                }
            }
        }
    }
}
