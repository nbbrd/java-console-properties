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
package internal.io;

import java.nio.charset.Charset;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import nbbrd.io.ConsoleProperties;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ConsoleProperties.Spi.class)
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandPrompt implements ConsoleProperties.Spi {

    @lombok.NonNull
    private final UnaryOperator<String> sys;

    @lombok.NonNull
    private final Utils.ExternalCommand cmd;

    public CommandPrompt() {
        this(System::getProperty, Utils.ExternalCommand.getDefault());
    }

    @Override
    public int getRank() {
        return 30;
    }

    @Override
    public Charset getStdInEncodingOrNull() {
        return getChcpEncodingOrNull();
    }

    @Override
    public Charset getStdOutEncodingOrNull() {
        return getChcpEncodingOrNull();
    }

    @Override
    public int getColumns() {
        return Utils.isWindows(sys)
                ? cmd.exec("powershell", "-command", "(Get-Host).ui.rawui.windowsize.width").map(Integer::valueOf).orElse(UNKNOWN_COLUMNS)
                : UNKNOWN_COLUMNS;
    }

    @Override
    public int getRows() {
        return Utils.isWindows(sys)
                ? cmd.exec("powershell", "-command", "(Get-Host).ui.rawui.windowsize.height").map(Integer::valueOf).orElse(UNKNOWN_ROWS)
                : UNKNOWN_ROWS;
    }

    private Charset getChcpEncodingOrNull() {
        return Utils.isWindows(sys)
                ? cmd.exec("cmd", "/C", "chcp").map(CommandPrompt::parseChcp).orElse(null)
                : null;
    }

    static Charset parseChcp(String chcp) {
        Matcher m = Pattern.compile("\\d+", Pattern.MULTILINE).matcher(chcp);
        if (m.find()) {
            return Charset.forName("cp" + m.group());
        }
        throw new IllegalArgumentException("Invalid chcp result: '" + chcp + "'");
    }
}
