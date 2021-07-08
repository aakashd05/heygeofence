package com.heyhub.geofence.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.heyhub.geofence.receivers.GeoFenceBroadcastReceiver;

public class GeoFenceHelper {

    Context mContext;
    int GEO_FENCE_REQUEST = 105;
    private PendingIntent pendingIntent;

    public GeoFenceHelper(Context context) {
        mContext=context;
    }

    public Geofence getGeoFence(String fenceId, LatLng latLng, float fenceRadius, int transitionType) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, fenceRadius)
                .setRequestId(fenceId)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public GeofencingRequest getGeoFencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(mContext, GeoFenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mContext, GEO_FENCE_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }

}
