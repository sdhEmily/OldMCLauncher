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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class CountedInputStream extends InputStream {
    private final InputStream parent;

    private final Object countLock = new Object();
    private long count;
    private long markCount;
    private int markReadLimit;

    private final Consumer<Long> postIncCallback;

    public CountedInputStream(InputStream parent, Consumer<Long> postIncCallback) {
        this.parent = parent;
        count = 0;
        markCount = 0;
        markReadLimit = 0;

        this.postIncCallback = postIncCallback;
    }

    public CountedInputStream(InputStream parent) {
        this(parent, null);
    }

    protected void incCount(long l) {
        long cpy;

        synchronized (countLock) {
            cpy = count;
            count += l;
            // If the stream supports marks and there is a mark in place and the markReadLimit has been exceeded...
            if (markSupported() && markReadLimit > 0 && count - markCount > markReadLimit) {
                markReadLimit = 0;
                markCount = 0;
            }
        }

        if (postIncCallback != null) postIncCallback.accept(cpy);
    }

    public long getCount() {
        synchronized (countLock) {
            return count;
        }
    }

    protected void resetCount() {
        synchronized (countLock) {
            count = markCount;
        }
    }

    public long getCountAndReset() {
        synchronized (countLock) {
            long temp = count;
            count = 0;
            return temp;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        int res = parent.read(b);
        incCount(res);
        return res;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int res = parent.read(b, off, len);
        incCount(res);
        return res;
    }

    @Override
    public long skip(long n) throws IOException {
        long res = parent.skip(n);
        incCount(res);
        return res;
    }

    @Override
    public int available() throws IOException {
        return parent.available();
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (markSupported()) {
            markCount = count;
            markReadLimit = readlimit;
        }

        parent.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        if (markSupported() && markReadLimit > 0) resetCount();
        parent.reset();
    }

    @Override
    public boolean markSupported() {
        return parent.markSupported();
    }

    @Override
    public int read() throws IOException {
        int res = parent.read();
        if (res >= 0) incCount(1L);
        return res;
    }
}
