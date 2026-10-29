package kz.prounbound;

import java.util.Locale;

/** Wire contract consumed by EngineeringRepositoryImpl.updateLocationInfo(). */
public final class OneMapPositionPayload {
    private OneMapPositionPayload() {}

    public static String encode(double longitude, double latitude, int speedKmh,
                                float bearing, double altitudeMeters) {
        if (Double.isNaN(longitude) || Double.isInfinite(longitude)
                || Double.isNaN(latitude) || Double.isInfinite(latitude)
                || Math.abs(longitude) > 180 || Math.abs(latitude) > 90
                || Float.isNaN(bearing) || Float.isInfinite(bearing)
                || Double.isNaN(altitudeMeters) || Double.isInfinite(altitudeMeters) || speedKmh < 0) {
            throw new IllegalArgumentException("Invalid positioning sample");
        }
        // Original MockLocationManager forwards Location.getSpeed(), in metres/second.
        return String.format(Locale.ROOT,
                "{\"longitude\":%.9f,\"latitude\":%.9f,\"speed\":%.6f,"
                + "\"bearing\":%.6f,\"altitude\":%.3f,\"locStatus\":true}",
                longitude, latitude, speedKmh / 3.6, bearing, altitudeMeters);
    }
}
