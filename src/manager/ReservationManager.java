package manager;

import model.Classroom;
import model.Reservation;
import model.ReservationStatus;
import model.User;
import exception.ReservationConflictException;
import exception.UserConflictException;
import util.FileUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia as reservas, salas de aula e usuários.
 */
public class ReservationManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Classroom> classrooms;
    private List<Reservation> reservations;
    private List<User> users;
    
    private static final String CLASSROOMS_FILE = "../data/classrooms.txt";
    private static final String RESERVATIONS_FILE = "../data/reservations.txt";
    private static final String USERS_FILE = "../data/users.txt";
    
    /**
     * Construtor do gerenciador de reservas.
     */
    public ReservationManager() {
        this.classrooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.users = new ArrayList<>();
        loadData();
    }

    /**
     * Adiciona uma sala de aula.
     * @param classroom A sala de aula a ser adicionada.
     */
    public void addClassroom(Classroom classroom) {
        if (!classrooms.contains(classroom)) {
            classrooms.add(classroom);
            saveData();
        } else {
            System.out.println("Sala " + classroom.getName() + " já existe.");
        }
    }

    /**
     * Remove uma sala de aula.
     * @param classroom A sala de aula a ser removida.
     */
    public void removeClassroom(Classroom classroom) {
        if (classrooms.contains(classroom)) {
            classrooms.remove(classroom);
            saveData();
        } else {
            System.out.println("Sala " + classroom.getName() + "não existe.");
        }
    }

    /**
     * Obtém uma sala de aula pelo nome.
     * @param name O nome da sala de aula.
     * @return A sala de aula, ou null se não for encontrada.
     */
    public Classroom getClassroomByName(String name) {
        return classrooms.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtém todas as salas de aula.
     * @return Uma lista de todas as salas de aula.
     */
    public List<Classroom> getAllClassrooms() {
        return new ArrayList<>(classrooms);
    }

    /**
     * Adiciona um usuário.
     * @param user O usuário a ser adicionado.
     * @throws UserConflictException Se o usuário já existir.
     */
    public void addUser(User user) throws UserConflictException {
        if (!users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))) {
            users.add(user);
            saveData();
        } else {
            throw new UserConflictException("Nome de usuário já existe. Por favor, escolha outro.");

        }
    }

    /**
     * Obtém um usuário pelo nome de usuário.
     * @param username O nome de usuário.
     * @return O usuário, ou null se não for encontrado.
     */
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtém todos os usuários.
     * @return Uma lista de todos os usuários.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Faz uma reserva.
     * @param classroom A sala de aula a ser reservada.
     * @param reservedBy O usuário que está fazendo a reserva.
     * @param date A data da reserva.
     * @param startTime A hora de início da reserva.
     * @param endTime A hora de término da reserva.
     * @param purpose O propósito da reserva.
     * @throws ReservationConflictException Se houver um conflito de reserva.
     */
    public void makeReservation(Classroom classroom, User reservedBy, LocalDate date, LocalTime startTime,
            LocalTime endTime, String purpose) throws ReservationConflictException {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Hora de início deve ser anterior à hora de término.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível reservar para uma data passada.");
        }

        Reservation newReservation = new Reservation(classroom, reservedBy, date, startTime, endTime, purpose);

        for (Reservation existingReservation : reservations) {
            if (existingReservation.getStatus().equals(ReservationStatus.CONFIRMED)
                    && newReservation.conflictsWith(existingReservation)) {
                Reservation.setNextReservationId(Reservation.getNextId() - 1);
                throw new ReservationConflictException(
                        "Conflito de reserva! A sala " + classroom.getName() +
                                " já está confirmada para " + existingReservation.getReservedBy().getUsername() +
                                " das " + existingReservation.getStartTime() +
                                " às " + existingReservation.getEndTime() +
                                " em " + existingReservation.getDate() + ".");
            }
        }

        reservations.add(newReservation);
        saveData();
    }

    /**
     * Encontra as salas de aula disponíveis.
     * @param date A data da reserva.
     * @param startTime A hora de início da reserva.
     * @param endTime A hora de término da reserva.
     * @return Uma lista de salas de aula disponíveis.
     */
    public List<Classroom> findAvailableClassrooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Classroom> available = new ArrayList<>(classrooms);

        for (Reservation res : reservations) {
            if (res.getStatus().equals(ReservationStatus.CONFIRMED) && res.getDate().equals(date) &&
                    !(endTime.isBefore(res.getStartTime()) || startTime.isAfter(res.getEndTime())
                            || startTime.equals(res.getEndTime()))) {
                available.remove(res.getClassroom());
            }
        }
        return available;
    }

    /**
     * Encontra as salas de aula disponíveis com uma capacidade mínima.
     * @param date A data da reserva.
     * @param startTime A hora de início da reserva.
     * @param endTime A hora de término da reserva.
     * @param minCapacity A capacidade mínima.
     * @return Uma lista de salas de aula disponíveis.
     */
    public List<Classroom> findAvailableClassrooms(LocalDate date, LocalTime startTime, LocalTime endTime,
            int minCapacity) {
        return findAvailableClassrooms(date, startTime, endTime).stream()
                .filter(c -> c.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }

    /**
     * Obtém todas as reservas.
     * @return Uma lista de todas as reservas.
     */
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    /**
     * Obtém as reservas por usuário.
     * @param user O usuário.
     * @return Uma lista de reservas para o usuário.
     */
    public List<Reservation> getReservationsByUser(User user) {
        return reservations.stream()
                .filter(r -> r.getReservedBy().equals(user))
                .collect(Collectors.toList());
    }

    /**
     * Obtém as reservas por sala de aula.
     * @param classroom A sala de aula.
     * @return Uma lista de reservas para a sala de aula.
     */
    public List<Reservation> getReservationsByClassroom(Classroom classroom) {
        return reservations.stream()
                .filter(r -> r.getClassroom().equals(classroom))
                .collect(Collectors.toList());
    }

    /**
     * Obtém as reservas pendentes.
     * @return Uma lista de reservas pendentes.
     */
    public List<Reservation> getPendingReservations() {
        return reservations.stream()
                .filter(r -> r.getStatus().equals(ReservationStatus.PENDING))
                .collect(Collectors.toList());
    }

    /**
     * Confirma uma reserva.
     * @param reservation A reserva a ser confirmada.
     * @throws ReservationConflictException Se houver um conflito de reserva.
     */
    public void confirmReservation(Reservation reservation) throws ReservationConflictException {
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new IllegalArgumentException("Reserva não está no status Pendente para ser confirmada.");
        }

        for (Reservation existingReservation : reservations) {
            if (existingReservation.equals(reservation)) {
                continue;
            }
            if (existingReservation.getStatus().equals(ReservationStatus.CONFIRMED) &&
                    reservation.conflictsWith(existingReservation)) {
                throw new ReservationConflictException(
                        "Não foi possível confirmar. Conflito com reserva já existente: Sala " +
                                existingReservation.getClassroom().getName() +
                                " das " + existingReservation.getStartTime() +
                                " às " + existingReservation.getEndTime() +
                                " em " + existingReservation.getDate() + ".");
            }
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " confirmada com sucesso.");
    }

    /**
     * Rejeita uma reserva.
     * @param reservation A reserva a ser rejeitada.
     * @param observation A observação para a rejeição.
     */
    public void rejectReservation(Reservation reservation, String observation) {
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new IllegalArgumentException("Reserva não está no status Pendente para ser rejeitada.");
        }
        reservation.setObservation(observation);
        reservation.setStatus(ReservationStatus.REJECTED);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " rejeitada com sucesso. Obs: " + observation);

    }

    /**
     * Cancela uma reserva.
     * @param reservation A reserva a ser cancelada.
     * @param observation A observação para o cancelamento.
     */
    public void cancelReservation(Reservation reservation, String observation) {
        if (reservation.getStatus().equals(ReservationStatus.REJECTED)
                || reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException(
                    "Não é possível cancelar uma reserva que já foi rejeitada ou cancelada.");
        }
        reservation.setObservation(observation);
        reservation.setStatus(ReservationStatus.CANCELLED);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " cancelada com sucesso. Obs: " + observation);

    }

    /**
     * Exclui uma reserva.
     * @param reservation A reserva a ser excluída.
     */
    public void deleteReservation(Reservation reservation) {
        reservations.remove(reservation);
        saveData();
        System.out.println("Reserva " + reservation.getId() + " deletada com sucesso.");
    }

    /**
     * Carrega os dados dos arquivos.
     */
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

            int maxId = reservations.stream()
                    .mapToInt(Reservation::getId)
                    .max()
                    .orElse(0);

            Reservation.setNextReservationId(maxId + 1);
            System.out.println("Próximo ID de reserva inicializado para: " + (maxId + 1));

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    /**
     * Salva os dados nos arquivos.
     */
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