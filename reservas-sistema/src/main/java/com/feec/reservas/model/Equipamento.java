package com.feec.reservas.model;

public class Equipamento extends ItemReservavel {
    private String modelo;
    private String numeroDeSerie;

    public Equipamento(String nome, String modelo, String numeroDeSerie) {
        super(nome);
        this.modelo = modelo;
        this.numeroDeSerie = numeroDeSerie;
    }

    // Implementação específica para Equipamento (Polimorfismo de Método)
    @Override
    public String getDescricaoDetalhada() {
        return String.format("Equipamento: %s (Modelo: %s, S/N: %s)", getNome(), modelo, numeroDeSerie);
    }
}