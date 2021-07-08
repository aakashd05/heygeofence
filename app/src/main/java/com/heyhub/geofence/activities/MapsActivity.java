package com.heyhub.geofence.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.GeofencingClient;
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
import com.heyhub.geofence.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private static final int FINE_LOCATION_REQUEST_CODE = 100;
    private GoogleMap mMap;
    GeofencingClient geoFencingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoFencingClient = new GeofencingClient(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        populateFences();
        mMap.setOnMapLongClickListener(this);
        checkPermissions();
    }

    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) ==
                        PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        openGeoFenceDetailDialog(latLng);
        mMap.setOnMarkerClickListener(this);
    }

    public void addFenceOnMap(LatLng latLng, String fenceName, int fenceRadius) {
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
        MarkerOptions mOptions = new MarkerOptions().position(latLng).title(fenceName).title(fenceName);
        mMap.addMarker(mOptions);
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        GeoFenceDeleteDialog geoFenceDeleteDialog = new GeoFenceDeleteDialog(this, marker);
        geoFenceDeleteDialog.show(getSupportFragmentManager(), "Delete Dialog");
        return false;
    }

    public void populateFences() {
        ArrayList<FenceModel> fenceModelsList = SharedPrefs.fetchFenceList(this);
        if (fenceModelsList.size() > 0) {
            for (FenceModel fenceModel : fenceModelsList) {
                LatLng fenceLatLng = new LatLng(fenceModel.getLat(), fenceModel.getLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fenceLatLng, 16));
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
            FenceModel fenceModel = (FenceModel) fenceListIterator.next();
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
}