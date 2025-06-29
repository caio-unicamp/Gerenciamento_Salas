package gui;

import manager.ReservationManager;
import model.Classroom;
import model.Reservation;
import model.User;
import exception.ReservationConflictException;

import javax.swing.*; // Interface gráfica 
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationPanel extends JPanel { // Interface gráfica
    private ReservationManager manager;
    private User loggedInUser;

    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;
    private JButton newReservationButton;
    private JButton cancelReservationButton;

    public ReservationPanel(ReservationManager manager, User loggedInUser) {
        this.manager = manager;
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        initComponents();
        refreshReservationList();
    }

    private void initComponents() {
        // Tabela de reservas
        String[] columnNames = { "ID", "Sala", "Data", "Início", "Término", "Propósito", "Status", "Observações" };
        reservationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        reservationTable = new JTable(reservationTableModel);
        reservationTable.setFillsViewportHeight(true);

        reservationTable.setAutoCreateColumnsFromModel(true); // Garante que a tabela use o modelo de colunas
        TableColumnModel tcm = reservationTable.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);
        tcm.getColumn(0).setWidth(0);
        tcm.getColumn(0).setPreferredWidth(0);
        tcm.getColumn(0).setResizable(false); // Impede que o usuário redimensione para ver

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        newReservationButton = new JButton("Nova Reserva");
        newReservationButton.addActionListener(e -> openNewReservationDialog());
        buttonPanel.add(newReservationButton);

        cancelReservationButton = new JButton("Cancelar Reserva Selecionada");
        cancelReservationButton.addActionListener(e -> cancelSelectedReservation());
        buttonPanel.add(cancelReservationButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshReservationList() {
        reservationTableModel.setRowCount(0); // Limpa a tabela
        List<Reservation> reservations = manager.getReservationsByUser(loggedInUser); // Exibe apenas as reservas do
                                                                                      // usuário logado
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

    private void openNewReservationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nova Reserva", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(this);

        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField startTimeField = new JTextField("08:00");
        JTextField endTimeField = new JTextField("09:00");
        JTextField purposeField = new JTextField();

        // Combobox para selecionar a sala
        JComboBox<String> classroomComboBox = new JComboBox<>();
        manager.getAllClassrooms().forEach(classroom -> classroomComboBox.addItem(classroom.getName()));
        if (classroomComboBox.getItemCount() > 0) {
            classroomComboBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Não há salas cadastradas para reservar.", "Erro",
                    JOptionPane.WARNING_MESSAGE);
            dialog.dispose();
            return;
        }

        dialog.add(new JLabel("Sala:"));
        dialog.add(classroomComboBox);
        dialog.add(new JLabel("Data (AAAA-MM-DD):"));
        dialog.add(dateField);
        dialog.add(new JLabel("Hora Início (HH:MM):"));
        dialog.add(startTimeField);
        dialog.add(new JLabel("Hora Término (HH:MM):"));
        dialog.add(endTimeField);
        dialog.add(new JLabel("Propósito:"));
        dialog.add(purposeField);

        JButton confirmButton = new JButton("Confirmar Reserva");
        getRootPane().setDefaultButton(confirmButton);
        confirmButton.addActionListener(e -> {
            try {
                Classroom selectedClassroom = (Classroom) manager.getAllClassrooms()
                        .get(classroomComboBox.getSelectedIndex());
                LocalDate date = LocalDate.parse(dateField.getText());
                LocalTime startTime = LocalTime.parse(startTimeField.getText());
                LocalTime endTime = LocalTime.parse(endTimeField.getText());
                String purpose = purposeField.getText();

                if (selectedClassroom == null) {
                    throw new IllegalArgumentException("Selecione uma sala.");
                }

                manager.makeReservation(selectedClassroom, loggedInUser, date, startTime, endTime, purpose);
                JOptionPane.showMessageDialog(dialog, "Reserva realizada com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshReservationList(); // Atualiza a tabela de reservas
                dialog.dispose();

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato de data ou hora inválido. Use AAAA-MM-DD e HH:MM.",
                        "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro de Entrada",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ReservationConflictException ex) { // Trata a exceção personalizada
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Conflito de Reserva",
                        JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        dialog.add(buttonPanel);

        dialog.pack();
        dialog.setVisible(true);
    }

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
            String observation = JOptionPane.showInputDialog(this, "Insira a justificativa para o cancelamento:",
                    "Justificativa do Cancelamento", JOptionPane.QUESTION_MESSAGE);
            if (observation == null || observation.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A justificativa é obrigatória para cancelar a reserva.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int reservationId = (int) reservationTableModel.getValueAt(selectedRow, 0);
            // Encontrar a reserva correta pelo ID (ou por objeto, se tiver uma referência)
            Reservation reservationToCancel = manager.getAllReservations().stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);
            if (reservationToCancel != null) {
                manager.cancelReservation(reservationToCancel, observation);
                JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshReservationList();
                if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao encontrar a reserva para cancelar.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}