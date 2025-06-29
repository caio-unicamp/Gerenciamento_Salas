package gui;

import manager.ReservationManager;
import model.Classroom;

import javax.swing.*; 
import java.awt.*;

/**
 * Diálogo para adicionar uma nova sala de aula ao sistema.
 */
public class AddClassroomDialog extends JDialog {
    private JTextField nameField;
    private JSpinner capacitySpinner;
    private JTextField locationField;
    private JCheckBox projectorCheckBox;
    private JTextField featuresField;
    private JButton addButton;

    private ReservationManager manager;

    /**
     * Construtor do diálogo de adição de sala.
     *
     * @param parent  Janela pai (Frame) para modalidade.
     * @param manager Gerenciador de reservas para adicionar a sala criada.
     */
    public AddClassroomDialog(Frame parent, ReservationManager manager) {
        super(parent, "Adicionar Nova Sala", true); 
        this.manager = manager;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    /**
     * Inicializa os componentes gráficos do diálogo.
     */
    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nome da Sala:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Capacidade:"));
        capacitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));
        formPanel.add(capacitySpinner);

        formPanel.add(new JLabel("Localização:"));
        locationField = new JTextField(20);
        formPanel.add(locationField);

        formPanel.add(new JLabel("Tem Projetor?"));
        projectorCheckBox = new JCheckBox();
        formPanel.add(projectorCheckBox);

        formPanel.add(new JLabel("Características:"));
        featuresField = new JTextField(30);
        formPanel.add(featuresField);

        addButton = new JButton("Adicionar Sala");
        addButton.addActionListener(e -> addClassroom());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Realiza a validação dos campos e adiciona a nova sala ao sistema.
     * Exibe mensagens de erro caso algum campo obrigatório não seja preenchido.
     * Fecha o diálogo ao adicionar com sucesso.
     */
    private void addClassroom() {
        String name = nameField.getText().trim();
        int capacity = (Integer) capacitySpinner.getValue();
        String location = locationField.getText().trim();
        boolean hasProjector = projectorCheckBox.isSelected();
        String featuresText = featuresField.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Localização da sala não podem ser vazios.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Classroom newClassroom = new Classroom(name, capacity, location, hasProjector);
        if (!featuresText.isEmpty()) {
            String[] featuresArray = featuresText.split(",");
            for (String feature : featuresArray) {
                newClassroom.addFeature(feature.trim());
            }
        }

        manager.addClassroom(newClassroom);
        JOptionPane.showMessageDialog(this, "Sala adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Fecha a janela após adicionar
    }
}