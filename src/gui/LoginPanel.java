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

        gbc.insets = new Insets(5, 5, 5, 5); // Um espaçamento padrão e consistente
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1; // Garante que cada componente ocupe uma célula

        // --- Usuário ---
        gbc.gridy = 1;

        // Label do Usuário
        gbc.gridx = 0;
        gbc.weightx = 0.0; // [ESSENCIAL] O label não deve esticar. Peso 0.
        gbc.fill = GridBagConstraints.NONE; // O label não preenche a célula.
        formPanel.add(new JLabel("Usuário:"), gbc);

        // Campo de texto do Usuário
        gbc.gridx = 1;
        gbc.weightx = 1.0; // [ESSENCIAL] O campo de texto deve esticar e ocupar o espaço extra.
        gbc.fill = GridBagConstraints.HORIZONTAL; // O campo preenche a célula horizontalmente.
        usernameField = new JTextField(20);
        usernameField.putClientProperty("JTextField.placeholderText", "Digite seu usuário");
        formPanel.add(usernameField, gbc);

        // --- Senha ---
        gbc.gridy = 2;

        // Label da Senha
        gbc.gridx = 0;
        gbc.weightx = 0.0; // O label não deve esticar.
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Senha:"), gbc);

        // Campo de texto da Senha
        gbc.gridx = 1;
        gbc.weightx = 1.0; // O campo de texto deve esticar.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        passwordField.putClientProperty("JTextField.placeholderText", "Digite sua senha");
        formPanel.add(passwordField, gbc);
        passwordField.addActionListener(e -> performLogin());


        // --- Botão de Login (Ação Primária) ---
        // Reseta os pesos para o botão não se deformar
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Ocupa as duas colunas
        gbc.weightx = 0.0; // Não estica
        gbc.fill = GridBagConstraints.NONE; // Não preenche
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza o botão
        gbc.insets = new Insets(15, 8, 8, 8); // Aumenta o espaço superior
        loginButton = new JButton("Entrar");

                /// --- Painel para Registrar e Esqueci a senha ---
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        registerButton = new JButton("Registrar");
        registerButton.putClientProperty("JButton.buttonType", "roundRect");
        bottomPanel.add(registerButton, BorderLayout.WEST);

        forgotPasswordLabel = new JLabel("<html><a href=''>Esqueci minha senha</a></html>", SwingConstants.RIGHT);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(forgotPasswordLabel, BorderLayout.EAST);

        // [CORRIGIDO] Define as restrições corretas para o painel inferior
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Deve ocupar as duas colunas
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza o painel
        gbc.fill = GridBagConstraints.HORIZONTAL; // Faz o painel preencher o espaço horizontal
        gbc.insets = new Insets(10, 8, 8, 8); // Adiciona um espaçamento superior

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