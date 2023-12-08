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

import lombok.Getter;

import java.util.Locale;

public enum OperatingSystem {
    WINDOWS("win"),
    LINUX("linux", "unix"),
    OSX("osx", "mac"),
    SOLARIS("sunos"),
    UNKNOWN();

    private final String[] searchStr;

    OperatingSystem(String... search) {
        this.searchStr = search;
    }

    @Getter private static OperatingSystem OS;

    private static void initOS() {
        if (OS != null) throw new IllegalStateException("OS already initialized");

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        for (OperatingSystem val : values()) {
            for (String search : val.searchStr) {
                if (osName.contains(search)) {
                    OS = val;
                    return;
                }
            }
        }

        OS = UNKNOWN;
    }

    static {
        initOS();
    }
}
