package gui;

import manager.ReservationManager;
import model.Classroom;
import model.Reservation;
import model.Administrator;
import model.User;
import model.Administrator;

import javax.swing.*; // Interface gráfica 

import exception.ReservationConflictException;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame { // Interface gráfica
    private ReservationManager manager;
    private User loggedInUser;
    private LoginDialog parentLoginDialog; // Referência ao diálogo de login que a abriu

    private JTabbedPane tabbedPane;
    private ClassroomPanel classroomPanel;
    private ReservationPanel reservationPanel;
    private AdminReservationPanel adminReservationPanel; // Novo painel do administrador
    private JButton logoutButton; // Novo botão de logout

    public MainFrame(ReservationManager manager, User loggedInUser, LoginDialog parentLoginDialog) {
        this.manager = manager;
        this.parentLoginDialog = parentLoginDialog; // Armazena a referência ao diálogo de login
        this.loggedInUser = loggedInUser;

        setTitle("Sistema de Gerenciamento de Reservas de Salas de Aula - Logado como: " + loggedInUser.getUsername()
                + " (" + loggedInUser.getRole() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Gerenciado pelo WindowListener
        setLocationRelativeTo(null); // Centraliza a janela

        initUI();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performLogout();
            }
        });
    }

    private void initUI() {
        // Painel superior para o título e botão de logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Bem-vindo(a), " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> performLogout());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Para alinhar o botão à direita
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH); // Adiciona o painel superior

        tabbedPane = new JTabbedPane();

        classroomPanel = new ClassroomPanel(manager);
        tabbedPane.addTab("Salas de Aula", classroomPanel);

        reservationPanel = new ReservationPanel(manager, loggedInUser);
        tabbedPane.addTab("Minhas Reservas", reservationPanel);

        if (loggedInUser.getRole().equals("Administrator")) {
            JPanel adminClassroomPanel = new AdminClassroomPanel(this,manager);
            tabbedPane.addTab("Administração de Salas", adminClassroomPanel);

            JPanel adminReservationPanel = new AdminReservationPanel(manager);
            tabbedPane.addTab("Gerenciar Reservas", adminReservationPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Tem certeza que deseja fazer logout?", "Logout",
                        JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.saveData(); // Salva os dados antes de fazer logout
            dispose(); // Fecha a MainFrame
            parentLoginDialog.clearFields(); // Limpa os campos do diálogo de login
            parentLoginDialog.setVisible(true); // Torna o diálogo de login visível novamente
        }
    }
    // Método para atualizar as abas quando necessário (ex: após uma reserva)
    public void refreshPanels() {
        classroomPanel.refreshClassroomList();
        reservationPanel.refreshReservationList();
    }
}