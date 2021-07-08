package com.heyhub.geofence.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.heyhub.geofence.models.FenceModel;
import com.heyhub.geofence.dialogs.GeoFenceDeleteDialog;
import com.heyhub.geofence.dialogs.GeoFenceDetailDialog;
import com.heyhub.geofence.R;
import com.heyhub.geofence.utils.GeoFenceHelper;
import com.heyhub.geofence.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {


    private String GEOFENCE_ID = "GEOFENCE_ID_";
    private static final int FINE_LOCATION_REQUEST_CODE = 100;
    private static final int BACKGROUND_LOCATION_REQUEST_CODE = 101;
    private GoogleMap mMap;
    GeofencingClient geoFencingClient;
    GeoFenceHelper fenceHelper;

    String[] permissionsRequired={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoFencingClient = LocationServices.getGeofencingClient(this);
        fenceHelper = new GeoFenceHelper(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkPermissions();
        addDefaultGeoFences();
        populateGeoFences();
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }
    }

    private void addDefaultGeoFences() {
        updateFenceModelList(new LatLng(51.511895, -0.117569), "London", 3220);
        updateFenceModelList(new LatLng(19.185490, 72.978910), "Thane", 500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if ((ContextCompat.checkSelfPermission(this, permissionsRequired[0])) ==
                        PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
        if (requestCode == BACKGROUND_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if ((ContextCompat.checkSelfPermission(this, permissionsRequired[1]) ==
                        PackageManager.PERMISSION_GRANTED)) {
                    addDefaultGeoFences();
                    populateGeoFences();
                }
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        checkPermissionForBackgroundLocation(latLng);
    }

    private void checkPermissionForBackgroundLocation(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) ==
                    PackageManager.PERMISSION_GRANTED) {
                openGeoFenceDetailDialog(latLng);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_REQUEST_CODE);
            }
        }
    }

    public void addFenceOnMap(LatLng latLng, String fenceName, int fenceRadius) {
        addGeoFence(latLng, fenceRadius);
        updateFenceModelList(latLng, fenceName, fenceRadius);
        addCustomMarker(latLng, fenceName);
        addMarkerCircle(latLng, fenceRadius);
    }

    private void updateFenceModelList(LatLng latLng, String fenceName, int fenceRadius) {
        FenceModel fenceModel = new FenceModel();
        fenceModel.setFenceName(fenceName);
        fenceModel.setRadius(fenceRadius);
        fenceModel.setLat(latLng.latitude);
        fenceModel.setLng(latLng.longitude);
        ArrayList<FenceModel> fenceModelsList = SharedPrefs.fetchFenceList(this);
        if (!fenceModelsList.contains(fenceModel)) {
            fenceModelsList.add(fenceModel);
            SharedPrefs.storeFenceList(this, fenceModelsList);
        }
    }

    private void openGeoFenceDetailDialog(LatLng latLng) {
        GeoFenceDetailDialog detailDialog = new GeoFenceDetailDialog(latLng, this);
        detailDialog.show(getSupportFragmentManager(), "Detail Dialog");
    }

    private void addMarkerCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng).radius(radius)
                .strokeColor(R.color.fence_blue)
                .fillColor(R.color.text_grey)
                .strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void addCustomMarker(LatLng latLng, String fenceName) {
        MarkerOptions mOptions = new MarkerOptions()
                .position(latLng).title(fenceName)
                .title(fenceName);
        Marker marker = mMap.addMarker(mOptions);
        marker.showInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GeoFenceDeleteDialog geoFenceDeleteDialog = new GeoFenceDeleteDialog(this, marker);
        geoFenceDeleteDialog.show(getSupportFragmentManager(), "Delete Dialog");
        return true;
    }

    public void populateGeoFences() {
        ArrayList<FenceModel> fenceModelsList = SharedPrefs.fetchFenceList(this);
        if (fenceModelsList.size() > 0) {
            for (FenceModel fenceModel : fenceModelsList) {
                LatLng fenceLatLng = new LatLng(fenceModel.getLat(), fenceModel.getLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fenceLatLng, 12));
                addGeoFence(fenceLatLng, fenceModel.getRadius());
                addCustomMarker(fenceLatLng, fenceModel.getFenceName());
                addMarkerCircle(fenceLatLng, fenceModel.getRadius());
            }
        } else {
            LatLng fenceLatLng = new LatLng(19.197462471234996, 72.84713995883112);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fenceLatLng, 16));
        }
    }

    public void deleteThisMarker(Marker mMarker) {
        mMap.clear();
        ArrayList<FenceModel> fenceModelsList = SharedPrefs.fetchFenceList(this);
        Iterator<FenceModel> fenceListIterator = fenceModelsList.iterator();
        while (fenceListIterator.hasNext()) {
            FenceModel fenceModel = fenceListIterator.next();
            LatLng fenceLatLng = new LatLng(fenceModel.getLat(), fenceModel.getLng());
            if (fenceLatLng.equals(mMarker.getPosition())) {
                fenceListIterator.remove();
            } else {
                addMarkerCircle(fenceLatLng, fenceModel.getRadius());
                addCustomMarker(fenceLatLng, fenceModel.getFenceName());
            }
        }
        SharedPrefs.storeFenceList(this, fenceModelsList);
    }

    @SuppressLint("MissingPermission")
    private void addGeoFence(LatLng latLng, float radius) {
        Geofence geofence = fenceHelper.getGeoFence(GEOFENCE_ID + new Random().nextInt(), latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = fenceHelper.getGeoFencingRequest(geofence);
        PendingIntent pendingIntent = fenceHelper.getPendingIntent();
        geoFencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Fence Added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
//                    String errorMessage = fenceHelper.getErrorString(e);
//                    Toast.makeText(this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                });
    }
}