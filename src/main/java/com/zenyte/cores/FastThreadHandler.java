package com.zenyte.cores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David O'Neill
 */
final class FastThreadHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FastThreadHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logger.error("(" + thread.getName() + ", fast pool) - Printing trace");
        throwable.printStackTrace();
    }
}
