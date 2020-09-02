package com.dupreeinca.lib_api_rest.model.dto.response;

import com.dupreeinca.lib_api_rest.model.view.Profile;

import java.util.List;

public class DataVisit{

    private String status;
    private boolean valid;
    private String result;
    private int code;



    public DataVisit(String status, boolean valid, String result, int code) {
        this.status = status;
        this.valid = valid;
        this.result = result;
        this.code = code;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }






}