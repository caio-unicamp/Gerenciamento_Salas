package exception;

// Exemplo de tratamento de exceções, incluindo uma exceção definida pelo grupo 
public class ReservationConflictException extends Exception {

    public ReservationConflictException(String message) {
        super(message);
    }

    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}