package gui;

import model.User;

/**
 * Interface para notificar quando o login for bem-sucedido.
 */
public interface LoginListener {
    void onLoginSuccess(User loggedInUser);
}