package gui;

import manager.ReservationManager;
import model.Classroom;
import model.Reservation;
import model.User;
import exception.ReservationConflictException;

import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Painel para o usuário gerenciar suas reservas.
 */
public class ReservationPanel extends JPanel { 
    private ReservationManager manager;
    private User loggedInUser;

    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;
    private JButton newReservationButton;
    private JButton cancelReservationButton;

    /**
     * Construtor para o painel de reservas.
     * @param manager O gerenciador de reservas.
     * @param loggedInUser O usuário logado.
     */
    public ReservationPanel(ReservationManager manager, User loggedInUser) {
        this.manager = manager;
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        initComponents();
        refreshReservationList();
    }

    /**
     * Inicializa os componentes da UI.
     */
    private void initComponents() {
        String[] columnNames = { "ID", "Sala", "Data", "Início", "Término", "Propósito", "Status", "Observações" };
        reservationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        reservationTable.setFillsViewportHeight(true);

        reservationTable.setAutoCreateColumnsFromModel(true);
        TableColumnModel tcm = reservationTable.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);
        tcm.getColumn(0).setWidth(0);
        tcm.getColumn(0).setPreferredWidth(0);
        tcm.getColumn(0).setResizable(false);

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        newReservationButton = new JButton("Nova Reserva");
        newReservationButton.addActionListener(e -> openNewReservationDialog());
        buttonPanel.add(newReservationButton);

        cancelReservationButton = new JButton("Cancelar Reserva Selecionada");
        cancelReservationButton.addActionListener(e -> cancelSelectedReservation());
        buttonPanel.add(cancelReservationButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Atualiza a lista de reservas.
     */
    public void refreshReservationList() {
        reservationTableModel.setRowCount(0);
        List<Reservation> reservations = manager.getReservationsByUser(loggedInUser);
        for (Reservation reservation : reservations) {
            reservationTableModel.addRow(new Object[] {
                    reservation.getId(),
                    reservation.getClassroom().getName(),
                    reservation.getDate().toString(),
                    reservation.getStartTime().toString(),
                    reservation.getEndTime().toString(),
                    reservation.getPurpose(),
                    reservation.getStatus().getName(),
                    reservation.getObservation()
            });
        }
    }

    /**
     * Abre o diálogo para uma nova reserva.
     */
    private void openNewReservationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nova Reserva", true);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); 
        dialog.setContentPane(mainPanel);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Detalhes da Reserva"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        dialog.setLocationRelativeTo(this);

        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField startTimeField = new JTextField("08:00");
        JTextField endTimeField = new JTextField("09:00");
        JTextField purposeField = new JTextField();

        JComboBox<String> classroomComboBox = new JComboBox<>();
        manager.getAllClassrooms().forEach(classroom -> classroomComboBox.addItem(classroom.getName()));
        
        if (classroomComboBox.getItemCount() <= 0) {
            JOptionPane.showMessageDialog(this, "Não há salas cadastradas para reservar.", "Erro", JOptionPane.WARNING_MESSAGE);
            dialog.dispose();
            return;
        }
        classroomComboBox.setSelectedIndex(0);

        formPanel.add(new JLabel("Sala:"));
        formPanel.add(classroomComboBox);
        formPanel.add(new JLabel("Data (AAAA-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Hora Início (HH:MM):"));
        formPanel.add(startTimeField);
        formPanel.add(new JLabel("Hora Término (HH:MM):"));
        formPanel.add(endTimeField);
        formPanel.add(new JLabel("Propósito:"));
        formPanel.add(purposeField);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Confirmar Reserva");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Cancela a reserva selecionada.
     */
    private void cancelSelectedReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar esta reserva?",
                "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int reservationId = (int) reservationTableModel.getValueAt(selectedRow, 0);
            Reservation reservationToCancel = manager.getAllReservations().stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);
            if (reservationToCancel != null) {
                manager.cancelReservation(reservationToCancel, "");
                JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshReservationList();
           
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao encontrar a reserva para cancelar.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}