package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList; // Exemplo de uso de Arrays (Listas) 
import java.util.List;

// Exemplo de classe 
public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L; // Para serialização
    private String name;
    private int capacity;
    private String location;
    private boolean hasProjector;
    private List<String> features; // Exemplo de uso de Arrays (Listas) 

    // Construtor
    public Classroom(String name, int capacity, String location, boolean hasProjector) {
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.hasProjector = hasProjector;
        this.features = new ArrayList<>();
    }

    // Sobrecarga de construtor (exemplo de sobrecarga de métodos) 
    public Classroom(String name, int capacity, String location, boolean hasProjector, List<String> features) {
        this(name, capacity, location, hasProjector);
        if (features != null) {
            this.features.addAll(features);
        }
    }

    // Métodos de acesso (Getters) 
    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    public boolean hasProjector() {
        return hasProjector;
    }

    public List<String> getFeatures() {
        return new ArrayList<>(features); // Retorna uma cópia para evitar modificação externa
    }

    // Métodos gerais 
    public void addFeature(String feature) {
        if (feature != null && !feature.trim().isEmpty() && !features.contains(feature)) {
            features.add(feature);
        }
    }

    public void removeFeature(String feature) {
        features.remove(feature);
    }

    @Override
    public String toString() {
        return "Classroom{" +
               "name='" + name + '\'' +
               ", capacity=" + capacity +
               ", location='" + location + '\'' +
               ", hasProjector=" + hasProjector +
               ", features=" + features +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return name.equals(classroom.name); // Salas são consideradas iguais se têm o mesmo nome
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}