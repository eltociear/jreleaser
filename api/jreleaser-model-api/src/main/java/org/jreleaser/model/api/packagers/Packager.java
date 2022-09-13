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

import org.jreleaser.model.Distribution;
import org.jreleaser.model.Stereotype;
import org.jreleaser.model.api.common.Activatable;
import org.jreleaser.model.api.common.Domain;
import org.jreleaser.model.api.common.ExtraProperties;

import java.util.Set;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public interface Packager extends Domain, ExtraProperties, Activatable {
    String getType();

    String getDownloadUrl();

    boolean supportsPlatform(String platform);

    boolean supportsDistribution(Distribution.DistributionType distributionType);

    Set<String> getSupportedFileExtensions(Distribution.DistributionType distributionType);

    Set<Stereotype> getSupportedStereotypes();

    boolean isSnapshotSupported();

    boolean isContinueOnError();
}
