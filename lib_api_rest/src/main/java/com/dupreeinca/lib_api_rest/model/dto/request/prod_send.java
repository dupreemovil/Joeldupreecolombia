package com.dupreeinca.lib_api_rest.model.dto.request;

public class prod_send {

    private int cant_pedi;
    private String codi_vent;

    public prod_send(int cant_pedi, String codi_vent) {
        this.cant_pedi = cant_pedi;
        this.codi_vent = codi_vent;
    }

    public int getCant_pedi() {
        return cant_pedi;
    }

    public void setCant_pedi(int cant_pedi) {
        this.cant_pedi = cant_pedi;
    }

    public String getCodi_vent() {
        return codi_vent;
    }

    public void setCodi_vent(String codi_vent) {
        this.codi_vent = codi_vent;
    }




}
