package gui;

import manager.ReservationManager;
import model.Student;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo para redefinição de senha.
 */
public class ForgotPasswordDialog extends JDialog {
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField raField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private JButton resetPasswordButton;
    private JComboBox<String> userTypeComboBox;

    private ReservationManager manager;
    private User foundUser;

    /**
     * Construtor do diálogo de esqueci a senha.
     * @param parent O frame pai.
     * @param manager O gerenciador de reservas.
     */
    public ForgotPasswordDialog(Frame parent, ReservationManager manager) {
        super(parent, "Recuperar Senha", true);
        this.manager = manager;
        
        initUI();
        setLocationRelativeTo(parent);
    }

    /**
     * Inicializa os componentes da UI.
     */
    private void initUI() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tipo de Usuário:"), gbc);
        gbc.gridx = 1;
        userTypeComboBox = new JComboBox<>(new String[]{"Estudante", "Administrador"});
        userTypeComboBox.addActionListener(e -> updateFieldsVisibility());
        formPanel.add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nome de Usuário:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel raLabel = new JLabel("RA/Matrícula:");
        formPanel.add(raLabel, gbc);
        gbc.gridx = 1;
        raField = new JTextField(20);
        formPanel.add(raField, gbc);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(separator, gbc);

        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Nova Senha:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        newPasswordField.setEnabled(false);
        formPanel.add(newPasswordField, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.gridx = 1;
        confirmNewPasswordField = new JPasswordField(20);
        confirmNewPasswordField.setEnabled(false);
        formPanel.add(confirmNewPasswordField, gbc);

        resetPasswordButton = new JButton("Validar e Redefinir Senha");
        getRootPane().setDefaultButton(resetPasswordButton);
        resetPasswordButton.addActionListener(e -> validateAndResetPassword());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetPasswordButton);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        updateFieldsVisibility();
    }
    
    /**
     * Atualiza a visibilidade dos campos com base no tipo de usuário.
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
     * Valida as informações do usuário e redefine a senha.
     */
    private void validateAndResetPassword() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String ra = raField.getText().trim();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de usuário, nome completo e email são obrigatórios.", "Erro de Validaçã-o", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = manager.getUserByUsername(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro de Validaçã-o", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean matches = user.getName().equalsIgnoreCase(fullName) && user.getEmail().equalsIgnoreCase(email);

        if (user instanceof Student) {
            Student student = (Student) user;
            matches = matches && student.getStudentId().equalsIgnoreCase(ra);
        }

        if (matches) {
            foundUser = user;
            usernameField.setEnabled(false);
            fullNameField.setEnabled(false);
            emailField.setEnabled(false);
            raField.setEnabled(false);
            resetPasswordButton.setText("Redefinir Senha");
            resetPasswordButton.removeActionListener(e -> validateAndResetPassword());
            resetPasswordButton.addActionListener(e -> resetUserPassword());
            
            newPasswordField.setEnabled(true);
            confirmNewPasswordField.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Informações validadas. Por favor, insira sua nova senha.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "As informações fornecidas não correspondem aos registros do usuário.", "Erro de Validaçã-o", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Redefine a senha do usuário.
     */
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

        foundUser.setPassword(newPassword); 
        manager.saveData();

        JOptionPane.showMessageDialog(this, "Senha redefinida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}