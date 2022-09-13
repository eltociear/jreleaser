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
package org.jreleaser.model.internal.common;

import org.jreleaser.bundle.RB;
import org.jreleaser.logging.JReleaserLogger;
import org.jreleaser.model.internal.JReleaserContext;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static org.jreleaser.model.internal.util.Artifacts.resolveForFileSet;
import static org.jreleaser.util.CollectionUtils.setOf;

/**
 * @author Andres Almiray
 * @since 0.8.0
 */
public final class FileSet extends AbstractModelObject<FileSet> implements Domain, ExtraProperties {
    private static final String GLOB_PREFIX = "glob:";

    private final Map<String, Object> extraProperties = new LinkedHashMap<>();
    private final Set<String> includes = new LinkedHashSet<>();
    private final Set<String> excludes = new LinkedHashSet<>();

    private String input;
    private String output;
    private Boolean failOnMissingInput;

    private final org.jreleaser.model.api.common.FileSet immutable = new org.jreleaser.model.api.common.FileSet() {
        @Override
        public Set<String> getIncludes() {
            return unmodifiableSet(includes);
        }

        @Override
        public Set<String> getExcludes() {
            return unmodifiableSet(excludes);
        }

        @Override
        public String getInput() {
            return input;
        }

        @Override
        public String getOutput() {
            return output;
        }

        @Override
        public boolean isFailOnMissingInput() {
            return FileSet.this.isFailOnMissingInput();
        }

        @Override
        public Map<String, Object> asMap(boolean full) {
            return unmodifiableMap(FileSet.this.asMap(full));
        }

        @Override
        public String getPrefix() {
            return FileSet.this.getPrefix();
        }

        @Override
        public Map<String, Object> getExtraProperties() {
            return unmodifiableMap(extraProperties);
        }
    };

    public org.jreleaser.model.api.common.FileSet asImmutable() {
        return immutable;
    }

    @Override
    public void merge(FileSet source) {
        this.input = merge(this.input, source.input);
        this.output = merge(this.output, source.output);
        this.failOnMissingInput = merge(this.failOnMissingInput, source.failOnMissingInput);
        setIncludes(merge(this.includes, source.includes));
        setExcludes(merge(this.excludes, source.excludes));
        setExtraProperties(merge(this.extraProperties, source.extraProperties));
    }

    @Override
    public String getPrefix() {
        return "artifact";
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes.clear();
        this.includes.addAll(includes);
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes.clear();
        this.excludes.addAll(excludes);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isFailOnMissingInput() {
        return failOnMissingInput == null || failOnMissingInput;
    }

    public void setFailOnMissingInput(Boolean failOnMissingInput) {
        this.failOnMissingInput = failOnMissingInput;
    }

    public boolean isFailOnMissingInputSet() {
        return failOnMissingInput != null;
    }

    @Override
    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    @Override
    public void setExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties.clear();
        this.extraProperties.putAll(extraProperties);
    }

    @Override
    public void addExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties.putAll(extraProperties);
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("input", input);
        props.put("output", output);
        props.put("includes", includes);
        props.put("excludes", excludes);
        props.put("failOnMissingInput", failOnMissingInput);
        props.put("extraProperties", getResolvedExtraProperties());
        return props;
    }

    public String getResolvedInput(JReleaserContext context) {
        return resolveForFileSet(input, context, this);
    }

    public String getResolvedOutput(JReleaserContext context) {
        return resolveForFileSet(output, context, this);
    }

    public Set<String> getResolvedIncludes(JReleaserContext context) {
        return includes.stream()
            .map(s -> resolveForFileSet(s, context, this))
            .collect(toSet());
    }

    public Set<String> getResolvedExcludes(JReleaserContext context) {
        return excludes.stream()
            .map(s -> resolveForFileSet(s, context, this))
            .collect(toSet());
    }

    public Set<Path> getResolvedPaths(JReleaserContext context) throws IOException {
        Path basedir = context.getBasedir().resolve(getResolvedInput(context)).normalize().toAbsolutePath();

        Set<String> resolvedIncludes = getResolvedIncludes(context);
        if (resolvedIncludes.isEmpty()) {
            resolvedIncludes = setOf("**/*");
        }
        resolvedIncludes = resolvedIncludes.stream()
            .map(s -> GLOB_PREFIX + s)
            .collect(toSet());

        Set<String> resolvedExcludes = getResolvedExcludes(context);
        resolvedExcludes = resolvedExcludes.stream()
            .map(s -> GLOB_PREFIX + s)
            .collect(toSet());

        if (!java.nio.file.Files.exists(basedir)) {
            if (isFailOnMissingInput()) {
                throw new IOException(RB.$("ERROR_artifacts_glob_missing_input",
                    context.getBasedir().relativize(basedir)));
            } else {
                context.getLogger().debug(RB.$("ERROR_artifacts_glob_missing_input",
                    context.getBasedir().relativize(basedir)));
            }
            return new LinkedHashSet<>();
        }

        GlobResolver resolver = new GlobResolver(context.getLogger(), basedir, resolvedIncludes, resolvedExcludes);

        java.nio.file.Files.walkFileTree(basedir, resolver);
        if (resolver.failed) {
            throw new IOException(RB.$("ERROR_artifacts_glob_resolution"));
        }

        return resolver.paths;
    }

    private static final class GlobResolver extends SimpleFileVisitor<Path> {
        private final JReleaserLogger logger;
        private final Set<ExtPathMatcher> includes = new LinkedHashSet<>();
        private final Set<ExtPathMatcher> excludes = new LinkedHashSet<>();
        private final Path basedir;
        private final Set<Path> paths = new LinkedHashSet<>();
        private boolean failed;

        private GlobResolver(JReleaserLogger logger, Path basedir, Set<String> includes, Set<String> excludes) {
            this.logger = logger;
            this.basedir = basedir;

            FileSystem fileSystem = FileSystems.getDefault();
            for (String s : includes) {
                this.includes.add(new ExtPathMatcher(fileSystem.getPathMatcher(s), s.contains("**")));
            }
            for (String s : excludes) {
                this.excludes.add(new ExtPathMatcher(fileSystem.getPathMatcher(s), s.contains("**")));
            }
        }

        private void match(Path path) {
            if (includes.stream().anyMatch(matcher -> matches(path, matcher)) &&
                excludes.stream().noneMatch(matcher -> matches(path, matcher))) {
                paths.add(basedir.relativize(path));
            }
        }

        private boolean matches(Path path, ExtPathMatcher matcher) {
            if (matcher.recursive) {
                return matcher.matcher.matches(path);
            } else {
                return basedir.normalize().equals(path.normalize().getParent()) &&
                    matcher.matcher.matches(path.getFileName());
            }
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (basedir.normalize().equals(dir.normalize())) return CONTINUE;

            if (includes.stream().anyMatch(matcher -> matcher.recursive)) {
                return CONTINUE;
            } else {
                return SKIP_SUBTREE;
            }
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            match(file);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
            failed = true;
            logger.error(RB.$("ERROR_artifacts_unexpected_error_path"),
                basedir.toAbsolutePath().relativize(file.toAbsolutePath()), e);
            return CONTINUE;
        }
    }

    private static final class ExtPathMatcher {
        private final PathMatcher matcher;
        private final boolean recursive;

        private ExtPathMatcher(PathMatcher matcher, boolean recursive) {
            this.matcher = matcher;
            this.recursive = recursive;
        }
    }
}
