import com.formdev.flatlaf.FlatLightLaf;
import gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        // Tenta aplicar o Look and Feel FlatLaf, que é mais moderno.
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // --- [NOVO] Configurações globais de fontes ---
            // Define uma fonte base mais moderna
            Font baseFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("defaultFont", baseFont);
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o Look and Feel FlatLaf.");
        }

        // Garante que a GUI seja criada na thread de eventos da AWT
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}