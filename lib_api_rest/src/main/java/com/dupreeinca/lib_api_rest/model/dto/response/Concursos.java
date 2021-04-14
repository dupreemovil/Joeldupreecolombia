package com.dupreeinca.lib_api_rest.model.dto.response;

import java.util.List;

public class Concursos {

    private List<TipoConDTO> concursos;

    public List<TipoConDTO> getConcursos() {
        return concursos;
    }

    public void setConcursos(List<TipoConDTO> concursos) {
        this.concursos = concursos;
    }


}
