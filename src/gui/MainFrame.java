package gui;

import manager.ReservationManager;
import model.User;

import javax.swing.*; // Interface gráfica 
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame { // Interface gráfica 
    private ReservationManager manager;
    private User loggedInUser;

    private JTabbedPane tabbedPane;
    private ClassroomPanel classroomPanel;
    private ReservationPanel reservationPanel;

    public MainFrame(ReservationManager manager, User loggedInUser) {
        this.manager = manager;
        this.loggedInUser = loggedInUser;

        setTitle("Sistema de Gerenciamento de Reservas de Salas de Aula - Logado como: " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Gerenciado pelo WindowListener
        setLocationRelativeTo(null); // Centraliza a janela

        initUI();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                                "Tem certeza que deseja sair?", "Sair",
                                JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.saveData(); // Salva os dados ao fechar 
                    System.exit(0);
                }
            }
        });
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        classroomPanel = new ClassroomPanel(manager);
        tabbedPane.addTab("Salas de Aula", classroomPanel);

        reservationPanel = new ReservationPanel(manager, loggedInUser);
        tabbedPane.addTab("Minhas Reservas", reservationPanel);

        if (loggedInUser.getRole().equals("Administrator")) {
            // Adicionar aba de administração de salas se for administrador
            JPanel adminPanel = new JPanel(new BorderLayout());
            JButton addClassroomButton = new JButton("Adicionar Nova Sala");
            addClassroomButton.addActionListener(e -> {
                AddClassroomDialog dialog = new AddClassroomDialog(this, manager);
                dialog.setVisible(true);
                classroomPanel.refreshClassroomList(); // Atualiza a lista após adicionar
            });
            adminPanel.add(addClassroomButton, BorderLayout.NORTH);
            adminPanel.add(new JScrollPane(new JTable(classroomPanel.getClassroomTableModel())), BorderLayout.CENTER); // Reutiliza a tabela de salas

            tabbedPane.addTab("Administração de Salas", adminPanel);

            JPanel adminReservationPanel = new AdminReservationPanel(manager);
            tabbedPane.addTab("Gerenciar Reservas", adminReservationPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Método para atualizar as abas quando necessário (ex: após uma reserva)
    public void refreshPanels() {
        classroomPanel.refreshClassroomList();
        reservationPanel.refreshReservationList();
    }
}