package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Classe utilitária para operações de arquivo (leitura e gravação) de listas de objetos serializáveis.
 */
public class FileUtil {

    /**
     * Escreve uma lista de objetos serializáveis em um arquivo.
     *
     * @param objects  Lista de objetos a serem gravados.
     * @param filename Caminho do arquivo de destino.
     * @param <T>      Tipo dos objetos (deve implementar Serializable).
     * @throws IOException Se ocorrer erro de escrita.
     */
    public static <T extends Serializable> void writeObjectToFile(List<T> objects, String filename) throws IOException {
        Path filePath = Paths.get(filename);
        Files.createDirectories(filePath.getParent()); // Garante que o diretório exista

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(objects);
        }
    }

    /**
     * Lê uma lista de objetos serializáveis de um arquivo.
     *
     * @param filename Caminho do arquivo de origem.
     * @param <T>      Tipo dos objetos (deve implementar Serializable).
     * @return Lista de objetos lidos ou null se o arquivo não existir ou estiver vazio.
     * @throws IOException            Se ocorrer erro de leitura.
     * @throws ClassNotFoundException Se a classe dos objetos não for encontrada.
     */
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