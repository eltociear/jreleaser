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
package org.jreleaser.model.internal.announce;

import org.jreleaser.model.Active;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.model.internal.common.CommitAuthor;
import org.jreleaser.model.internal.common.CommitAuthorAware;
import org.jreleaser.model.internal.packagers.AbstractRepositoryTap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toSet;
import static org.jreleaser.model.api.announce.ArticleAnnouncer.TYPE;

/**
 * @author Andres Almiray
 * @since 0.6.0
 */
public final class ArticleAnnouncer extends AbstractAnnouncer<ArticleAnnouncer, org.jreleaser.model.api.announce.ArticleAnnouncer> implements CommitAuthorAware {
    private final Set<Artifact> files = new LinkedHashSet<>();
    private final CommitAuthor commitAuthor = new CommitAuthor();
    private final Repository repository = new Repository();

    private String templateDirectory;

    private final org.jreleaser.model.api.announce.ArticleAnnouncer immutable = new org.jreleaser.model.api.announce.ArticleAnnouncer() {
        private Set<? extends org.jreleaser.model.api.common.Artifact> files;

        @Override
        public String getType() {
            return org.jreleaser.model.api.announce.ArticleAnnouncer.TYPE;
        }

        @Override
        public Set<? extends org.jreleaser.model.api.common.Artifact> getFiles() {
            if (null == files) {
                files = ArticleAnnouncer.this.files.stream()
                    .map(Artifact::asImmutable)
                    .collect(toSet());
            }
            return files;
        }

        @Override
        public Repository getRepository() {
            return repository.asImmutable();
        }

        @Override
        public String getTemplateDirectory() {
            return templateDirectory;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isSnapshotSupported() {
            return ArticleAnnouncer.this.isSnapshotSupported();
        }

        @Override
        public Active getActive() {
            return active;
        }

        @Override
        public boolean isEnabled() {
            return ArticleAnnouncer.this.isEnabled();
        }

        @Override
        public org.jreleaser.model.api.common.CommitAuthor getCommitAuthor() {
            return commitAuthor.asImmutable();
        }

        @Override
        public Map<String, Object> asMap(boolean full) {
            return unmodifiableMap(ArticleAnnouncer.this.asMap(full));
        }

        @Override
        public String getPrefix() {
            return ArticleAnnouncer.this.getPrefix();
        }

        @Override
        public Map<String, Object> getExtraProperties() {
            return unmodifiableMap(extraProperties);
        }

        @Override
        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        @Override
        public Integer getReadTimeout() {
            return readTimeout;
        }
    };

    public ArticleAnnouncer() {
        super(TYPE);
    }

    @Override
    public org.jreleaser.model.api.announce.ArticleAnnouncer asImmutable() {
        return immutable;
    }

    @Override
    public void merge(ArticleAnnouncer source) {
        super.merge(source);
        this.templateDirectory = merge(this.templateDirectory, source.templateDirectory);
        setFiles(merge(this.files, source.files));
        setCommitAuthor(source.commitAuthor);
        setRepository(source.repository);
    }

    public Set<Artifact> getFiles() {
        return Artifact.sortArtifacts(files);
    }

    public void setFiles(Set<Artifact> files) {
        this.files.clear();
        this.files.addAll(files);
    }

    public void addFiles(Set<Artifact> files) {
        this.files.addAll(files);
    }

    public void addFile(Artifact artifact) {
        if (null != artifact) {
            this.files.add(artifact);
        }
    }

    @Override
    public CommitAuthor getCommitAuthor() {
        return commitAuthor;
    }

    @Override
    public void setCommitAuthor(CommitAuthor commitAuthor) {
        this.commitAuthor.merge(commitAuthor);
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository.merge(repository);
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    @Override
    protected void asMap(boolean full, Map<String, Object> props) {
        props.put("commitAuthor", commitAuthor.asMap(full));
        props.put("repository", repository.asMap(full));

        Map<String, Map<String, Object>> mappedArtifacts = new LinkedHashMap<>();
        int i = 0;
        for (Artifact artifact : getFiles()) {
            mappedArtifacts.put("files " + (i++), artifact.asMap(full));
        }
        props.put("files", mappedArtifacts);
        props.put("templateDirectory", templateDirectory);
    }

    public static final class Repository extends AbstractRepositoryTap<Repository> {
        private final org.jreleaser.model.api.announce.ArticleAnnouncer.Repository immutable = new org.jreleaser.model.api.announce.ArticleAnnouncer.Repository() {
            @Override
            public String getBasename() {
                return basename;
            }

            @Override
            public String getCanonicalRepoName() {
                return Repository.this.getCanonicalRepoName();
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getTagName() {
                return tagName;
            }

            @Override
            public String getBranch() {
                return branch;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getToken() {
                return token;
            }

            @Override
            public String getCommitMessage() {
                return commitMessage;
            }

            @Override
            public Active getActive() {
                return active;
            }

            @Override
            public boolean isEnabled() {
                return Repository.this.isEnabled();
            }

            @Override
            public Map<String, Object> asMap(boolean full) {
                return unmodifiableMap(Repository.this.asMap(full));
            }

            @Override
            public String getOwner() {
                return owner;
            }
        };

        public Repository() {
            super("article", "article");
        }

        private org.jreleaser.model.api.announce.ArticleAnnouncer.Repository asImmutable() {
            return immutable;
        }
    }
}
