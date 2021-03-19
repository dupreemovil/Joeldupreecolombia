package com.dupreeinca.lib_api_rest.model.dto.response;

import com.dupreeinca.lib_api_rest.model.dto.response.realm.Madrugon;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Oferta;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListProductMadrugonDTO {

    @SerializedName("productos")
    public List<Madrugon> productos;

    public List<Madrugon> getProductos() {
        return productos;
    }




}
