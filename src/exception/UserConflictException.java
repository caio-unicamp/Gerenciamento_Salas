package exception;

/**
 * Exceção lançada quando ocorre um conflito de usuário.
 * Por exemplo, ao tentar cadastrar um nome de usuário já existente no sistema.
 */
public class UserConflictException extends Exception {
    /**
     * Construtor da exceção com mensagem.
     * @param message Mensagem descritiva do conflito.
     */
    public UserConflictException(String message) {
        super(message);
    }

    /**
     * Construtor da exceção com mensagem e causa.
     * @param message Mensagem descritiva do conflito.
     * @param cause   Causa original da exceção.
     */
    public UserConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
