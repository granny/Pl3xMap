package net.pl3x.map.logger;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.configuration.Lang;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Pl3xLogger extends Logger {
    private static final ComponentLogger LOGGER = ComponentLogger.logger(org.apache.logging.log4j.LogManager.getRootLogger().getName());

    public Pl3xLogger() {
        super("Pl3xMap", null);
        LogManager.getLogManager().addLogger(this);
    }

    private void doLog(LogRecord lr) {
        Level level = lr.getLevel();
        if (level == Level.INFO) {
            LOGGER.info(MiniMessage.miniMessage().deserialize(Lang.PREFIX_LOGGER + lr.getMessage()));
        } else if (level == Level.WARNING) {
            LOGGER.warn(MiniMessage.miniMessage().stripTags(Lang.PREFIX_LOGGER + lr.getMessage()));
        } else if (level == Level.SEVERE) {
            LOGGER.error(MiniMessage.miniMessage().stripTags(Lang.PREFIX_LOGGER + lr.getMessage()));
        } else if (level == Level.CONFIG) {
            LOGGER.debug(MiniMessage.miniMessage().deserialize(Lang.PREFIX_LOGGER + lr.getMessage()));
        }
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
