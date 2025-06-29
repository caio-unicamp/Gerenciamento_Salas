package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JButton forgotPasswordButton;
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

        setSize(300, 250);
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

        createAccountButton = new JButton("Criar Conta");
        createAccountButton.addActionListener(e -> openRegisterDialog());

        forgotPasswordButton = new JButton("Esqueci Minha Senha");
        forgotPasswordButton.addActionListener(e -> openForgotPasswordDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); 
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        topButtonsPanel.add(loginButton);
        topButtonsPanel.add(createAccountButton); // Adiciona o botão de criar conta
        
        buttonPanel.add(topButtonsPanel);
        buttonPanel.add(forgotPasswordButton); // Adiciona o botão de esqueci minha senha

        forgotPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        
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

    private void openRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this, manager);
        registerDialog.setVisible(true);
    }

    private void openForgotPasswordDialog() {
        ForgotPasswordDialog forgotDialog = new ForgotPasswordDialog(this, manager);
        forgotDialog.setVisible(true);
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