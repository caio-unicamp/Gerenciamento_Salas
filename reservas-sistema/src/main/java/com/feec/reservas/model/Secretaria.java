package com.feec.reservas.model;

public class Secretaria extends Usuario {
    private String setor;

    public Secretaria(String nome, String email, String senha, String setor) {
        super(nome, email, senha);
        this.setor = setor;
    }

    public String getSetor() {
        return setor;
    }

    /**
     * Implementação do método abstrato para a classe Secretaria.
     */
    @Override
    public String getTipoUsuario() {
        return "SECRETARIA";
    }
}