package com.dupreeinca.lib_api_rest.model.dto.response;

import java.util.List;

public class ConcursosDTO {


    private String status;
    private boolean valid;
    private Concursos result;
    private int num_pedi;
    private int id_pedido;
    private int code;


    public Concursos getResult() {
        return result;
    }

    public void setResult(Concursos result) {
        this.result = result;
    }

    public int getNum_pedi() {
        return num_pedi;
    }

    public void setNum_pedi(int num_pedi) {
        this.num_pedi = num_pedi;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }




}
