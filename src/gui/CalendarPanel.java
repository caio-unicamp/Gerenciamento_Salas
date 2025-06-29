package gui;

import manager.ReservationManager;
import model.Reservation;
import model.ReservationStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder; 
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Comparator;

public class CalendarPanel extends JPanel {
    private ReservationManager manager;
    private JPanel calendarGridPanel;
    private JLabel monthYearLabel;
    private JButton prevMonthButton;
    private JButton nextMonthButton;
    private JTable reservationsForDayTable;
    private DefaultTableModel reservationsForDayTableModel;

    private YearMonth currentMonth;
    private LocalDate selectedDate;

    private static final Locale BRAZIL_LOCALE = Locale.of("pt", "BR");

    // Cores personalizadas
    private static final Color HEADER_BG_COLOR = new Color(70, 130, 180); // SteelBlue
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Color WEEKDAY_COLOR = new Color(50, 50, 50); // Cinza escuro
    private static final Color SELECTED_DAY_COLOR = new Color(173, 216, 230); // LightBlue
    private static final Color TODAY_COLOR = new Color(255, 223, 186); // Pêssego claro (para o dia atual)
    private static final Color RESERVATION_DAY_BORDER_COLOR = new Color(0, 100, 0); // DarkGreen

    public CalendarPanel(ReservationManager manager) {
        this.manager = manager;
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        // Aumenta o espaçamento entre os componentes e as margens
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Margens maiores
        setBackground(new Color(240, 240, 240)); // Fundo cinza claro para o painel principal

        initComponents();
        updateCalendar();
        displayReservationsForSelectedDay();
    }

