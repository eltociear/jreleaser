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
package org.jreleaser.model.api.packagers;

import org.jreleaser.model.Sdkman;
import org.jreleaser.model.api.common.TimeoutAware;

/**
 * @author Andres Almiray
 * @since 0.6.0
 */
public interface SdkmanPackager extends Packager, TimeoutAware {
    String SDKMAN_CONSUMER_KEY = "SDKMAN_CONSUMER_KEY";
    String SDKMAN_CONSUMER_TOKEN = "SDKMAN_CONSUMER_TOKEN";
    String TYPE = "sdkman";
    String SKIP_SDKMAN = "skipSdkman";

    String getCandidate();

    String getReleaseNotesUrl();

    Sdkman.Command getCommand();

    String getConsumerKey();

    String getConsumerToken();

    boolean isPublished();
}
