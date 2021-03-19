package com.dupreeinca.lib_api_rest.model.dto.response;

import java.util.List;

public class ProductMadrugonDTO {
    private String status;
    private boolean valid;
    private ListProductMadrugonDTO result;
    private int code;


    public String getStatus() {
        return status;
    }

    public boolean isValid() {
        return valid;
    }

    public ListProductMadrugonDTO getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }


}