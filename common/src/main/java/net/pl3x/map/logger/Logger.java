package net.pl3x.map.logger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;

public class Logger {
    private static final Pl3xLogger LOGGER = new Pl3xLogger("Pl3xMap");

    public static Pl3xLogger getInstance() {
        return LOGGER;
    }

    public static void debug(String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warning(message);
    }

    public static void severe(String message) {
        LOGGER.severe(message);
    }

    public static class Pl3xLogger extends java.util.logging.Logger {
        public Pl3xLogger(String name) {
            super(name, null);
            LogManager.getLogManager().addLogger(this);
            // this filter lets us hide undertow/xnio/jboss messages to the logger
            ((org.apache.logging.log4j.core.Logger) org.apache.logging.log4j.LogManager.getRootLogger()).addFilter(new LogFilter());
        }

        @Override
        public void info(String msg) {
            // send through ConsoleSender so logger will strip colors before putting in log file
            Pl3xMap.api().getConsole().send(Lang.parse(msg));
        }

        @Override
        public void info(Supplier<String> msgSupplier) {
            info(msgSupplier.get());
        }

        private void doLog(LogRecord lr) {
            if (lr.getLevel() == Level.INFO) {
                info(lr.getMessage());
                return;
            }
            lr.setLoggerName(getName());
            log(lr);
        }

        /*
         * The rest of this is just copying methods in order to use the modified doLog private method above..
         */

        @Override
        public void log(Level level, String msg) {
            if (!isLoggable(level)) {
                return;
            }
            LogRecord lr = new LogRecord(level, msg);
            doLog(lr);
        }

        @Override
        public void log(Level level, Supplier<String> msgSupplier) {
            if (!isLoggable(level)) {
                return;
            }
            LogRecord lr = new LogRecord(level, msgSupplier.get());
            doLog(lr);
        }

        @Override
        public void log(Level level, String msg, Object param1) {
            if (!isLoggable(level)) {
                return;
            }
            LogRecord lr = new LogRecord(level, msg);
            Object[] params = {param1};
            lr.setParameters(params);
            doLog(lr);
        }

        @Override
        public void log(Level level, String msg, Object[] params) {
            if (!isLoggable(level)) {
                return;
            }
            LogRecord lr = new LogRecord(level, msg);
            lr.setParameters(params);
            doLog(lr);
        }

        @Override
        public void log(Level level, String msg, Throwable thrown) {
            if (!isLoggable(level)) {
                return;
            }
            LogRecord lr = new LogRecord(level, msg);
            lr.setThrown(thrown);
            doLog(lr);
        }

        @Override
        public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
            if (!isLoggable(level)) {
                return;
            }
            LogRecord lr = new LogRecord(level, msgSupplier.get());
            lr.setThrown(thrown);
            doLog(lr);
        }

        @Override
        public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
            if (!isLoggable(Level.FINER)) {
                return;
            }
            LogRecord lr = new LogRecord(Level.FINER, "THROW");
            lr.setSourceClassName(sourceClass);
            lr.setSourceMethodName(sourceMethod);
            lr.setThrown(thrown);
            doLog(lr);
        }
    }
}
