package com.feec.reservas.dao;

import com.feec.reservas.model.Equipamento;
import com.feec.reservas.model.ItemReservavel;
import com.feec.reservas.model.Sala;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com Itens Reserváveis (Salas e Equipamentos).
 */
public class ItemReservavelDAO {

    /**
     * Lista todos os itens (salas e equipamentos) do banco.
     * @return Uma lista polimórfica de ItemReservavel.
     */
    public List<ItemReservavel> listarTodos() throws SQLException {
        List<ItemReservavel> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens_reservaveis";

        try (Connection conn = ConexaoSQLite.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String tipo = rs.getString("tipo_item");
                ItemReservavel item = null;

                if ("SALA".equalsIgnoreCase(tipo)) {
                    item = new Sala(
                            rs.getString("nome"),
                            rs.getInt("capacidade"),
                            rs.getString("localizacao")
                    );
                } else if ("EQUIPAMENTO".equalsIgnoreCase(tipo)) {
                    item = new Equipamento(
                            rs.getString("nome"),
                            rs.getString("modelo"),
                            rs.getString("numero_de_serie")
                    );
                }
                
                if (item != null) {
                    item.setId(rs.getInt("id"));
                    itens.add(item);
                }
            }
        }
        return itens;
    }

    /**
     * Cria dados de exemplo para teste.
     */
    public void criarDadosIniciaisSeNecessario() throws SQLException {
        String sqlCount = "SELECT COUNT(id) FROM itens_reservaveis";
        String sqlInsert = "INSERT INTO itens_reservaveis(nome, tipo_item, capacidade, localizacao, modelo, numero_de_serie) VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoSQLite.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCount)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Banco de itens vazio. Criando dados iniciais...");
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    // Inserindo uma Sala
                    pstmt.setString(1, "Sala de Reuniões 101");
                    pstmt.setString(2, "SALA");
                    pstmt.setInt(3, 10);
                    pstmt.setString(4, "Bloco A");
                    pstmt.setString(5, null);
                    pstmt.setString(6, null);
                    pstmt.executeUpdate();

                    // Inserindo um Equipamento
                    pstmt.setString(1, "Projetor Epson PowerLite");
                    pstmt.setString(2, "EQUIPAMENTO");
                    pstmt.setNull(3, Types.INTEGER);
                    pstmt.setString(4, null);
                    pstmt.setString(5, "X39");
                    pstmt.setString(6, "SN1298457");
                    pstmt.executeUpdate();
                }
            }
        }
    }
}