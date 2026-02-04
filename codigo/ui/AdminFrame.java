package ldj.ui;

import ldj.model.Game;
import ldj.storege.Storege;
import ldj.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminFrame extends JFrame { // <- importante: extends JFrame

    private final Theme theme = new Theme();

    private final JTextField titleField       = new JTextField(30);
    private final JTextField imageUrlField    = new JTextField(30);
    private final JTextField openUrlField     = new JTextField(30);
    private final JTextField portalLabelField = new JTextField(30);
    private final JTextField portalUrlField   = new JTextField(30);

    public AdminFrame() {
        setTitle("Admin - Adicionar Jogo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(16,16,16,16));
        setContentPane(root);

        JLabel header = new JLabel("Adicionar novo jogo ao catálogo", SwingConstants.LEFT);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;
        addRow(form, c, row++, "Título*", titleField);
        addRow(form, c, row++, "URL da imagem", imageUrlField);
        addRow(form, c, row++, "URL para abrir*", openUrlField);
        addRow(form, c, row++, "Rótulo do portal", portalLabelField);
        addRow(form, c, row++, "URL do portal", portalUrlField);

        JButton btnSalvar = new JButton("Salvar jogo");
        JButton btnCatalogo = new JButton("Abrir Catálogo");
        JButton btnSair = new JButton("Sair");

        theme.stylePrimaryButton(btnSalvar);
        theme.stylePrimaryButton(btnCatalogo);
        theme.stylePrimaryButton(btnSair);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.add(btnSalvar);
        actions.add(btnCatalogo);
        actions.add(btnSair);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        // Ações
        btnSalvar.addActionListener(e -> onSave());
        btnCatalogo.addActionListener(e -> {
            new MainFrame().setVisible(true);
            dispose();
        });
        btnSair.addActionListener(e -> {
            dispose();
            new ldj.auth.LoginFrame().setVisible(true);
        });

        applyThemeRecursive(getContentPane());
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.gridwidth = 1;
        JLabel l = new JLabel(label);
        panel.add(l, c);

        c.gridx = 1; c.weightx = 1; c.gridwidth = 2;
        panel.add(field, c);
    }

    private void onSave() {
        String title = titleField.getText().trim();
        String img   = imageUrlField.getText().trim();
        String open  = openUrlField.getText().trim();
        String plab  = portalLabelField.getText().trim();
        String purl  = portalUrlField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o título do jogo.");
            titleField.requestFocus();
            return;
        }
        if (open.isEmpty() || !isLikelyUrl(open)) {
            JOptionPane.showMessageDialog(this, "Informe uma URL válida para abrir o jogo.");
            openUrlField.requestFocus();
            return;
        }
        if (!img.isEmpty() && !isLikelyUrl(img)) {
            JOptionPane.showMessageDialog(this, "URL de imagem inválida.");
            imageUrlField.requestFocus();
            return;
        }
        if (!purl.isEmpty() && !isLikelyUrl(purl)) {
            JOptionPane.showMessageDialog(this, "URL do portal inválida.");
            portalUrlField.requestFocus();
            return;
        }

        Game g = new Game(title, img, open, plab, purl);
        Storege.addCustomGame(g);

        JOptionPane.showMessageDialog(this, "Jogo salvo no catálogo!");
        clearForm();
    }

    private boolean isLikelyUrl(String s) {
        return s.startsWith("http://") || s.startsWith("https://");
    }

    private void clearForm() {
        titleField.setText("");
        imageUrlField.setText("");
        openUrlField.setText("");
        portalLabelField.setText("");
        portalUrlField.setText("");
        titleField.requestFocus();
    }

    // Aplicação de tema semelhante ao MainFrame
    private void applyThemeRecursive(Component comp) {
        if (comp instanceof JPanel) comp.setBackground(theme.bg());
        if (comp instanceof JLabel) ((JLabel)comp).setForeground(theme.fg());
        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(theme.inputBg());
            tf.setForeground(theme.fg());
            tf.setCaretColor(theme.fg());
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme.border()),
                    new EmptyBorder(8,10,8,10)
            ));
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                applyThemeRecursive(child);
            }
        }
        getContentPane().setBackground(theme.bg());
    }
}