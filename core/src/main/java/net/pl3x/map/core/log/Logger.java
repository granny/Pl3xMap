/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.log;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Logger {
    static {
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new LogFilter());
    }

    public static void debug(@NotNull String message) {
        if (Config.DEBUG_MODE) {
            log("<gray>[<yellow>DEBUG</yellow>] " + message);
        }
    }

    public static void info(@NotNull String message) {
        log("<gray>[INFO] " + message);
    }

    public static void severe(@NotNull String message) {
        severe(message, null);
    }

    public static void severe(@NotNull String message, @Nullable Throwable throwable) {
        log("<gray>[<red>ERROR</red>]</gray> <red>" + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public static void warn(@NotNull String message) {
        warn(message, null);
    }

    public static void warn(@NotNull String message, @Nullable Throwable throwable) {
        log("<gray>[<yellow>WARN</yellow>]</gray> <yellow>" + message);
        if (throwable != null && Config.DEBUG_MODE) {
            throwable.printStackTrace();
        }
    }

    private static void log(String message) {
        Pl3xMap.api().adventure().console().sendMessage(Lang.parse(Lang.PREFIX_COMMAND + message));
    }
}
