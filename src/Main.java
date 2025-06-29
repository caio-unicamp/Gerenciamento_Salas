// src/Main.java
import gui.LoginDialog;
import gui.MainFrame;
import manager.ReservationManager;
import model.Administrator;
import model.Classroom;
import model.Student;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Criar o gerenciador de reservas
        ReservationManager manager = new ReservationManager();

        // Adicionar alguns dados de teste iniciais se os arquivos estiverem vazios/não existirem
        if (manager.getAllClassrooms().isEmpty()) {
            manager.addClassroom(new Classroom("Sala A101", 30, "Prédio A", true));
            manager.addClassroom(new Classroom("Laboratório B205", 20, "Prédio B", true));
            manager.addClassroom(new Classroom("Auditório Principal", 100, "Prédio C", true));
            manager.addClassroom(new Classroom("Sala de Reuniões D301", 8, "Prédio D", false));
        }

        if (manager.getAllUsers().isEmpty()) {
            manager.addUser(new Administrator("admin", "admin123", "Administrador Principal", "admin@unicamp.br"));
            manager.addUser(new Student("aluno1", "aluno123", "João Silva", "joao.silva@unicamp.br", "RA123456"));
            manager.addUser(new Student("aluno2", "aluno123", "Maria Oliveira", "maria.oliveria@unicamp.br", "RA654321"));
        }

        // Garante que a GUI será iniciada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame dummyFrame = new JFrame();
            dummyFrame.setVisible(false);
            dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            LoginDialog loginDialog = new LoginDialog(dummyFrame, manager);

            // Adiciona o listener para lidar com o sucesso do login
            loginDialog.setLoginListener(user -> {
                // Quando o login for bem-sucedido, cria e exibe a MainFrame
                MainFrame mainFrame = new MainFrame(manager, user, loginDialog);
                mainFrame.setVisible(true);
                mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            });

            loginDialog.setVisible(true);

            if (loginDialog.getAuthenticatedUser() == null) {
                JOptionPane.showMessageDialog(null, "Login cancelado ou falhou. Encerrando o sistema.", "Sair", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        });
    }
}