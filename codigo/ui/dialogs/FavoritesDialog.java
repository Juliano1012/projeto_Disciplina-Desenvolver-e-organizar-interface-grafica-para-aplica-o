
package ldj.ui.dialogs;

import ldj.model.Game;
import ldj.data.GameData;
import ldj.storege.Storege;
import ldj.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class FavoritesDialog extends JDialog {
    private final Theme theme;
    private final JPanel listPanel = new JPanel();

    public FavoritesDialog(JFrame parent, Theme theme) {
        super(parent, " Meus Favoritos", true);
        this.theme = theme;

        setLayout(new BorderLayout());
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton close = new JButton("Fechar");
        theme.stylePrimaryButton(close);
        close.addActionListener(e -> dispose());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        top.add(close);
        add(top, BorderLayout.NORTH);

        setPreferredSize(new Dimension(600, 480));
        applyTheme();
        reload();
        pack();
        setLocationRelativeTo(parent);
    }

    private void applyTheme() {
        getContentPane().setBackground(theme.bg());
        listPanel.setBackground(theme.bg());
    }

    private void reload() {
        listPanel.removeAll();
        List<String> fav = Storege.getFavorites();

        if (fav.isEmpty()) {
            JLabel empty = new JLabel("Nenhum favorito salvo.");
            empty.setForeground(theme.fg());
            empty.setBorder(new EmptyBorder(16,16,16,16));
            listPanel.add(empty);
        } else {
            for (String gameName : fav) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(theme.cardBg());
                row.setBorder(new EmptyBorder(12, 12, 12, 12));

                JLabel name = new JLabel(gameName);
                name.setForeground(theme.fg());
                name.setFont(name.getFont().deriveFont(Font.BOLD, 16f));

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                actions.setOpaque(false);

                JButton play = new JButton(" Jogar");
                JButton remove = new JButton("️ Remover");
                theme.stylePrimaryButton(play);
                theme.stylePrimaryButton(remove);

                play.addActionListener(e -> {
                    Optional<Game> g = GameData.all().stream()
                            .filter(x -> x.title.equalsIgnoreCase(gameName))
                            .findFirst();
                    if (g.isPresent()) {
                        try {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(new java.net.URI(g.get().openUrl));
                                Storege.addHistory(gameName);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Falha ao abrir link.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Link não encontrado.");
                    }
                });

                remove.addActionListener(e -> {
                    Storege.removeFavorite(gameName);
                    reload();
                });

                actions.add(play);
                actions.add(remove);

                row.add(name, BorderLayout.CENTER);
                row.add(actions, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(8));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}
