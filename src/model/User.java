package model;

import java.io.Serializable;

/**
 * Classe abstrata que representa um usuário do sistema.
 * Pode ser estendida para diferentes tipos de usuários, como Estudante ou Administrador.
 */
public abstract class User implements Serializable {
    private String username;
    protected String password; // Simplificado para o exemplo, em um sistema real usaria hash
    private String name;
    private String email;

    /**
     * Construtor do usuário.
     *
     * @param username Nome de usuário.
     * @param password Senha do usuário.
     * @param name     Nome completo.
     * @param email    Email do usuário.
     */
    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    /**
     * Retorna o nome de usuário.
     * @return Nome de usuário.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retorna a senha do usuário.
     * @return Senha.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retorna o nome completo do usuário.
     * @return Nome completo.
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna o email do usuário.
     * @return Email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retorna o papel do usuário (deve ser implementado pelas subclasses).
     * @return Papel do usuário.
     */
    public abstract String getRole();

    /**
     * Altera a senha do usuário.
     * @param newPassword Nova senha.
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * Autentica o usuário comparando a senha informada.
     * @param enteredPassword Senha informada.
     * @return true se a senha estiver correta, false caso contrário.
     */
    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    /**
     * Retorna uma representação em string do usuário.
     * @return String representando o usuário.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }

    /**
     * Compara dois usuários pelo nome de usuário (case insensitive).
     * @param o Objeto a ser comparado.
     * @return true se os nomes de usuário forem iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return username.equalsIgnoreCase(user.username);
    }

    /**
     * Retorna o hash code baseado no nome de usuário (case insensitive).
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }
}