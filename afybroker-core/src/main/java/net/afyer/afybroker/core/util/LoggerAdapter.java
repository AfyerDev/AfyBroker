package net.afyer.afybroker.core.util;


import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * @author Nipuru
 * @since 2025/09/29 11:35
 */
public final class LoggerAdapter {

    private LoggerAdapter() {}

    /**
     * 将 jul 转换为 slf4j logger
     */
    public static org.slf4j.Logger toSlf4j(java.util.logging.Logger logger) {
        return new Logger() {
            @Override
            public String getName() {
                return logger.getName();
            }

            @Override
            public boolean isTraceEnabled() {
                return logger.isLoggable(java.util.logging.Level.FINER);
            }

            @Override
            public void trace(String msg) {
                logger.log(java.util.logging.Level.FINER, msg);
            }

            @Override
            public void trace(String format, Object arg) {
                if (isTraceEnabled()) {
                    logger.log(java.util.logging.Level.FINER, format.replaceFirst("\\{}", String.valueOf(arg)));
                }
            }

            @Override
            public void trace(String format, Object arg1, Object arg2) {
                if (isTraceEnabled()) {
                    logger.log(java.util.logging.Level.FINER, format.replaceFirst("\\{}", String.valueOf(arg1)).replaceFirst("\\{}", String.valueOf(arg2)));
                }
            }

            @Override
            public void trace(String format, Object... arguments) {
                if (isTraceEnabled()) {
                    logger.log(java.util.logging.Level.FINER, String.format(format.replace("{}", "%s"), arguments));
                }
            }

            @Override
            public void trace(String msg, Throwable t) {
                logger.log(java.util.logging.Level.FINER, msg, t);
            }

            @Override
            public boolean isTraceEnabled(Marker marker) {
                return isTraceEnabled();
            }

            @Override
            public void trace(Marker marker, String msg) {
                trace(msg);
            }

            @Override
            public void trace(Marker marker, String format, Object arg) {
                trace(format, arg);
            }

            @Override
            public void trace(Marker marker, String format, Object arg1, Object arg2) {
                trace(format, arg1, arg2);
            }

            @Override
            public void trace(Marker marker, String format, Object... argArray) {
                trace(format, argArray);
            }

            @Override
            public void trace(Marker marker, String msg, Throwable t) {
                trace(msg, t);
            }

            @Override
            public boolean isDebugEnabled() {
                return logger.isLoggable(java.util.logging.Level.FINE);
            }

            @Override
            public void debug(String msg) {
                logger.log(java.util.logging.Level.FINE, msg);
            }

            @Override
            public void debug(String format, Object arg) {
                if (isDebugEnabled()) {
                    logger.log(java.util.logging.Level.FINE, format.replaceFirst("\\{}", String.valueOf(arg)));
                }
            }

            @Override
            public void debug(String format, Object arg1, Object arg2) {
                if (isDebugEnabled()) {
                    logger.log(java.util.logging.Level.FINE, format.replaceFirst("\\{}", String.valueOf(arg1)).replaceFirst("\\{}", String.valueOf(arg2)));
                }
            }

            @Override
            public void debug(String format, Object... arguments) {
                if (isDebugEnabled()) {
                    logger.log(java.util.logging.Level.FINE, String.format(format.replace("{}", "%s"), arguments));
                }
            }

            @Override
            public void debug(String msg, Throwable t) {
                logger.log(java.util.logging.Level.FINE, msg, t);
            }

            @Override
            public boolean isDebugEnabled(Marker marker) {
                return isDebugEnabled();
            }

            @Override
            public void debug(Marker marker, String msg) {
                debug(msg);
            }

            @Override
            public void debug(Marker marker, String format, Object arg) {
                debug(format, arg);
            }

            @Override
            public void debug(Marker marker, String format, Object arg1, Object arg2) {
                debug(format, arg1, arg2);
            }

            @Override
            public void debug(Marker marker, String format, Object... arguments) {
                debug(format, arguments);
            }

            @Override
            public void debug(Marker marker, String msg, Throwable t) {
                debug(msg, t);
            }

            @Override
            public boolean isInfoEnabled() {
                return logger.isLoggable(java.util.logging.Level.INFO);
            }

            @Override
            public void info(String msg) {
                logger.info(msg);
            }

            @Override
            public void info(String format, Object arg) {
                if (isInfoEnabled()) {
                    logger.log(java.util.logging.Level.INFO, format.replaceFirst("\\{}", String.valueOf(arg)));
                }
            }

            @Override
            public void info(String format, Object arg1, Object arg2) {
                if (isInfoEnabled()) {
                    logger.log(java.util.logging.Level.INFO, format.replaceFirst("\\{}", String.valueOf(arg1)).replaceFirst("\\{}", String.valueOf(arg2)));
                }
            }

            @Override
            public void info(String format, Object... arguments) {
                if (isInfoEnabled()) {
                    logger.log(java.util.logging.Level.INFO, String.format(format.replace("{}", "%s"), arguments));
                }
            }

            @Override
            public void info(String msg, Throwable t) {
                logger.log(java.util.logging.Level.INFO, msg, t);
            }

            @Override
            public boolean isInfoEnabled(Marker marker) {
                return isInfoEnabled();
            }

            @Override
            public void info(Marker marker, String msg) {
                info(msg);
            }

            @Override
            public void info(Marker marker, String format, Object arg) {
                info(format, arg);
            }

            @Override
            public void info(Marker marker, String format, Object arg1, Object arg2) {
                info(format, arg1, arg2);
            }

            @Override
            public void info(Marker marker, String format, Object... arguments) {
                info(format, arguments);
            }

            @Override
            public void info(Marker marker, String msg, Throwable t) {
                info(msg, t);
            }

            @Override
            public boolean isWarnEnabled() {
                return logger.isLoggable(java.util.logging.Level.WARNING);
            }

            @Override
            public void warn(String msg) {
                logger.warning(msg);
            }

            @Override
            public void warn(String format, Object arg) {
                if (isWarnEnabled()) {
                    logger.log(java.util.logging.Level.WARNING, format.replaceFirst("\\{}", String.valueOf(arg)));
                }
            }

            @Override
            public void warn(String format, Object... arguments) {
                if (isWarnEnabled()) {
                    logger.log(java.util.logging.Level.WARNING, String.format(format.replace("{}", "%s"), arguments));
                }
            }

            @Override
            public void warn(String format, Object arg1, Object arg2) {
                if (isWarnEnabled()) {
                    logger.log(java.util.logging.Level.WARNING, format.replaceFirst("\\{}", String.valueOf(arg1)).replaceFirst("\\{}", String.valueOf(arg2)));
                }
            }

            @Override
            public void warn(String msg, Throwable t) {
                logger.log(java.util.logging.Level.WARNING, msg, t);
            }

            @Override
            public boolean isWarnEnabled(Marker marker) {
                return isWarnEnabled();
            }

            @Override
            public void warn(Marker marker, String msg) {
                warn(msg);
            }

            @Override
            public void warn(Marker marker, String format, Object arg) {
                warn(format, arg);
            }

            @Override
            public void warn(Marker marker, String format, Object arg1, Object arg2) {
                warn(format, arg1, arg2);
            }

            @Override
            public void warn(Marker marker, String format, Object... arguments) {
                warn(format, arguments);
            }

            @Override
            public void warn(Marker marker, String msg, Throwable t) {
                warn(msg, t);
            }

            @Override
            public boolean isErrorEnabled() {
                return logger.isLoggable(java.util.logging.Level.SEVERE);
            }

            @Override
            public void error(String msg) {
                logger.severe(msg);
            }

            @Override
            public void error(String format, Object arg) {
                if (isErrorEnabled()) {
                    logger.log(java.util.logging.Level.SEVERE, format.replaceFirst("\\{}", String.valueOf(arg)));
                }
            }

            @Override
            public void error(String format, Object arg1, Object arg2) {
                if (isErrorEnabled()) {
                    logger.log(java.util.logging.Level.SEVERE, format.replaceFirst("\\{}", String.valueOf(arg1)).replaceFirst("\\{}", String.valueOf(arg2)));
                }
            }

            @Override
            public void error(String format, Object... arguments) {
                if (isErrorEnabled()) {
                    logger.log(java.util.logging.Level.SEVERE, String.format(format.replace("{}", "%s"), arguments));
                }
            }

            @Override
            public void error(String msg, Throwable t) {
                logger.log(java.util.logging.Level.SEVERE, msg, t);
            }

            @Override
            public boolean isErrorEnabled(Marker marker) {
                return isErrorEnabled();
            }

            @Override
            public void error(Marker marker, String msg) {
                error(msg);
            }

            @Override
            public void error(Marker marker, String format, Object arg) {
                error(format, arg);
            }

            @Override
            public void error(Marker marker, String format, Object arg1, Object arg2) {
                error(format, arg1, arg2);
            }

            @Override
            public void error(Marker marker, String format, Object... arguments) {
                error(format, arguments);
            }

            @Override
            public void error(Marker marker, String msg, Throwable t) {
                error(msg, t);
            }
        };
    }
}
