import com.formdev.flatlaf.FlatLightLaf;
import gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Tenta aplicar o Look and Feel FlatLaf, que é mais moderno.
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
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