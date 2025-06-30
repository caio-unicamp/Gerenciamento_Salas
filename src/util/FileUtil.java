package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Classe utilitária para operações de arquivo.
 */
public class FileUtil {

    /**
     * Grava uma lista de objetos em um arquivo.
     *
     * @param objects A lista de objetos a ser gravada.
     * @param filename O nome do arquivo.
     * @param <T> O tipo dos objetos na lista.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    public static <T extends Serializable> void writeObjectToFile(List<T> objects, String filename) throws IOException {
        Path filePath = Paths.get(filename);
        Files.createDirectories(filePath.getParent());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(objects);
        }
    }

    /**
     * Lê uma lista de objetos de um arquivo.
     *
     * @param filename O nome do arquivo.
     * @param <T> O tipo dos objetos na lista.
     * @return A lista de objetos lida do arquivo.
     * @throws IOException Se ocorrer um erro de I/O.
     * @throws ClassNotFoundException Se a classe do objeto não for encontrada.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> readObjectFromFile(String filename) throws IOException, ClassNotFoundException {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<T>) ois.readObject();
        }
    }
}