package model;

public class Student extends User {
    private String studentId;

    public Student(String username, String password, String name, String email, String studentId) {
        super(username, password, name, email);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return "Student{" +
               "username='" + getUsername() + '\'' +
               ", name='" + getName() + '\'' +
               ", studentId='" + studentId + '\'' +
               '}';
    }
}