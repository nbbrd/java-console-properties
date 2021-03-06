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

import java.nio.charset.Charset;
import java.util.ServiceLoader;
import nbbrd.console.properties.ConsoleProperties;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class CommandPromptTest {

    @Test
    public void testRegistration() {
        assertThat(ServiceLoader.load(ConsoleProperties.Spi.class))
                .anyMatch(CommandPrompt.class::isInstance);
    }

    @Test
    public void testParseChcp() {
        assertThat(CommandPrompt.parseChcp("Active code page: 437")).isEqualTo(Charset.forName("IBM437"));
        assertThatIllegalArgumentException().isThrownBy(() -> CommandPrompt.parseChcp(""));
        assertThatIllegalArgumentException().isThrownBy(() -> CommandPrompt.parseChcp("Active code page: "));
    }
}
