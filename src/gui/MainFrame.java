package gui;

import manager.ReservationManager;
import model.Administrator;
import model.Classroom;
import model.Student;
import model.User;

import javax.swing.*;
import exception.UserConflictException;
import java.awt.*;
import java.net.URL;

/**
 * O frame principal da aplicação.
 */
public class MainFrame extends JFrame implements LoginListener {
    private ReservationManager manager;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    /**
     * Construtor do MainFrame.
     */
    public MainFrame() {
        this.manager = new ReservationManager();

        initializeDefaultData();

        setTitle("Sistema de Gerenciamento de Salas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setIcon();

        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        showLoginPanel();
    }

    /**
     * Inicializa os dados padrão.
     */
    private void initializeDefaultData() {
        try {
            if (manager.getAllClassrooms().isEmpty()) {
                manager.addClassroom(new Classroom("Sala A101", 30, "Prédio A", true));
                manager.addClassroom(new Classroom("Laboratório B205", 20, "Prédio B", true));
                manager.addClassroom(new Classroom("Auditório Principal", 100, "Prédio C", true));
                manager.addClassroom(new Classroom("Sala de Reuniões D301", 8, "Prédio D", false));
            }

            if (manager.getAllUsers().isEmpty()) {
                manager.addUser(new Administrator("admin", "admin123", "Administrador Principal", "admin@unicamp.br"));
                manager.addUser(new Student("aluno1", "aluno123", "João Silva", "joao.silva@unicamp.br", "RA123456"));
                manager.addUser(new Student("aluno2", "aluno123", "Maria Oliveira", "maria.oliveria@unicamp.br", "RA654321"));
            }
        } catch (UserConflictException e) {
            System.err.println("Erro ao adicionar usuários padrão: " + e.getMessage());
        }
    }

    /**
     * Define o ícone da aplicação.
     */
    private void setIcon() {
        URL iconURL = getClass().getResource("/resources/icon.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Arquivo de ícone não encontrado: /resources/icon.png");
        }
    }

    /**
     * Chamado quando o login é bem-sucedido.
     * @param loggedInUser O usuário que fez login.
     */
    @Override
    public void onLoginSuccess(User loggedInUser) {
        showMainApplicationPanel(loggedInUser);
    }

    /**
     * Mostra o painel de login.
     */
    private void showLoginPanel() {
        setTitle("Sistema de Gerenciamento de Salas - Login");
        
        setMinimumSize(null);

        mainPanel.removeAll();
        LoginPanel loginPanel = new LoginPanel(manager, this);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        loginPanel.clearFields();

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * Mostra o painel principal da aplicação.
     * @param loggedInUser O usuário que fez login.
     */
    private void showMainApplicationPanel(User loggedInUser) {
        setTitle("Sistema de Gerenciamento de Salas");
        mainPanel.removeAll();
        JPanel appContentPanel = createAppPanel(loggedInUser);
        mainPanel.add(appContentPanel, BorderLayout.CENTER);

        setSize(1200, 800);
        
        setMinimumSize(new Dimension(900, 600));

        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * Cria o painel da aplicação.
     * @param loggedInUser O usuário que fez login.
     * @return O painel da aplicação.
     */
    private JPanel createAppPanel(User loggedInUser) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JLabel welcomeLabel = new JLabel("Bem-vindo(a), " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> performLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        this.tabbedPane = new JTabbedPane();

        ClassroomPanel classroomPanel = new ClassroomPanel(manager);
        ReservationPanel reservationPanel = new ReservationPanel(manager, loggedInUser);
        CalendarPanel calendarPanel = new CalendarPanel(manager);

        tabbedPane.addTab("Salas de Aula", classroomPanel);
        tabbedPane.addTab("Minhas Reservas", reservationPanel);
        tabbedPane.addTab("Calendário", calendarPanel);

        if (loggedInUser.getRole().equals("Administrator")) {
            AdminClassroomPanel adminClassroomPanel = new AdminClassroomPanel(this, manager, this::refreshAllPanels);
            AdminReservationPanel adminReservationPanel = new AdminReservationPanel(manager);

            tabbedPane.addTab("Admin: Salas", adminClassroomPanel);
            tabbedPane.addTab("Admin: Reservas", adminReservationPanel);
        }

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Realiza o logout do usuário.
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja fazer logout?", "Confirmar Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.saveData();
            showLoginPanel();
        }
    }

    /**
     * Atualiza todos os painéis.
     */
    private void refreshAllPanels() {
        if (this.tabbedPane == null) return;

        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof ClassroomPanel) ((ClassroomPanel) comp).refreshClassroomList();
            if (comp instanceof ReservationPanel) ((ReservationPanel) comp).refreshReservationList();
            if (comp instanceof CalendarPanel) ((CalendarPanel) comp).refreshCalendarAndReservations();
            if (comp instanceof AdminReservationPanel) ((AdminReservationPanel) comp).refreshReservationsList();
            if (comp instanceof AdminClassroomPanel) ((AdminClassroomPanel) comp).refreshClassroomList();
        }
        System.out.println("Painéis atualizados via callback.");
    }

    /**
     * Painel de administração de salas de aula.
     */
    public class AdminClassroomPanel extends gui.AdminClassroomPanel {
        private Runnable refreshCallback;

        /**
         * Construtor do painel de administração de salas de aula.
         * @param mainFrame O frame principal.
         * @param manager O gerenciador de reservas.
         * @param refreshCallback O callback de atualização.
         */
        public AdminClassroomPanel(Frame mainFrame, ReservationManager manager, Runnable refreshCallback) {
            super(mainFrame, manager);
            this.refreshCallback = refreshCallback;
        }

        /**
         * Chamado quando os dados são alterados.
         */
        @Override
        protected void onDataChanged() {
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        }
    }
}