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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class ManifestHelper {

    public Optional<Manifest> getByTitle(String title) throws IOException {
        return get(manifest -> title.equals(manifest.getMainAttributes().getValue(IMPL_TITLE_HEADER)));
    }

    public Optional<Manifest> get(Predicate<? super Manifest> filter) throws IOException {
        Enumeration<URL> resources = ManifestHelper.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try (InputStream stream = url.openStream()) {
                Manifest manifest = new Manifest(stream);
                if (filter.test(manifest)) {
                    return Optional.of(manifest);
                }
            }
        }
        return Optional.empty();
    }

    public String[] getVersion(Manifest manifest) {
        Attributes attr = manifest.getMainAttributes();
        return new String[]{attr.getValue(IMPL_TITLE_HEADER) + " version \""
                + attr.getValue(IMPL_VERSION_HEADER) + "\""};
    }

    public final String IMPL_TITLE_HEADER = "Implementation-Title";
    public final String IMPL_VERSION_HEADER = "Implementation-Version";
}
