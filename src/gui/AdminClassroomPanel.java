package gui;

import manager.ReservationManager;
import model.Classroom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel de administração para gerenciamento de salas de aula.
 * Permite adicionar, remover e visualizar salas cadastradas.
 */
public class AdminClassroomPanel extends JPanel {
    private ReservationManager manager;
    private JTable classroomTable;
    private DefaultTableModel classroomTableModel;
    private JButton addButton;
    private JButton removeButton;
    private Frame mainFrame;

    /**
     * Construtor do painel de administração de salas.
     *
     * @param mainFrame Janela principal da aplicação.
     * @param manager   Gerenciador de reservas e salas.
     */
    public AdminClassroomPanel(Frame mainFrame, ReservationManager manager) {
        this.manager = manager;
        this.mainFrame=mainFrame;
        setLayout(new BorderLayout());
        initComponents();
        refreshClassroomList(); // Carrega todas as reservas ao iniciar
    }

    /**
     * Atualiza a tabela de salas exibindo todas as salas cadastradas.
     */
    public void refreshClassroomList() {
        classroomTableModel.setRowCount(0); // Limpa a tabela
        List<Classroom> classrooms = manager.getAllClassrooms();
        for (Classroom classroom : classrooms) {
            classroomTableModel.addRow(new Object[] {
                    classroom.getName(),
                    classroom.getCapacity(),
                    classroom.getLocation(),
                    classroom.hasProjector() ? "Sim" : "Não",
                    String.join(", ", classroom.getFeatures())
            });
        }
    }

    /**
     * Inicializa os componentes gráficos do painel.
     */
    private void initComponents() {
        // Configuração da tabela de reservas (agora exibirá todas)
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

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Adicionar Nova Sala");
        addButton.addActionListener(e -> addSelectedClassroom());
        buttonPanel.add(addButton);

        removeButton = new JButton("Remover Selecionada");
        removeButton.addActionListener(e -> removeSelectedClassroom());
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    /**
     * Abre o diálogo para adicionar uma nova sala e atualiza a lista após a adição.
     */
    private void addSelectedClassroom(){
        AddClassroomDialog dialog = new AddClassroomDialog(mainFrame, manager);
        dialog.setVisible(true);
        refreshClassroomList(); // Atualiza a lista após adicionar
    }

    /**
     * Remove a sala selecionada na tabela, após confirmação do usuário.
     * Exibe mensagens de erro caso não haja seleção ou ocorra algum problema.
     */
    private void removeSelectedClassroom() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma sala para confirmar.", "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover esta sala?",
                "Confirmar removimento de sala", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String classroomName = (String) classroomTableModel.getValueAt(selectedRow, 0);
            Classroom classroomToRemove = manager.getAllClassrooms().stream()
                    .filter(r -> r.getName() == classroomName)
                    .findFirst()
                    .orElse(null);
            if (classroomToRemove != null) {
                try {
                    manager.removeClassroom(classroomToRemove);
                    JOptionPane.showMessageDialog(this, "Sala removida com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshClassroomList();
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
    }

}