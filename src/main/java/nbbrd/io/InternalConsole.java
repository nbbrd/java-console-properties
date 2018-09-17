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
package nbbrd.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@lombok.extern.java.Log
@lombok.experimental.UtilityClass
public class InternalConsole {

    @ServiceProvider(service = ConsoleProperties.Spi.class)
    @lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class JdkProperty implements ConsoleProperties.Spi {

        private final UnaryOperator<String> sys;

        public JdkProperty() {
            this(System::getProperty);
        }

        @Override
        public int getRank() {
            return 10;
        }

        @Override
        public Charset getStdOutEncodingOrNull() {
            String result = sys.apply("sun.stdout.encoding");
            return result != null ? Charset.forName(result) : null;
        }

        @Override
        public int getColumns() {
            return UNKNOWN_COLUMNS;
        }

        @Override
        public int getRows() {
            return UNKNOWN_ROWS;
        }
    }

    @ServiceProvider(service = ConsoleProperties.Spi.class)
    @lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class BashLocale implements ConsoleProperties.Spi {

        private final UnaryOperator<String> sys;
        private final UnaryOperator<String> env;
        private final ExternalCommand cmd;

        public BashLocale() {
            this(System::getProperty, System::getenv, InternalConsole::execToString);
        }

        @Override
        public int getRank() {
            return 20;
        }

        @Override
        public Charset getStdOutEncodingOrNull() {
            return isMingwXterm(sys, env)
                    ? cmd.exec("locale", "charmap").map(Charset::forName).orElse(null)
                    : null;
        }

        @Override
        public int getColumns() {
            return isMingwXterm(sys, env)
                    ? cmd.exec("tput", "cols").map(Integer::valueOf).orElse(UNKNOWN_COLUMNS)
                    : UNKNOWN_COLUMNS;
        }

        @Override
        public int getRows() {
            return isMingwXterm(sys, env)
                    ? cmd.exec("tput", "lines").map(Integer::valueOf).orElse(UNKNOWN_ROWS)
                    : UNKNOWN_ROWS;
        }
    }

    @ServiceProvider(service = ConsoleProperties.Spi.class)
    @lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CommandPrompt implements ConsoleProperties.Spi {

        private final UnaryOperator<String> sys;
        private final ExternalCommand cmd;

        public CommandPrompt() {
            this(System::getProperty, InternalConsole::execToString);
        }

        @Override
        public int getRank() {
            return 30;
        }

        @Override
        public Charset getStdOutEncodingOrNull() {
            return isWindows(sys)
                    ? cmd.exec("cmd", "/C", "chcp").map(this::parseChcp).orElse(null)
                    : null;
        }

        @Override
        public int getColumns() {
            return isWindows(sys)
                    ? cmd.exec("powershell", "-command", "(Get-Host).ui.rawui.windowsize.width").map(Integer::valueOf).orElse(UNKNOWN_COLUMNS)
                    : UNKNOWN_COLUMNS;
        }

        @Override
        public int getRows() {
            return isWindows(sys)
                    ? cmd.exec("powershell", "-command", "(Get-Host).ui.rawui.windowsize.height").map(Integer::valueOf).orElse(UNKNOWN_ROWS)
                    : UNKNOWN_ROWS;
        }

        private Charset parseChcp(String chcp) {
            Matcher m = Pattern.compile("\\d+", Pattern.MULTILINE).matcher(chcp);
            if (m.find()) {
                return Charset.forName("cp" + m.group());
            }
            throw new RuntimeException("Invalid chcp result: '" + chcp + "'");
        }
    }

    private boolean isWindows(UnaryOperator<String> sys) {
        String result = sys.apply("os.name");
        return result != null && result.startsWith("Windows");
    }

    private boolean isCygwin(UnaryOperator<String> sys, UnaryOperator<String> env) {
        return isWindows(sys)
                && env.apply("PWD") != null
                && env.apply("PWD").startsWith("/")
                && !"cygwin".equals(env.apply("TERM"));
    }

    private boolean isMingwXterm(UnaryOperator<String> sys, UnaryOperator<String> env) {
        return isWindows(sys)
                && env.apply("MSYSTEM") != null
                && env.apply("MSYSTEM").startsWith("MINGW")
                && "xterm".equals(env.apply("TERM"));
    }

    @FunctionalInterface
    private interface ExternalCommand {

        Optional<String> exec(String... command);
    }

    private void execTo(Appendable appendable, String... command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                appendable.append(line);
            }
        } finally {
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new IOException("Invalid exit value: " + exitValue);
            }
        }
    }

    private Optional<String> execToString(String... command) {
        try {
            StringBuilder sb = new StringBuilder();
            execTo(sb, command);
            return Optional.of(sb.toString());
        } catch (IOException | InterruptedException ex) {
            if (log.isLoggable(Level.WARNING)) {
                log.log(Level.WARNING, "Failed to execute command: " + Arrays.toString(command), ex);
            }
        }
        return Optional.empty();
    }
}
