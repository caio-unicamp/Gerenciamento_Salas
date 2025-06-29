package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// Classe utilitária para operações de arquivo (leitura e gravação) 
public class FileUtil {

    // Método para escrever um objeto (ou lista de objetos) em um arquivo
    public static <T extends Serializable> void writeObjectToFile(List<T> objects, String filename) throws IOException {
        Path filePath = Paths.get(filename);
        Files.createDirectories(filePath.getParent()); // Garante que o diretório exista

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(objects);
        }
    }

    // Método para ler um objeto (ou lista de objetos) de um arquivo
    @SuppressWarnings("unchecked") // Supressão para o cast de Object para List<T>
    public static <T extends Serializable> List<T> readObjectFromFile(String filename) throws IOException, ClassNotFoundException {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) { // Verifica se o arquivo existe e não está vazio
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<T>) ois.readObject();
        }
    }
}