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

    private static final Color HEADER_BG_COLOR = new Color(70, 130, 180);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Color WEEKDAY_COLOR = new Color(50, 50, 50);
    private static final Color SELECTED_DAY_COLOR = new Color(173, 216, 230);
    private static final Color TODAY_COLOR = new Color(255, 223, 186);
    private static final Color RESERVATION_DAY_BORDER_COLOR = new Color(0, 100, 0);

    public CalendarPanel(ReservationManager manager) {
        this.manager = manager;
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 240));

        initComponents();
        updateCalendar();
        displayReservationsForSelectedDay();
    }

    private void initComponents() {
        JPanel monthNavPanel = new JPanel(new BorderLayout(5, 1));
        monthNavPanel.setBackground(HEADER_BG_COLOR);
        monthNavPanel.setBorder(new EmptyBorder(1, 10, 1, 10));

        prevMonthButton = new JButton("<<");
        prevMonthButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        prevMonthButton.setForeground(HEADER_TEXT_COLOR);
        prevMonthButton.setBackground(HEADER_BG_COLOR);
        prevMonthButton.setBorderPainted(false);
        prevMonthButton.setFocusPainted(false);
        prevMonthButton.addActionListener(e -> navigateMonth(-1));

        nextMonthButton = new JButton(">>");
        nextMonthButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        nextMonthButton.setForeground(HEADER_TEXT_COLOR);
        nextMonthButton.setBackground(HEADER_BG_COLOR);
        nextMonthButton.setBorderPainted(false);
        nextMonthButton.setFocusPainted(false);
        nextMonthButton.addActionListener(e -> navigateMonth(1));

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        monthYearLabel.setForeground(HEADER_TEXT_COLOR);

        monthNavPanel.add(prevMonthButton, BorderLayout.WEST);
        monthNavPanel.add(monthYearLabel, BorderLayout.CENTER);
        monthNavPanel.add(nextMonthButton, BorderLayout.EAST);
        add(monthNavPanel, BorderLayout.NORTH);

        // ALTERAÇÃO AQUI: Aumentar ainda mais o espaçamento vertical
        // Isso dá mais "liberdade" para os botões expandirem verticalmente.
        calendarGridPanel = new JPanel(new GridLayout(0, 7, 5, 1)); // VGap de 20
        calendarGridPanel.setBackground(Color.WHITE);
        calendarGridPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(1, 5, 1, 5),
            new LineBorder(Color.LIGHT_GRAY, 1)
        ));
        add(calendarGridPanel, BorderLayout.CENTER);

        JPanel reservationsPanel = new JPanel(new BorderLayout(5, 1));
        reservationsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            "Reservas para o dia selecionado",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 16),
            WEEKDAY_COLOR
        ));
        reservationsPanel.setBackground(Color.WHITE);

        String[] tableColumnNames = {"Sala", "Horário", "Usuário", "Propósito"};
        reservationsForDayTableModel = new DefaultTableModel(tableColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsForDayTable = new JTable(reservationsForDayTableModel);
        reservationsForDayTable.setFillsViewportHeight(true);
        reservationsForDayTable.setRowHeight(25);
        reservationsForDayTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        reservationsForDayTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        reservationsForDayTable.getTableHeader().setBackground(new Color(220, 220, 220));

        reservationsPanel.add(new JScrollPane(reservationsForDayTable), BorderLayout.CENTER);

        add(reservationsPanel, BorderLayout.SOUTH);
    }

    private void navigateMonth(int months) {
        currentMonth = currentMonth.plusMonths(months);
        updateCalendar();
        try {
            selectedDate = currentMonth.atDay(selectedDate.getDayOfMonth());
        } catch (java.time.DateTimeException e) {
            selectedDate = currentMonth.atEndOfMonth();
        }
        displayReservationsForSelectedDay();
    }

    private void updateCalendar() {
        calendarGridPanel.removeAll();

        monthYearLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, BRAZIL_LOCALE) + " " + currentMonth.getYear());

        String[] dayNames = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setForeground(WEEKDAY_COLOR);
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
            dayButton.setFocusPainted(false);
            dayButton.setBackground(Color.WHITE);
            // ALTERAÇÃO AQUI: Aumentar o padding interno do botão para dar mais altura
            dayButton.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5)); // Aumentado padding vertical para 10

            if (date.equals(selectedDate)) {
                dayButton.setBackground(SELECTED_DAY_COLOR);
                dayButton.setBorder(new LineBorder(Color.DARK_GRAY, 2));
            } else if (date.equals(LocalDate.now())) {
                dayButton.setBackground(TODAY_COLOR);
                dayButton.setBorder(new LineBorder(Color.ORANGE, 2));
            } else {
                dayButton.setBackground(Color.WHITE);
                dayButton.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            }

            boolean hasConfirmedReservations = manager.getAllReservations().stream()
                .filter(r -> r.getStatus().equals(ReservationStatus.CONFIRMED))
                .anyMatch(r -> r.getDate().equals(date));
            if (hasConfirmedReservations) {
                dayButton.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(RESERVATION_DAY_BORDER_COLOR, 2),
                    dayButton.getBorder()
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
            reservationsForDayTableModel.addRow(new Object[]{
                res.getClassroom().getName(),
                res.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + res.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
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