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

        try {
            // Adicionar alguns dados de teste iniciais se os arquivos estiverem vazios/não existirem
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
            System.err.println("Erro ao adicionar usuários: " + e.getMessage());
        }

        
        setTitle("Sistema de Gerenciamento de Salas");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fechar a janela encerra a aplicação
        setLocationRelativeTo(null);
        
        // Define o ícone da aplicação
        setIcon();

        // Usa um painel principal para trocar os conteúdos
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Inicia mostrando o painel de login
        showLoginPanel();
    }

    private void setIcon() {
        try {
            // CORREÇÃO: Adicionada a barra "/" no início do caminho para torná-lo absoluto.
            URL iconURL = getClass().getResource("/resources/icon.png");
            
            if (iconURL != null) {
                setIconImage(new ImageIcon(iconURL).getImage());
            } else {
                // Mensagem de erro também corrigida para refletir o caminho buscado.
                System.err.println("Arquivo de ícone não encontrado: /resources/icon.png");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar o ícone: " + e.getMessage());
            e.printStackTrace(); // É bom ter o stack trace para depurar
        }
    }

    /**
     * Limpa a janela e exibe o painel de login.
     */
    private void showLoginPanel() {
        setTitle("Sistema de Gerenciamento de Salas - Login");
        mainPanel.removeAll();
        LoginPanel loginPanel = new LoginPanel(manager, this);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        loginPanel.clearFields(); // Garante que os campos estejam limpos
        
        // Atualiza a UI
        revalidate();
        repaint();
    }
    
    /**
     * Limpa a janela e exibe o painel principal da aplicação após o login.
     * @param loggedInUser O usuário que fez o login.
     */
    private void showMainApplicationPanel(User loggedInUser) {
        setTitle("Sistema de Gerenciamento de Salas - Logado como: " + loggedInUser.getName());
        mainPanel.removeAll();
        
        // Painel que contém as abas e o botão de logout
        JPanel appContentPanel = createAppPanel(loggedInUser);
        mainPanel.add(appContentPanel, BorderLayout.CENTER);

        // Atualiza a UI
        revalidate();
        repaint();
    }
    
    /**
     * Constrói o painel principal do sistema (com abas, etc.).
     * @param loggedInUser O usuário logado.
     * @return O painel construído.
     */
    private JPanel createAppPanel(User loggedInUser) {
        JPanel panel = new JPanel(new BorderLayout());

        // Painel superior com boas-vindas e logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel welcomeLabel = new JLabel("Bem-vindo(a), " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> performLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Painel de Abas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Instancia e adiciona os painéis das abas
        ClassroomPanel classroomPanel = new ClassroomPanel(manager);
        ReservationPanel reservationPanel = new ReservationPanel(manager, loggedInUser);
        CalendarPanel calendarPanel = new CalendarPanel(manager);
        
        tabbedPane.addTab("Salas de Aula", classroomPanel);
        tabbedPane.addTab("Minhas Reservas", reservationPanel);
        tabbedPane.addTab("Calendário de Reservas", calendarPanel);

        if (loggedInUser.getRole().equals("Administrator")) {

            AdminClassroomPanel adminClassroomPanel = new AdminClassroomPanel(this, manager, () -> refreshAllPanels());
            tabbedPane.addTab("Administração de Salas", adminClassroomPanel);
            tabbedPane.addTab("Gerenciar Reservas", new AdminReservationPanel(manager));
        }

        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Realiza o logout, salvando os dados e retornando à tela de login.
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja fazer logout?", "Logout",
                        JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.saveData();
            showLoginPanel();
        }
    }

    /**
     * Chamado pelo LoginPanel quando o login é bem-sucedido.
     * @param loggedInUser O usuário que acabou de autenticar.
     */
    @Override
    public void onLoginSuccess(User loggedInUser) {
        // Troca para o painel principal da aplicação
        showMainApplicationPanel(loggedInUser);
    }

    /**
     * Método para atualizar todos os painéis relevantes.
     * @param tabbedPane O painel de abas contendo os painéis a serem atualizados.
     */
        /**
     * Este método agora é chamado pelo callback. Ele atualiza todos os painéis
     * que precisam refletir as novas mudanças de dados.
     */
    private void refreshAllPanels() {
        if (this.tabbedPane == null) return;
        
        // Itera por todos os componentes dentro do painel de abas
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
     * Adicionamos uma sobrecarga para o construtor de AdminClassroomPanel
     * para passar um callback de atualização.
     */
    public class AdminClassroomPanel extends gui.AdminClassroomPanel {
        private Runnable refreshCallback;

        public AdminClassroomPanel(Frame mainFrame, ReservationManager manager, Runnable refreshCallback) {
            super(mainFrame, manager);
            this.refreshCallback = refreshCallback;
        }

        @Override
        protected void onDataChanged() {
            refreshCallback.run();
        }
    }
}