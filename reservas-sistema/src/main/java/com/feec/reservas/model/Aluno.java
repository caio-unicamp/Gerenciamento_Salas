package com.feec.reservas.model;

public class Aluno extends Usuario {
    private String matricula;

    public Aluno(String nome, String email, String senha, String matricula) {
        super(nome, email, senha); // Chama o construtor da classe mãe
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    /**
     * Implementação do método abstrato para a classe Aluno.
     */
    @Override
    public String getTipoUsuario() {
        return "ALUNO";
    }
}