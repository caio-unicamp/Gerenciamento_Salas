package gui;

import manager.ReservationManager;
import model.Reservation;
import model.ReservationStatus;
import exception.ReservationConflictException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminReservationPanel extends JPanel {
    private ReservationManager manager;
    private JTable reservationsTable; // Renomeado para refletir que exibe todas
    private DefaultTableModel reservationsTableModel; // Renomeado
    private JButton confirmButton;
    private JButton rejectButton;
    private JButton cancelButton; // NOVO BOTÃO PARA CANCELAR
    private JButton deleteButton;
    private JButton refreshButton;

    public AdminReservationPanel(ReservationManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        initComponents();
        refreshReservationsList(); // Carrega todas as reservas ao iniciar
    }

    private void initComponents() {
        // Configuração da tabela de reservas (agora exibirá todas)
        String[] columnNames = {"ID", "Sala", "Usuário", "Data", "Início", "Término", "Propósito", "Status"};
        reservationsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        confirmButton = new JButton("Confirmar Selecionada");
        confirmButton.addActionListener(e -> confirmSelectedReservation());
        buttonPanel.add(confirmButton);

        rejectButton = new JButton("Rejeitar Selecionada");
        rejectButton.addActionListener(e -> rejectSelectedReservation());
        buttonPanel.add(rejectButton);

        cancelButton = new JButton("Cancelar Selecionada"); // NOVO BOTÃO
        cancelButton.addActionListener(e -> cancelSelectedReservation()); // NOVO LISTENER
        buttonPanel.add(cancelButton);

        deleteButton = new JButton("Deletar Selecionada"); // NOVO BOTÃO
        deleteButton.addActionListener(e -> deleteSelectedReservation()); // NOVO LISTENER
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Atualizar Lista");
        refreshButton.addActionListener(e -> refreshReservationsList());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Renomeado e modificado para exibir TODAS as reservas, não apenas pendentes
    public void refreshReservationsList() {
        reservationsTableModel.setRowCount(0); // Limpa a tabela
        List<Reservation> allReservations = manager.getAllReservations(); // Pega TODAS as reservas
        for (Reservation reservation : allReservations) {
            reservationsTableModel.addRow(new Object[]{
                reservation.getId(),
                reservation.getClassroom().getName(),
                reservation.getReservedBy().getUsername(),
                reservation.getDate().toString(),
                reservation.getStartTime().toString(),
                reservation.getEndTime().toString(),
                reservation.getPurpose(),
                reservation.getStatus().getName()
            });
        }
    }

    private void confirmSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para confirmar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        Reservation reservationToConfirm = manager.getAllReservations().stream()
                                                    .filter(r -> r.getId() == reservationId)
                                                    .findFirst()
                                                    .orElse(null);
        if (reservationToConfirm != null) {
            try {
                manager.confirmReservation(reservationToConfirm);
                JOptionPane.showMessageDialog(this, "Reserva confirmada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                refreshReservationsList(); // Atualiza a lista
                if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (ReservationConflictException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Conflito de Confirmação", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Reserva não encontrada para confirmação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para rejeitar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        Reservation reservationToReject = manager.getAllReservations().stream()
                                                  .filter(r -> r.getId() == reservationId)
                                                  .findFirst()
                                                  .orElse(null);
        if (reservationToReject != null) {
            try {
                manager.rejectReservation(reservationToReject);
                JOptionPane.showMessageDialog(this, "Reserva rejeitada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                refreshReservationsList(); // Atualiza a lista
                if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Reserva não encontrada para rejeição.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NOVO MÉTODO PARA CANCELAR RESERVAS
    private void cancelSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja CANCELAR esta reserva?", "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
            Reservation reservationToCancel = manager.getAllReservations().stream()
                                                    .filter(r -> r.getId() == reservationId)
                                                    .findFirst()
                                                    .orElse(null);
            if (reservationToCancel != null) {
                try {
                    manager.cancelReservation(reservationToCancel); // Reutiliza o método do manager
                    JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshReservationsList(); // Atualiza a lista
                    if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                        ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocorreu um erro ao cancelar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reserva não encontrada para cancelar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para deletar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja DELETAR esta reserva?", "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
            Reservation reservationToDelete = manager.getAllReservations().stream()
                                                        .filter(r -> r.getId() == reservationId)
                                                        .findFirst()
                                                        .orElse(null);
            if (reservationToDelete != null) {
                try {
                    manager.deleteReservation(reservationToDelete);
                    JOptionPane.showMessageDialog(this, "Reserva deletada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshReservationsList(); // Atualiza a lista
                    if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                        ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reserva não encontrada para deletar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}