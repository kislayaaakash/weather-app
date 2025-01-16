package com.ps.weatherapp.utilities;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeManager {

    public static String extractTimeFromUnix(long unixTimestamp, int timeZoneOffsetInSeconds) {
        // Convert Unix timestamp to UTC time
        Instant utcInstant = Instant.ofEpochSecond(unixTimestamp);

        // Add the offset to the UTC time
        Instant adjustedInstant = utcInstant.plusSeconds(timeZoneOffsetInSeconds);

        // Convert to LocalTime based on the adjusted Instant
        LocalTime localTime = LocalTime.ofInstant(adjustedInstant, ZoneOffset.UTC);

        // Format the time to HH:mm:ss
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localTime.format(timeFormatter);
    }

    public static String extractDateFromUnix(long unixTimestamp, int timeZoneOffsetInSeconds) {
        // Convert Unix timestamp to UTC time
        Instant utcInstant = Instant.ofEpochSecond(unixTimestamp);

        // Add the offset to the UTC time
        Instant adjustedInstant = utcInstant.plusSeconds(timeZoneOffsetInSeconds);

        // Convert to LocalDate based on the adjusted Instant
        LocalDate localDate = LocalDate.ofInstant(adjustedInstant, ZoneOffset.UTC);

        // Format the date to yyyy-MM-dd
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(dateFormatter);
    }

    private String extractDateTimeFromUnix(long unixUtcTime, int timeZoneOffsetInSeconds) {
        // Convert the Unix UTC time to an Instant
        Instant instant = Instant.ofEpochSecond(unixUtcTime);

        // Create a ZoneOffset from the timezone offset in seconds
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timeZoneOffsetInSeconds);

        // Convert the Instant to a ZonedDateTime using the ZoneOffset
        ZonedDateTime zonedDateTime = instant.atZone(offset);

        // Define the desired output format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the ZonedDateTime to a string
        return zonedDateTime.format(formatter);
    }
}
