package com.kitsune.foxlib.util;

import java.io.PrintStream;

public class Log {

    private static void message (PrintStream level, String prefix, String message){
        level.println("[" + prefix + "] " + message);
    }

    /**
     * Log a message to the console
     * @param prefix - the prefix of the message
     * @param message - the message
     */
    public static void info (String prefix, String message){
        message(System.out, prefix, message);
    }

    /**
     * Log a warning to the console
     * @param prefix - the prefix of the message
     * @param message - the message
     */
    public static void warn (String prefix, String message){
        message(System.err, prefix, message);
    }

    /**
     * Log an error to the console
     * @param prefix - the prefix of the message
     * @param message - the message
     */
    public static void error (String prefix, String message){
        message(System.err, prefix, message);
    }

}

