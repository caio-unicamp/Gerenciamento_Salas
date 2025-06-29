package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L; // Para serialização
    private static int nextReservationId = 1; // Variável estática 

    private int id;
    private Classroom classroom;
    private User reservedBy;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    private ReservationStatus status;
    private String observation;

    public Reservation(Classroom classroom, User reservedBy, LocalDate date, LocalTime startTime, LocalTime endTime, String purpose) {
        this.id = generateNextId(); // Utiliza método estático 
        this.classroom = classroom;
        this.reservedBy = reservedBy;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = ReservationStatus.PENDING; // Status inicial
        this.observation = "";
    }

    // Método estático para gerar IDs únicos 
    private static synchronized int generateNextId() {
        return nextReservationId++;
    }

    public static synchronized void setNextReservationId(int newId) {
        nextReservationId = newId;
    }

    public static synchronized int getNextId() {
        return nextReservationId;
    }

    // Getters 
    public int getId() {
        return id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public User getReservedBy() {
        return reservedBy;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    // Setters (para permitir modificações, se necessário)
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getObservation() {
        return observation != null ? observation : "";
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id == that.id; // Duas reservas são iguais se tiverem o mesmo ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}