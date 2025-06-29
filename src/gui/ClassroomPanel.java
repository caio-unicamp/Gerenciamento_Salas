package gui;

import manager.ReservationManager;
import model.Classroom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para visualização das salas cadastradas no sistema.
 */
public class ClassroomPanel extends JPanel {  
    private ReservationManager manager;
    private JTable classroomTable;
    private DefaultTableModel classroomTableModel;

    /**
     * Construtor do painel de salas.
     *
     * @param manager Gerenciador de reservas e salas.
     */
    public ClassroomPanel(ReservationManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        initComponents();
        refreshClassroomList();
    }

    /**
     * Inicializa os componentes gráficos do painel de salas.
     */
    private void initComponents() {
        // Configuração da tabela de salas
        String[] columnNames = {"Nome", "Capacidade", "Localização", "Projetor", "Características"};
        classroomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        classroomTable = new JTable(classroomTableModel);
        classroomTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(classroomTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Atualiza a tabela exibindo todas as salas cadastradas.
     */
    public void refreshClassroomList() {
        classroomTableModel.setRowCount(0); // Limpa a tabela
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
     * Retorna o modelo da tabela de salas.
     *
     * @return DefaultTableModel utilizado pela tabela de salas.
     */
    public DefaultTableModel getClassroomTableModel() {
        return classroomTableModel;
    }
}