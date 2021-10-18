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

module nbbrd.console.properties {
    requires static lombok;
    requires static nbbrd.service;
    requires static org.checkerframework.checker.qual;
    requires static nbbrd.design;

    // optional dependencies
    requires static info.picocli;
    requires static nbbrd.picocsv;
    requires static nbbrd.io.win;
    requires static org.yaml.snakeyaml;

    requires nbbrd.io.base;
    requires java.logging;

    exports nbbrd.console.properties;
    exports nbbrd.console.picocli;
    exports nbbrd.console.picocli.csv;
    exports nbbrd.console.picocli.text;
    exports nbbrd.console.picocli.yaml;

    uses nbbrd.console.properties.ConsoleProperties.Spi;
    provides nbbrd.console.properties.ConsoleProperties.Spi with
            internal.console.properties.x.ChcpConsoleProvider,
            internal.console.properties.x.PowerShellConsoleProvider,
            internal.console.properties.x.LocaleConsoleProvider,
            internal.console.properties.x.SunPropertyConsoleProvider,
            internal.console.properties.x.TputConsoleProvider;

    uses nbbrd.console.picocli.Profile.Spi;
    provides nbbrd.console.picocli.Profile.Spi with
            internal.console.picocli.csv.ExcelProfile;
}
