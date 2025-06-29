package gui;

import manager.ReservationManager;
import model.Administrator;
import model.Student;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField raField; // Para estudantes
    private JComboBox<String> userTypeComboBox;
    private JButton registerButton;

    private ReservationManager manager;

    public RegisterDialog(LoginDialog parent, ReservationManager manager) {
        super(parent, "Criar Nova Conta", true);
        this.manager = manager;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Tipo de Usuário
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tipo de Usuário:"), gbc);
        gbc.gridx = 1;
        userTypeComboBox = new JComboBox<>(new String[]{"Estudante", "Administrador"});
        userTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFieldsVisibility();
            }
        });
        formPanel.add(userTypeComboBox, gbc);

        // Linha 1: Usuário
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nome de Usuário:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Linha 2: Senha
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Linha 3: Nome Completo
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        // Linha 4: Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Linha 5: RA (apenas para estudante)
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel raLabel = new JLabel("RA/Matrícula:");
        formPanel.add(raLabel, gbc);
        gbc.gridx = 1;
        raField = new JTextField(20);
        formPanel.add(raField, gbc);

        // Botão de Registro
        registerButton = new JButton("Registrar");
        registerButton.addActionListener(e -> performRegistration());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Inicializa a visibilidade dos campos com base na seleção inicial
        updateFieldsVisibility();
    }

    private void updateFieldsVisibility() {
        String selectedType = (String) userTypeComboBox.getSelectedItem();
        boolean isStudent = "Estudante".equals(selectedType);

        raField.setVisible(isStudent);
        // Oculta/mostra o label correspondente
        for (Component comp : raField.getParent().getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().equals("RA/Matrícula:")) {
                comp.setVisible(isStudent);
                break;
            }
        }
        pack(); // Ajusta o tamanho do diálogo após mudar a visibilidade
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String userType = (String) userTypeComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos obrigatórios devem ser preenchidos.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (manager.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Nome de usuário já existe. Por favor, escolha outro.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser;
        if ("Estudante".equals(userType)) {
            String ra = raField.getText().trim();
            if (ra.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo RA/Matrícula é obrigatório para estudantes.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            newUser = new Student(username, password, fullName, email, ra);
        } else { // Administrador
            // Em um sistema de produção, a criação de administradores não seria feita via UI pública.
            // Para fins de demonstração, estamos permitindo.
            newUser = new Administrator(username, password, fullName, email);
        }

        manager.addUser(newUser); // Este método já chama saveData() internamente
        JOptionPane.showMessageDialog(this, "Usuário " + username + " registrado com sucesso!", "Registro Concluído", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Fecha o diálogo de registro
    }
}