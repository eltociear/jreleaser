/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2022 The JReleaser authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.cli;

import org.jreleaser.cli.internal.JReleaserModelPrinter;
import org.jreleaser.engine.context.ModelValidator;
import org.jreleaser.model.api.JReleaserContext.Mode;
import org.jreleaser.model.internal.JReleaserContext;
import picocli.CommandLine;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
@CommandLine.Command(name = "config")
public class Config extends AbstractPlatformAwareModelCommand {
    @CommandLine.Option(names = {"-f", "--full"})
    boolean full;

    @CommandLine.ArgGroup
    Exclusive exclusive;

    static class Exclusive {
        @CommandLine.Option(names = {"--announce"}, required = true)
        boolean announce;

        @CommandLine.Option(names = {"-a", "--assembly"}, required = true)
        boolean assembly;

        @CommandLine.Option(names = {"--changelog"}, required = true)
        boolean changelog;

        @CommandLine.Option(names = {"-d", "--download"}, required = true)
        boolean download;
    }

    @Override
    protected void doExecute(JReleaserContext context) {
        ModelValidator.validate(context);
        new JReleaserModelPrinter(parent.out).print(context.getModel().asMap(full));
        context.report();
    }

    @Override
    protected Mode getMode() {
        if (download()) return Mode.DOWNLOAD;
        if (assembly()) return Mode.ASSEMBLE;
        if (changelog()) return Mode.CHANGELOG;
        if (announce()) return Mode.ANNOUNCE;
        return Mode.CONFIG;
    }

    private boolean download() {
        return exclusive != null && exclusive.download;
    }

    private boolean assembly() {
        return exclusive != null && exclusive.assembly;
    }

    private boolean changelog() {
        return exclusive != null && exclusive.changelog;
    }

    private boolean announce() {
        return exclusive != null && exclusive.announce;
    }
}
