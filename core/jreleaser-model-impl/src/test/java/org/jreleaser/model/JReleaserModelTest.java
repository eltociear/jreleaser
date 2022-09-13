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
package org.jreleaser.model;

import org.jreleaser.model.internal.JReleaserModel;
import org.jreleaser.model.internal.release.GithubReleaser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Geroge Gastaldi
 * @since 1.2.0
 */
@Disabled
public class JReleaserModelTest {
    @Test
    void shouldRenderProjectNameCapitalizedWithSpaces() {
        JReleaserModel model = new JReleaserModel();
        model.getProject().setName("quarkiverse-parent");
        model.getRelease().setGithub(new GithubReleaser());
        Map<String, Object> props = model.props();
        assertEquals("Quarkiverse Parent", props.get(Constants.KEY_PROJECT_NAME_CAPITALIZED));
    }
}