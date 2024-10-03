// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21

//DEPS ai.ancf.lmos:arc-runner:0.81.0

package ai.ancf.lmos.arc.runner;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        var exitCode = new CommandLine(new Arc()).execute(args);
        System.out.println(exitCode);
    }
}