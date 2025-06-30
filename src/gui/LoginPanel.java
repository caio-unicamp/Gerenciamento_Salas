package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de login.
 */
public class LoginPanel extends JPanel {
    private ReservationManager manager;
    private LoginListener loginListener;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel forgotPasswordLabel;

    /**
     * Construtor para o painel de login.
     * @param manager O gerenciador de reservas.
     * @param loginListener O ouvinte para o sucesso do login.
     */
    public LoginPanel(ReservationManager manager, LoginListener loginListener) {
        this.manager = manager;
        this.loginListener = loginListener;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 400));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Sistema de Reservas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        usernameField.putClientProperty("JTextField.placeholderText", "Digite seu usuário");
        formPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        passwordField.putClientProperty("JTextField.placeholderText", "Digite sua senha");
        formPanel.add(passwordField, gbc);
        passwordField.addActionListener(e -> performLogin());

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 8, 8, 8);
        loginButton = new JButton("Entrar");

        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        registerButton = new JButton("Registrar");
        registerButton.putClientProperty("JButton.buttonType", "roundRect");
        bottomPanel.add(registerButton, BorderLayout.WEST);

        forgotPasswordLabel = new JLabel("<html><a href=''>Esqueci minha senha</a></html>", SwingConstants.RIGHT);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(forgotPasswordLabel, BorderLayout.EAST);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 8, 8);

        formPanel.add(bottomPanel, gbc);

        add(formPanel);

        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> openRegisterDialog());
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openForgotPasswordDialog();
            }
        });
    }

    /**
     * Realiza a tentativa de login.
     */
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

    /**
     * Abre o diálogo de registro.
     */
    private void openRegisterDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        RegisterDialog registerDialog = new RegisterDialog(parentFrame, manager);
        registerDialog.setVisible(true);
    }

    /**
     * Abre o diálogo de esqueci a senha.
     */
    private void openForgotPasswordDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(parentFrame, manager);
        forgotPasswordDialog.setVisible(true);
    }

    /**
     * Limpa os campos de texto.
     */
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }
}