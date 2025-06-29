package com.feec.reservas.dao;

import com.feec.reservas.model.Aluno;
import com.feec.reservas.model.Secretaria;
import com.feec.reservas.model.Usuario;

import java.sql.*;

/**
 * DAO para operações relacionadas a Usuários (Alunos e Secretarias).
 */
public class UsuarioDAO {

    /**
     * Autentica um usuário com base no email e senha.
     * @return Um objeto Aluno ou Secretaria se as credenciais forem válidas, caso contrário, null.
     */
    public Usuario autenticar(String email, String senha) throws SQLException {
        // Em um sistema real, a senha deve ser comparada com um HASH, não em texto plano!
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try (Connection conn = ConexaoSQLite.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Se encontrou, cria o objeto correspondente (Aluno ou Secretaria)
                    String tipo = rs.getString("tipo_usuario");
                    if ("ALUNO".equalsIgnoreCase(tipo)) {
                        Aluno aluno = new Aluno(
                                rs.getString("nome"),
                                rs.getString("email"),
                                "********", // Não retorne a senha
                                rs.getString("matricula")
                        );
                        aluno.setId(rs.getInt("id"));
                        return aluno;
                    } else if ("SECRETARIA".equalsIgnoreCase(tipo)) {
                        Secretaria secretaria = new Secretaria(
                                rs.getString("nome"),
                                rs.getString("email"),
                                "********", // Não retorne a senha
                                rs.getString("setor")
                        );
                        secretaria.setId(rs.getInt("id"));
                        return secretaria;
                    }
                }
            }
        }
        return null; // Retorna null se não encontrar o usuário ou a senha estiver incorreta
    }

    /**
     * Cria alguns dados de exemplo no banco se ele estiver vazio.
     * Útil para poder testar a aplicação pela primeira vez.
     */
    public void criarDadosIniciaisSeNecessario() throws SQLException {
        String sqlCount = "SELECT COUNT(id) FROM usuarios";
        String sqlInsert = "INSERT INTO usuarios(nome, email, senha, tipo_usuario, matricula, setor) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCount)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Banco de usuários vazio. Criando dados iniciais...");
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    // Inserindo um Aluno
                    pstmt.setString(1, "Carlos Andrade");
                    pstmt.setString(2, "carlos@aluno.com");
                    pstmt.setString(3, "123"); // SENHA EM TEXTO PURO - APENAS PARA EXEMPLO
                    pstmt.setString(4, "ALUNO");
                    pstmt.setString(5, "RA123456");
                    pstmt.setString(6, null);
                    pstmt.executeUpdate();

                    // Inserindo uma Secretária
                    pstmt.setString(1, "Ana Paula");
                    pstmt.setString(2, "ana@secretaria.com");
                    pstmt.setString(3, "admin"); // SENHA EM TEXTO PURO - APENAS PARA EXEMPLO
                    pstmt.setString(4, "SECRETARIA");
                    pstmt.setString(5, null);
                    pstmt.setString(6, "Coordenação");
                    pstmt.executeUpdate();
                }
            }
        }
    }
}