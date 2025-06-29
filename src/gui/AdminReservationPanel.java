package gui;

import manager.ReservationManager;
import model.Reservation;
import exception.ReservationConflictException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.util.List;

/**
 * Painel de administração para gerenciamento de reservas.
 * Permite confirmar, rejeitar, cancelar, deletar e visualizar reservas cadastradas.
 */
public class AdminReservationPanel extends JPanel {
    private ReservationManager manager;
    private JTable reservationsTable; 
    private DefaultTableModel reservationsTableModel; 
    private JButton confirmButton;
    private JButton rejectButton;
    private JButton cancelButton; 
    private JButton deleteButton;
    private JButton refreshButton;

    /**
     * Construtor do painel de administração de reservas.
     *
     * @param manager Gerenciador de reservas.
     */
    public AdminReservationPanel(ReservationManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        initComponents();
        refreshReservationsList(); // Carrega todas as reservas ao iniciar
    }

    /**
     * Inicializa os componentes gráficos do painel.
     */
    private void initComponents() {
        // Configuração da tabela de reservas
        String[] columnNames = {"ID", "Sala", "Usuário", "Data", "Início", "Término", "Propósito", "Status", "Observações"};
        reservationsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setFillsViewportHeight(true);

        reservationsTable.setAutoCreateColumnsFromModel(true);
        TableColumnModel tcm = reservationsTable.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);
        tcm.getColumn(0).setWidth(0);
        tcm.getColumn(0).setPreferredWidth(0);
        tcm.getColumn(0).setResizable(false);

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

        cancelButton = new JButton("Cancelar Selecionada"); 
        cancelButton.addActionListener(e -> cancelSelectedReservation());
        buttonPanel.add(cancelButton);

        deleteButton = new JButton("Deletar Selecionada");
        deleteButton.addActionListener(e -> deleteSelectedReservation()); 
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Atualizar Lista");
        refreshButton.addActionListener(e -> refreshReservationsList());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Atualiza a tabela exibindo todas as reservas cadastradas.
     */
    public void refreshReservationsList() {
        reservationsTableModel.setRowCount(0); // Limpa a tabela
        List<Reservation> allReservations = manager.getAllReservations();
        for (Reservation reservation : allReservations) {
            reservationsTableModel.addRow(new Object[] {
                    reservation.getId(),
                    reservation.getClassroom().getName(),
                    reservation.getReservedBy().getUsername(),
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
     * Confirma a reserva selecionada na tabela, após confirmação do usuário.
     * Exibe mensagens de erro caso não haja seleção ou ocorra algum problema.
     */
    private void confirmSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para confirmar.", "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Reserva confirmada com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshReservationsList();
                if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (ReservationConflictException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Conflito de Confirmação",
                        JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Reserva não encontrada para confirmação.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Rejeita a reserva selecionada na tabela, solicitando justificativa do usuário.
     * Exibe mensagens de erro caso não haja seleção, justificativa ou ocorra algum problema.
     */
    private void rejectSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para rejeitar.", "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja REJEITAR esta reserva?",
                "Confirmar Rejeição", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {

            String observation = JOptionPane.showInputDialog(this, "Insira a justificativa para a rejeição:",
                    "Justificativa da Rejeição", JOptionPane.QUESTION_MESSAGE);
            if (observation == null || observation.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A justificativa é obrigatória para rejeitar a reserva.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
            Reservation reservationToReject = manager.getAllReservations().stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);
            if (reservationToReject != null) {
                try {
                    manager.rejectReservation(reservationToReject, observation);
                    JOptionPane.showMessageDialog(this, "Reserva rejeitada com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshReservationsList();
                    if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                        ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reserva não encontrada para rejeição.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

<<<<<<< HEAD
=======
    /**
     * Cancela a reserva selecionada na tabela, solicitando justificativa do usuário.
     * Exibe mensagens de erro caso não haja seleção, justificativa ou ocorra algum problema.
     */
>>>>>>> 3d08e3f913bc2cc9b87f52bd9423501f79380acd
    private void cancelSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja CANCELAR esta reserva?",
                "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String observation = JOptionPane.showInputDialog(this, "Insira a justificativa para o cancelamento:",
                    "Justificativa do Cancelamento", JOptionPane.QUESTION_MESSAGE);
            if (observation == null || observation.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A justificativa é obrigatória para cancelar a reserva.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
            Reservation reservationToCancel = manager.getAllReservations().stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);
            if (reservationToCancel != null) {
                try {
                    manager.cancelReservation(reservationToCancel, observation);
                    JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshReservationsList();
                    if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                        ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocorreu um erro ao cancelar: " + ex.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reserva não encontrada para cancelar.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deleta a reserva selecionada na tabela, após confirmação do usuário.
     * Exibe mensagens de erro caso não haja seleção ou ocorra algum problema.
     */
    private void deleteSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para deletar.", "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja DELETAR esta reserva?",
                "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
            Reservation reservationToDelete = manager.getAllReservations().stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);
            if (reservationToDelete != null) {
                try {
                    manager.deleteReservation(reservationToDelete);
                    JOptionPane.showMessageDialog(this, "Reserva deletada com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshReservationsList();
                    if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
                        ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshPanels();
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reserva não encontrada para deletar.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}