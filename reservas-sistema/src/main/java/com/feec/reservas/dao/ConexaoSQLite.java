package com.feec.reservas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoSQLite {

    // NÃO usaremos mais uma variável estática para a conexão.
    // private static Connection conexao;

    /**
     * Este método agora SEMPRE cria e retorna uma NOVA conexão com o banco.
     * O try-with-resources em cada DAO será responsável por fechá-la.
     * @return Uma nova conexão com o banco de dados.
     * @throws SQLException se a conexão falhar.
     */
    public static Connection getConexao() throws SQLException {
        // A lógica de verificação foi removida.
        // O método agora é mais simples e direto.
        String url = "jdbc:sqlite:reservas.db";
        Connection conn = DriverManager.getConnection(url);

        // A criação das tabelas pode ser feita na primeira conexão.
        // Como o arquivo .db persiste, isso só executará uma vez.
        // Uma abordagem alternativa seria verificar a existência do arquivo.
        criarTabelasIniciais(conn);

        return conn;
    }
    
    // Este método permanece o mesmo.
    private static void criarTabelasIniciais(Connection conn) throws SQLException {
        // SQL para criar as tabelas
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    nome TEXT NOT NULL,\n" +
                        "    email TEXT NOT NULL UNIQUE,\n" +
                        "    senha TEXT NOT NULL,\n" +
                        "    tipo_usuario TEXT NOT NULL,\n" +
                        "    matricula TEXT, \n" +
                        "    setor TEXT\n" +
                        ");";

        String sqlItens = "CREATE TABLE IF NOT EXISTS itens_reservaveis (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    nome TEXT NOT NULL,\n" +
                        "    tipo_item TEXT NOT NULL,\n" +
                        "    capacidade INTEGER,\n" +
                        "    localizacao TEXT,\n" +
                        "    modelo TEXT,\n" +
                        "    numero_de_serie TEXT\n" +
                        ");";

        String sqlReservas = "CREATE TABLE IF NOT EXISTS reservas (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    usuario_id INTEGER NOT NULL,\n" +
                        "    item_id INTEGER NOT NULL,\n" +
                        "    data_hora_inicio TEXT NOT NULL,\n" +
                        "    data_hora_fim TEXT NOT NULL,\n" +
                        "    status TEXT NOT NULL,\n" +
                        "    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),\n" +
                        "    FOREIGN KEY (item_id) REFERENCES itens_reservaveis(id)\n" +
                        ");";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlItens);
            stmt.execute(sqlReservas);
        }
    }
}