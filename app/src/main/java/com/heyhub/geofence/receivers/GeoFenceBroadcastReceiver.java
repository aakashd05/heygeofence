package com.heyhub.geofence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.WorkManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.heyhub.geofence.R;
import com.heyhub.geofence.activities.MapsActivity;
import com.heyhub.geofence.utils.Constants;
import com.heyhub.geofence.utils.NotificationHelper;

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }
        int transitionType = geofencingEvent.getGeofenceTransition();
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                notificationHelper.sendHighPriorityNotification(Constants.GEOFENCE_NOTIFICATION_TITLE, context.getString(R.string.geofence_enter), MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                notificationHelper.sendHighPriorityNotification(Constants.GEOFENCE_NOTIFICATION_TITLE, context.getString(R.string.geofence_dwell), MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                notificationHelper.sendHighPriorityNotification(Constants.GEOFENCE_NOTIFICATION_TITLE, context.getString(R.string.geofence_exit), MapsActivity.class);
                break;
        }
    }
}
