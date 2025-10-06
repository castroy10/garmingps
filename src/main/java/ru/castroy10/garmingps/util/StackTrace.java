package ru.castroy10.garmingps.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StackTrace {

    public String printStackTrace(final Throwable e) {
        final StringBuilder sb = new StringBuilder();

        sb.append(e.toString()).append(System.lineSeparator());
        for (final StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString())
              .append(System.lineSeparator());
        }

        Throwable cause = e.getCause();
        while (cause != null) {
            sb.append("Caused by: ")
              .append(cause)
              .append(System.lineSeparator());
            for (final StackTraceElement element : cause.getStackTrace()) {
                sb.append(element.toString())
                  .append(System.lineSeparator());
            }
            cause = cause.getCause();
        }

        return sb.toString();
    }


}
