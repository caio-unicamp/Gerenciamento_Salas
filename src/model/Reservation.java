package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa uma reserva.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextReservationId = 1;

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
     * Construtor para uma reserva.
     * @param classroom A sala de aula.
     * @param reservedBy O usuário que reservou.
     * @param date A data da reserva.
     * @param startTime A hora de início.
     * @param endTime A hora de término.
     * @param purpose O propósito da reserva.
     */
    public Reservation(Classroom classroom, User reservedBy, LocalDate date, LocalTime startTime, LocalTime endTime, String purpose) {
        this.id = generateNextId();
        this.classroom = classroom;
        this.reservedBy = reservedBy;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = ReservationStatus.PENDING;
        this.observation = "";
    }

    /**
     * Gera o próximo ID de reserva.
     * @return O próximo ID de reserva.
     */
    private static synchronized int generateNextId() {
        return nextReservationId++;
    }

    /**
     * Define o próximo ID de reserva.
     * @param newId O novo ID.
     */
    public static synchronized void setNextReservationId(int newId) {
        nextReservationId = newId;
    }

    /**
     * Obtém o próximo ID de reserva.
     * @return O próximo ID de reserva.
     */
    public static synchronized int getNextId() {
        return nextReservationId;
    }

    /**
     * Obtém o ID da reserva.
     * @return O ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtém a sala de aula.
     * @return A sala de aula.
     */
    public Classroom getClassroom() {
        return classroom;
    }

    /**
     * Obtém o usuário que fez a reserva.
     * @return O usuário.
     */
    public User getReservedBy() {
        return reservedBy;
    }

    /**
     * Obtém a data da reserva.
     * @return A data.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Obtém a hora de início.
     * @return A hora de início.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Obtém a hora de término.
     * @return A hora de término.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Obtém o propósito da reserva.
     * @return O propósito.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Obtém o status da reserva.
     * @return O status.
     */
    public ReservationStatus getStatus() {
        return status;
    }

    /**
     * Define o status da reserva.
     * @param status O novo status.
     */
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    /**
     * Define a data da reserva.
     * @param date A nova data.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Define a hora de início da reserva.
     * @param startTime A nova hora de início.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Define a hora de término da reserva.
     * @param endTime A nova hora de término.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Define o propósito da reserva.
     * @param purpose O novo propósito.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Obtém a observação da reserva.
     * @return A observação.
     */
    public String getObservation() {
        return observation != null ? observation : "";
    }

    /**
     * Define a observação da reserva.
     * @param observation A nova observação.
     */
    public void setObservation(String observation) {
        this.observation = observation;
    }

    /**
     * Verifica se esta reserva conflita com outra.
     * @param other A outra reserva.
     * @return true se houver conflito, false caso contrário.
     */
    public boolean conflictsWith(Reservation other) {
       if (!other.getStatus().equals(ReservationStatus.CONFIRMED)) {
            return false;
        }

        if (!this.classroom.equals(other.classroom) || !this.date.equals(other.date)) {
            return false;
        }

        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime) || this.startTime.equals(other.endTime));
    }

    /**
     * Retorna uma representação em string da reserva.
     * @return A representação em string.
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
     * Verifica se dois objetos Reservation são iguais.
     * @param o O objeto a ser comparado.
     * @return true se os objetos forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id == that.id;
    }

    /**
     * Retorna o código hash para o objeto.
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}