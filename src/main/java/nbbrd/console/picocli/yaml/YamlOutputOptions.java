/*
 * Copyright 2020 National Bank of Belgium
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
package nbbrd.console.picocli.yaml;

import nbbrd.console.picocli.Profilable;
import nbbrd.console.picocli.text.TextOutputOptions;
import picocli.CommandLine;

/**
 * @author Philippe Charles
 */
@lombok.Getter
@lombok.Setter
public class YamlOutputOptions extends TextOutputOptions implements YamlOutput, Profilable {

    @CommandLine.Option(
            names = "--dummy-yaml-option",
            hidden = true,
            defaultValue = "false"
    )
    private boolean dummyYamlOption;
}
