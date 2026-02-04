
package ldj;

import javax.swing.SwingUtilities;
import ldj.auth.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ldj.auth.LoginFrame().setVisible(true);
        });
    }
}
