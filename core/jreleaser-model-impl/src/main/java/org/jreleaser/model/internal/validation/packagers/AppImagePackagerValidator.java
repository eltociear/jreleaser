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
package org.jreleaser.model.internal.validation.packagers;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.Active;
import org.jreleaser.model.Stereotype;
import org.jreleaser.model.api.JReleaserContext.Mode;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.JReleaserModel;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.model.internal.common.Icon;
import org.jreleaser.model.internal.distributions.Distribution;
import org.jreleaser.model.internal.packagers.AppImagePackager;
import org.jreleaser.model.internal.release.Releaser;
import org.jreleaser.model.internal.validation.common.Validator;
import org.jreleaser.util.Errors;

import java.util.List;

import static org.jreleaser.model.api.packagers.AppImagePackager.SKIP_APPIMAGE;
import static org.jreleaser.model.internal.validation.common.ExtraPropertiesValidator.mergeExtraProperties;
import static org.jreleaser.model.internal.validation.common.TemplateValidator.validateTemplate;
import static org.jreleaser.model.internal.validation.distributions.DistributionsValidator.validateArtifactPlatforms;
import static org.jreleaser.util.CollectionUtils.listOf;
import static org.jreleaser.util.StringUtils.isBlank;
import static org.jreleaser.util.StringUtils.isNotBlank;
import static org.jreleaser.util.StringUtils.isTrue;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public abstract class AppImagePackagerValidator extends Validator {
    public static void validateAppImage(JReleaserContext context, Mode mode, Distribution distribution, AppImagePackager packager, Errors errors) {
        context.getLogger().debug("distribution.{}.appImage", distribution.getName());
        JReleaserModel model = context.getModel();
        AppImagePackager parentPackager = model.getPackagers().getAppImage();

        if (!packager.isActiveSet() && parentPackager.isActiveSet()) {
            packager.setActive(parentPackager.getActive());
        }
        if (!packager.resolveEnabled(context.getModel().getProject(), distribution)) {
            context.getLogger().debug(RB.$("validation.disabled"));
            return;
        }
        Releaser service = model.getRelease().getReleaser();
        if (!service.isReleaseSupported()) {
            context.getLogger().debug(RB.$("validation.disabled.release"));
            packager.disable();
            return;
        }

        List<Artifact> candidateArtifacts = packager.resolveCandidateArtifacts(context, distribution);
        if (candidateArtifacts.size() == 0) {
            packager.setActive(Active.NEVER);
            context.getLogger().debug(RB.$("validation.disabled.no.artifacts"));
            packager.disable();
            return;
        } else if (candidateArtifacts.stream()
            .filter(artifact -> isBlank(artifact.getPlatform()))
            .count() > 1) {
            errors.configuration(RB.$("validation_packager_multiple_artifacts", "distribution." + distribution.getName() + ".appImage"));
            context.getLogger().debug(RB.$("validation.disabled.multiple.artifacts"));
            packager.disable();
            return;
        }

        if (isBlank(packager.getComponentId()) && isNotBlank(parentPackager.getComponentId())) {
            packager.setComponentId(parentPackager.getComponentId());
        }
        if (isBlank(packager.getComponentId())) {
            errors.configuration(RB.$("validation_must_not_be_blank", "distribution." + distribution.getName() + ".appImage.componentId"));
        }

        if (packager.getCategories().isEmpty()) {
            packager.setCategories(parentPackager.getCategories());
        }
        if (packager.getCategories().isEmpty()) {
            errors.configuration(RB.$("validation_is_empty", "distribution." + distribution.getName() + ".appImage.categories"));
        }

        if (!packager.isRequiresTerminalSet() && parentPackager.isRequiresTerminalSet()) {
            packager.setRequiresTerminal(parentPackager.isRequiresTerminal());
        }
        if (distribution.getStereotype() == Stereotype.CLI) {
            packager.setRequiresTerminal(true);
        }

        if (isBlank(packager.getDeveloperName())) {
            packager.setDeveloperName(parentPackager.getDeveloperName());
        }

        if (packager.getScreenshots().isEmpty()) {
            packager.setScreenshots(parentPackager.getScreenshots());
        }
        if (packager.getScreenshots().isEmpty()) {
            errors.configuration(RB.$("validation_is_empty", "distribution." + distribution.getName() + ".appImage.screenshots"));
        }
        validateScreenshots(context, mode, packager.getScreenshots(), errors, "distribution." + distribution.getName() + ".appImage");
        packager.getScreenshots().removeIf(screenshot -> isTrue(screenshot.getExtraProperties().get(SKIP_APPIMAGE)));
        if (packager.getScreenshots().isEmpty()) {
            errors.configuration(RB.$("validation_is_empty", "distribution." + distribution.getName() + ".appImage.screenshots"));
        }

        if (packager.getIcons().isEmpty()) {
            packager.setIcons(parentPackager.getIcons());
        }
        if (packager.getIcons().isEmpty()) {
            errors.configuration(RB.$("validation_is_empty", "distribution." + distribution.getName() + ".appImage.icons"));
        }
        validateIcons(context, mode, packager.getIcons(), errors, "distribution." + distribution.getName() + ".appImage");
        packager.getIcons().removeIf(icon -> isTrue(icon.getExtraProperties().get(SKIP_APPIMAGE)));
        if (packager.getIcons().isEmpty()) {
            errors.configuration(RB.$("validation_is_empty", "distribution." + distribution.getName() + ".appImage.icons"));
        }
        for (int i = 0; i < packager.getIcons().size(); i++) {
            Icon icon = packager.getIcons().get(i);
            if (icon.getWidth() != null && !icon.getWidth().equals(icon.getHeight())) {
                errors.configuration(RB.$("validation_must_be_equal",
                    "distribution." + distribution.getName() + ".appImage.icons[" + i + "].width", icon.getWidth(),
                    "distribution." + distribution.getName() + ".appImage.icons[" + i + "].height", icon.getHeight()));
            }
        }
        if (packager.getIcons().size() == 1) {
            packager.getIcons().get(0).setPrimary(true);
        }
        if (packager.getIcons().stream()
            .mapToInt(s -> s.isPrimary() ? 1 : 0)
            .sum() == 0) {
            errors.configuration(RB.$("validation_no_primary_icon", "distribution." + distribution.getName() + ".appImage.icons"));
        }

        if (isBlank(packager.getRepository().getName())) {
            packager.getRepository().setName(distribution.getName() + "-appimage");
        }
        packager.getRepository().setTapName(distribution.getName() + "-appimage");

        if (distribution.getStereotype() != Stereotype.CLI && distribution.getStereotype() != Stereotype.DESKTOP) {
            errors.configuration(RB.$("validation_stereotype_invalid",
                "distribution." + distribution.getName() + ".stereotype",
                distribution.getStereotype(),
                listOf(Stereotype.CLI, Stereotype.DESKTOP)));
        }

        validateCommitAuthor(packager, parentPackager);
        AppImagePackager.AppImageRepository repository = packager.getRepository();
        repository.resolveEnabled(model.getProject());
        validateTap(context, distribution, repository, parentPackager.getRepository(), "appImage.repository");
        validateTemplate(context, distribution, packager, parentPackager, errors);
        mergeExtraProperties(packager, parentPackager);
        validateContinueOnError(packager, parentPackager);
        if (isBlank(packager.getDownloadUrl())) {
            packager.setDownloadUrl(parentPackager.getDownloadUrl());
        }
        validateArtifactPlatforms(context, distribution, packager, candidateArtifacts, errors);
    }
}
