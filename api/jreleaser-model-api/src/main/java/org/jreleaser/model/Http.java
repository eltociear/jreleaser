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

import java.util.Locale;
import java.util.Map;

import static org.jreleaser.util.StringUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public interface Http {
    String getUsername();

    String getPassword();

    Authorization getAuthorization();

    Map<String, String> getHeaders();

    enum Method {
        PUT,
        POST;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        public static Method of(String str) {
            if (isBlank(str)) return null;
            return Method.valueOf(str.toUpperCase(Locale.ENGLISH).trim());
        }
    }

    enum Authorization {
        NONE,
        BASIC,
        BEARER;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        public static Authorization of(String str) {
            if (isBlank(str)) return null;
            return Authorization.valueOf(str.toUpperCase(Locale.ENGLISH).trim());
        }
    }
}
