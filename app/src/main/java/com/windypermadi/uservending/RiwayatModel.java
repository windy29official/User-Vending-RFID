package com.windypermadi.uservending;

public class RiwayatModel {
    private String idpembelian;
    private String nama_produk;
    private String harga;
    private String tgl_pembelian;

    public RiwayatModel(String idpembelian, String nama_produk, String harga, String tgl_pembelian) {
        this.idpembelian = idpembelian;
        this.nama_produk = nama_produk;
        this.harga = harga;
        this.tgl_pembelian = tgl_pembelian;
    }

    public String getIdpembelian() {
        return idpembelian;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public String getHarga() {
        return harga;
    }

    public String getTgl_pembelian() {
        return tgl_pembelian;
    }
}
