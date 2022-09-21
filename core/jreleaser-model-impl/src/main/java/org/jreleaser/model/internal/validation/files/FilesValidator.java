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
package org.jreleaser.model.internal.validation.files;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.Active;
import org.jreleaser.model.api.JReleaserContext.Mode;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.files.Files;
import org.jreleaser.model.internal.validation.common.Validator;
import org.jreleaser.util.Errors;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class FilesValidator extends Validator {
    public static void validateFiles(JReleaserContext context, Mode mode, Errors errors) {
        if (!mode.validateConfig()) {
            return;
        }

        context.getLogger().debug("files");
        Files files = context.getModel().getFiles();

        if (!files.isActiveSet()) {
            files.setActive(Active.ALWAYS);
        }
        if (!files.resolveEnabled(context.getModel().getProject())) {
            context.getLogger().debug(RB.$("validation.disabled"));
            return;
        }

        files.getArtifacts()
            .forEach(artifact -> {
                if (context.isPlatformSelected(artifact)) artifact.activate();
            });

        validateGlobs(context, files.getGlobs(), "files.glob", errors);
    }
}