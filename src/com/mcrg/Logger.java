package com.mcrg;

public class Logger {

    private static boolean ENABLE_LOGGING;

    public Logger(boolean set) {
        ENABLE_LOGGING = set;
    }

    public void log(final String message) {
        if (ENABLE_LOGGING) {
            System.out.println(message);
        }
    }
}