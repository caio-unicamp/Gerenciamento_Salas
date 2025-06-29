package com.feec.reservas.model;

/**
 * Classe abstrata que serve como base para Aluno e Secretaria.
 * Define os atributos e comportamentos comuns a todos os usuários.
 */
public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String email;
    protected String senha; // Em um projeto real, armazene um hash da senha.

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // --- MÉTODOS ABSTRATOS E GETTERS/SETTERS ATUALIZADOS ---

    /**
     * Método abstrato que força as classes filhas a implementarem e retornarem seu tipo.
     * Essencial para a lógica no MainController.
     * @return Uma String representando o tipo de usuário (ex: "ALUNO", "SECRETARIA").
     */
    public abstract String getTipoUsuario();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}