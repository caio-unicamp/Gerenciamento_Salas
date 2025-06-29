package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList;  
import java.util.List;

/**
 * Representa uma sala de aula no sistema.
 * Contém informações como nome, capacidade, localização, presença de projetor e características adicionais.
 */
public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L; // Para serialização
    private String name;
    private int capacity;
    private String location;
    private boolean hasProjector;
    private List<String> features; 

    /**
     * Construtor principal da sala.
     *
     * @param name         Nome da sala.
     * @param capacity     Capacidade máxima da sala.
     * @param location     Localização da sala.
     * @param hasProjector Indica se a sala possui projetor.
     */
    public Classroom(String name, int capacity, String location, boolean hasProjector) {
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.hasProjector = hasProjector;
        this.features = new ArrayList<>();
    }

    /**
     * Construtor sobrecarregado que permite adicionar características extras.
     *
     * @param name         Nome da sala.
     * @param capacity     Capacidade máxima da sala.
     * @param location     Localização da sala.
     * @param hasProjector Indica se a sala possui projetor.
     * @param features     Lista de características adicionais.
     */
    public Classroom(String name, int capacity, String location, boolean hasProjector, List<String> features) {
        this(name, capacity, location, hasProjector);
        if (features != null) {
            this.features.addAll(features);
        }
    }

    // Métodos de acesso (Getters) 

    /**
     * Retorna o nome da sala.
     * @return Nome da sala.
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna a capacidade máxima da sala.
     * @return Capacidade da sala.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Retorna a localização da sala.
     * @return Localização da sala.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Indica se a sala possui projetor.
     * @return true se possui projetor, false caso contrário.
     */
    public boolean hasProjector() {
        return hasProjector;
    }

    /**
     * Retorna uma lista das características adicionais da sala.
     * @return Lista de características (cópia defensiva).
     */
    public List<String> getFeatures() {
        return new ArrayList<>(features); // Retorna uma cópia para evitar modificação externa
    }

    // Métodos gerais 

    /**
     * Adiciona uma característica à sala, se ainda não existir.
     * @param feature Característica a ser adicionada.
     */
    public void addFeature(String feature) {
        if (feature != null && !feature.trim().isEmpty() && !features.contains(feature)) {
            features.add(feature);
        }
    }

    /**
     * Remove uma característica da sala.
     * @param feature Característica a ser removida.
     */
    public void removeFeature(String feature) {
        features.remove(feature);
    }

    /**
     * Retorna uma representação em string da sala.
     * @return String representando a sala.
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
     * Compara duas salas pelo nome.
     * @param o Objeto a ser comparado.
     * @return true se os nomes forem iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return name.equals(classroom.name); // Salas são consideradas iguais se têm o mesmo nome
    }

    /**
     * Retorna o hash code baseado no nome da sala.
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}