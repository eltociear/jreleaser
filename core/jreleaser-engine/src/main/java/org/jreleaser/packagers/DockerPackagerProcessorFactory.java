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
package org.jreleaser.packagers;

import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.packagers.DockerPackager;
import org.jreleaser.model.spi.packagers.PackagerProcessorFactory;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
@ServiceProviderFor(PackagerProcessorFactory.class)
public class DockerPackagerProcessorFactory implements PackagerProcessorFactory<DockerPackager, DockerPackagerProcessor> {
    @Override
    public String getName() {
        return org.jreleaser.model.api.packagers.DockerPackager.TYPE;
    }

    @Override
    public DockerPackagerProcessor getPackagerNameProcessor(JReleaserContext context) {
        return new DockerPackagerProcessor(context);
    }
}