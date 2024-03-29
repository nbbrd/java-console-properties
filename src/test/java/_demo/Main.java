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
package _demo;

import nbbrd.console.properties.ConsoleProperties;

import java.nio.charset.Charset;

/**
 * @author Philippe Charles
 */
public class Main {

    public static void main(String[] args) {
        ConsoleProperties result = ConsoleProperties.ofServiceLoader();
        System.out.println("StdInEncoding: " + result.getStdInEncoding());
        System.out.println("StdOutEncoding: " + result.getStdOutEncoding());
        System.out.println("Rows: " + result.getRows());
        System.out.println("Columns: " + result.getColumns());
        System.out.println("---");
        System.out.println("Default encoding: " + Charset.defaultCharset());
    }
}
