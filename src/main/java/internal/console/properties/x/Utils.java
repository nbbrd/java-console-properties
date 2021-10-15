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
package internal.console.properties.x;

import nbbrd.io.sys.ProcessReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import java.util.logging.Level;

/**
 * @author Philippe Charles
 */
@lombok.extern.java.Log
@lombok.experimental.UtilityClass
class Utils {

    static final int QUICK_RANK = 10;
    static final int NORMAL_RANK = 20;
    static final int SLOW_RANK = 30;

    static final String MSYSTEM_ENV = "MSYSTEM";
    static final String TERM_ENV = "TERM";

    static boolean isCygwin(UnaryOperator<String> env) {
        return env.apply("PWD") != null
                && env.apply("PWD").startsWith("/")
                && !"cygwin".equals(env.apply("TERM"));
    }

    static boolean isMingwXterm(UnaryOperator<String> env) {
        return env.apply(MSYSTEM_ENV) != null
                && env.apply(MSYSTEM_ENV).startsWith("MINGW")
                && isXterm(env);
    }

    static boolean isXterm(UnaryOperator<String> env) {
        return "xterm".equals(env.apply(TERM_ENV));
    }

    static Optional<String> execToString(BiConsumer<IOException, String[]> onError, String... command) {
        try {
            return Optional.of(ProcessReader.readToString(
                    new ProcessBuilder(command)
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .start()
            ));
        } catch (IOException ex) {
            onError.accept(ex, command);
        }
        return Optional.empty();
    }

    static void logCommandException(IOException ex, String[] command) {
        if (log.isLoggable(Level.WARNING)) {
            log.log(Level.WARNING, "Failed to execute command: " + Arrays.toString(command), ex);
        }
    }
}
