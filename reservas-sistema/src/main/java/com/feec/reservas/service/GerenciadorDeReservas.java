package com.feec.reservas.service;

import com.feec.reservas.dao.ReservaDAO; // Supondo que você criará este DAO
import com.feec.reservas.exception.ReservaConflitanteException;
import com.feec.reservas.model.Aluno;
import com.feec.reservas.model.ItemReservavel;
import com.feec.reservas.model.Reserva;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Camada de serviço para orquestrar as operações de reserva.
 * Contém a lógica de negócio, como validações complexas.
 */
public class GerenciadorDeReservas {

    // Em um sistema real, o ReservaDAO seria injetado (Injeção de Dependência)
    private final ReservaDAO reservaDAO;

    public GerenciadorDeReservas() {
        this.reservaDAO = new ReservaDAO(); // Instanciando diretamente para simplificar
    }

    /**
     * Lógica para um aluno solicitar uma nova reserva.
     * Aqui poderiam entrar regras de negócio, como "aluno só pode ter X reservas pendentes".
     */
    public void solicitarReserva(Aluno aluno, ItemReservavel item, LocalDateTime dataInicio) throws SQLException, ReservaConflitanteException {
        // A data final pode ser calculada com base em regras (ex: reserva dura 2 horas)
        LocalDateTime dataFim = dataInicio.plusHours(2);

        Reserva novaReserva = new Reserva(aluno, item, dataInicio, dataFim);

        // A validação de conflito foi delegada ao DAO, mas poderia estar aqui também.
        reservaDAO.criar(novaReserva);
    }

    /**
     * Aprova uma reserva existente.
     */
    public void aprovarReserva(Reserva reserva) throws SQLException {
        reserva.aprovar();
        reservaDAO.atualizarStatus(reserva);
        // Futuramente: Enviar notificação por email para o aluno.
    }

    /**
     * Nega uma reserva existente.
     */
    public void negarReserva(Reserva reserva) throws SQLException {
        reserva.negar();
        reservaDAO.atualizarStatus(reserva);
        // Futuramente: Enviar notificação por email para o aluno.
    }

    public List<Reserva> listarTodasAsReservas() throws SQLException {
        return reservaDAO.listarTodas();
    }

    public List<Reserva> listarReservasPorAluno(int alunoId) throws SQLException {
        return reservaDAO.listarPorAluno(alunoId);
    }
}