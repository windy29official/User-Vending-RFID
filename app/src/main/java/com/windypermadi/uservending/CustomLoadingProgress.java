package com.windypermadi.uservending;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class CustomLoadingProgress {
    public static void errorDialog(final Context context, final String alertText) {
        final View inflater = LayoutInflater.from(context).inflate(R.layout.model_popup_error_pengambilan_data, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}


