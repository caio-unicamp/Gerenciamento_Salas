package model;

/**
 * Representa um usuário do tipo Administrador no sistema.
 * Administradores podem gerenciar detalhes de salas e possuem permissões especiais.
 */
public class Administrator extends User {

    /**
     * Construtor do administrador.
     *
     * @param username Nome de usuário.
     * @param password Senha.
     * @param name     Nome completo.
     * @param email    Email do administrador.
     */
    public Administrator(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    /**
     * Retorna o papel do usuário.
     *
     * @return String "Administrator".
     */
    @Override
    public String getRole() {
        return "Administrator";
    }

    /**
     * Retorna uma representação em string do administrador.
     *
     * @return String representando o administrador.
     */
    @Override
    public String toString() {
        return "Administrator{" +
               "username='" + getUsername() + '\'' +
               ", name='" + getName() + '\'' +
               '}';
    }
}