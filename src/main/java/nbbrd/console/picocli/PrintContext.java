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
package nbbrd.console.picocli;

import nbbrd.console.picocli.csv.CsvOutputOptions;
import nbbrd.console.properties.ConsoleProperties;
import nbbrd.io.function.IOConsumer;
import nbbrd.picocsv.Csv;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * @author Philippe Charles
 */
@CommandLine.Command(
        name = "print-context",
        mixinStandardHelpOptions = true,
        description = {"Print system and environment context."},
        optionListHeading = "Options:%n",
        helpCommand = true
)
public class PrintContext implements Callable<Void> {

    @CommandLine.ArgGroup(validate = false, heading = "%nCSV options:%n")
    private CsvOutputOptions output = new CsvOutputOptions();

    @lombok.Getter
    @lombok.Setter
    @CommandLine.Option(
            names = {"-t", "--type"},
            paramLabel = "<type>",
            description = "Context type (${COMPLETION-CANDIDATES}).",
            defaultValue = "SYS"
    )
    private ContextType type;

    @Override
    public Void call() throws Exception {
        try (Csv.Writer writer = output.newCsvWriter(this::getStdOutEncoding)) {
            getType().acceptWithIO(writer);
        }
        return null;
    }

    private Optional<Charset> getStdOutEncoding() {
        return ConsoleProperties.ofServiceLoader().getStdOutEncoding();
    }

    public enum ContextType implements IOConsumer<Csv.Writer> {
        SYS {
            @Override
            public void acceptWithIO(Csv.Writer w) throws IOException {
                w.writeField("Property");
                w.writeField("Value");
                w.writeEndOfLine();
                for (Map.Entry<Object, Object> env : System.getProperties().entrySet()) {
                    w.writeField(Objects.toString(env.getKey(), ""));
                    w.writeField(Objects.toString(env.getValue(), ""));
                    w.writeEndOfLine();
                }
            }
        },
        ENV {
            @Override
            public void acceptWithIO(Csv.Writer w) throws IOException {
                w.writeField("Key");
                w.writeField("Value");
                w.writeEndOfLine();
                for (Map.Entry<String, String> env : System.getenv().entrySet()) {
                    w.writeField(env.getKey());
                    w.writeField(env.getValue());
                    w.writeEndOfLine();
                }
            }
        };
    }
}
