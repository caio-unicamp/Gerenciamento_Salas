package model;

/**
 * Enumeração dos possíveis status de uma reserva de sala.
 * - CONFIRMED: Reserva confirmada.
 * - PENDING: Reserva pendente de confirmação.
 * - CANCELLED: Reserva cancelada.
 * - REJECTED: Reserva rejeitada.
 */
public enum ReservationStatus {
    CONFIRMED("Confirmada"),
    PENDING("Pendente"),
    CANCELLED("Cancelada"),
    REJECTED("Rejeitada");

    private String name;

    /**
     * Construtor do status da reserva.
     * @param name Nome legível do status.
     */
    ReservationStatus(String name){
        this.name = name;
    }

    /**
     * Retorna o nome legível do status.
     * @return Nome do status.
     */
    public String getName(){
        return name;
    }
}