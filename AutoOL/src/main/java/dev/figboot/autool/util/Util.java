/*
 * AutoOL
 * Copyright (C) 2022  bigfoot547 <olauncher@figboot.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://github.com/olauncher/autool .
 */

package dev.figboot.autool.util;

import dev.figboot.autool.ui.ProgressUpdater;
import dev.figboot.autool.util.exception.CancelledException;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class Util {
    private Util() { }

    @SneakyThrows
    public static MessageDigest createSHA1Digest() {
        return MessageDigest.getInstance("SHA-1");
    }

    public static String digestToString(MessageDigest digest, int padLen) {
        return String.format("%1$0" + padLen + "x", new BigInteger(1, digest.digest()));
    }

    public static boolean checkFileIntegrity(File file, String hash, ProgressUpdater progress) {
        MessageDigest digest;
        byte[] buf = new byte[4096];
        try (CountedInputStream cis = new CountedInputStream(new DigestInputStream(new FileInputStream(file), digest = createSHA1Digest()))) {
            while (cis.read(buf) > 0) {
                if (progress.cancelOperation()) throw new CancelledException();
                progress.setProgress((int)cis.getCount());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error checking file integrity", ex);
        }

        return hash.equalsIgnoreCase(digestToString(digest, 40));
    }
}
