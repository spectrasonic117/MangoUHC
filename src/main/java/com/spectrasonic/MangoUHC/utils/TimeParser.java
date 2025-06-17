package com.spectrasonic.MangoUHC.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    public static int parseTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return -1;
        }

        timeString = timeString.toLowerCase().trim();
        Matcher matcher = TIME_PATTERN.matcher(timeString);

        if (!matcher.matches()) {
            return -1;
        }

        int totalSeconds = 0;
        boolean foundAnyTime = false;

        String hoursStr = matcher.group(1);
        if (hoursStr != null && !hoursStr.isEmpty()) {
            try {
                int hours = Integer.parseInt(hoursStr);
                if (hours < 0) return -1;
                totalSeconds += hours * 3600;
                foundAnyTime = true;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        String minutesStr = matcher.group(2);
        if (minutesStr != null && !minutesStr.isEmpty()) {
            try {
                int minutes = Integer.parseInt(minutesStr);
                if (minutes < 0 || minutes >= 60) return -1;
                totalSeconds += minutes * 60;
                foundAnyTime = true;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        String secondsStr = matcher.group(3);
        if (secondsStr != null && !secondsStr.isEmpty()) {
            try {
                int seconds = Integer.parseInt(secondsStr);
                if (seconds < 0 || seconds >= 60) return -1;
                totalSeconds += seconds;
                foundAnyTime = true;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        return foundAnyTime && totalSeconds > 0 ? totalSeconds : -1;
    }

    public static String formatTime(int totalSeconds) {
        if (totalSeconds <= 0) {
            return "0s";
        }

        StringBuilder result = new StringBuilder();

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            result.append(hours).append("h ");
        }

        if (minutes > 0) {
            result.append(minutes).append("m ");
        }

        if (seconds > 0 || result.isEmpty()) {
            result.append(seconds).append("s");
        }

        return result.toString().trim();
    }

    public static boolean isValidTimeFormat(String timeString) {
        return parseTime(timeString) > 0;
    }
}
