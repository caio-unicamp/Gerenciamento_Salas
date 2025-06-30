package exception;

/**
 * Exceção para conflitos de reserva.
 */
public class ReservationConflictException extends Exception {

    /**
     * Construtor com uma mensagem.
     * @param message A mensagem de erro.
     */
    public ReservationConflictException(String message) {
        super(message);
    }

    /**
     * Construtor com uma mensagem e uma causa.
     * @param message A mensagem de erro.
     * @param cause A causa da exceção.
     */
    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}