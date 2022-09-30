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
package org.jreleaser.sdk.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.jreleaser.bundle.RB;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.download.Downloader;
import org.jreleaser.model.internal.download.FtpDownloader;
import org.jreleaser.model.spi.download.DownloadException;
import org.jreleaser.sdk.commons.AbstractArtifactDownloader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.jreleaser.util.StringUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class FtpArtifactDownloader extends AbstractArtifactDownloader<org.jreleaser.model.api.download.FtpDownloader, FtpDownloader> {
    private FtpDownloader downloader;

    public FtpArtifactDownloader(JReleaserContext context) {
        super(context);
    }

    @Override
    public FtpDownloader getDownloader() {
        return downloader;
    }

    @Override
    public void setDownloader(FtpDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public String getType() {
        return org.jreleaser.model.api.download.ScpDownloader.TYPE;
    }

    @Override
    public void download(String name) throws DownloadException {
        FTPClient ftp = FtpUtils.open(context, downloader);

        try {
            for (Downloader.Asset asset : downloader.getAssets()) {
                downloadAsset(name, ftp, asset);
            }
        } finally {
            FtpUtils.close(downloader, ftp);
        }
    }

    private void downloadAsset(String name, FTPClient ftp, Downloader.Asset asset) throws DownloadException {
        String input = asset.getResolvedInput(context, downloader);
        String output = asset.getResolvedOutput(context, downloader, Paths.get(input).getFileName().toString());

        if (isBlank(output)) {
            output = Paths.get(input).getFileName().toString();
        }

        Path outputPath = context.getDownloadDirectory().resolve(name).resolve(output);
        context.getLogger().info("{} -> {}", input, context.relativizeToBasedir(outputPath));

        try {
            Files.createDirectories(outputPath.getParent());
        } catch (IOException e) {
            throw new DownloadException(RB.$("ERROR_unexpected_download", input), e);
        }

        try (OutputStream out = Files.newOutputStream(outputPath, CREATE, TRUNCATE_EXISTING, WRITE)) {
            Files.createDirectories(outputPath.toAbsolutePath().getParent());
            ftp.retrieveFile(input, out);
        } catch (IOException e) {
            throw new DownloadException(RB.$("ERROR_unexpected_download", input), e);
        }

        unpack(asset.getUnpack(), outputPath);
    }
}
