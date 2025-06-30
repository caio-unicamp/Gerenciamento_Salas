package gui;

import manager.ReservationManager;
import model.Reservation;
import exception.ReservationConflictException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * Painel de administração para gerenciamento de reservas.
 */
public class AdminReservationPanel extends JPanel {
    private ReservationManager manager;
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private Runnable onDataChangedCallback; // Adicionado

    /**
     * Construtor do painel de administração de reservas.
     * @param manager O gerenciador de reservas.
     * @param onDataChangedCallback Callback a ser chamado quando os dados mudam.
     */
    public AdminReservationPanel(ReservationManager manager, Runnable onDataChangedCallback) { // Modificado
        this.manager = manager;
        this.onDataChangedCallback = onDataChangedCallback; // Adicionado
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshReservationsList();
    }

    /**
     * Inicializa os componentes da interface gráfica.
     */
    private void initComponents() {
        String[] columnNames = {"ID", "Sala", "Usuário", "Data", "Início", "Término", "Propósito", "Status", "Observações"};
        reservationsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setFillsViewportHeight(true);
        reservationsTable.setRowHeight(28);
        reservationsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        reservationsTable.setShowVerticalLines(false);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(reservationsTableModel);
        reservationsTable.setRowSorter(sorter);

        hideIdColumn(reservationsTable);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton confirmButton = new JButton("Confirmar");
        confirmButton.addActionListener(e -> confirmSelectedReservation());
        buttonPanel.add(confirmButton);

        JButton rejectButton = new JButton("Rejeitar");
        rejectButton.addActionListener(e -> rejectSelectedReservation());
        buttonPanel.add(rejectButton);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> cancelSelectedReservation());
        buttonPanel.add(cancelButton);

        JButton deleteButton = new JButton("Deletar");
        deleteButton.putClientProperty("JButton.buttonType", "primary");
        deleteButton.addActionListener(e -> deleteSelectedReservation());
        buttonPanel.add(deleteButton);
        
        buttonPanel.add(Box.createHorizontalGlue());

        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> refreshReservationsList());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Oculta a coluna de ID na tabela.
     * @param table A tabela na qual a coluna será ocultada.
     */
    private void hideIdColumn(JTable table) {
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);
        tcm.getColumn(0).setWidth(0);
        tcm.getColumn(0).setPreferredWidth(0);
        tcm.getColumn(0).setResizable(false);
    }

    /**
     * Atualiza a lista de reservas na tabela.
     */
    public void refreshReservationsList() {
        int selectedRow = getSelectedModelRow();
        
        reservationsTableModel.setRowCount(0);
        List<Reservation> allReservations = manager.getAllReservations();
        allReservations.sort(Comparator.comparing(Reservation::getDate).thenComparing(Reservation::getStartTime).reversed());

        for (Reservation reservation : allReservations) {
            reservationsTableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getClassroom().getName(),
                    reservation.getReservedBy().getUsername(),
                    reservation.getDate(),
                    reservation.getStartTime(),
                    reservation.getEndTime(),
                    reservation.getPurpose(),
                    reservation.getStatus().getName(),
                    reservation.getObservation()
            });
        }
        
        if (selectedRow != -1 && selectedRow < reservationsTableModel.getRowCount()) {
             int viewRow = reservationsTable.convertRowIndexToView(selectedRow);
             reservationsTable.setRowSelectionInterval(viewRow, viewRow);
        }
    }
    
    /**
     * Obtém a linha do modelo selecionada na tabela.
     * @return O índice da linha do modelo selecionada, ou -1 se nenhuma linha estiver selecionada.
     */
    private int getSelectedModelRow() {
        int selectedViewRow = reservationsTable.getSelectedRow();
        if (selectedViewRow != -1) {
            return reservationsTable.convertRowIndexToModel(selectedViewRow);
        }
        return -1;
    }

    /**
     * Obtém a reserva selecionada na tabela.
     * @return A reserva selecionada, ou null se nenhuma reserva for selecionada.
     */
    private Reservation getSelectedReservation() {
        int selectedRow = getSelectedModelRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva na tabela.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        return manager.getAllReservations().stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Confirma a reserva selecionada.
     */
    private void confirmSelectedReservation() {
        Reservation reservationToConfirm = getSelectedReservation();
        if (reservationToConfirm == null) return;

        try {
            manager.confirmReservation(reservationToConfirm);
            JOptionPane.showMessageDialog(this, "Reserva confirmada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (onDataChangedCallback != null) { // Adicionado
                onDataChangedCallback.run();
            }
        } catch (IllegalArgumentException | ReservationConflictException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Confirmar", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Rejeita a reserva selecionada.
     */
    private void rejectSelectedReservation() {
        Reservation reservationToReject = getSelectedReservation();
        if (reservationToReject == null) return;

        String observation = JOptionPane.showInputDialog(this, "Insira a justificativa para a rejeição:", "Rejeitar Reserva", JOptionPane.QUESTION_MESSAGE);
        if (observation == null || observation.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "A justificativa é obrigatória para rejeitar a reserva.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            manager.rejectReservation(reservationToReject, observation);
            JOptionPane.showMessageDialog(this, "Reserva rejeitada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (onDataChangedCallback != null) { // Adicionado
                onDataChangedCallback.run();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Rejeitar", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Cancela a reserva selecionada.
     */
    private void cancelSelectedReservation() {
        Reservation reservationToCancel = getSelectedReservation();
        if (reservationToCancel == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja CANCELAR esta reserva?", "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String observation = JOptionPane.showInputDialog(this, "Insira a justificativa para o cancelamento:", "Cancelar Reserva", JOptionPane.QUESTION_MESSAGE);
        if (observation == null) return;

        try {
            manager.cancelReservation(reservationToCancel, observation);
            JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (onDataChangedCallback != null) { // Adicionado
                onDataChangedCallback.run();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao cancelar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Exclui a reserva selecionada.
     */
    private void deleteSelectedReservation() {
        Reservation reservationToDelete = getSelectedReservation();
        if (reservationToDelete == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Esta ação é irreversível.\nTem certeza que deseja DELETAR esta reserva?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            manager.deleteReservation(reservationToDelete);
            JOptionPane.showMessageDialog(this, "Reserva deletada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (onDataChangedCallback != null) { // Adicionado
                onDataChangedCallback.run();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}