package model;

import java.io.Serializable;

// Exemplo de classe abstrata [cite: 8]
public abstract class User implements Serializable{
    private String username;
    private String password; // Simplificado para o exemplo, em um sistema real usaria hash
    private String name;
    private String email;

    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Exemplo de polimorfismo de método (overriding) 
    public abstract String getRole();

    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    @Override
    public String toString() {
        return "User{" +
               "username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", role='" + getRole() + '\'' +
               '}';
    }
}