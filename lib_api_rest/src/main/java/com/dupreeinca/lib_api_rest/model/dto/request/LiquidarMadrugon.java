package com.dupreeinca.lib_api_rest.model.dto.request;

public class LiquidarMadrugon {



    private String id;
    private int cantidad;
    private String valor;
    private String talla;

    public LiquidarMadrugon(String id, int cantidad) {
        this.id = id;
        this.cantidad = cantidad;
    }

    public LiquidarMadrugon(String id, int cantidad,String valor,String talla) {
        this.id = id;
        this.cantidad = cantidad;
        this.valor    = valor;
        this.talla = talla;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getValor() {  return valor;    }
}
