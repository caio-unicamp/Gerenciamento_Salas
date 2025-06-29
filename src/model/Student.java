package model;

/**
 * Representa um usuário do tipo Estudante no sistema.
 * Estudantes podem realizar reservas de salas e possuem um RA/Matrícula.
 */
public class Student extends User {
    private String studentId;

    /**
     * Construtor do estudante.
     *
     * @param username  Nome de usuário.
     * @param password  Senha.
     * @param name      Nome completo.
     * @param email     Email do estudante.
     * @param studentId RA/Matrícula do estudante.
     */
    public Student(String username, String password, String name, String email, String studentId) {
        super(username, password, name, email);
        this.studentId = studentId;
    }

    /**
     * Retorna o RA/Matrícula do estudante.
     * @return RA/Matrícula.
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Retorna o papel do usuário.
     * @return String "Student".
     */
    @Override
    public String getRole() {
        return "Student";
    }

    /**
     * Retorna uma representação em string do estudante.
     * @return String representando o estudante.
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