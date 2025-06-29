package model;

public enum ReservationStatus {
    CONFIRMED("Confirmada"), PENDING("Pendente"), CANCELLED("Cancelada"), REJECTED("Rejeitada");

    private String name;

    ReservationStatus(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}