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
package nbbrd.console.picocli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
@lombok.Data
public class MultiFileInputOptions implements MultiFileInput {

    @CommandLine.Parameters(
            paramLabel = "<file>",
            description = "Input file(s).",
            arity = "1..*"
    )
    private List<Path> files;

    @CommandLine.Option(
            names = {"-r", "--recursive"},
            description = "Recursive walking.",
            defaultValue = "false"
    )
    private boolean recursive;

    @CommandLine.Option(
            names = {"-E", "--skip-errors"},
            description = "Skip errors.",
            defaultValue = "false"
    )
    private boolean skipErrors;
}
