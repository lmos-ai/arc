// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21

//DEPS ai.ancf.lmos:arc-runner:0.80.0
//DEPS org.slf4j:slf4j-api:2.0.16
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.2
//DEPS io.projectreactor:reactor-core:3.6.9

package arc.runner;

import ai.ancf.lmos.arc.runner.Arc;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        var exitCode = new CommandLine(new Arc()).execute(args);
        System.out.println(exitCode);
    }
}