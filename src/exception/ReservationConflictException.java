package exception;

/**
 * Exceção lançada quando ocorre um conflito de reserva de sala.
 * Por exemplo, ao tentar reservar uma sala já confirmada para o mesmo horário.
 */
public class ReservationConflictException extends Exception {

    /**
     * Construtor da exceção com mensagem.
     * @param message Mensagem descritiva do conflito.
     */
    public ReservationConflictException(String message) {
        super(message);
    }

    /**
     * Construtor da exceção com mensagem e causa.
     * @param message Mensagem descritiva do conflito.
     * @param cause   Causa original da exceção.
     */
    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}