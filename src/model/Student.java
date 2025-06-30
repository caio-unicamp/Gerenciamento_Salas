package model;

/**
 * Representa um usuário do tipo Estudante.
 */
public class Student extends User {
    private String studentId;

    /**
     * Construtor para a classe Student.
     *
     * @param username O nome de usuário.
     * @param password A senha.
     * @param name O nome completo.
     * @param email O email.
     * @param studentId O ID do estudante.
     */
    public Student(String username, String password, String name, String email, String studentId) {
        super(username, password, name, email);
        this.studentId = studentId;
    }

    /**
     * Obtém o ID do estudante.
     *
     * @return O ID do estudante.
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Obtém a função do usuário.
     *
     * @return A string "Student".
     */
    @Override
    public String getRole() {
        return "Student";
    }

    /**
     * Retorna uma representação em string do objeto Student.
     *
     * @return Uma representação em string do objeto.
     */
    @Override
    public String toString() {
        return "Student{" +
               "username='" + getUsername() + '\'' +
               ", name='" + getName() + '\'' +
               ", studentId='" + studentId + '\'' +
               '}';
    }
}