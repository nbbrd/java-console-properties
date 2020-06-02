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

import nbbrd.console.picocli.yaml.YamlOutputOptions;
import nbbrd.console.properties.ConsoleProperties;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

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
    private YamlOutputOptions output = new YamlOutputOptions();

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
        output.dump(getYaml(), getType().get(), this::getStdOutEncoding);
        return null;
    }

    private Yaml getYaml() {
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(opts);
    }

    private Optional<Charset> getStdOutEncoding() {
        return ConsoleProperties.ofServiceLoader().getStdOutEncoding();
    }

    public enum ContextType implements Supplier<Object> {
        SYS {
            @Override
            public Object get() {
                return new TreeMap<>(System.getProperties());
            }
        },
        ENV {
            @Override
            public Object get() {
                return new TreeMap<>(System.getenv());
            }
        };
    }
}
