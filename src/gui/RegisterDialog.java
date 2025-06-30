package gui;

import manager.ReservationManager;
import model.Administrator;
import model.Student;
import model.User;

import javax.swing.*;

import exception.UserConflictException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo para registro de um novo usuário.
 */
public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField raField;
    private JComboBox<String> userTypeComboBox;
    private JButton registerButton;

    private ReservationManager manager;

    /**
     * Construtor do diálogo de registro.
     * @param parent O frame pai.
     * @param manager O gerenciador de reservas.
     */
    public RegisterDialog(Frame parent, ReservationManager manager) {
        super(parent, "Criar Nova Conta", true);
        this.manager = manager;

        setLayout(new GridLayout(5, 2, 10, 10));
        pack();
        setLocationRelativeTo(parent);
        initUI();
    }

    /**
     * Inicializa os componentes da UI.
     */
    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tipo de Usuário:"), gbc);
        gbc.gridx = 1;
        userTypeComboBox = new JComboBox<>(new String[] { "Estudante", "Administrador" });
        userTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFieldsVisibility();
            }
        });
        formPanel.add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nome de Usuário:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel raLabel = new JLabel("RA/Matrícula:");
        formPanel.add(raLabel, gbc);
        gbc.gridx = 1;
        raField = new JTextField(20);
        formPanel.add(raField, gbc);

        registerButton = new JButton("Registrar");
        getRootPane().setDefaultButton(registerButton);
        registerButton.addActionListener(e -> performRegistration());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateFieldsVisibility();
    }

    /**
     * Atualiza a visibilidade dos campos.
     */
    private void updateFieldsVisibility() {
        String selectedType = (String) userTypeComboBox.getSelectedItem();
        boolean isStudent = "Estudante".equals(selectedType);

        raField.setVisible(isStudent);
        for (Component comp : raField.getParent().getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().equals("RA/Matrícula:")) {
                comp.setVisible(isStudent);
                break;
            }
        }
        pack();
    }

    /**
     * Valida os campos do formulário.
     * @return true se os campos forem válidos, false caso contrário.
     */
    private boolean validateFields() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String userType = (String) userTypeComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos obrigatórios devem ser preenchidos.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("Estudante".equals(userType)) {
            String ra = raField.getText().trim();
            if (ra.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo RA/Matrícula é obrigatório para estudantes.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Realiza o registro do usuário.
     */
    private void performRegistration() {
        if (!validateFields()) return;

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String userType = (String) userTypeComboBox.getSelectedItem();

        try {
            User newUser;
            if ("Estudante".equals(userType)) {
                String ra = raField.getText().trim();
                newUser = new Student(username, password, fullName, email, ra);
            } else {
                newUser = new Administrator(username, password, fullName, email);
            }

            manager.addUser(newUser);
            JOptionPane.showMessageDialog(this, "Usuário " + username + " registrado com sucesso!", "Registro Concluído", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (UserConflictException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Conflito de Usuário", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}