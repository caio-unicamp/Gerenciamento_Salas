package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de Login da aplicação, com visual moderno.
 */
public class LoginPanel extends JPanel {
    private ReservationManager manager;
    private LoginListener loginListener;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel forgotPasswordLabel;

    public LoginPanel(ReservationManager manager, LoginListener loginListener) {
        this.manager = manager;
        this.loginListener = loginListener;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Painel central para o formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 400));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Título
        JLabel titleLabel = new JLabel("Sistema de Reservas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);

        // Reset insets
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Usuário
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Usuário:"), gbc);

        usernameField = new JTextField(20);
        usernameField.putClientProperty("JTextField.placeholderText", "Digite seu usuário");
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(usernameField, gbc);

        // Senha
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Senha:"), gbc);

        passwordField = new JPasswordField(20);
        passwordField.putClientProperty("JTextField.placeholderText", "Digite sua senha");
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        passwordField.addActionListener(e -> performLogin());

        // Botão de Login (Ação Primária)
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Entrar");
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        loginButton.putClientProperty("JButton.buttonType", "primary"); // Estilo FlatLaf para botão primário
        formPanel.add(loginButton, gbc);

        // Painel para Registrar e Esqueci a senha
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        registerButton = new JButton("Registrar");
        registerButton.putClientProperty("JButton.buttonType", "roundRect");
        bottomPanel.add(registerButton, BorderLayout.WEST);

        forgotPasswordLabel = new JLabel("<html><a href=''>Esqueci minha senha</a></html>", SwingConstants.RIGHT);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(forgotPasswordLabel, BorderLayout.EAST);

        gbc.gridy = 4;
        formPanel.add(bottomPanel, gbc);

        // Adiciona o painel do formulário ao painel principal
        add(formPanel);

        // Action Listeners
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> openRegisterDialog());
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openForgotPasswordDialog();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = manager.getUserByUsername(username);

        if (user != null && user.authenticate(password)) {
            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos.",
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private void openRegisterDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        RegisterDialog registerDialog = new RegisterDialog(parentFrame, manager);
        registerDialog.setVisible(true);
    }

    private void openForgotPasswordDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(parentFrame, manager);
        forgotPasswordDialog.setVisible(true);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }
}