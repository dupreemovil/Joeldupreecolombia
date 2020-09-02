package com.dupreinca.dupree.mh_required_api;

/**
 * Created by cloudemotion on 27/8/17.
 */

public class RequiredAuth {
    private String username;
    private String password;
    private String token;

    private String token_campana;

    public RequiredAuth(String username, String password, String token,String token_campana) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.token_campana = token_campana;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getToken_campana() {
        return token_campana;
    }

}
