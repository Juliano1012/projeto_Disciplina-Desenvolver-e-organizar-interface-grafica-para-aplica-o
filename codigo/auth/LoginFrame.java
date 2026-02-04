package ldj.auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import ldj.ui.MainFrame;
import ldj.ui.AdminFrame; // <- garante o import correto

public class LoginFrame extends JFrame {

    private final JTextField userField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);

    public LoginFrame() {
        setTitle("Login - LdJ");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login LdJ", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(title, c);

        c.gridwidth = 1; c.gridy++;
        panel.add(new JLabel("Usuário:"), c);
        c.gridx = 1;
        panel.add(userField, c);

        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Senha:"), c);
        c.gridx = 1;
        panel.add(passField, c);

        JButton loginBtn = new JButton("Entrar");
        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        panel.add(loginBtn, c);

        loginBtn.addActionListener(e -> login());

        add(panel);
    }

    private void login() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (AuthService.authenticate(user, pass)) {
            if (AuthService.isAdmin(user)) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, administrador!");
                dispose();
                // AQUI: AdminFrame reconhecido e com setVisible(true) funcionando
                new AdminFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Login realizado com sucesso!");
                dispose();
                new MainFrame().setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha incorretos!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}