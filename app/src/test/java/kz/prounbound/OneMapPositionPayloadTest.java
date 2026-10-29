package kz.prounbound;

import org.junit.Test;
import java.util.Locale;
import static org.junit.Assert.*;

public class OneMapPositionPayloadTest {
    @Test public void degreesStayUnchangedAndKmhBecomesMetersPerSecond() {
        String wire = OneMapPositionPayload.encode(76.904876, 43.326128, 36, 90, 812.5);
        assertTrue(wire.contains("\"longitude\":76.904876000"));
        assertTrue(wire.contains("\"latitude\":43.326128000"));
        assertTrue(wire.contains("\"speed\":10.000000"));
        assertTrue(wire.contains("\"altitude\":812.500"));
        assertTrue(wire.contains("\"locStatus\":true"));
    }

    @Test public void decimalSeparatorDoesNotDependOnDeviceLocale() {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertTrue(OneMapPositionPayload.encode(-3.5, 51.2, 0, 0, 0)
                    .contains("\"longitude\":-3.500000000"));
        } finally { Locale.setDefault(old); }
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonFiniteInputIsRejectedBeforeItCanReachTheReceiver() {
        OneMapPositionPayload.encode(Double.NaN, 43, 36, 90, 0);
    }
}
