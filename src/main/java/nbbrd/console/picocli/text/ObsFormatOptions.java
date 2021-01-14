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
package nbbrd.console.picocli.text;

import nbbrd.console.picocli.LocaleConverter;
import picocli.CommandLine;

import java.util.Locale;

/**
 * @author Philippe Charles
 */
@lombok.Data
public class ObsFormatOptions implements ObsFormat {

    @CommandLine.Option(
            names = {"-L", "--locale"},
            paramLabel = "<locale>",
            converter = LocaleConverter.class,
            defaultValue = "",
            description = "Locale used to format dates, times and numbers."
    )
    private Locale locale;

    @CommandLine.Option(
            names = {"-D", "--date"},
            paramLabel = "<pattern>",
            defaultValue = "yyyy-MM-dd",
            description = "Pattern used to format dates."
    )
    private String datePattern;

    @CommandLine.Option(
            names = {"-S", "--datetime"},
            paramLabel = "<pattern>",
            defaultValue = "yyyy-MM-ddTHH:mm:ss",
            description = "Pattern used to format dates and times."
    )
    private String datetimePattern;

    @CommandLine.Option(
            names = {"-N", "--number"},
            paramLabel = "<pattern>",
            defaultValue = "",
            description = "Pattern used to format numbers."
    )
    private String numberPattern;

    @CommandLine.Option(
            names = {"--no-grouping"},
            defaultValue = "false",
            description = "Ignore number grouping."
    )
    private boolean ignoreNumberGrouping;
}
