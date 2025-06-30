package gui;

import manager.ReservationManager;
import model.Classroom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel de administração para gerenciamento de salas de aula.
 */
public class AdminClassroomPanel extends JPanel {
    protected ReservationManager manager;
    private JTable classroomTable;
    private DefaultTableModel classroomTableModel;
    private JButton addButton;
    private JButton removeButton;
    private Frame mainFrame;

    public AdminClassroomPanel(Frame mainFrame, ReservationManager manager) {
        this.manager = manager;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        initComponents();
        refreshClassroomList();
    }

    public void refreshClassroomList() {
        classroomTableModel.setRowCount(0);
        List<Classroom> classrooms = manager.getAllClassrooms();
        for (Classroom classroom : classrooms) {
            classroomTableModel.addRow(new Object[]{
                    classroom.getName(), classroom.getCapacity(), classroom.getLocation(),
                    classroom.hasProjector() ? "Sim" : "Não", String.join(", ", classroom.getFeatures())
            });
        }
    }
    
    // Método que será sobrescrito pela classe aninhada em MainFrame
    protected void onDataChanged() {
        // Atualiza os painéis na MainFrame (implementação padrão, pode ser sobrescrita)
        if (SwingUtilities.getWindowAncestor(this) instanceof MainFrame) {
            ((MainFrame) SwingUtilities.getWindowAncestor(this)).revalidate();
            ((MainFrame) SwingUtilities.getWindowAncestor(this)).repaint();
        }
    }

    private void initComponents() {
        String[] columnNames = {"Nome", "Capacidade", "Localização", "Projetor", "Características"};
        classroomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        classroomTable = new JTable(classroomTableModel);
        classroomTable.setFillsViewportHeight(true);
        add(new JScrollPane(classroomTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Adicionar Nova Sala");
        addButton.addActionListener(e -> addSelectedClassroom());
        buttonPanel.add(addButton);

        removeButton = new JButton("Remover Selecionada");
        removeButton.addActionListener(e -> removeSelectedClassroom());
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void addSelectedClassroom() {
        AddClassroomDialog dialog = new AddClassroomDialog(mainFrame, manager);
        dialog.setVisible(true);
        // Após adicionar, notificamos que os dados mudaram
        onDataChanged();
    }

    private void removeSelectedClassroom() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma sala para remover.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover esta sala?", "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String classroomName = (String) classroomTableModel.getValueAt(selectedRow, 0);
            Classroom classroomToRemove = manager.getAllClassrooms().stream()
                    .filter(r -> r.getName().equals(classroomName)).findFirst().orElse(null);

            if (classroomToRemove != null) {
                try {
                    manager.removeClassroom(classroomToRemove);
                    JOptionPane.showMessageDialog(this, "Sala removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    // Após remover, notificamos que os dados mudaram
                    onDataChanged();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}