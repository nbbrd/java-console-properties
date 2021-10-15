/*
 * Copyright 2019 National Bank of Belgium
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

import lombok.AccessLevel;
import nbbrd.console.properties.ConsoleProperties;
import nbbrd.io.sys.OS;
import nbbrd.service.ServiceProvider;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.BiConsumer;

import static internal.console.properties.x.Utils.SLOW_RANK;

/**
 * @author Philippe Charles
 */
@ServiceProvider(ConsoleProperties.Spi.class)
@lombok.AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class PowerShellConsoleProvider implements ConsoleProperties.Spi {

    @lombok.NonNull
    private final BiConsumer<IOException, String[]> onError;

    public PowerShellConsoleProvider() {
        this(Utils::logCommandException);
    }

    @Override
    public boolean isAvailable() {
        return OS.NAME.equals(OS.Name.WINDOWS) || isCoreAvailable();
    }

    @Override
    public int getRank() {
        return SLOW_RANK;
    }

    @Override
    public Charset getStdInEncodingOrNull() {
        return execPowerShell("[Console]::InputEncoding.WebName")
                .map(String::trim)
                .map(Charset::forName)
                .orElse(null);
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        return execPowerShell("[Console]::OutputEncoding.WebName")
                .map(String::trim)
                .map(Charset::forName)
                .orElse(null);
    }

    @Override
    public int getColumns() {
        return execPowerShell("(Get-Host).ui.rawui.windowsize.width")
                .map(Integer::valueOf)
                .orElse(UNKNOWN_COLUMNS);
    }

    @Override
    public int getRows() {
        return execPowerShell("(Get-Host).ui.rawui.windowsize.height")
                .map(Integer::valueOf)
                .orElse(UNKNOWN_ROWS);
    }

    private Optional<String> execPowerShell(String command) {
        return Utils.execToString(onError, getExecutable(), "-command", command);
    }

    private boolean isCoreAvailable() {
        try {
            return WhichWrapper.isAvailable(CORE_EXECUTABLE);
        } catch (IOException ex) {
            onError.accept(ex, new String[0]);
            return false;
        }
    }

    private static String getExecutable() {
        return OS.NAME.equals(OS.Name.WINDOWS) ? WIN_EXECUTABLE : CORE_EXECUTABLE;
    }

    private static final String WIN_EXECUTABLE = "powershell";
    private static final String CORE_EXECUTABLE = "pwsh";
}
