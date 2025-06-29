package com.feec.reservas.model;

public class Sala extends ItemReservavel {
    private int capacidade;
    private String localizacao;

    public Sala(String nome, int capacidade, String localizacao) {
        super(nome);
        this.capacidade = capacidade;
        this.localizacao = localizacao;
    }

    // Implementação específica para Sala (Polimorfismo de Método)
    @Override
    public String getDescricaoDetalhada() {
        return String.format("Sala: %s (Capacidade: %d, Local: %s)", getNome(), capacidade, localizacao);
    }
}
