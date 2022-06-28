package com.ffanaticism.intruder.serviceprovider.util;

import com.ffanaticism.intruder.serviceprovider.exception.CastingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TimeUtil {
    private static final String SECONDS_SPLIT_REGEX = ":";

    public static float getOffsetInSeconds(String offset) throws CastingException {
        return getTimeInSeconds(offset);
    }

    public static float getDurationInSeconds(String duration, String offset) throws CastingException {
        return getTimeInSeconds(duration) - getTimeInSeconds(offset);
    }

    public static float getTimeInSeconds(String time) throws CastingException {
        var timeInSeconds = 0f;

        var splitOffset = time.trim().split(SECONDS_SPLIT_REGEX, 2);

        if (splitOffset.length > 1) {
            var minutes = 0f;
            var seconds = 0f;

            try {
                minutes = Float.parseFloat(splitOffset[0]);
                seconds = Float.parseFloat(splitOffset[1]);
            } catch (NumberFormatException e) {
                log.error("Error during time to minutes and seconds casting");
                throw new CastingException();
            }

            timeInSeconds = minutes * 60 + seconds;
        } else if (splitOffset.length == 1 && !splitOffset[0].isBlank()) {
            try {
                timeInSeconds = Float.parseFloat(splitOffset[0]);
            } catch (NumberFormatException e) {
                log.error("Error during time to seconds casting");
                throw new CastingException();
            }
        }

        return timeInSeconds;
    }
}
