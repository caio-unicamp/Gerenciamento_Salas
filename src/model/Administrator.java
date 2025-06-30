package model;

/**
 * Representa um usuário Administrador.
 */
public class Administrator extends User {

    /**
     * Construtor para a classe Administrator.
     *
     * @param username O nome de usuário.
     * @param password A senha.
     * @param name O nome completo.
     * @param email O email.
     */
    public Administrator(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    /**
     * Obtém a função do usuário.
     *
     * @return A string "Administrator".
     */
    @Override
    public String getRole() {
        return "Administrator";
    }

    /**
     * Retorna uma representação em string do objeto Administrator.
     *
     * @return Uma representação em string do objeto.
     */
    @Override
    public String toString() {
        return "Administrator{" +
               "username='" + getUsername() + '\'' +
               ", name='" + getName() + '\'' +
               '}';
    }
}