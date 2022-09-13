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
package org.jreleaser.sdk.disco.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Andres Almiray
 * @since 0.9.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    private String pkgInfoUri;
    private String pkgDownloadRedirect;

    public String getPkgInfoUri() {
        return pkgInfoUri;
    }

    public void setPkgInfoUri(String pkgInfoUri) {
        this.pkgInfoUri = pkgInfoUri;
    }

    public String getPkgDownloadRedirect() {
        return pkgDownloadRedirect;
    }

    public void setPkgDownloadRedirect(String pkgDownloadRedirect) {
        this.pkgDownloadRedirect = pkgDownloadRedirect;
    }
}
