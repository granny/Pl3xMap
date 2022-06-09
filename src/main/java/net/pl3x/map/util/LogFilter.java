package net.pl3x.map.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.List;

public class LogFilter implements Filter {
    private final List<String> filters = new ArrayList<>();

    public static boolean ENABLED = false;

    public LogFilter() {
        filters.add("io.undertow");
        filters.add("org.xnio");
        filters.add("org.xnio.nio");
        filters.add("org.jboss.threads");
    }

    public Result checkMessage(String message) {
        if (ENABLED) {
            for (String filter : filters) {
                if (message.contains(filter)) {
                    return Result.DENY;
                }
            }
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object... objects) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object o, Throwable throwable) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return checkMessage(logger.getName());
    }

    @Override
    public Result filter(LogEvent logEvent) {
        return checkMessage(logEvent.getLoggerName());
    }

    @Override
    public State getState() {
        try {
            return State.STARTED;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}