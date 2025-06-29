package com.feec.reservas.model;

// Cumpre o requisito de: Enumerações
public enum StatusReserva {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    NEGADA("Negada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusReserva(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}