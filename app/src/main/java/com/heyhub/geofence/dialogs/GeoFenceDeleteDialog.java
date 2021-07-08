package com.heyhub.geofence.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.Marker;
import com.heyhub.geofence.R;
import com.heyhub.geofence.activities.MapsActivity;

public class GeoFenceDeleteDialog extends DialogFragment implements View.OnClickListener {

    private final Context mContext;
    private final Marker mMarker;
    TextView btnYes, btnNo;

    public GeoFenceDeleteDialog(Context context, Marker marker) {
        mContext = context;
        mMarker = marker;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_geofencing_delete, null);
        setCancelable(false);
        initViewsAndSetListeners(view);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private void initViewsAndSetListeners(View view) {
        btnYes = view.findViewById(R.id.btn_yes);
        btnNo = view.findViewById(R.id.btn_no);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                ((MapsActivity) mContext).deleteThisMarker(mMarker);
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
    }
}
