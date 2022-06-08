package net.pl3x.map.util;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Pl3xLogger extends Logger {
    public Pl3xLogger() {
        super("\u001b[36mPl3xMap\u001b[0m", null);
        LogManager.getLogManager().addLogger(this);
    }
}
