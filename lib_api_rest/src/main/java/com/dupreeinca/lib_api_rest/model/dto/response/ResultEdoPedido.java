package com.dupreeinca.lib_api_rest.model.dto.response;

/**
 * Created by cloudemotion on 12/9/17.
 */

public class ResultEdoPedido {
    private String id_pedido;
    private ListaProductos productos;
    private ListaOfertas ofertas;


    private ListaMadrugon madrugon;
    private String puntos;
    private String precio_total;
    private String estado_pedido;
    private String asesora;
    private String campana;


    public String getId_pedido() {
        return id_pedido;
    }

    public ListaProductos getProductos() {
        return productos;
    }

    public ListaOfertas getOfertas() {
        return ofertas;
    }

    public String getPuntos() {
        return puntos;
    }

    public String getPrecio_total() {
        return precio_total;
    }

    public String getEstado_pedido() {
        return estado_pedido;
    }

    public String getAsesora() {
        return asesora;
    }

    public String getCampana() {
        return campana;
    }

    public ListaMadrugon getMadrugon() {
        return madrugon;
    }

}
