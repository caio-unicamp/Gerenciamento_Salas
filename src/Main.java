import com.formdev.flatlaf.FlatLightLaf;
import gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

/**
 * Classe principal da aplicação.
 */
public class Main {
    /**
     * Método principal.
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            Font baseFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("defaultFont", baseFont);
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o Look and Feel FlatLaf.");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}