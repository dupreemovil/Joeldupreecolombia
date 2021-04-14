package com.dupreeinca.lib_api_rest.model.dto.request;

import java.util.List;

public class ConcursoSend {


    private String codi_camp;
    private String codi_usua;
    private String nume_iden;
    private List<prod_send> productos;

    public ConcursoSend(String codi_camp, String codi_usua, String nume_iden, List<prod_send> productos) {
        this.codi_camp = codi_camp;
        this.codi_usua = codi_usua;
        this.nume_iden = nume_iden;
        this.productos = productos;
    }

    public String getCodi_camp() {
        return codi_camp;
    }

    public void setCodi_camp(String codi_camp) {
        this.codi_camp = codi_camp;
    }

    public String getCodi_usua() {
        return codi_usua;
    }

    public void setCodi_usua(String codi_usua) {
        this.codi_usua = codi_usua;
    }

    public String getNume_iden() {
        return nume_iden;
    }

    public void setNume_iden(String nume_iden) {
        this.nume_iden = nume_iden;
    }

    public List<prod_send> getProductos() {
        return productos;
    }

    public void setProductos(List<prod_send> productos) {
        this.productos = productos;
    }




}
