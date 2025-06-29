// src/gui/LoginDialog.java
package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    // Novo: Botão para criar conta
    private JButton createAccountButton;
    private ReservationManager manager;
    private User authenticatedUser;

    public interface LoginListener {
        void onLoginSuccess(User user);
    }
    private LoginListener loginListener;

    public LoginDialog(Frame parent, ReservationManager manager) {
        super(parent, "Login", true);
        this.manager = manager;
        this.authenticatedUser = null;

        setSize(300, 220); // Aumentei um pouco o tamanho para o novo botão
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Usuário:"));
        usernameField = new JTextField(15);
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Senha:"));
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(loginButton);

        // Novo: Botão Criar Conta
        createAccountButton = new JButton("Criar Conta");
        createAccountButton.addActionListener(e -> openRegisterDialog());


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton); // Adiciona o novo botão ao painel

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = manager.getUserByUsername(username);

        if (user != null && user.authenticate(password)) {
            authenticatedUser = user;
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
            if (loginListener != null) {
                loginListener.onLoginSuccess(authenticatedUser);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Novo: Método para abrir o diálogo de registro
    private void openRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this, manager);
        registerDialog.setVisible(true);
        // Opcional: Após o registro, o usuário é fechado, e o LoginDialog permanece aberto.
        // O usuário precisará inserir as novas credenciais para logar.
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        authenticatedUser = null; 
    }
}