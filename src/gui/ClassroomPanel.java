package gui;

import manager.ReservationManager;
import model.Classroom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para exibir as salas de aula.
 */
public class ClassroomPanel extends JPanel {  
    private ReservationManager manager;
    private JTable classroomTable;
    private DefaultTableModel classroomTableModel;

    /**
     * Construtor do painel de salas de aula.
     * @param manager O gerenciador de reservas.
     */
    public ClassroomPanel(ReservationManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        initComponents();
        refreshClassroomList();
    }

    /**
     * Inicializa os componentes da interface gráfica.
     */
    private void initComponents() {
        String[] columnNames = {"Nome", "Capacidade", "Localização", "Projetor", "Características"};
        classroomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classroomTable = new JTable(classroomTableModel);
        classroomTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(classroomTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Atualiza a lista de salas de aula na tabela.
     */
    public void refreshClassroomList() {
        classroomTableModel.setRowCount(0);
        List<Classroom> classrooms = manager.getAllClassrooms();
        for (Classroom classroom : classrooms) {
            classroomTableModel.addRow(new Object[]{
                classroom.getName(),
                classroom.getCapacity(),
                classroom.getLocation(),
                classroom.hasProjector() ? "Sim" : "Não",
                String.join(", ", classroom.getFeatures())
            });
        }
    }

    /**
     * Obtém o modelo da tabela de salas de aula.
     * @return O modelo da tabela.
     */
    public DefaultTableModel getClassroomTableModel() {
        return classroomTableModel;
    }
}