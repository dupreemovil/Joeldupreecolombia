package com.dupreinca.dupree.model_view;

import java.util.List;

public class Respuesta {

    public String id;
    public List<String> respuestas;

    public Respuesta(String id, List<String> respuestas) {
        this.id = id;
        this.respuestas = respuestas;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }





}
