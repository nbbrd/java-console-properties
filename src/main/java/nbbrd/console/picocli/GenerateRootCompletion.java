package nbbrd.console.picocli;

import picocli.AutoComplete;
import picocli.CommandLine;

@CommandLine.Command(name = "generate-completion", version = "generate-completion " + CommandLine.VERSION,
        mixinStandardHelpOptions = true,
        description = {
                "Generate bash/zsh completion script for ${PARENT-COMMAND-NAME:-the parent command of this command}.",
                "Run the following command to give `${PARENT-COMMAND-NAME:-$PARENTCOMMAND}` TAB completion in the current shell:",
                "",
                "  source <(${PARENT-COMMAND-FULL-NAME:-$PARENTCOMMAND} ${COMMAND-NAME})",
                ""},
        optionListHeading = "Options:%n",
        helpCommand = true
)
public class GenerateRootCompletion implements Runnable {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public void run() {
        String script = AutoComplete.bash(
                spec.root().name(),
                spec.root().commandLine());
        // not PrintWriter.println: scripts with Windows line separators fail in strange ways!
        spec.commandLine().getOut().print(script);
        spec.commandLine().getOut().print('\n');
        spec.commandLine().getOut().flush();
    }
}
