package com.heyhub.geofence.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.heyhub.geofence.R;
import com.heyhub.geofence.activities.MapsActivity;

public class GeoFenceDetailDialog extends DialogFragment implements View.OnClickListener {


    private EditText fenceNameEt, fenceRadiusEt;
    private TextView submitBtn;
    private ImageView closeBtn;

    private Context mContext;
    private LatLng mLatLng;

    public GeoFenceDetailDialog(LatLng latLng, Context context) {
        mContext = context;
        mLatLng = latLng;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_geofencing_detail, null);
        setCancelable(false);
        initViewsAndSetListeners(view);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private void initViewsAndSetListeners(View view) {
        fenceNameEt = view.findViewById(R.id.et_fence_name);
        fenceRadiusEt = view.findViewById(R.id.et_fence_radius);
        submitBtn = view.findViewById(R.id.submit_btn);
        closeBtn = view.findViewById(R.id.close_details);
        closeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_details:
                dismiss();
                break;
            case R.id.submit_btn:
                if (!fenceRadiusEt.getText().toString().isEmpty() && !fenceNameEt.getText().toString().isEmpty()) {
                    dismiss();
                    ((MapsActivity) mContext).addFenceOnMap(mLatLng, fenceNameEt.getText().toString(), Integer.parseInt(fenceRadiusEt.getText().toString()));
                } else {
                    Toast.makeText(mContext, getString(R.string.enter_proper_details), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