    private void initComponents() {
        // --- Painel de Navegação do Mês (Top) ---
        JPanel monthNavPanel = new JPanel(new BorderLayout(10, 0)); // Espaçamento horizontal
        monthNavPanel.setBackground(HEADER_BG_COLOR); // Cor de fundo do cabeçalho
        monthNavPanel.setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding interno

        prevMonthButton = new JButton("<<"); // Ícones ou texto mais curto
        prevMonthButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        prevMonthButton.setForeground(HEADER_TEXT_COLOR);
        prevMonthButton.setBackground(HEADER_BG_COLOR);
        prevMonthButton.setBorderPainted(false); // Remove borda padrão
        prevMonthButton.setFocusPainted(false); // Remove foco ao clicar
        prevMonthButton.addActionListener(e -> navigateMonth(-1));

        nextMonthButton = new JButton(">>"); // Ícones ou texto mais curto
        nextMonthButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        nextMonthButton.setForeground(HEADER_TEXT_COLOR);
        nextMonthButton.setBackground(HEADER_BG_COLOR);
        nextMonthButton.setBorderPainted(false);
        nextMonthButton.setFocusPainted(false);
        nextMonthButton.addActionListener(e -> navigateMonth(1));

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 22)); // Fonte maior e negrito
        monthYearLabel.setForeground(HEADER_TEXT_COLOR); // Cor do texto

        monthNavPanel.add(prevMonthButton, BorderLayout.WEST);
        monthNavPanel.add(monthYearLabel, BorderLayout.CENTER);
        monthNavPanel.add(nextMonthButton, BorderLayout.EAST);
        add(monthNavPanel, BorderLayout.NORTH);

        // --- Grade do Calendário ---
        calendarGridPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // Aumenta o espaçamento entre as células
        calendarGridPanel.setBackground(Color.WHITE); // Fundo branco para o grid do calendário
        calendarGridPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(5, 5, 5, 5), // Padding interno para a grade
                new LineBorder(Color.LIGHT_GRAY, 1) // Borda ao redor da grade
        ));
        add(calendarGridPanel, BorderLayout.CENTER);

        // --- Painel de Reservas para o Dia Selecionado ---
        JPanel reservationsPanel = new JPanel(new BorderLayout(5, 5));
        reservationsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), // Borda cinza para o título
                "Reservas para o dia selecionado", // Título
                javax.swing.border.TitledBorder.LEFT, // Alinhamento do título
                javax.swing.border.TitledBorder.TOP, // Posição do título
                new Font("SansSerif", Font.BOLD, 16), // Fonte do título
                WEEKDAY_COLOR // Cor do título
        ));
        reservationsPanel.setBackground(Color.WHITE); // Fundo branco

        String[] tableColumnNames = { "Sala", "Horário", "Usuário", "Propósito" };
        reservationsForDayTableModel = new DefaultTableModel(tableColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsForDayTable = new JTable(reservationsForDayTableModel);
        reservationsForDayTable.setFillsViewportHeight(true);
        reservationsForDayTable.setRowHeight(25); // Altura das linhas da tabela
        reservationsForDayTable.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Fonte da tabela
        reservationsForDayTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13)); // Fonte do cabeçalho da
                                                                                                // tabela
        reservationsForDayTable.getTableHeader().setBackground(new Color(220, 220, 220)); // Fundo do cabeçalho da
                                                                                          // tabela

        reservationsPanel.add(new JScrollPane(reservationsForDayTable), BorderLayout.CENTER);

        add(reservationsPanel, BorderLayout.SOUTH);
    }

    private void navigateMonth(int months) {
        currentMonth = currentMonth.plusMonths(months);
        updateCalendar();
        // Não reseta selectedDate para o primeiro dia do mês ao navegar,
        // mas tenta manter o mesmo dia, se possível.
        try {
            selectedDate = currentMonth.atDay(selectedDate.getDayOfMonth());
        } catch (java.time.DateTimeException e) {
            // Se o dia não existir no novo mês (ex: 31 de janeiro para fevereiro),
            // vai para o último dia do novo mês.
            selectedDate = currentMonth.atEndOfMonth();
        }
        displayReservationsForSelectedDay();
    }

    private void updateCalendar() {
        calendarGridPanel.removeAll();

        monthYearLabel.setText(
                currentMonth.getMonth().getDisplayName(TextStyle.FULL, BRAZIL_LOCALE) + " " + currentMonth.getYear());

        String[] dayNames = { "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb" };
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setForeground(WEEKDAY_COLOR); // Cor dos nomes dos dias da semana
            calendarGridPanel.add(label);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int firstDayOfWeekValue = firstDayOfMonth.getDayOfWeek().getValue();
        int offset = firstDayOfWeekValue;

        if (firstDayOfWeekValue == DayOfWeek.SUNDAY.getValue()) {
            offset = 0;
        } else {
            offset = firstDayOfWeekValue;
        }

        for (int i = 0; i < offset; i++) {
            calendarGridPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
            dayButton.setFocusPainted(false); // Remove o quadrado de foco
            dayButton.setBackground(Color.WHITE); // Cor de fundo padrão dos dias
            dayButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding interno para o texto do dia

            // Estilos de destaque para o dia
            if (date.equals(selectedDate)) {
                dayButton.setBackground(SELECTED_DAY_COLOR); // Dia selecionado
                dayButton.setBorder(new LineBorder(Color.DARK_GRAY, 2)); // Borda mais escura
            } else if (date.equals(LocalDate.now())) {
                dayButton.setBackground(TODAY_COLOR); // Dia atual
                dayButton.setBorder(new LineBorder(Color.ORANGE, 2)); // Borda laranja
            } else {
                dayButton.setBackground(Color.WHITE); // Dias normais
                dayButton.setBorder(new LineBorder(Color.LIGHT_GRAY, 1)); // Borda fina clara
            }

            // Destaque para dias com reservas confirmadas (borda extra)
            boolean hasConfirmedReservations = manager.getAllReservations().stream()
                    .filter(r -> r.getStatus().equals(ReservationStatus.CONFIRMED))
                    .anyMatch(r -> r.getDate().equals(date));
            if (hasConfirmedReservations) {
                dayButton.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(RESERVATION_DAY_BORDER_COLOR, 2), // Borda de reserva
                        dayButton.getBorder() // Mantém a borda original (selecionado, hoje, normal)
                ));
            }

            final LocalDate clickedDate = date;
            dayButton.addActionListener(e -> {
                selectedDate = clickedDate;
                updateCalendar();
                displayReservationsForSelectedDay();
            });
            calendarGridPanel.add(dayButton);
        }

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }

    private void displayReservationsForSelectedDay() {
        reservationsForDayTableModel.setRowCount(0);

        if (selectedDate == null) {
            return;
        }

        List<Reservation> reservations = manager.getAllReservations().stream()
                .filter(r -> r.getDate().equals(selectedDate))
                .filter(r -> r.getStatus().equals(ReservationStatus.CONFIRMED))
                .sorted(Comparator.comparing(Reservation::getStartTime))
                .collect(Collectors.toList());

        for (Reservation res : reservations) {
            reservationsForDayTableModel.addRow(new Object[] {
                    res.getClassroom().getName(),
                    res.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
                            + res.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    res.getReservedBy().getName(),
                    res.getPurpose()
            });
        }
    }

    public void refreshCalendarAndReservations() {
        updateCalendar();
        displayReservationsForSelectedDay();
    }
}