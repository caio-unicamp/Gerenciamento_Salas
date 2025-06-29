package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa uma reserva de sala no sistema.
 * Contém informações sobre a sala, usuário, data, horários, propósito, status e observações.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L; // Para serialização
    private static int nextReservationId = 1; // Variável estática para IDs únicos

    private int id;
    private Classroom classroom;
    private User reservedBy;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    private ReservationStatus status;
    private String observation;

    /**
     * Construtor da reserva.
     *
     * @param classroom  Sala reservada.
     * @param reservedBy Usuário que fez a reserva.
     * @param date       Data da reserva.
     * @param startTime  Horário de início.
     * @param endTime    Horário de término.
     * @param purpose    Propósito da reserva.
     */
    public Reservation(Classroom classroom, User reservedBy, LocalDate date, LocalTime startTime, LocalTime endTime, String purpose) {
        this.id = generateNextId(); // Utiliza método estático para ID único
        this.classroom = classroom;
        this.reservedBy = reservedBy;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = ReservationStatus.PENDING; // Status inicial
        this.observation = "";
    }

    /**
     * Método estático para gerar IDs únicos para reservas.
     * @return Próximo ID disponível.
     */
    private static synchronized int generateNextId() {
        return nextReservationId++;
    }

    /**
     * Define o próximo ID de reserva (usado ao carregar dados).
     * @param newId Novo valor para o próximo ID.
     */
    public static synchronized void setNextReservationId(int newId) {
        nextReservationId = newId;
    }

    /**
     * Retorna o próximo ID de reserva a ser utilizado.
     * @return Próximo ID.
     */
    public static synchronized int getNextId() {
        return nextReservationId;
    }

    // Getters 

    /**
     * Retorna o ID da reserva.
     * @return ID da reserva.
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna a sala reservada.
     * @return Sala.
     */
    public Classroom getClassroom() {
        return classroom;
    }

    /**
     * Retorna o usuário que fez a reserva.
     * @return Usuário.
     */
    public User getReservedBy() {
        return reservedBy;
    }

    /**
     * Retorna a data da reserva.
     * @return Data.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Retorna o horário de início da reserva.
     * @return Horário de início.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Retorna o horário de término da reserva.
     * @return Horário de término.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Retorna o propósito da reserva.
     * @return Propósito.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Retorna o status da reserva.
     * @return Status.
     */
    public ReservationStatus getStatus() {
        return status;
    }

    // Setters (para permitir modificações, se necessário)

    /**
     * Define o status da reserva.
     * @param status Novo status.
     */
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    /**
     * Define a data da reserva.
     * @param date Nova data.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Define o horário de início da reserva.
     * @param startTime Novo horário de início.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Define o horário de término da reserva.
     * @param endTime Novo horário de término.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Define o propósito da reserva.
     * @param purpose Novo propósito.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Retorna a observação da reserva.
     * @return Observação (string vazia se nula).
     */
    public String getObservation() {
        return observation != null ? observation : "";
    }

    /**
     * Define a observação da reserva.
     * @param observation Observação.
     */
    public void setObservation(String observation) {
        this.observation = observation;
    }

    /**
     * Verifica se esta reserva se sobrepõe a outra reserva.
     * @param other A outra reserva para verificar.
     * @return true se houver sobreposição, false caso contrário.
     */
    public boolean conflictsWith(Reservation other) {
       if (!other.getStatus().equals(ReservationStatus.CONFIRMED)) {
            return false; // Reservas que não estão confirmadas não causam conflito para novas reservas pendentes.
        }

        if (!this.classroom.equals(other.classroom) || !this.date.equals(other.date)) {
            return false; // Salas ou datas diferentes, não há conflito
        }

        // Verifica a sobreposição de horários
        // [Este.Inicio, Este.Fim) vs [Outro.Inicio, Outro.Fim)
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime) || this.startTime.equals(other.endTime));
    }

    /**
     * Retorna uma representação em string da reserva.
     * @return String representando a reserva.
     */
    @Override
    public String toString() {
        return "Reservation{" +
               "id=" + id +
               ", classroom=" + classroom.getName() +
               ", reservedBy=" + reservedBy.getUsername() +
               ", date=" + date +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", purpose='" + purpose + '\'' +
               ", status=" + status.getName() +
               ", observation='" + observation + '\'' + 
               '}';
    }

    /**
     * Compara duas reservas pelo ID.
     * @param o Objeto a ser comparado.
     * @return true se os IDs forem iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id == that.id; // Duas reservas são iguais se tiverem o mesmo ID
    }

    /**
     * Retorna o hash code baseado no ID da reserva.
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}