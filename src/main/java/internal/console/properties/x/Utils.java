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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Level;

/**
 * @author Philippe Charles
 */
@lombok.extern.java.Log
@lombok.experimental.UtilityClass
class Utils {

    boolean isWindows(UnaryOperator<String> sys) {
        String result = sys.apply("os.name");
        return result != null && result.startsWith("Windows");
    }

    boolean isCygwin(UnaryOperator<String> sys, UnaryOperator<String> env) {
        return isWindows(sys)
                && env.apply("PWD") != null
                && env.apply("PWD").startsWith("/")
                && !"cygwin".equals(env.apply("TERM"));
    }

    boolean isMingwXterm(UnaryOperator<String> sys, UnaryOperator<String> env) {
        return isWindows(sys)
                && env.apply("MSYSTEM") != null
                && env.apply("MSYSTEM").startsWith("MINGW")
                && "xterm".equals(env.apply("TERM"));
    }

    @FunctionalInterface
    interface ExternalCommand {

        @NonNull
        Optional<String> exec(@NonNull String... command);

        static ExternalCommand getDefault() {
            return Utils::execToString;
        }
    }

    private Optional<String> execToString(String... command) {
        try {
            return Optional.of(ProcessReader.readToString(command));
        } catch (IOException ex) {
            if (log.isLoggable(Level.WARNING)) {
                log.log(Level.WARNING, "Failed to execute command: " + Arrays.toString(command), ex);
            }
        }
        return Optional.empty();
    }
}
