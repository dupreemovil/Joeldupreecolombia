package com.dupreinca.dupree.mh_required_api;

public class RequiredSms {

    public String nume_iden;

    public String codigo;

    public RequiredSms(String nume_iden, String codigo) {
        this.nume_iden = nume_iden;
        this.codigo = codigo;
    }

    public String getNume_iden() {
        return nume_iden;
    }

    public void setNume_iden(String nume_iden) {
        this.nume_iden = nume_iden;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }







}
