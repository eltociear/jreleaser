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
package org.jreleaser.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.workflow.Workflows;

/**
 * Uploads all artifacts.
 *
 * @author Andres Almiray
 * @since 0.3.0
 */
@Mojo(name = "upload")
public class JReleaserUploadMojo extends AbstractPlatformAwareJReleaserMojo {
    /**
     * Include an uploader by type.
     */
    @Parameter(property = "jreleaser.uploaders")
    private String[] includedUploaders;

    /**
     * Exclude an uploader by type.
     */
    @Parameter(property = "jreleaser.excluded.uploaders")
    private String[] excludedUploaders;

    /**
     * Include an uploader by name.
     */
    @Parameter(property = "jreleaser.uploader.names")
    private String[] includedUploaderNames;

    /**
     * Exclude an uploader by name.
     */
    @Parameter(property = "jreleaser.excluded.uploader.names")
    private String[] excludedUploaderNames;

    /**
     * Include a distribution.
     */
    @Parameter(property = "jreleaser.distributions")
    private String[] includedDistributions;

    /**
     * Exclude a distribution.
     */
    @Parameter(property = "jreleaser.excluded.distributions")
    private String[] excludedDistributions;

    /**
     * Skip execution.
     */
    @Parameter(property = "jreleaser.upload.skip")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Banner.display(project, getLog());
        if (skip) {
            getLog().info("Execution has been explicitly skipped.");
            return;
        }

        JReleaserContext context = createContext();
        context.setIncludedUploaderTypes(collectEntries(includedUploaders, true));
        context.setIncludedUploaderNames(collectEntries(includedUploaderNames));
        context.setIncludedDistributions(collectEntries(includedDistributions));
        context.setExcludedUploaderTypes(collectEntries(excludedUploaders, true));
        context.setExcludedUploaderNames(collectEntries(excludedUploaderNames));
        context.setExcludedDistributions(collectEntries(excludedDistributions));
        Workflows.upload(context).execute();
    }
}
