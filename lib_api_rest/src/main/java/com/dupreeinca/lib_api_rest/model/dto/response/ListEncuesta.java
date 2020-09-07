package com.dupreeinca.lib_api_rest.model.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class ListEncuesta {

        @SerializedName("encuesta")
        List<ItemEncuesta> lista;

        public List<ItemEncuesta> getLista() {
                return lista;
        }

        public void setLista(List<ItemEncuesta> lista) {
                this.lista = lista;
        }

}
