package com.woilsy.mock.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

    private static boolean DEBUG;

    public static final String NAME = "Mock";

    private static final Logger LOGGER = Logger.getLogger(NAME);

    public static void setDebug(boolean openOrClose) {
        DEBUG = openOrClose;
    }

    public static boolean isOpenDebug() {
        return LogUtil.DEBUG;
    }

    public static void i(String msg) {
        if (DEBUG) {
            LOGGER.log(Level.INFO, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            LOGGER.log(Level.SEVERE, msg);
        }
    }

    public static void e(String msg, Throwable t) {
        if (DEBUG) {
            LOGGER.log(Level.SEVERE, msg, t);
        }
    }
}
