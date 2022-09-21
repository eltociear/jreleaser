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
import org.jreleaser.model.internal.announce.TwitterAnnouncer;
import org.jreleaser.model.internal.validation.common.Validator;
import org.jreleaser.util.Errors;

import java.nio.file.Files;

import static org.jreleaser.model.api.announce.TwitterAnnouncer.TWITTER_ACCESS_TOKEN;
import static org.jreleaser.model.api.announce.TwitterAnnouncer.TWITTER_ACCESS_TOKEN_SECRET;
import static org.jreleaser.model.api.announce.TwitterAnnouncer.TWITTER_CONSUMER_KEY;
import static org.jreleaser.model.api.announce.TwitterAnnouncer.TWITTER_CONSUMER_SECRET;
import static org.jreleaser.util.StringUtils.isBlank;
import static org.jreleaser.util.StringUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class TwitterAnnouncerValidator extends Validator {
    public static void validateTwitter(JReleaserContext context, TwitterAnnouncer twitter, Errors errors) {
        context.getLogger().debug("announce.twitter");
        if (!twitter.resolveEnabled(context.getModel().getProject())) {
            context.getLogger().debug(RB.$("validation.disabled"));
            return;
        }

        twitter.setConsumerKey(
            checkProperty(context,
                TWITTER_CONSUMER_KEY,
                "twitter.consumerKey",
                twitter.getConsumerKey(),
                errors,
                context.isDryrun()));

        twitter.setConsumerSecret(
            checkProperty(context,
                TWITTER_CONSUMER_SECRET,
                "twitter.consumerSecret",
                twitter.getConsumerSecret(),
                errors,
                context.isDryrun()));

        twitter.setAccessToken(
            checkProperty(context,
                TWITTER_ACCESS_TOKEN,
                "twitter.accessToken",
                twitter.getAccessToken(),
                errors,
                context.isDryrun()));

        twitter.setAccessTokenSecret(
            checkProperty(context,
                TWITTER_ACCESS_TOKEN_SECRET,
                "twitter.accessTokenSecret",
                twitter.getAccessTokenSecret(),
                errors,
                context.isDryrun()));

        if (isNotBlank(twitter.getStatusTemplate()) &&
            !Files.exists(context.getBasedir().resolve(twitter.getStatusTemplate().trim()))) {
            errors.configuration(RB.$("validation_directory_not_exist", "twitter.statusTemplate", twitter.getStatusTemplate()));
        }

        if (isBlank(twitter.getStatus()) && isBlank(twitter.getStatusTemplate()) && twitter.getStatuses().isEmpty()) {
            twitter.setStatus(RB.$("default.release.message"));
        }

        validateTimeout(twitter);
    }
}