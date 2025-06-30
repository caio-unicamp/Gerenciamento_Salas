package gui;

import model.User;

/**
 * Interface para um ouvinte de login.
 */
public interface LoginListener {
    /**
     * Chamado quando o login é bem-sucedido.
     * @param loggedInUser O usuário que fez o login.
     */
    void onLoginSuccess(User loggedInUser);
}