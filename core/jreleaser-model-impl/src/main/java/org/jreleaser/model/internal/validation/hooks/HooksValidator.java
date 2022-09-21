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
package org.jreleaser.model.internal.validation.hooks;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.api.JReleaserContext.Mode;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.hooks.Hooks;
import org.jreleaser.model.internal.validation.common.Validator;
import org.jreleaser.util.Errors;

import static org.jreleaser.model.internal.validation.hooks.CommandHooksValidator.validateCommandHooks;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public abstract class HooksValidator extends Validator {
    public static void validateHooks(JReleaserContext context, Mode mode, Errors errors) {
        context.getLogger().debug("hooks");

        Hooks hooks = context.getModel().getHooks();
        validateCommandHooks(context, mode, errors);

        boolean activeSet = hooks.isActiveSet();
        hooks.resolveEnabled(context.getModel().getProject());

        if (hooks.isEnabled()) {
            boolean enabled = hooks.getCommand().isEnabled();

            if (!activeSet && !enabled) {
                context.getLogger().debug(RB.$("validation.disabled"));
                hooks.disable();
            }
        }
    }
}