package com.windypermadi.uservending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RiwayatTransaksi extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    private RecyclerView rv_produk;
    private ArrayList<RiwayatModel> RiwayatModel;
    private String id_tiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);
        Intent i = getIntent();
        id_tiket = i.getStringExtra("kode_rfid");

        rv_produk = findViewById(R.id.rv_produk);

        RiwayatModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        rv_produk.setHasFixedSize(true);
        rv_produk.setLayoutManager(x);
        rv_produk.setNestedScrollingEnabled(true);

        LoadData();
    }

    private void LoadData() {
        customProgress.showProgress(RiwayatTransaksi.this, false);
        RiwayatModel.clear();
        AndroidNetworking.get(Config.HOST + "riwayat_pembelian.php")
                .addQueryParameter("TAG", "lihat")
                .addQueryParameter("kode_rfid", id_tiket)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                RiwayatModel bk = new RiwayatModel(
                                        responses.getString("idpembelian"),
                                        responses.getString("nama_produk"),
                                        responses.getString("harga_format"),
                                        responses.getString("tgl_pembelian"));
                                RiwayatModel.add(bk);
                            }

                            LaporanAdapter adapter = new LaporanAdapter(getApplicationContext(), RiwayatModel);
                            rv_produk.setAdapter(adapter);

                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomLoadingProgress.errorDialog(RiwayatTransaksi.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomLoadingProgress.errorDialog(RiwayatTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ProductViewHolder> {
        private Context mCtx;
        private List<RiwayatModel> RiwayatModel;

        public LaporanAdapter(Context mCtx, List<RiwayatModel> RiwayatModel) {
            this.mCtx = mCtx;
            this.RiwayatModel = RiwayatModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.row_model_produk, null);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            final RiwayatModel item = RiwayatModel.get(position);
            holder.text_nama.setText(item.getNama_produk());
            holder.text_harga.setText(item.getHarga());
            holder.text_stok.setText(item.getTgl_pembelian());
        }

        @Override
        public int getItemCount() {
            return RiwayatModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_harga, text_stok;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_harga = itemView.findViewById(R.id.text_harga);
                text_stok = itemView.findViewById(R.id.text_stok);
            }
        }
    }
}