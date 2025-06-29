package model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private String username;
    protected String password; // Simplificado para o exemplo, em um sistema real usaria hash
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

    public abstract String getRole();

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return username.equalsIgnoreCase(user.username);
    }

    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }
}