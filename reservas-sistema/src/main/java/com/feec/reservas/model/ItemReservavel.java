package com.feec.reservas.model;

// Cumpre os requisitos de: Classe Abstrata e prepara para o Polimorfismo.
public abstract class ItemReservavel {
    protected int id;
    protected String nome;
    
    // Variável estática: contador de quantos itens foram instanciados no total.
    // Cumpre o requisito de: Variável Estática.
    private static int totalItensCriados = 0;

    public ItemReservavel(String nome) {
        this.nome = nome;
        totalItensCriados++;
    }

    // Método abstrato que será implementado de forma diferente nas classes filhas (Polimorfismo de Método)
    public abstract String getDescricaoDetalhada();

    // Método estático para acessar a variável estática.
    // Cumpre o requisito de: Método Estático.
    public static int getTotalItensCriados() {
        return totalItensCriados;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
}