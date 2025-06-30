package model;

/**
 * Enumeração para os status de reserva.
 */
public enum ReservationStatus {
    CONFIRMED("Confirmada"),
    PENDING("Pendente"),
    CANCELLED("Cancelada"),
    REJECTED("Rejeitada");

    private String name;

    /**
     * Construtor para o enum ReservationStatus.
     * @param name O nome do status.
     */
    ReservationStatus(String name){
        this.name = name;
    }

    /**
     * Obtém o nome do status.
     * @return O nome do status.
     */
    public String getName(){
        return name;
    }
}