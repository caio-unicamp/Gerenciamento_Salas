package gui;

import manager.ReservationManager;
import model.Reservation;
import model.ReservationStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale; // Importar Locale
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

    // CORREÇÃO: Usar Locale.of() em vez do construtor depreciado
    private static final Locale BRAZIL_LOCALE = Locale.of("pt", "BR"); // Substituído new Locale("pt", "BR")

    public CalendarPanel(ReservationManager manager) {
        this.manager = manager;
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        updateCalendar();
        displayReservationsForSelectedDay();
    }

    private void initComponents() {
        JPanel monthNavPanel = new JPanel(new BorderLayout());
        prevMonthButton = new JButton("<< Mês Anterior");
        prevMonthButton.addActionListener(e -> navigateMonth(-1));
        nextMonthButton = new JButton("Próximo Mês >>");
        nextMonthButton.addActionListener(e -> navigateMonth(1));
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        monthNavPanel.add(prevMonthButton, BorderLayout.WEST);
        monthNavPanel.add(monthYearLabel, BorderLayout.CENTER);
        monthNavPanel.add(nextMonthButton, BorderLayout.EAST);
        add(monthNavPanel, BorderLayout.NORTH);

        calendarGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        add(calendarGridPanel, BorderLayout.CENTER);

        JPanel reservationsPanel = new JPanel(new BorderLayout());
        reservationsPanel.setBorder(BorderFactory.createTitledBorder("Reservas para o dia selecionado"));

        String[] tableColumnNames = {"Sala", "Horário", "Usuário", "Propósito"};
        reservationsForDayTableModel = new DefaultTableModel(tableColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsForDayTable = new JTable(reservationsForDayTableModel);
        reservationsForDayTable.setFillsViewportHeight(true);
        reservationsPanel.add(new JScrollPane(reservationsForDayTable), BorderLayout.CENTER);

        add(reservationsPanel, BorderLayout.SOUTH);
    }

    private void navigateMonth(int months) {
        currentMonth = currentMonth.plusMonths(months);
        updateCalendar();
        if (selectedDate.getMonth() != currentMonth.getMonth() || selectedDate.getYear() != currentMonth.getYear()) {
            selectedDate = currentMonth.atDay(1);
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

            if (date.equals(selectedDate)) {
                dayButton.setBackground(Color.LIGHT_GRAY);
            } else {
                dayButton.setBackground(null);
            }

            boolean hasConfirmedReservations = manager.getAllReservations().stream()
                .filter(r -> r.getStatus().equals(ReservationStatus.CONFIRMED))
                .anyMatch(r -> r.getDate().equals(date));
            if (hasConfirmedReservations) {
                dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            } else {
                dayButton.setBorder(new JButton().getBorder());
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