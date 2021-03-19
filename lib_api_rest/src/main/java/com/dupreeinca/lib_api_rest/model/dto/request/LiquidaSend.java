package com.dupreeinca.lib_api_rest.model.dto.request;

import java.util.List;

public class LiquidaSend {




    private String id_pedido;
    private List<String> paquetones;
    private List<LiquidarMadrugon> madrugon;


    private List<LiquidarProducto> productos;

    private List<LiquidarProducto> ofertas;
    private String cana_envi;
    private String cons_terc;

    public LiquidaSend(String id_pedido, List<LiquidarProducto> productos,List<LiquidarMadrugon> madrugon, List<LiquidarProducto> ofertas,String cana_envi,String cons_terc) {
        this.id_pedido = id_pedido;
        this.madrugon = madrugon;
        this.productos = productos;
        this.ofertas = ofertas;
        this.cana_envi = cana_envi;
        this.cons_terc = cons_terc;
    }

    public List<LiquidarProducto> getProductos() {
        return productos;
    }

    public List<LiquidarProducto> getOfertas() {
        return ofertas;
    }

    public List<LiquidarMadrugon> getMadrugon() {
        return madrugon;
    }

    public void setMadrugon(List<LiquidarMadrugon> madrugon) {
        this.madrugon = madrugon;
    }

    public String getCana_envi() {
        return cana_envi;
    }

    public void setCana_envi(String cana_envi) {
        this.cana_envi = cana_envi;
    }

    public String getCons_terc() {
        return cons_terc;
    }

    public void setCons_terc(String cons_terc) {
        this.cons_terc = cons_terc;
    }


    public void setPaquetones(List<String> paquetones) {
        this.paquetones = paquetones;
    }

    public void setProductos(List<LiquidarProducto> productos) {
        this.productos = productos;
    }

    public void setOfertas(List<LiquidarProducto> ofertas) {
        this.ofertas = ofertas;
    }

    public String getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
    }
}
