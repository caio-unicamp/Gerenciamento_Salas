package com.feec.reservas.dao;

import com.feec.reservas.exception.ReservaConflitanteException;
import com.feec.reservas.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para realizar todas as operações de persistência relacionadas à entidade Reserva.
 * Esta classe é responsável por traduzir objetos Reserva para registros no banco de dados e vice-versa.
 */
public class ReservaDAO {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Insere uma nova reserva no banco de dados.
     * Antes de inserir, verifica se já existe uma reserva APROVADA para o mesmo item e horário.
     * @param reserva O objeto Reserva a ser persistido.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     * @throws ReservaConflitanteException Se o horário para o item já estiver ocupado.
     */
    public void criar(Reserva reserva) throws SQLException, ReservaConflitanteException {
        // 1. Verificar se há conflito de horário para o item desejado
        String sqlCheck = "SELECT COUNT(id) FROM reservas WHERE item_id = ? AND status = 'APROVADA' AND data_hora_fim > ? AND data_hora_inicio < ?";

        try (Connection conn = ConexaoSQLite.getConexao();
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
            
            pstmtCheck.setInt(1, reserva.getItem().getId());
            pstmtCheck.setString(2, reserva.getDataHoraInicio().format(dtf));
            pstmtCheck.setString(3, reserva.getDataHoraFim().format(dtf));

            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new ReservaConflitanteException("Este item já possui uma reserva aprovada para o horário solicitado.");
                }
            }
        }

        // 2. Se não houver conflito, inserir a nova reserva
        String sqlInsert = "INSERT INTO reservas(usuario_id, item_id, data_hora_inicio, data_hora_fim, status) VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoSQLite.getConexao();
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmtInsert.setInt(1, reserva.getSolicitante().getId());
            pstmtInsert.setInt(2, reserva.getItem().getId());
            pstmtInsert.setString(3, reserva.getDataHoraInicio().format(dtf));
            pstmtInsert.setString(4, reserva.getDataHoraFim().format(dtf));
            pstmtInsert.setString(5, reserva.getStatus().name()); // Salva o nome do Enum (PENDENTE, APROVADA, etc.)
            
            pstmtInsert.executeUpdate();
            
            // Opcional: Recuperar o ID gerado e atribuir ao objeto
            try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Atualiza o status de uma reserva existente no banco de dados.
     * @param reserva O objeto Reserva com o ID e o novo status.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public void atualizarStatus(Reserva reserva) throws SQLException {
        String sql = "UPDATE reservas SET status = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reserva.getStatus().name());
            pstmt.setInt(2, reserva.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca todas as reservas cadastradas no sistema.
     * @return Uma lista de objetos Reserva.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public List<Reserva> listarTodas() throws SQLException {
        String sql = "SELECT r.id as res_id, r.data_hora_inicio, r.data_hora_fim, r.status, " +
                     "u.id as user_id, u.nome as user_nome, u.email as user_email, u.matricula, " +
                     "i.id as item_id, i.nome as item_nome, i.tipo_item, i.capacidade, i.localizacao, i.modelo, i.numero_de_serie " +
                     "FROM reservas r " +
                     "JOIN usuarios u ON r.usuario_id = u.id " +
                     "JOIN itens_reservaveis i ON r.item_id = i.id " +
                     "ORDER BY r.data_hora_inicio DESC";
        
        return buscarReservas(sql, null);
    }
    
    /**
     * Busca todas as reservas de um aluno específico.
     * @param alunoId O ID do aluno.
     * @return Uma lista de objetos Reserva.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public List<Reserva> listarPorAluno(int alunoId) throws SQLException {
        String sql = "SELECT r.id as res_id, r.data_hora_inicio, r.data_hora_fim, r.status, " +
                     "u.id as user_id, u.nome as user_nome, u.email as user_email, u.matricula, " +
                     "i.id as item_id, i.nome as item_nome, i.tipo_item, i.capacidade, i.localizacao, i.modelo, i.numero_de_serie " +
                     "FROM reservas r " +
                     "JOIN usuarios u ON r.usuario_id = u.id " +
                     "JOIN itens_reservaveis i ON r.item_id = i.id " +
                     "WHERE r.usuario_id = ? " +
                     "ORDER BY r.data_hora_inicio DESC";

        return buscarReservas(sql, alunoId);
    }

    /**
     * Método auxiliar privado para executar queries de busca de reservas e mapear o resultado.
     * Evita duplicação de código entre `listarTodas` e `listarPorAluno`.
     * @param sql A query SQL a ser executada.
     * @param idParam O parâmetro de ID (ex: alunoId) a ser usado na query, ou null se não houver.
     * @return Uma lista de objetos Reserva.
     * @throws SQLException
     */
    private List<Reserva> buscarReservas(String sql, Integer idParam) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();

        try (Connection conn = ConexaoSQLite.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (idParam != null) {
                pstmt.setInt(1, idParam);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Mapeamento do Aluno
                    Aluno aluno = new Aluno(
                            rs.getString("user_nome"),
                            rs.getString("user_email"),
                            "********",
                            rs.getString("matricula")
                    );
                    aluno.setId(rs.getInt("user_id"));

                    // Mapeamento polimórfico do ItemReservavel
                    ItemReservavel item;
                    String tipoItem = rs.getString("tipo_item");
                    if ("SALA".equalsIgnoreCase(tipoItem)) {
                        item = new Sala(
                                rs.getString("item_nome"),
                                rs.getInt("capacidade"),
                                rs.getString("localizacao")
                        );
                    } else {
                        item = new Equipamento(
                                rs.getString("item_nome"),
                                rs.getString("modelo"),
                                rs.getString("numero_de_serie")
                        );
                    }
                    item.setId(rs.getInt("item_id"));
                    
                    // Mapeamento da Reserva
                    Reserva reserva = new Reserva(
                            rs.getInt("res_id"),
                            aluno,
                            item,
                            LocalDateTime.parse(rs.getString("data_hora_inicio"), dtf),
                            LocalDateTime.parse(rs.getString("data_hora_fim"), dtf),
                            StatusReserva.valueOf(rs.getString("status"))
                    );
                    reservas.add(reserva);
                }
            }
        }
        return reservas;
    }

    /**
     * Cria dados de exemplo para teste.
     */
    public void criarDadosIniciaisSeNecessario() throws SQLException {
        String sqlCount = "SELECT COUNT(id) FROM reservas";
        String sqlInsert = "INSERT INTO reservas(usuario_id, item_id, data_hora_inicio, data_hora_fim, status) VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoSQLite.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCount)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Banco de reservas vazio. Criando dados iniciais...");
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    // Reserva Pendente do Aluno (ID 1) para o Item (ID 2)
                    pstmt.setInt(1, 1); // ID do Aluno "Carlos Andrade"
                    pstmt.setInt(2, 2); // ID do Equipamento "Projetor Epson"
                    pstmt.setString(3, LocalDateTime.now().plusDays(3).withHour(14).withMinute(0).format(dtf));
                    pstmt.setString(4, LocalDateTime.now().plusDays(3).withHour(16).withMinute(0).format(dtf));
                    pstmt.setString(5, StatusReserva.PENDENTE.name());
                    pstmt.executeUpdate();

                    // Reserva Aprovada do Aluno (ID 1) para o Item (ID 1)
                    pstmt.setInt(1, 1); // ID do Aluno "Carlos Andrade"
                    pstmt.setInt(2, 1); // ID da Sala "Sala de Reuniões 101"
                    pstmt.setString(3, LocalDateTime.now().plusDays(5).withHour(10).withMinute(0).format(dtf));
                    pstmt.setString(4, LocalDateTime.now().plusDays(5).withHour(12).withMinute(0).format(dtf));
                    pstmt.setString(5, StatusReserva.APROVADA.name());
                    pstmt.executeUpdate();
                }
            }
        }
    }
}