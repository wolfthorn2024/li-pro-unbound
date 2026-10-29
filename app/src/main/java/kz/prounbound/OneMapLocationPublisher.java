package kz.prounbound;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import kz.prounbound.route.LoopRoute;

/** App-scoped engineering broadcasts; never creates or updates Android test providers. */
final class OneMapLocationPublisher {
    static final String ACTION = "com.liauto.onemap.TROJAN_HORSE";
    private static final ComponentName RECEIVER = new ComponentName("com.liauto.onemap",
            "com.liauto.onemap.engineering.receiver.EngineeringReceiver");
    private final Context context;
    private boolean active;
    private long sentCount;

    OneMapLocationPublisher(Context context) { this.context = context.getApplicationContext(); }

    void start() {
        try {
            android.content.pm.ActivityInfo info = context.getPackageManager().getReceiverInfo(RECEIVER, 0);
            if (!info.enabled || !info.exported || !info.applicationInfo.enabled) {
                throw new IllegalStateException("OneMap engineering receiver is disabled or not exported");
            }
            if (info.permission != null && context.checkCallingOrSelfPermission(info.permission)
                    != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException("OneMap receiver requires " + info.permission);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("OneMap engineering receiver was not found in this Android profile", e);
        }
        sentCount = 0;
        active = true;
    }

    void publish(LoopRoute.Position position, int speedKmh, int accuracyMeters,
                 float verticalAccuracyMeters, float speedAccuracyMps, float bearingAccuracyDegrees) {
        if (!active) return;
        // GPX points are WGS84. location_info copies coordinates directly with isEncrypted=0.
        // The receiver ignores accuracy/provider fields, unlike Android mock GPS.
        String json = OneMapPositionPayload.encode(position.longitude, position.latitude,
                speedKmh, position.bearing,
                position.hasAltitude() ? position.altitudeEllipsoidMeters : 0.0);
        context.sendBroadcast(new Intent(ACTION).setComponent(RECEIVER)
                .putExtra("key", "location_info").putExtra("param_1", json));
        sentCount++;
        if (sentCount == 1 || sentCount % 20 == 0) {
            Log.i("ProUnboundOneMap", "broadcast_sent=" + sentCount + " payload=" + json);
        }
    }

    long sentCount() { return sentCount; }

    void stop() {
        active = false;
        Log.i("ProUnboundOneMap", "broadcast_stopped count=" + sentCount);
        // No synthetic invalid fix on stop: simply cease sending new samples.
    }
}
