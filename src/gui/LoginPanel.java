package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de Login da aplicação.
 * Não é mais um JDialog, mas um JPanel para ser exibido na janela principal.
 */
public class LoginPanel extends JPanel {
    private ReservationManager manager;
    private LoginListener loginListener; // Listener para notificar o sucesso do login

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel forgotPasswordLabel;

    public LoginPanel(ReservationManager manager, LoginListener loginListener) {
        this.manager = manager;
        this.loginListener = loginListener;

        // Configuração do Layout
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240)); // Um fundo mais suave
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Login do Sistema", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(titleLabel, gbc);

        // Usuário
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        add(new JLabel("Usuário:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        add(usernameField, gbc);

        // Senha
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        add(new JLabel("Senha:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        add(passwordField, gbc);
        
        // Ação de login ao pressionar Enter na senha
        passwordField.addActionListener(e -> performLogin());

        // Painel de Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Entrar");
        registerButton = new JButton("Registrar");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.setOpaque(false); // Fundo transparente

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
        
        // "Esqueci minha senha"
        forgotPasswordLabel = new JLabel("<html><a href=''>Esqueci minha senha</a></html>");
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        add(forgotPasswordLabel, gbc);

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
            // Notifica o listener que o login foi bem-sucedido
            JOptionPane.showMessageDialog(this,
                    "Acesso liberado!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos.",
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
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