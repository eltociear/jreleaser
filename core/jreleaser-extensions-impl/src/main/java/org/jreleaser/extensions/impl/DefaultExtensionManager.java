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
package org.jreleaser.extensions.impl;

import org.jreleaser.bundle.RB;
import org.jreleaser.extensions.api.Extension;
import org.jreleaser.extensions.api.ExtensionManager;
import org.jreleaser.extensions.api.ExtensionPoint;
import org.jreleaser.logging.JReleaserLogger;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.jreleaser.util.StringUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
@org.jreleaser.infra.nativeimage.annotations.NativeImage
@ServiceProviderFor(ExtensionManager.class)
public final class DefaultExtensionManager implements ExtensionManager {
    private final Map<String, ExtensionDef> extensionDefs = new LinkedHashMap<>();
    private final Set<ExtensionPoint> allExtensionPoints = new LinkedHashSet<>();
    private final Map<String, Set<ExtensionPoint>> extensionPoints = new LinkedHashMap<>();

    public ExtensionBuilder configureExtension(String name) {
        return new ExtensionBuilder(name, this);
    }

    public void load(JReleaserLogger logger, Path basedir) {
        extensionPoints.clear();
        allExtensionPoints.clear();

        Set<String> visitedExtensionNames = new LinkedHashSet<>();
        Set<String> visitedExtensionTypes = new LinkedHashSet<>();

        // load defaults
        for (Extension extension : resolveServiceLoader()) {
            processExtension(logger, extension, visitedExtensionNames, visitedExtensionTypes);
        }

        for (Map.Entry<String, ExtensionDef> e : extensionDefs.entrySet()) {
            String extensionName = e.getKey();
            ExtensionDef extensionDef = e.getValue();
            if (visitedExtensionNames.contains(extensionName) || !extensionDef.isEnabled()) {
                continue;
            }

            createClassLoader(logger, basedir, extensionDef).ifPresent(classLoader -> {
                for (Extension extension : ServiceLoader.load(Extension.class, classLoader)) {
                    processExtension(logger, extension, visitedExtensionNames, visitedExtensionTypes);
                }
            });
        }
    }

    public <T extends ExtensionPoint> Set<T> findExtensionPoints(Class<T> extensionPointType) {
        return (Set<T>) extensionPoints.computeIfAbsent(extensionPointType.getName(), k -> {
            Set<T> set = new LinkedHashSet<>();

            for (ExtensionPoint extensionPoint : allExtensionPoints) {
                if (extensionPointType.isAssignableFrom(extensionPoint.getClass())) {
                    set.add((T) extensionPoint);
                }
            }

            return Collections.unmodifiableSet(set);
        });
    }

    private Optional<ClassLoader> createClassLoader(JReleaserLogger logger, Path basedir, ExtensionDef extensionDef) {
        Path directoryPath = Paths.get(extensionDef.getDirectory());
        if (!directoryPath.isAbsolute()) {
            directoryPath = basedir.resolve(directoryPath);
        }

        if (!Files.exists(directoryPath)) {
            logger.warn(RB.$("extension.manager.load.directory.missing", extensionDef.getName(), directoryPath.toAbsolutePath()));
            return Optional.empty();
        }

        List<Path> jars = null;
        try {
            jars = Files.list(directoryPath)
                .filter(path -> path.getFileName().toString().endsWith(".jar"))
                .collect(toList());
        } catch (IOException e) {
            logger.trace(e);
            logger.warn(RB.$("extension.manager.load.directory.error", extensionDef.getName(), directoryPath.toAbsolutePath()));
            return Optional.empty();
        }

        if (jars.isEmpty()) {
            logger.warn(RB.$("extension.manager.load.empty.jars", extensionDef.getName(), directoryPath.toAbsolutePath()));
            return Optional.empty();
        }

        URL[] urls = new URL[jars.size()];
        for (int i = 0; i < jars.size(); i++) {
            Path jar = jars.get(i);
            try {
                urls[i] = jar.toUri().toURL();
            } catch (MalformedURLException e) {
                logger.trace(e);
                logger.warn(RB.$("extension.manager.load.jar.error", extensionDef.getName(), jar.toAbsolutePath()));
                return Optional.empty();
            }
        }

        return Optional.of(new URLClassLoader(urls, getClass().getClassLoader()));
    }

