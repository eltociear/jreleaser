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
package org.jreleaser.gradle.plugin.dsl.announce

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.jreleaser.gradle.plugin.dsl.common.Activatable

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
interface Announce extends Activatable {
    ArticleAnnouncer getArticle()

    DiscordAnnouncer getDiscord()

    DiscussionsAnnouncer getDiscussions()

    GitterAnnouncer getGitter()

    GoogleChatAnnouncer getGoogleChat()

    SmtpAnnouncer getMail()

    SmtpAnnouncer getSmtp()

    MastodonAnnouncer getMastodon()

    MattermostAnnouncer getMattermost()

    SdkmanAnnouncer getSdkman()

    SlackAnnouncer getSlack()

    TeamsAnnouncer getTeams()

    TelegramAnnouncer getTelegram()

    TwitterAnnouncer getTwitter()

    ZulipAnnouncer getZulip()

    NamedDomainObjectContainer<HttpAnnouncer> getHttp()

    NamedDomainObjectContainer<WebhookAnnouncer> getWebhooks()

    void article(Action<? super ArticleAnnouncer> action)

    void discord(Action<? super DiscordAnnouncer> action)

    void discussions(Action<? super DiscussionsAnnouncer> action)

    void gitter(Action<? super GitterAnnouncer> action)

    void googleChat(Action<? super GoogleChatAnnouncer> action)

    void http(Action<? super NamedDomainObjectContainer<HttpAnnouncer>> action)

    void mail(Action<? super SmtpAnnouncer> action)

    void smtp(Action<? super SmtpAnnouncer> action)

    void mastodon(Action<? super MastodonAnnouncer> action)

    void mattermost(Action<? super MattermostAnnouncer> action)

    void sdkman(Action<? super SdkmanAnnouncer> action)

    void slack(Action<? super SlackAnnouncer> action)

    void teams(Action<? super TeamsAnnouncer> action)

    void telegram(Action<? super TelegramAnnouncer> action)

    void twitter(Action<? super TwitterAnnouncer> action)

    void zulip(Action<? super ZulipAnnouncer> action)

    void webhooks(Action<? super NamedDomainObjectContainer<WebhookAnnouncer>> action)

    void article(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ArticleAnnouncer) Closure<Void> action)

    void discord(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiscordAnnouncer) Closure<Void> action)

    void discussions(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiscussionsAnnouncer) Closure<Void> action)

    void gitter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GitterAnnouncer) Closure<Void> action)

    void googleChat(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GoogleChatAnnouncer) Closure<Void> action)

    void http(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void mail(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SmtpAnnouncer) Closure<Void> action)

    void smtp(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SmtpAnnouncer) Closure<Void> action)

    void mastodon(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MastodonAnnouncer) Closure<Void> action)

    void mattermost(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MattermostAnnouncer) Closure<Void> action)

    void sdkman(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SdkmanAnnouncer) Closure<Void> action)

    void slack(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SlackAnnouncer) Closure<Void> action)

    void teams(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TeamsAnnouncer) Closure<Void> action)

    void telegram(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TelegramAnnouncer) Closure<Void> action)

    void twitter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TwitterAnnouncer) Closure<Void> action)

    void zulip(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ZulipAnnouncer) Closure<Void> action)

    void webhooks(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)
}