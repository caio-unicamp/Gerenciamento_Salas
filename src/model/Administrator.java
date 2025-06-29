package model;

public class Administrator extends User {

    public Administrator(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    @Override
    public String getRole() {
        return "Administrator";
    }

    // Métodos específicos para administrador, por exemplo, gerenciar salas
    public void manageClassroomDetails() {
        System.out.println("Administrador " + getUsername() + " está gerenciando detalhes de salas.");
        // Implementar lógica de adição/edição/remoção de salas
    }

    @Override
    public String toString() {
        return "Administrator{" +
               "username='" + getUsername() + '\'' +
               ", name='" + getName() + '\'' +
               '}';
    }
}