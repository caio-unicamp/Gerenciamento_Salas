package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList;  
import java.util.List;

/**
 * Representa uma sala de aula.
 */
public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int capacity;
    private String location;
    private boolean hasProjector;
    private List<String> features; 

    /**
     * Construtor para uma sala de aula.
     * @param name O nome da sala.
     * @param capacity A capacidade da sala.
     * @param location A localização da sala.
     * @param hasProjector Se a sala tem um projetor.
     */
    public Classroom(String name, int capacity, String location, boolean hasProjector) {
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.hasProjector = hasProjector;
        this.features = new ArrayList<>();
    }

    /**
     * Construtor para uma sala de aula com características.
     * @param name O nome da sala.
     * @param capacity A capacidade da sala.
     * @param location A localização da sala.
     * @param hasProjector Se a sala tem um projetor.
     * @param features As características da sala.
     */
    public Classroom(String name, int capacity, String location, boolean hasProjector, List<String> features) {
        this(name, capacity, location, hasProjector);
        if (features != null) {
            this.features.addAll(features);
        }
    }

    /**
     * Obtém o nome da sala.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtém a capacidade da sala.
     * @return A capacidade.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Obtém a localização da sala.
     * @return A localização.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Verifica se a sala tem um projetor.
     * @return true se tiver um projetor, false caso contrário.
     */
    public boolean hasProjector() {
        return hasProjector;
    }

    /**
     * Obtém as características da sala.
     * @return A lista de características.
     */
    public List<String> getFeatures() {
        return new ArrayList<>(features);
    }

    /**
     * Adiciona uma característica à sala.
     * @param feature A característica a ser adicionada.
     */
    public void addFeature(String feature) {
        if (feature != null && !feature.trim().isEmpty() && !features.contains(feature)) {
            features.add(feature);
        }
    }

    /**
     * Remove uma característica da sala.
     * @param feature A característica a ser removida.
     */
    public void removeFeature(String feature) {
        features.remove(feature);
    }

    /**
     * Retorna uma representação em string da sala de aula.
     * @return A representação em string.
     */
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

    /**
     * Verifica se dois objetos Classroom são iguais.
     * @param o O objeto a ser comparado.
     * @return true se os objetos forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return name.equals(classroom.name);
    }

    /**
     * Retorna o código hash do objeto.
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}