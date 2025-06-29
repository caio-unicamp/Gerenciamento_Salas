package gui;

import manager.ReservationManager;
import model.Classroom;
import model.Reservation;
import model.User;

import javax.swing.*; // Interface gráfica 

import exception.ReservationConflictException;

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

        setTitle("Sistema de Gerenciamento de Reservas de Salas de Aula - Logado como: " + loggedInUser.getUsername()
                + " (" + loggedInUser.getRole() + ")");
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
            JPanel buttonPanel = new JPanel();
            JButton addClassroomButton = new JButton("Adicionar Nova Sala");
            addClassroomButton.addActionListener(e -> {
                AddClassroomDialog dialog = new AddClassroomDialog(this, manager);
                dialog.setVisible(true);
                classroomPanel.refreshClassroomList(); // Atualiza a lista após adicionar
            });
            buttonPanel.add(addClassroomButton);
            // Reutiliza a tabela de salas

            JButton removeClassroomButton = new JButton("Remover Sala");
            JTable classroomTable = new JTable(classroomPanel.getClassroomTableModel());
            removeClassroomButton.addActionListener(e -> {
                int selectedRow = classroomTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Selecione uma sala para confirmar.", "Nenhuma Seleção",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover esta sala?", "Confirmar removimento de sala", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String classroomName = (String) classroomPanel.getClassroomTableModel().getValueAt(selectedRow, 0);
                    Classroom classroomToRemove = manager.getAllClassrooms().stream()
                            .filter(r -> r.getName() == classroomName)
                            .findFirst()
                            .orElse(null);
                    if (classroomToRemove != null) {
                        try {
                            manager.removeClassroom(classroomToRemove);
                            JOptionPane.showMessageDialog(this, "Sala removida com sucesso!", "Sucesso",
                                    JOptionPane.INFORMATION_MESSAGE);
                            classroomPanel.refreshClassroomList();
                            if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                                ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                            }
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Sala não encontrada para confirmação.", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(removeClassroomButton);
            adminPanel.add(buttonPanel, BorderLayout.NORTH);
            adminPanel.add(new JScrollPane(classroomTable), BorderLayout.CENTER);

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