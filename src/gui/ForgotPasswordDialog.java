// src/gui/ForgotPasswordDialog.java
package gui;

import manager.ReservationManager;
import model.Administrator;
import model.Student;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForgotPasswordDialog extends JDialog {
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField raField; // Campo específico para estudantes
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private JButton resetPasswordButton;

    private ReservationManager manager;
    private User foundUser; // Para armazenar o usuário encontrado após a validação

    public ForgotPasswordDialog(Frame parent, ReservationManager manager) {
        super(parent, "Esqueci Minha Senha", true);
        this.manager = manager;
        this.foundUser = null;

        setSize(400, 350);
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
        gbc.anchor = GridBagConstraints.WEST;

        // Campo: Nome de Usuário
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nome de Usuário:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Campo: Nome Completo
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        // Campo: Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Campo: RA/Matrícula (opcional, visível apenas para estudantes)
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel raLabel = new JLabel("RA/Matrícula (se estudante):");
        formPanel.add(raLabel, gbc);
        gbc.gridx = 1;
        raField = new JTextField(20);
        formPanel.add(raField, gbc);

        // Separador para as senhas
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(separator, gbc);

        // Campo: Nova Senha
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Nova Senha:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        newPasswordField.setEnabled(false); // Desabilitado inicialmente
        formPanel.add(newPasswordField, gbc);

        // Campo: Confirmar Nova Senha
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.gridx = 1;
        confirmNewPasswordField = new JPasswordField(20);
        confirmNewPasswordField.setEnabled(false); // Desabilitado inicialmente
        formPanel.add(confirmNewPasswordField, gbc);

        // Botão: Redefinir Senha
        resetPasswordButton = new JButton("Validar e Redefinir Senha");
        resetPasswordButton.addActionListener(e -> validateAndResetPassword());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetPasswordButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void validateAndResetPassword() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String ra = raField.getText().trim(); // Pode ser vazio para administradores

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de usuário, nome completo e email são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = manager.getUserByUsername(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verifica as informações adicionais
        boolean matches = user.getFullName().equalsIgnoreCase(fullName) && user.getEmail().equalsIgnoreCase(email);

        if (user instanceof Student) {
            Student student = (Student) user;
            matches = matches && student.getRa().equalsIgnoreCase(ra);
        } else if (user instanceof Administrator) {
            // Para administradores, o campo RA/Matrícula não é relevante.
            // Se o usuário digitou algo no RA, pode ser um erro ou confusão.
            // Poderíamos adicionar uma validação mais rigorosa aqui se quisermos que RA seja estritamente vazio para admins.
            // Por simplicidade, assumimos que se é admin e o RA está preenchido, ele será ignorado na comparação.
            // Ou, poderíamos fazer: matches = matches && ra.isEmpty();
        }

        if (matches) {
            // Informações de identificação corretas, agora permitir redefinir a senha
            foundUser = user;
            usernameField.setEnabled(false);
            fullNameField.setEnabled(false);
            emailField.setEnabled(false);
            raField.setEnabled(false);
            resetPasswordButton.setText("Redefinir Senha");
            resetPasswordButton.removeActionListener(e -> validateAndResetPassword()); // Remove o listener anterior
            resetPasswordButton.addActionListener(e -> resetUserPassword()); // Adiciona o novo listener
            
            newPasswordField.setEnabled(true);
            confirmNewPasswordField.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Informações validadas. Por favor, insira sua nova senha.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "As informações fornecidas não correspondem aos registros do usuário.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetUserPassword() {
        if (foundUser == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário validado para redefinir a senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmNewPasswordField.getPassword());

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha ambos os campos de nova senha.", "Erro de Senha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem. Por favor, tente novamente.", "Erro de Senha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Definir a nova senha (assumindo que a classe User tem um setter para senha)
        foundUser.setPassword(newPassword); // IMPORTANTE: Verifique se User.java tem setPassword()
        manager.saveData(); // Salva as mudanças na lista de usuários (persistência)

        JOptionPane.showMessageDialog(this, "Senha redefinida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Fecha o diálogo após a redefinição
    }
}