
package ldj.ui;

import ldj.model.Game;
import ldj.storege.Storege;
import ldj.theme.Theme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;

public class GameCard extends JPanel {
    private final Game game;
    private final Theme theme;
    private final JLabel imgLabel = new JLabel();
    private final JLabel title = new JLabel();

    public GameCard(Game game, Theme theme) {
        this.game = game;
        this.theme = theme;

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(300, 360));
        setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.insets = new Insets(4,0,4,0);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Imagem
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(new Color(0x0b1220));
        imgLabel.setPreferredSize(new Dimension(276, 170));
        c.gridy = 0; c.fill = GridBagConstraints.BOTH;
        add(imgLabel, c);

        // Título
        title.setText(game.title);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 1; c.fill = GridBagConstraints.HORIZONTAL;
        add(title, c);

        // Botões
        JButton btnOpen   = new JButton("Abrir " + firstWord(game.title));
        JButton btnPortal = new JButton(game.portalLabel);
        JButton btnFav    = new JButton("⭐ Favoritar");

        theme.stylePrimaryButton(btnOpen);
        theme.stylePrimaryButton(btnPortal);
        theme.stylePrimaryButton(btnFav);

        JPanel btns = new JPanel(new GridLayout(3,1,6,6));
        btns.setOpaque(false);
        btns.add(btnOpen);
        btns.add(btnPortal);
        btns.add(btnFav);

        c.gridy = 2; c.weighty = 1.0; c.anchor = GridBagConstraints.PAGE_END;
        add(btns, c);

        // Ações
        btnOpen.addActionListener(e -> {
            open(game.openUrl);
            Storege.addHistory(game.title);
        });

        btnPortal.addActionListener(e -> open(game.portalUrl));

        btnFav.addActionListener(e -> {
            Storege.addFavorite(game.title);
            JOptionPane.showMessageDialog(this, game.title + " favoritado!");
        });

        applyTheme();
        loadImageAsync(game.imageUrl);
    }

    private void applyTheme() {
        setBackground(theme.cardBg());
        title.setForeground(theme.fg());
    }

    private String firstWord(String s) {
        String[] p = s.split("\\s+");
        return p.length > 0 ? p[0] : s;
    }

    private void open(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this, "Abertura de links não suportada.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Falha ao abrir: " + url);
        }
    }

    private void loadImageAsync(String url) {
        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() {
                try {
                    BufferedImage img = ImageIO.read(new URL(url));
                    if (img == null) return null;
                    int targetW = 276, targetH = 170;
                    double sw = (double) targetW / img.getWidth();
                    double sh = (double) targetH / img.getHeight();
                    double scale = Math.min(sw, sh);
                    int w = Math.max(1, (int)(img.getWidth()*scale));
                    int h = Math.max(1, (int)(img.getHeight()*scale));
                    Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    BufferedImage canvas = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = canvas.createGraphics();
                    g2.setColor(new Color(0x0b1220));
                    g2.fillRect(0,0,targetW,targetH);
                    g2.drawImage(scaled, (targetW-w)/2, (targetH-h)/2, null);
                    g2.dispose();
                    return canvas;
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void done() {
                try {
                    BufferedImage img = get();
                    if (img != null) imgLabel.setIcon(new ImageIcon(img));
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    // ======= Métodos expostos para o MainFrame (evita erros) =======
    public String gameTitle() { return game.title; }   // <- usado no filtro
    public void applyThemeNow() { applyTheme(); }      // <- usado ao alternar tema
}

