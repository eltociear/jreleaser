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
package org.jreleaser.extensions.api;

import org.jreleaser.model.api.JReleaserContext;

import java.util.Map;

/**
 * Defines an extension point for a given feature.
 *
 * @author Andres Almiray
 * @since 1.3.0
 */
public interface ExtensionPoint {
    /**
     * Initializes the extension point with values defined in the configuration DSL.
     *
     * @param context the current execution context.
     * @param properties a {@code Map} of key/value pairs.
     */
    default void init(JReleaserContext context, Map<String, Object> properties) {
        // noop
    }
}
