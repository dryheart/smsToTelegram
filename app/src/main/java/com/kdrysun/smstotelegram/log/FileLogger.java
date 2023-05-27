package com.kdrysun.smstotelegram.log;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class FileLogger {
    private static FileHandler fileHandler;
    private static String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
    private static final String LOG_FILE_NAME = "FileLog%g.txt";
    private static final int LOG_FILE_SIZE_LIMIT = 512*1024;
    private static final int LOG_FILE_MAX_COUNT = 1;
    private static final SimpleDateFormat formatter =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS: ", Locale.getDefault());

    static {
        try {
            fileHandler = new FileHandler(filePath +
                    File.separator +
                    LOG_FILE_NAME, LOG_FILE_SIZE_LIMIT, LOG_FILE_MAX_COUNT, true);

            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    StringBuilder ret = new StringBuilder(80);
                    ret.append(formatter.format(new Date()));
                    ret.append(record.getMessage());
                    ret.append("\n");
                    return ret.toString();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger(Class cls) {
        Logger logger = Logger.getLogger(cls.getName());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        return logger;
    }
}
