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
package org.jreleaser.model.internal.checksum;

import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.common.AbstractModelObject;
import org.jreleaser.model.internal.common.Domain;
import org.jreleaser.util.Algorithm;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.jreleaser.mustache.Templates.resolveTemplate;

/**
 * @author Andres Almiray
 * @since 0.4.0
 */
public final class Checksum extends AbstractModelObject<Checksum> implements Domain {
    private final Set<Algorithm> algorithms = new LinkedHashSet<>();
    private Boolean individual;
    private String name;
    private Boolean files;

    private final org.jreleaser.model.api.checksum.Checksum immutable = new org.jreleaser.model.api.checksum.Checksum() {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isIndividual() {
            return Checksum.this.isIndividual();
        }

        @Override
        public Set<Algorithm> getAlgorithms() {
            return unmodifiableSet(algorithms);
        }

        @Override
        public boolean isFiles() {
            return Checksum.this.isFiles();
        }

        @Override
        public Map<String, Object> asMap(boolean full) {
            return unmodifiableMap(Checksum.this.asMap(full));
        }
    };

    public org.jreleaser.model.api.checksum.Checksum asImmutable() {
        return immutable;
    }

    @Override
    public void merge(Checksum source) {
        this.name = merge(this.name, source.name);
        this.individual = merge(this.individual, source.individual);
        this.files = merge(this.files, source.files);
        setAlgorithms(merge(this.algorithms, source.algorithms));
    }

    public String getResolvedName(JReleaserContext context) {
        Map<String, Object> props = context.fullProps();
        context.getModel().getRelease().getReleaser().fillProps(props, context.getModel());
        return resolveTemplate(name, props);
    }

    public String getResolvedName(JReleaserContext context, Algorithm algorithm) {
        String resolvedName = context.getModel().getChecksum().getResolvedName(context);
        int pos = resolvedName.lastIndexOf(".");
        if (pos != -1) {
            resolvedName = resolvedName.substring(0, pos) + "_" + algorithm.formatted() + resolvedName.substring(pos);
        } else {
            resolvedName += "." + algorithm.formatted();
        }

        return resolvedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIndividual() {
        return individual != null && individual;
    }

    public void setIndividual(Boolean individual) {
        this.individual = individual;
    }

    public boolean isIndividualSet() {
        return individual != null;
    }

    public Set<Algorithm> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(Set<Algorithm> algorithms) {
        this.algorithms.clear();
        this.algorithms.addAll(algorithms);
    }

    public boolean isFiles() {
        return files == null || files;
    }

    public void setFiles(Boolean files) {
        this.files = files;
    }

    public boolean isFilesSet() {
        return files != null;
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("name", name);
        props.put("individual", isIndividual());
        props.put("algorithms", algorithms);
        props.put("files", isFiles());
        return props;
    }
}
