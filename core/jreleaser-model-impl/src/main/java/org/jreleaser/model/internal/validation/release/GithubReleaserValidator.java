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
package org.jreleaser.model.internal.validation.release;

import org.jreleaser.model.api.JReleaserContext.Mode;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.release.GithubReleaser;
import org.jreleaser.util.Errors;

import static org.jreleaser.model.api.release.Releaser.DRAFT;
import static org.jreleaser.model.api.release.Releaser.PRERELEASE_PATTERN;


/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class GithubReleaserValidator extends BaseReleaserValidator {
    public static boolean validateGithub(JReleaserContext context, Mode mode, GithubReleaser github, Errors errors) {
        if (null == github) return false;
        context.getLogger().debug("release.github");

        validateGitService(context, mode, github, errors);

        if (context.getModel().getProject().isSnapshot()) {
            github.getPrerelease().setEnabled(true);
        }

        github.getPrerelease().setPattern(
            checkProperty(context,
                PRERELEASE_PATTERN,
                "release.github.prerelease.pattern",
                github.getPrerelease().getPattern(),
                ""));
        github.getPrerelease().isPrerelease(context.getModel().getProject().getResolvedVersion());

        if (!github.isDraftSet()) {
            github.setDraft(
                checkProperty(context,
                    DRAFT,
                    "github.draft",
                    null,
                    false));
        }

        if (github.isDraft()) {
            github.getMilestone().setClose(false);
        }

        return github.isEnabled();
    }
}
