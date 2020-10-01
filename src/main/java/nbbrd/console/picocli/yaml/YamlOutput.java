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

import nbbrd.console.picocli.text.TextOutput;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Philippe Charles
 */
public interface YamlOutput extends TextOutput {

    default void dump(Yaml yaml, Object item) throws IOException {
        try (Writer writer = newCharWriter()) {
            yaml.dump(item, writer);
        }
    }

    default void dumpAll(Yaml yaml, Collection<?> items) throws IOException {
        try (Writer writer = newCharWriter()) {
            yaml.dumpAll(items.iterator(), writer);
        }
    }

    static PropertyUtils newLinkedPropertyUtils() {
        return new PropertyUtils() {
            @Override
            protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
                return getPropertiesMap(type, bAccess)
                        .values()
                        .stream()
                        .filter(property -> property.isReadable() && (isAllowReadOnlyProperties() || property.isWritable()))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }
        };
    }
}
