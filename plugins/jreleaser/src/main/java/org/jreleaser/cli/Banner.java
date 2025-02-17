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
package org.jreleaser.cli;

import org.jreleaser.model.JReleaserOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
final class Banner {
    private static final Banner BANNER = new Banner();
    private final ResourceBundle bundle = ResourceBundle.getBundle(Banner.class.getName());
    private final String productVersion = bundle.getString("product.version");
    private final String productId = bundle.getString("product.id");
    private final String productName = bundle.getString("product.name");
    private final String banner = MessageFormat.format(bundle.getString("product.banner"), productName, productVersion);

    private Banner() {
        // noop
    }

    public static void display(PrintWriter out) {
        try {
            File jreleaserDir = new File(System.getProperty("user.home"));
            String envJreleaserDir = System.getenv("JRELEASER_DIR");
            if (envJreleaserDir != null && !envJreleaserDir.isEmpty()) {
                File dir = new File(envJreleaserDir);
                if (dir.exists()) {
                    jreleaserDir = dir;
                }
            }

            File parent = new File(jreleaserDir, "/.jreleaser/caches");
            File markerFile = getMarkerFile(parent, BANNER);
            if (!markerFile.exists()) {
                if (!JReleaserOutput.isQuiet()) out.println(BANNER.banner);
                markerFile.getParentFile().mkdirs();
                PrintStream fout = new PrintStream(new FileOutputStream(markerFile));
                fout.println("1");
                fout.close();
                writeQuietly(markerFile, "1");
            } else {
                try {
                    int count = Integer.parseInt(readQuietly(markerFile));
                    if (count < 3) {
                        if (!JReleaserOutput.isQuiet()) out.println(BANNER.banner);
                    }
                    writeQuietly(markerFile, (count + 1) + "");
                } catch (NumberFormatException e) {
                    writeQuietly(markerFile, "1");
                    if (!JReleaserOutput.isQuiet()) out.println(BANNER.banner);
                }
            }
        } catch (IOException ignored) {
            // noop
        }
    }

    private static void writeQuietly(File file, String text) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(file));
            out.println(text);
            out.close();
        } catch (IOException ignored) {
            // ignored
        }
    }

    private static String readQuietly(File file) {
        try {
            Scanner in = new Scanner(new FileInputStream(file));
            return in.next();
        } catch (Exception ignored) {
            return "";
        }
    }

    private static File getMarkerFile(File parent, Banner b) {
        return new File(parent,
            "jreleaser" +
                File.separator +
                b.productId +
                File.separator +
                b.productVersion +
                File.separator +
                "marker.txt");
    }
}
