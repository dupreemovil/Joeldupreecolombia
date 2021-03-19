package com.dupreeinca.lib_api_rest.model.dto.response.realm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Madrugon extends RealmObject {


    @PrimaryKey
    private String codi_vent;//se usa en la db
    private String campana;
    private String name;
    private String descr;
    private String url_img;
    private String tipo_oferta;
    private String orde_prom;
    private String prec_vent;
    private String codi_prod;
    private RealmList<talla> tallas;


    private long time=0;//para control de fecha de item, agregado (o usar incremento)

    private int cantidad=0;

    private String talla="";


    private String tallasel="";



    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }


    public String getTallasel() {
        return tallasel;
    }

    public void setTallasel(String tallasel) {
        this.tallasel = tallasel;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCodi_prod() {
        return codi_prod;
    }

    public void setCodi_prod(String codi_prod) {
        this.codi_prod = codi_prod;
    }


    public RealmList<talla> getTallas() {
        return tallas;
    }

    public void setTallas(RealmList<talla> tallas) {
        this.tallas = tallas;
    }



    private int cantidadServer=0;//indica cuanto se envio al server



    public String getCodi_vent() {
        return codi_vent;
    }

    public void setCodi_vent(String codi_vent) {
        this.codi_vent = codi_vent;
    }

    public String getCampana() {
        return campana;
    }

    public void setCampana(String campana) {
        this.campana = campana;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }

    public String getTipo_oferta() {
        return tipo_oferta;
    }

    public void setTipo_oferta(String tipo_oferta) {
        this.tipo_oferta = tipo_oferta;
    }

    public String getOrde_prom() {
        return orde_prom;
    }

    public void setOrde_prom(String orde_prom) {
        this.orde_prom = orde_prom;
    }


    public int getCantidadServer() {
        return cantidadServer;
    }


    public String getPrec_vent() {
        return prec_vent;
    }

    public void setPrec_vent(String prec_vent) {
        this.prec_vent = prec_vent;
    }


    public void setCantidadServer(int cantidadServer) {
        this.cantidadServer = cantidadServer;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * para evitar conflictos de realm con retrofit
     * new Gson().toJson(realm.copyFromRealm(managedModel));
     * @param realm
     * @return
     */
    public Madrugon toUnmanaged(Realm realm) {
        return isManaged() ? realm.copyFromRealm(this) : this;
    }
}
