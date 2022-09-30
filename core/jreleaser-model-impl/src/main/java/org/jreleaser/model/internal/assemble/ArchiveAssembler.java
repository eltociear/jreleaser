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
package org.jreleaser.model.internal.assemble;

import org.jreleaser.model.Active;
import org.jreleaser.model.Archive;
import org.jreleaser.model.Distribution;
import org.jreleaser.model.Stereotype;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.model.internal.common.FileSet;
import org.jreleaser.util.PlatformUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.jreleaser.model.api.assemble.ArchiveAssembler.TYPE;
import static org.jreleaser.mustache.Templates.resolveTemplate;

/**
 * @author Andres Almiray
 * @since 0.8.0
 */
public final class ArchiveAssembler extends AbstractAssembler<ArchiveAssembler, org.jreleaser.model.api.assemble.ArchiveAssembler> {
    private final Set<Archive.Format> formats = new LinkedHashSet<>();

    private String archiveName;
    private Boolean attachPlatform;
    private Distribution.DistributionType distributionType;

    private final org.jreleaser.model.api.assemble.ArchiveAssembler immutable = new org.jreleaser.model.api.assemble.ArchiveAssembler() {
        private List<? extends org.jreleaser.model.api.common.FileSet> fileSets;
        private Set<? extends org.jreleaser.model.api.common.Artifact> outputs;

        @Override
        public String getArchiveName() {
            return archiveName;
        }

        @Override
        public boolean isAttachPlatform() {
            return ArchiveAssembler.this.isAttachPlatform();
        }

        @Override
        public Set<Archive.Format> getFormats() {
            return unmodifiableSet(formats);
        }

        @Override
        public org.jreleaser.model.api.platform.Platform getPlatform() {
            return platform.asImmutable();
        }

        @Override
        public Distribution.DistributionType getDistributionType() {
            return ArchiveAssembler.this.getDistributionType();
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public Stereotype getStereotype() {
            return ArchiveAssembler.this.getStereotype();
        }

        @Override
        public boolean isExported() {
            return ArchiveAssembler.this.isExported();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<? extends org.jreleaser.model.api.common.FileSet> getFileSets() {
            if (null == fileSets) {
                fileSets = ArchiveAssembler.this.fileSets.stream()
                    .map(FileSet::asImmutable)
                    .collect(toList());
            }
            return fileSets;
        }

        @Override
        public Set<? extends org.jreleaser.model.api.common.Artifact> getOutputs() {
            if (null == outputs) {
                outputs = ArchiveAssembler.this.outputs.stream()
                    .map(Artifact::asImmutable)
                    .collect(toSet());
            }
            return outputs;
        }

        @Override
        public Active getActive() {
            return active;
        }

        @Override
        public boolean isEnabled() {
            return ArchiveAssembler.this.isEnabled();
        }

        @Override
        public Map<String, Object> asMap(boolean full) {
            return unmodifiableMap(ArchiveAssembler.this.asMap(full));
        }

        @Override
        public String getPrefix() {
            return ArchiveAssembler.this.getPrefix();
        }

        @Override
        public Map<String, Object> getExtraProperties() {
            return unmodifiableMap(extraProperties);
        }
    };

    public ArchiveAssembler() {
        super(TYPE);
    }

    @Override
    public org.jreleaser.model.api.assemble.ArchiveAssembler asImmutable() {
        return immutable;
    }

    @Override
    public Distribution.DistributionType getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(Distribution.DistributionType distributionType) {
        this.distributionType = distributionType;
    }

    public void setDistributionType(String distributionType) {
        this.distributionType = Distribution.DistributionType.of(distributionType);
    }

    @Override
    public void merge(ArchiveAssembler source) {
        super.merge(source);
        this.archiveName = merge(source.archiveName, source.archiveName);
        this.distributionType = merge(source.distributionType, source.distributionType);
        this.attachPlatform = merge(source.attachPlatform, source.attachPlatform);
        setFormats(merge(this.formats, source.formats));
    }

    public String getResolvedArchiveName(JReleaserContext context) {
        Map<String, Object> props = context.fullProps();
        props.putAll(props());
        String result = resolveTemplate(archiveName, props);
        if (isAttachPlatform()) {
            result += "-" + getPlatform().applyReplacements(PlatformUtils.getCurrentFull());
        }
        return result;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public boolean isAttachPlatformSet() {
        return attachPlatform != null;
    }

    public boolean isAttachPlatform() {
        return attachPlatform != null && attachPlatform;
    }

    public void setAttachPlatform(Boolean attachPlatform) {
        this.attachPlatform = attachPlatform;
    }

    public Set<Archive.Format> getFormats() {
        return formats;
    }

    public void setFormats(Set<Archive.Format> formats) {
        this.formats.clear();
        this.formats.addAll(formats);
    }

    public void addFormat(Archive.Format format) {
        this.formats.add(format);
    }

    public void addFormat(String str) {
        this.formats.add(Archive.Format.of(str));
    }

    @Override
    protected void asMap(boolean full, Map<String, Object> props) {
        props.put("archiveName", archiveName);
        props.put("distributionType", distributionType);
        props.put("attachPlatform", isAttachPlatform());
        props.put("formats", formats);
    }
}
