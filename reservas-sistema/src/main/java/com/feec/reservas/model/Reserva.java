package com.feec.reservas.model;

import java.time.LocalDateTime;

public class Reserva {
    private int id;
    private Aluno solicitante;
    private ItemReservavel item;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusReserva status;

    // Construtor principal
    public Reserva(Aluno solicitante, ItemReservavel item, LocalDateTime inicio, LocalDateTime fim) {
        this.solicitante = solicitante;
        this.item = item;
        this.dataHoraInicio = inicio;
        this.dataHoraFim = fim;
        this.status = StatusReserva.PENDENTE;
    }

    // Construtor sobrecarregado (para criar objetos a partir do DAO)
    public Reserva(int id, Aluno solicitante, ItemReservavel item, LocalDateTime inicio, LocalDateTime fim, StatusReserva status) {
        this(solicitante, item, inicio, fim);
        this.id = id;
        this.status = status;
    }

    // --- GETTERS E SETTERS CORRIGIDOS ---
    // Estes métodos são essenciais para o TableView e para o DAO funcionarem.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Aluno getSolicitante() {
        return solicitante;
    }

    public ItemReservavel getItem() {
        return item;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public StatusReserva getStatus() {
        return status;
    }

    // Métodos para gerenciar a reserva
    public void aprovar() {
        this.status = StatusReserva.APROVADA;
    }

    public void negar() {
        this.status = StatusReserva.NEGADA;
    }
}