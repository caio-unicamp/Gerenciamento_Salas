import gui.LoginDialog;
import gui.MainFrame;
import manager.ReservationManager;
import model.Administrator;
import model.Classroom;
import model.Student;
import model.User;


import javax.swing.*; // Interface gráfica 
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Criar o gerenciador de reservas
        ReservationManager manager = new ReservationManager();

        // Adicionar alguns dados de teste iniciais se os arquivos estiverem vazios/não existirem
        // Isso só acontecerá na primeira execução ou se os arquivos forem apagados
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
            // Cria um Frame "fantasma" que será o pai do LoginDialog para centralizá-lo corretamente.
            JFrame dummyFrame = new JFrame();
            dummyFrame.setVisible(false); // Nunca visível
            dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Sai se o dummyFrame for fechado (não deve acontecer)
            
            LoginDialog loginDialog = new LoginDialog(dummyFrame, manager);
            loginDialog.setVisible(true);

            User authenticatedUser = loginDialog.getAuthenticatedUser();
            do{
                if (authenticatedUser == null) {
                    JOptionPane.showMessageDialog(null, "Login cancelado ou falhou. Encerrando o sistema.", "Sair", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else {
                    MainFrame mainFrame = new MainFrame(manager, authenticatedUser, loginDialog);
                    mainFrame.setVisible(true);
                    while (mainFrame.isVisible()) {
                        try{
                            Thread.sleep(10);
                        }catch(Exception e){
                            Thread.currentThread().interrupt();
                            System.err.println("MainFrame loop interrompido.");
                        }
                    }
                }
            }while (authenticatedUser != null);
        });
    }
}