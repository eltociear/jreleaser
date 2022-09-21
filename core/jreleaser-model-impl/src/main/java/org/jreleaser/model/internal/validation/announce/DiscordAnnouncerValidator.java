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
package org.jreleaser.model.internal.validation.announce;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.announce.DiscordAnnouncer;
import org.jreleaser.model.internal.validation.common.Validator;
import org.jreleaser.util.Errors;

import java.nio.file.Files;

import static org.jreleaser.model.api.announce.DiscordAnnouncer.DISCORD_WEBHOOK;
import static org.jreleaser.util.StringUtils.isBlank;
import static org.jreleaser.util.StringUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 0.2.0
 */
public abstract class DiscordAnnouncerValidator extends Validator {
    private static final String DEFAULT_DISCORD_TPL = "src/jreleaser/templates/discord.tpl";

    public static void validateDiscord(JReleaserContext context, DiscordAnnouncer discord, Errors errors) {
        context.getLogger().debug("announce.discord");
        if (!discord.resolveEnabled(context.getModel().getProject())) {
            context.getLogger().debug(RB.$("validation.disabled"));
            return;
        }

        discord.setWebhook(
            checkProperty(context,
                DISCORD_WEBHOOK,
                "discord.webhook",
                discord.getWebhook(),
                errors,
                context.isDryrun()));

        if (isBlank(discord.getMessage()) && isBlank(discord.getMessageTemplate())) {
            if (Files.exists(context.getBasedir().resolve(DEFAULT_DISCORD_TPL))) {
                discord.setMessageTemplate(DEFAULT_DISCORD_TPL);
            } else {
                discord.setMessage(RB.$("default.release.message"));
            }
        }

        if (isNotBlank(discord.getMessageTemplate()) &&
            !Files.exists(context.getBasedir().resolve(discord.getMessageTemplate().trim()))) {
            errors.configuration(RB.$("validation_directory_not_exist", "discord.messageTemplate", discord.getMessageTemplate()));
        }

        validateTimeout(discord);
    }
}