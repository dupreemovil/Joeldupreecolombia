package com.dupreeinca.lib_api_rest.model.dto.response.realm;

public class ItemCarritoM {


    private String id;
    private String name;
    private String valor;
    private String valor_descuento;//solo ofertas
    private String url_img;
    private int cantidad;

    //control


    private long time=0;//para control de fecha de item, agregado (o usar incremento), solo cataloog
    private int type=0;

    public String tallasel;

    public ItemCarritoM(MadCarrito item){
        setId(item.getCodi_vent());
        setName(item.getName());
        setValor(item.getPrec_vent());
        setValor_descuento(item.getPrec_vent());
        setUrl_img(item.getUrl_img());
        setCantidad(item.getCantidad());
        setTallasel(item.getTallasel());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValor_descuento() {
        return valor_descuento;
    }

    public void setValor_descuento(String valor_descuento) {
        this.valor_descuento = valor_descuento;
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }


    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTallasel() {
        return tallasel;
    }

    public void setTallasel(String tallasel) {
        this.tallasel = tallasel;
    }

}
