package com.dupreeinca.lib_api_rest.model.dto.response;

import java.util.List;

public class ListaMadrugon {


    private String precio_total;


    private List<DetalleMadrugon> productos;


    public String getPrecio_total() {
        return precio_total;
    }

    public List<DetalleMadrugon> getProductos() {
        return productos;
    }

}