    private void processExtension(JReleaserLogger logger, Extension extension, Set<String> visitedExtensionNames, Set<String> visitedExtensionTypes) {
        String extensionName = extension.getName();
        String extensionType = extension.getClass().getName();

        if (visitedExtensionNames.contains(extensionName) || visitedExtensionTypes.contains(extensionType)) {
            return;
        }

        logger.debug(RB.$("extension.manager.load", extensionName, extensionType));
        visitedExtensionNames.add(extensionName);
        visitedExtensionTypes.add(extensionType);

        ExtensionDef extensionDef = extensionDefs.get(extensionName);

        if (null != extensionDef && !extensionDef.isEnabled()) {
            logger.debug(RB.$("extension.manager.disabled", extensionName));
            return;
        }

        for (ExtensionPoint extensionPoint : extension.provides()) {
            String extensionPointTypeName = extensionPoint.getClass().getName();
            if (null != extensionDef && extensionDef.getExtensionPoints().containsKey(extensionPointTypeName)) {
                extensionPoint.init(extensionDef.getExtensionPoints().get(extensionPointTypeName)
                    .getProperties());
            }
            logger.debug(RB.$("extension.manager.add.extension.point", extensionPointTypeName, extensionName));
            allExtensionPoints.add(extensionPoint);
        }
    }

    private static ServiceLoader<Extension> resolveServiceLoader() {
        // Check if the type.classLoader works
        ServiceLoader<Extension> handlers = ServiceLoader.load(Extension.class, Extension.class.getClassLoader());
        if (handlers.iterator().hasNext()) {
            return handlers;
        }

        // If *nothing* else works
        return ServiceLoader.load(Extension.class);
    }

    private static class ExtensionDef {
        private final String name;
        private final String directory;
        private final boolean enabled;
        private final Map<String, ExtensionPointDef> extensionPoints = new LinkedHashMap<>();

        private ExtensionDef(String name, String directory, boolean enabled, Map<String, ExtensionPointDef> extensionPoints) {
            this.name = name;
            this.directory = directory;
            this.enabled = enabled;
            this.extensionPoints.putAll(extensionPoints);
        }

        private String getName() {
            return name;
        }

        private String getDirectory() {
            return directory;
        }

        private boolean isEnabled() {
            return enabled;
        }

        private Map<String, ExtensionPointDef> getExtensionPoints() {
            return extensionPoints;
        }
    }

    private static class ExtensionPointDef {
        private final String type;
        private final Map<String, Object> properties = new LinkedHashMap<>();

        private ExtensionPointDef(String type, Map<String, Object> properties) {
            this.type = type;
            this.properties.putAll(properties);
        }

        private String getType() {
            return type;
        }

        private Map<String, Object> getProperties() {
            return properties;
        }
    }

    public static class ExtensionBuilder {
        private final Map<String, ExtensionPointDef> extensionPoints = new LinkedHashMap<>();
        private final String name;
        private final DefaultExtensionManager defaultExtensionManager;
        private String directory;
        private boolean enabled;

        public ExtensionBuilder(String name, DefaultExtensionManager defaultExtensionManager) {
            this.name = name;
            this.defaultExtensionManager = defaultExtensionManager;

            String jreleaserHome = System.getenv("JRELEASER_USER_HOME");
            if (isBlank(jreleaserHome)) {
                jreleaserHome = System.getProperty("user.home") + File.separator + ".jreleaser";
            }
            Path baseExtensionsDirectory = Paths.get(jreleaserHome).resolve("extensions");
            this.directory = baseExtensionsDirectory.resolve(name).toAbsolutePath().toString();
        }

        public ExtensionBuilder withDirectory(String directory) {
            this.directory = directory;
            return this;
        }

        public ExtensionBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public ExtensionBuilder withExtensionPoint(String type, Map<String, Object> properties) {
            extensionPoints.put(type, new ExtensionPointDef(type, properties));
            return this;
        }

        public void build() {
            defaultExtensionManager.extensionDefs.put(name,
                new ExtensionDef(name, directory, enabled, extensionPoints));
        }
    }
}
