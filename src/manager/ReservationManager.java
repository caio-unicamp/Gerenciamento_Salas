package manager;

import model.Classroom;
import model.Reservation;
import model.User;
import exception.ReservationConflictException;
import util.FileUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList; // Exemplo de uso de Arrays (Listas) 
import java.util.List;
import java.util.stream.Collectors;

// Exemplo de relacionamento (agregação): ReservationManager 'agrega' Classroom e Reservation [cite: 8]
public class ReservationManager implements Serializable {
    private static final long serialVersionUID = 1L; // Para serialização

    private List<Classroom> classrooms; // Exemplo de uso de Arrays (Listas) 
    private List<Reservation> reservations; // Exemplo de uso de Arrays (Listas) 
    private List<User> users; // Exemplo de uso de Arrays (Listas) 

    public ReservationManager() {
        this.classrooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.users = new ArrayList<>();
        loadData(); // Carrega os dados ao iniciar o manager 
    }

    // --- Métodos de Gerenciamento de Salas ---

    public void addClassroom(Classroom classroom) {
        if (!classrooms.contains(classroom)) {
            classrooms.add(classroom);
            saveData(); // Salva após adicionar 
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
            saveData(); // Salva após adicionar
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

    /**
     * Faz uma nova reserva de sala de aula.
     * @param classroom Sala a ser reservada.
     * @param reservedBy Usuário que está fazendo a reserva.
     * @param date Data da reserva.
     * @param startTime Horário de início.
     * @param endTime Horário de término.
     * @param purpose Propósito da reserva.
     * @throws ReservationConflictException Se houver conflito de horário com uma reserva existente. 
     */
    public void makeReservation(Classroom classroom, User reservedBy, LocalDate date, LocalTime startTime, LocalTime endTime, String purpose) throws ReservationConflictException {
        // Validar horários
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Hora de início deve ser anterior à hora de término.");
        }
        if (date.isBefore(LocalDate.now())) {
             throw new IllegalArgumentException("Não é possível reservar para uma data passada.");
        }


        Reservation newReservation = new Reservation(classroom, reservedBy, date, startTime, endTime, purpose);

        // Verificar conflitos com reservas existentes para a mesma sala e data 
        for (Reservation existingReservation : reservations) {
            if (newReservation.conflictsWith(existingReservation)) {
                throw new ReservationConflictException(
                    "Conflito de reserva! A sala " + classroom.getName() +
                    " já está reservada das " + existingReservation.getStartTime() +
                    " às " + existingReservation.getEndTime() +
                    " em " + existingReservation.getDate() + "."
                );
            }
        }

        reservations.add(newReservation);
        saveData(); // Salva após fazer a reserva 
    }

    /**
     * Busca salas disponíveis para um determinado período.
     * Exemplo de sobrecarga de método 
     * @param date Data desejada.
     * @param startTime Horário de início desejado.
     * @param endTime Horário de término desejado.
     * @return Lista de salas disponíveis.
     */
    public List<Classroom> findAvailableClassrooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Classroom> available = new ArrayList<>(classrooms);

        for (Reservation res : reservations) {
            // Se a reserva atual é para a mesma data e há sobreposição de horário
            if (res.getDate().equals(date) &&
                !(endTime.isBefore(res.getStartTime()) || startTime.isAfter(res.getEndTime()) || startTime.equals(res.getEndTime()))) {
                available.remove(res.getClassroom()); // Remove a sala se houver conflito
            }
        }
        return available;
    }

    /**
     * Busca salas disponíveis com base na capacidade e no período.
     * Exemplo de sobrecarga de método 
     * @param date Data desejada.
     * @param startTime Horário de início desejado.
     * @param endTime Horário de término desejado.
     * @param minCapacity Capacidade mínima desejada.
     * @return Lista de salas disponíveis que atendem à capacidade.
     */
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

    public void cancelReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            System.out.println("Reserva " + reservation.getId() + " cancelada com sucesso.");
            saveData(); // Salva após cancelar 
        } else {
            System.out.println("Reserva não encontrada.");
        }
    }

    // --- Métodos de Persistência de Dados (Leitura e Gravação de Arquivos) ---

    // Leitura e gravação de arquivos 
    private static final String CLASSROOMS_FILE = "../data/classrooms.txt";
    private static final String RESERVATIONS_FILE = "../data/reservations.txt";
    private static final String USERS_FILE = "../data/users.txt";

    @SuppressWarnings("unchecked")
    public void loadData() {
        try {
            // Carrega salas
            List<?> loadedClassrooms = FileUtil.readObjectFromFile(CLASSROOMS_FILE);
            if (loadedClassrooms != null) {
                this.classrooms = (List<Classroom>) loadedClassrooms;
                System.out.println("Salas carregadas: " + this.classrooms.size());
            }

            // Carrega usuários
            List<?> loadedUsers = FileUtil.readObjectFromFile(USERS_FILE);
            if (loadedUsers != null) {
                this.users = (List<User>) loadedUsers;
                System.out.println("Usuários carregados: " + this.users.size());
            }

            // Carrega reservas
            List<?> loadedReservations = FileUtil.readObjectFromFile(RESERVATIONS_FILE);
            if (loadedReservations != null) {
                this.reservations = (List<Reservation>) loadedReservations;
                System.out.println("Reservas carregadas: " + this.reservations.size());
            }

            // Garante que o nextReservationId seja maior que os IDs existentes para evitar duplicação
            int maxId = reservations.stream()
                                    .mapToInt(Reservation::getId)
                                    .max()
                                    .orElse(0);

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            // Tratar a exceção de forma mais robusta em um sistema real 
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
            // Tratar a exceção de forma mais robusta em um sistema real 
        }
    }
}