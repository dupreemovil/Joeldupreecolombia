package com.dupreeinca.lib_api_rest.model.dto.response;

import java.util.List;

public class LiquidaDTO {


    private String status;
    private boolean valid;
    private String result;
    private String mensaje;
    private String total_pedido;
    private String codigo;
    private int code;
    private String mensaje_premio;
    private String activa_premio;

    private List<RaiseDTO> raise;
    public LiquidaDTO() {
    }
    public LiquidaDTO(String codigo) {
        this.codigo = codigo;
    }

    public LiquidaDTO(String mensaje, String total_pedido, String codigo) {
        this.mensaje = mensaje;
        this.total_pedido = total_pedido;
        this.codigo = codigo;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTotal_pedido() {
        return total_pedido;
    }

    public void setTotal_pedido(String total_pedido) {
        this.total_pedido = total_pedido;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMensaje_premio() {
        return mensaje_premio;
    }

    public void setMensaje_premio(String mensaje_premio) {
        this.mensaje_premio = mensaje_premio;
    }

    public String getActiva_premio() {
        return activa_premio;
    }

    public void setActiva_premio(String activa_premio) {
        this.activa_premio = activa_premio;
    }



    public List<RaiseDTO> getRaise() {
        return raise;
    }

    public void setRaise(List<RaiseDTO> raise) {
        this.raise = raise;
    }


}
