package com.windypermadi.uservending;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class inputJaeingan extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    private EditText et_ip;
    String ipjaringan_address;
    static String ipjaringan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_jaeingan);
        et_ip = findViewById(R.id.et_ip);
        findViewById(R.id.text_simpan).setOnClickListener(view -> {
            cekip(et_ip.getText().toString().trim());
        });
    }

    private void cekip(String ip) {
        customProgress.showProgress(this, false);
        AndroidNetworking.post("http://" + ip + "/rfid_finding/api/cekip.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ipjaringan_address = response.optString("ip_address");
                        ipjaringan = "http://" + response.optString("ip_address") + "/rfid_finding/api/";
                        popupBerhasil(response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                popupPeringatan(body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            popupPeringatan("IP Jaringan yang kamu masukkan salah. Mohon periksa lagi.");
                        }
                    }
                });
    }

    private void popupBerhasil(String isi) {
        customProgress.hideProgress();
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(inputJaeingan.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.model_popoup_berhasil, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView keterangan = dialogView.findViewById(R.id.keterangan);
        keterangan.setText(isi);
        dialogView.findViewById(R.id.btnLogin).setOnClickListener(v -> {
            Intent i = new Intent(inputJaeingan.this, MainActivity.class);
            startActivity(i);
            finish();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void popupPeringatan(String isi) {
        customProgress.hideProgress();
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(inputJaeingan.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.model_popup_error_pengambilan_data, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        TextView keterangan = dialogView.findViewById(R.id.keterangan);
        keterangan.setText(isi);
        dialogView.findViewById(R.id.ok).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }
}