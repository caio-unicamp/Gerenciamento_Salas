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
 * A janela principal (JFrame) única da aplicação.
 * Gerencia a exibição do painel de login e do painel principal do sistema.
 * Implementa LoginListener para saber quando trocar de painel.
 */
public class MainFrame extends JFrame implements LoginListener {
    private ReservationManager manager;
    private JPanel mainPanel; // Painel principal que troca de conteúdo
    private JTabbedPane tabbedPane; // Manter referência ao painel de abas

    public MainFrame() {
        this.manager = new ReservationManager();

        // Adiciona dados de teste iniciais se os arquivos estiverem vazios.
        initializeDefaultData();

        setTitle("Sistema de Gerenciamento de Salas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setIcon();

        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        showLoginPanel();
    }

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

    private void setIcon() {
        // O caminho para o recurso deve ser absoluto a partir da raiz do classpath.
        URL iconURL = getClass().getResource("/resources/icon.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Arquivo de ícone não encontrado: /resources/icon.png");
        }
    }

    @Override
    public void onLoginSuccess(User loggedInUser) {
        showMainApplicationPanel(loggedInUser);
    }

    private void showLoginPanel() {
        setTitle("Sistema de Gerenciamento de Salas - Login");
        mainPanel.removeAll();
        LoginPanel loginPanel = new LoginPanel(manager, this);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        loginPanel.clearFields();
        pack(); // Ajusta o tamanho da janela ao conteúdo do painel de login
        setLocationRelativeTo(null); // Centraliza a janela menor
        revalidate();
        repaint();
    }

    private void showMainApplicationPanel(User loggedInUser) {
        setTitle("Sistema de Gerenciamento de Salas");
        mainPanel.removeAll();
        JPanel appContentPanel = createAppPanel(loggedInUser);
        mainPanel.add(appContentPanel, BorderLayout.CENTER);
        setSize(1200, 800); // Define o tamanho maior para a tela principal
        setLocationRelativeTo(null); // Centraliza a janela maior
        revalidate();
        repaint();
    }

    private JPanel createAppPanel(User loggedInUser) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior com boas-vindas e logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JLabel welcomeLabel = new JLabel("Bem-vindo(a), " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        // Adicionar ícone ao botão de logout
        // logoutButton.setIcon(new FlatSVGIcon("resources/logout.svg"));
        logoutButton.addActionListener(e -> performLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Painel de Abas
        this.tabbedPane = new JTabbedPane();

        // Instancia e adiciona os painéis das abas
        ClassroomPanel classroomPanel = new ClassroomPanel(manager);
        ReservationPanel reservationPanel = new ReservationPanel(manager, loggedInUser);
        CalendarPanel calendarPanel = new CalendarPanel(manager);

        // Adicionar ícones às abas
        // tabbedPane.addTab("Salas", new FlatSVGIcon("resources/classroom.svg"), classroomPanel);
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

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja fazer logout?", "Confirmar Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.saveData();
            showLoginPanel();
        }
    }

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

    // Classe aninhada para passar o callback de atualização
    public class AdminClassroomPanel extends gui.AdminClassroomPanel {
        private Runnable refreshCallback;

        public AdminClassroomPanel(Frame mainFrame, ReservationManager manager, Runnable refreshCallback) {
            super(mainFrame, manager);
            this.refreshCallback = refreshCallback;
        }

        @Override
        protected void onDataChanged() {
            // Em vez de repintar o frame inteiro, chama o callback para atualizar todos os painéis
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        }
    }
}