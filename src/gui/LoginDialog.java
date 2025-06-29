package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*; // Interface gráfica 
import java.awt.*;

public class LoginDialog extends JDialog { // Interface gráfica 
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private ReservationManager manager;
    private User authenticatedUser;

    public LoginDialog(Frame parent, ReservationManager manager) {
        super(parent, "Login", true); // Modal
        this.manager = manager;
        this.authenticatedUser = null;

        setSize(300, 180);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
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
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

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
            dispose(); // Fecha a janela de login
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}