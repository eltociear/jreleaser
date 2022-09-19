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
package org.jreleaser.sdk.tool;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.api.JReleaserContext;
import org.jreleaser.model.api.signing.SigningException;
import org.jreleaser.sdk.command.Command;
import org.jreleaser.sdk.command.CommandException;
import org.jreleaser.sdk.command.CommandExecutor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.jreleaser.util.StringUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.0.0
 */
public class Cosign extends AbstractTool {
    public Cosign(JReleaserContext context, String version) {
        //Cosign outputs its version information on the err-channel
        super(context, "cosign", version, true);
    }

    public boolean checkPassword(Path keyFile, byte[] password) {
        ByteArrayInputStream in = new ByteArrayInputStream(password);

        Command command = tool.asCommand()
            .arg("public-key")
            .arg("--key")
            .arg(keyFile.toAbsolutePath().toString());

        try {
            executeCommand(() -> new CommandExecutor(context.getLogger(), true)
                .executeCommandWithInput(command, in));
            return true;
        } catch (CommandException e) {
            context.getLogger().debug(RB.$("ERROR_password_incorrect"));
        }

        return false;
    }

    public Path generateKeyPair(byte[] password) throws SigningException {
        Command command = tool.asCommand()
            .arg("generate-key-pair");

        Path homeDir = resolveJReleaserHomeDir();
        try {
            executeCommand(() -> new CommandExecutor(context.getLogger(), true)
                .environment("COSIGN_PASSWORD", new String(password))
                .executeCommand(homeDir, command));
        } catch (CommandException e) {
            throw new SigningException(RB.$("ERROR_unexpected_generate_key_pair"), e);
        }

        context.getLogger().info(RB.$("cosign.generated.keys.at"), homeDir.toAbsolutePath());
        return homeDir.resolve("cosign.key");
    }

    public void signBlob(Path keyFile, byte[] password, Path input, Path destinationDir) throws SigningException {
        context.getLogger().info("{}", context.relativizeToBasedir(input));

        ByteArrayInputStream in = new ByteArrayInputStream(password);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Command command = tool.asCommand()
            .arg("sign-blob")
            .arg("--key")
            .arg(keyFile.toAbsolutePath().toString())
            .arg(input.toAbsolutePath().toString());

        try {
            executeCommand(() -> new CommandExecutor(context.getLogger(), true)
                .executeCommandWithInputCapturing(command, in, out));
        } catch (CommandException e) {
            throw new SigningException(RB.$("ERROR_unexpected_error_signing", input.toAbsolutePath()), e);
        }

        try {
            Path signature = destinationDir.resolve(input.getFileName() + ".sig");
            Files.write(signature, out.toByteArray());
        } catch (IOException e) {
            throw new SigningException(RB.$("ERROR_unexpected_error_signing", input), e);
        }
    }

    public void verifyBlob(Path keyFile, Path signature, Path input) throws SigningException {
        context.getLogger().debug("{}", context.relativizeToBasedir(signature));
        Command command = tool.asCommand()
            .arg("verify-blob")
            .arg("--key")
            .arg(keyFile.toAbsolutePath().toString())
            .arg("--signature")
            .arg(signature.toAbsolutePath().toString())
            .arg(input.toAbsolutePath().toString());

        try {
            executeCommand(() -> new CommandExecutor(context.getLogger(), true)
                .executeCommand(command));
        } catch (CommandException e) {
            throw new SigningException(RB.$("ERROR_signing_verify_signature",
                context.relativizeToBasedir(signature)), e);
        }
    }

    private Path resolveJReleaserHomeDir() {
        String home = System.getenv("JRELEASER_USER_HOME");
        if (isBlank(home)) {
            home = System.getProperty("user.home") + File.separator + ".jreleaser";
        }

        return Paths.get(home);
    }
}
