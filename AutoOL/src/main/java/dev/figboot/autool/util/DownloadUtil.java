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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.DigestInputStream;

public class DownloadUtil {
    private DownloadUtil() { }

    public static String download(URL url, Proxy proxy, ProgressUpdater progress, OutputStream stream) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
        conn.setUseCaches(false);
        conn.setDefaultUseCaches(false);
        conn.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
        conn.setRequestProperty("Expires", "0");
        conn.setRequestProperty("Pragma", "no-cache");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(30000);

        byte[] buf = new byte[4096];
        int readc, received = 0;
        String ret;

        try (DigestInputStream is = new DigestInputStream(conn.getInputStream(), Util.createSHA1Digest())) {
            if (conn.getResponseCode() / 100 != 2) {
                throw new RuntimeException("HTTP request to '" + url + "' returned non 2xx status: " + conn.getResponseCode() + " " + conn.getResponseMessage());
            }

            progress.setMaxProgress(conn.getContentLength());

            while ((readc = is.read(buf)) > 0) {
                if (progress.cancelOperation()) throw new CancelledException();

                progress.setProgress(received += readc);
                stream.write(buf, 0, readc);
            }
            ret = Util.digestToString(is.getMessageDigest(), 40);
        }

        return ret;
    }
}
