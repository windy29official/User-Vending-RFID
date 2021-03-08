package com.windypermadi.uservending;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    String nama, hp, saldo, tanggal_daftar;
    public static boolean status_scan = false;
    public static String id_tiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_scan).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Scan.class);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status_scan) {
            InkuiriScan(id_tiket);
        }
    }

    private void InkuiriScan(String kode_tiket) {
        customProgress.showProgress(MainActivity.this, false);
        AndroidNetworking.get(inputJaeingan.ipjaringan + "member.php")
                .addQueryParameter("TAG", "lihat")
                .addQueryParameter("kode_rfid", kode_tiket)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        status_scan = false;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                nama = responses.optString("nama");
                                hp = responses.optString("no_telp");
                                saldo = responses.optString("saldo_sekarang_format");
                                tanggal_daftar = responses.optString("tanggal_daftar");
                                popupBerhasil(nama, hp, saldo, tanggal_daftar);
                            }

                            customProgress.hideProgress();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        status_scan = false;
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomLoadingProgress.errorDialog(MainActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomLoadingProgress.errorDialog(MainActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
//                        if (error.getErrorCode() == 400) {
//                            try {
//                                JSONObject body = new JSONObject(error.getErrorBody());
//                                String kode = body.optString("kode");
//                                if (kode.equals("0")) {
//                                    //tiket tidak terkait
//                                    CustomLoadingProgress.errorDialog(MainActivity.this, body.optString("pesan"));
//                                } else if (kode.equals("1")) {
//                                    //tiket sudah dipake
//                                    CustomLoadingProgress.errorDialog(MainActivity.this, body.optString("pesan"));
//                                } else {
//                                    //2 tiket dibatalkan
//                                    CustomLoadingProgress.errorDialog(MainActivity.this, body.optString("pesan"));
//                                }
//                            } catch (JSONException ignored) {
//                            }
//                        } else {
//                            CustomLoadingProgress.errorDialog(MainActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
//                        }
                        customProgress.hideProgress();
                    }
                });
    }

    private void popupBerhasil(String nama, String hp, String saldo, String tgl) {
        customProgress.hideProgress();
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.model_popup_inkuiri_scan, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView text_nama = dialogView.findViewById(R.id.text_nama);
        TextView text_hp = dialogView.findViewById(R.id.text_hp);
        TextView text_tgl = dialogView.findViewById(R.id.text_tgl);
        TextView text_saldo = dialogView.findViewById(R.id.text_saldo);
        TextView text_riwayat = dialogView.findViewById(R.id.text_riwayat);
        text_nama.setText(nama);
        text_hp.setText(hp);
        text_tgl.setText(tgl);
        text_saldo.setText(saldo);
        dialogView.findViewById(R.id.ok).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        text_riwayat.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, RiwayatTransaksi.class);
            i.putExtra("kode_rfid", id_tiket);
            startActivity(i);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }
}