package model;

import java.io.Serializable;

/**
 * Classe abstrata para um usuário.
 */
public abstract class User implements Serializable {
    private String username;
    protected String password;
    private String name;
    private String email;

    /**
     * Construtor para um usuário.
     * @param username O nome de usuário.
     * @param password A senha.
     * @param name O nome.
     * @param email O email.
     */
    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    /**
     * Obtém o nome de usuário.
     * @return O nome de usuário.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtém a senha.
     * @return A senha.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obtém o nome.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtém o email.
     * @return O email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtém a função do usuário.
     * @return A função.
     */
    public abstract String getRole();

    /**
     * Define a senha.
     * @param newPassword A nova senha.
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * Autentica o usuário.
     * @param enteredPassword A senha inserida.
     * @return true se a senha estiver correta, false caso contrário.
     */
    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    /**
     * Retorna uma representação em string do usuário.
     * @return A representação em string.
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
     * Verifica se dois objetos User são iguais.
     * @param o O objeto a ser comparado.
     * @return true se os objetos forem iguais, false caso contrário.
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
     * Retorna o código hash para o objeto.
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }
}