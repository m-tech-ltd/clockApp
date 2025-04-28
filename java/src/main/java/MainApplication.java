import com.github.mtechltd.CustomClockApp;

import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        // Use the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the application
        SwingUtilities.invokeLater(() -> {
            CustomClockApp app = new CustomClockApp();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
    }
}
