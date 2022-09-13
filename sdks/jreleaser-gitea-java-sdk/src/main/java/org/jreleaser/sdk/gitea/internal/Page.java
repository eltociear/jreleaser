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
package org.jreleaser.sdk.gitea.internal;

import org.jreleaser.sdk.commons.Links;

import java.util.Collection;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class Page<T> {

    private final Links links;
    private final T content;

    public Page(Map<String, Collection<String>> headers, T content) {
        this.links = Links.of(headers.get("link"));
        this.content = content;
    }

    public boolean hasLinks() {
        return !links.isEmpty();
    }

    public Links getLinks() {
        return links;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Page[" +
            "links=" + links +
            "]";
    }
}
