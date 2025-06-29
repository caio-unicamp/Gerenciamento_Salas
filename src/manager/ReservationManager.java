package manager;

import model.Classroom;
import model.Reservation;
import model.ReservationStatus; // Importar o enum
import model.User;
import exception.ReservationConflictException;
import util.FileUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Classroom> classrooms;
    private List<Reservation> reservations;
    private List<User> users;

    public ReservationManager() {
        this.classrooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.users = new ArrayList<>();
        loadData();
    }

    // --- Métodos de Gerenciamento de Salas ---

    public void addClassroom(Classroom classroom) {
        if (!classrooms.contains(classroom)) {
            classrooms.add(classroom);
            saveData();
        } else {
            System.out.println("Sala " + classroom.getName() + " já existe.");
        }
    }

    public Classroom getClassroomByName(String name) {
        return classrooms.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Classroom> getAllClassrooms() {
        return new ArrayList<>(classrooms);
    }

    // --- Métodos de Gerenciamento de Usuários ---

    public void addUser(User user) {
        if (!users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))) {
            users.add(user);
            saveData();
        } else {
            System.out.println("Usuário " + user.getUsername() + " já existe.");
        }
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // --- Métodos de Gerenciamento de Reservas ---

    public void makeReservation(Classroom classroom, User reservedBy, LocalDate date, LocalTime startTime, LocalTime endTime, String purpose) throws ReservationConflictException {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Hora de início deve ser anterior à hora de término.");
        }
        if (date.isBefore(LocalDate.now())) {
             throw new IllegalArgumentException("Não é possível reservar para uma data passada.");
        }

        Reservation newReservation = new Reservation(classroom, reservedBy, date, startTime, endTime, purpose);

        // Ao fazer uma nova reserva, verificar conflitos APENAS com reservas JÁ CONFIRMADAS.
        // Reservas pendentes não causam conflito neste estágio.
        for (Reservation existingReservation : reservations) {
            if (existingReservation.getStatus().equals(ReservationStatus.CONFIRMED) && newReservation.conflictsWith(existingReservation)) {
                throw new ReservationConflictException(
                    "Conflito de reserva! A sala " + classroom.getName() +
                    " já está **confirmada** para " + existingReservation.getReservedBy().getUsername() +
                    " das " + existingReservation.getStartTime() +
                    " às " + existingReservation.getEndTime() +
                    " em " + existingReservation.getDate() + "."
                );
            }
        }

        reservations.add(newReservation);
        saveData();
    }

    /**
     * Busca salas disponíveis para um determinado período, considerando apenas reservas CONFIRMADAS.
     * @param date Data desejada.
     * @param startTime Horário de início desejado.
     * @param endTime Horário de término desejado.
     * @return Lista de salas disponíveis.
     */
    public List<Classroom> findAvailableClassrooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Classroom> available = new ArrayList<>(classrooms);

        for (Reservation res : reservations) {
            // Apenas reservas CONFIRMADAS afetam a disponibilidade
            if (res.getStatus().equals(ReservationStatus.CONFIRMED) && res.getDate().equals(date) &&
                !(endTime.isBefore(res.getStartTime()) || startTime.isAfter(res.getEndTime()) || startTime.equals(res.getEndTime()))) {
                available.remove(res.getClassroom());
            }
        }
        return available;
    }

    public List<Classroom> findAvailableClassrooms(LocalDate date, LocalTime startTime, LocalTime endTime, int minCapacity) {
        return findAvailableClassrooms(date, startTime, endTime).stream()
                .filter(c -> c.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservations.stream()
                .filter(r -> r.getReservedBy().equals(user))
                .collect(Collectors.toList());
    }

    public List<Reservation> getReservationsByClassroom(Classroom classroom) {
        return reservations.stream()
                .filter(r -> r.getClassroom().equals(classroom))
                .collect(Collectors.toList());
    }

    public List<Reservation> getPendingReservations() {
        return reservations.stream()
                .filter(r -> r.getStatus().equals(ReservationStatus.PENDING))
                .collect(Collectors.toList());
    }

    /**
     * Confirma uma reserva pendente.
     * @param reservation A reserva a ser confirmada.
     * @throws ReservationConflictException Se a confirmação causar conflito com uma reserva CONFIRMED existente.
     */
    public void confirmReservation(Reservation reservation) throws ReservationConflictException {
        // Verificar se a reserva já está confirmada ou cancelada/rejeitada
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new IllegalArgumentException("Reserva não está no status Pendente para ser confirmada.");
        }

        // Antes de confirmar, VERIFICAR NOVAMENTE se a confirmação causaria conflito
        // com OUTRAS reservas JÁ CONFIRMADAS.
        // A reserva 'reservation' que estamos tentando confirmar NÃO deve ser verificada contra si mesma.
        for (Reservation existingReservation : reservations) {
            if (existingReservation.equals(reservation)) {
                continue; // Pula a própria reserva
            }
            if (existingReservation.getStatus().equals(ReservationStatus.CONFIRMED) &&
                reservation.conflictsWith(existingReservation)) { // Reutiliza a lógica de conflito
                throw new ReservationConflictException(
                    "Não foi possível confirmar. Conflito com reserva já existente: Sala " +
                    existingReservation.getClassroom().getName() +
                    " das " + existingReservation.getStartTime() +
                    " às " + existingReservation.getEndTime() +
                    " em " + existingReservation.getDate() + "."
                );
            }
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " confirmada com sucesso.");
    }

    public void rejectReservation(Reservation reservation) {
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new IllegalArgumentException("Reserva não está no status Pendente para ser rejeitada.");
        }
        reservation.setStatus(ReservationStatus.REJECTED);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " rejeitada com sucesso.");
    }


    public void cancelReservation(Reservation reservation) {
        // Permitir cancelar qualquer reserva, independente do status, mas geralmente se cancela CONFIRMED ou PENDING
        if (!reservation.getStatus().equals(ReservationStatus.PENDING) && !reservation.getStatus().equals(ReservationStatus.CONFIRMED)) {
            throw new IllegalArgumentException("Reserva não está no status Pendente ou Confirmada para ser cancelada.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " cancelada com sucesso.");
    }


    // --- Métodos de Persistência de Dados (Leitura e Gravação de Arquivos) ---

    private static final String CLASSROOMS_FILE = "../data/classrooms.txt";
    private static final String RESERVATIONS_FILE = "../data/reservations.txt";
    private static final String USERS_FILE = "../data/users.txt";

    @SuppressWarnings("unchecked")
    public void loadData() {
        try {
            List<?> loadedClassrooms = FileUtil.readObjectFromFile(CLASSROOMS_FILE);
            if (loadedClassrooms != null) {
                this.classrooms = (List<Classroom>) loadedClassrooms;
                System.out.println("Salas carregadas: " + this.classrooms.size());
            }

            List<?> loadedUsers = FileUtil.readObjectFromFile(USERS_FILE);
            if (loadedUsers != null) {
                this.users = (List<User>) loadedUsers;
                System.out.println("Usuários carregados: " + this.users.size());
            }

            List<?> loadedReservations = FileUtil.readObjectFromFile(RESERVATIONS_FILE);
            if (loadedReservations != null) {
                this.reservations = (List<Reservation>) loadedReservations;
                System.out.println("Reservas carregadas: " + this.reservations.size());
            }

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            FileUtil.writeObjectToFile(classrooms, CLASSROOMS_FILE);
            FileUtil.writeObjectToFile(users, USERS_FILE);
            FileUtil.writeObjectToFile(reservations, RESERVATIONS_FILE);
            System.out.println("Dados salvos com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }
}