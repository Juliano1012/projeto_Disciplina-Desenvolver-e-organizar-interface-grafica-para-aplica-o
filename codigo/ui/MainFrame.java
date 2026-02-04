
package ldj.ui;

import ldj.data.GameData;
import ldj.model.Game;
import ldj.theme.Theme;
import ldj.ui.dialogs.FavoritesDialog;
import ldj.ui.dialogs.HistoryDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {

    private final Theme theme = new Theme();

    // Troquei para GridBagLayout porque eu quero 3 cards fixos por linha (3 x N)
    private final JPanel cardsContainer = new JPanel(new GridBagLayout());

    private final JTextField searchField = new JTextField(22);
    private final JButton themeToggle = new JButton("🌙/☀️");
    private final JButton btnHistorico = new JButton("📜 Histórico");
    private final JButton btnFavoritos = new JButton("⭐ Favoritos");

    private final List<Game> allGames = GameData.all();
    private final List<GameCard> cardViews = new ArrayList<>();

    public MainFrame() {
        setTitle("LdJ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Barra superior (busca + botões)
        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);

        theme.stylePrimaryButton(btnHistorico);
        theme.stylePrimaryButton(btnFavoritos);
        theme.stylePrimaryButton(themeToggle);

        c.gridx = 0; c.gridy = 0; top.add(btnHistorico, c);
        c.gridx = 1; top.add(btnFavoritos, c);
        c.gridx = 2; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; top.add(searchField, c);
        c.gridx = 3; c.weightx = 0; c.fill = GridBagConstraints.NONE; top.add(themeToggle, c);

        // Área dos cards com rolagem
        cardsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);

        // Montagem inicial dos cards
        buildCards();

        // Eventos de busca (qualquer mudança no texto dispara o filtro)
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });

        // Alterno entre tema claro/escuro e reaplico no conteúdo inteiro
        themeToggle.addActionListener(e -> {
            theme.dark = !theme.dark;
            applyThemeRecursive(getContentPane());
        });

        // Abro dialog de Favoritos
        btnFavoritos.addActionListener(e ->
                new FavoritesDialog(this, theme).setVisible(true));

        // Abro dialog de Histórico
        btnHistorico.addActionListener(e ->
                new HistoryDialog(this, theme).setVisible(true));

        // Aplico o tema inicial
        applyThemeRecursive(getContentPane());
    }

    // Aqui eu posiciono todos os cards em uma grade de 3 colunas (3 por linha)
    private void buildCards() {
        cardsContainer.removeAll();
        cardViews.clear();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16); // espaço entre os cards
        gbc.fill = GridBagConstraints.NONE;      // mantenho o tamanho preferido do GameCard
        gbc.anchor = GridBagConstraints.NORTHWEST;

        final int colunas = 3;
        int visibleIndex = 0;

        for (Game g : allGames) {
            GameCard card = new GameCard(g, theme);
            cardViews.add(card);

            // calculo a coluna (0..2) e a linha (0..N) pela ordem
            gbc.gridx = visibleIndex % colunas;
            gbc.gridy = visibleIndex / colunas;
            cardsContainer.add(card, gbc);

            visibleIndex++;
        }

        // adiciono um "glue" para ocupar o espaço restante e manter tudo colado no topo/esquerda
        gbc.gridx = 0;
        gbc.gridwidth = colunas;
        gbc.weightx = 1;
        gbc.weighty = 1;
        cardsContainer.add(Box.createGlue(), gbc);

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // Filtro pelo texto e, depois, recompacto os visíveis mantendo 3 por linha
    private void filter() {
        String q = searchField.getText().trim().toLowerCase(Locale.ROOT);

        for (GameCard card : cardViews) {
            boolean show = card.gameTitle().toLowerCase(Locale.ROOT).contains(q);
            card.setVisible(show);
        }

        // Reorganizo a grade só com os que ficaram visíveis
        relayoutVisibleCards();
    }

    // Reposiciono apenas os cards que estão visíveis (3 colunas, várias linhas)
    private void relayoutVisibleCards() {
        cardsContainer.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        final int colunas = 3;
        int visibleIndex = 0;

        for (GameCard card : cardViews) {
            if (!card.isVisible()) continue;

            gbc.gridx = visibleIndex % colunas;
            gbc.gridy = visibleIndex / colunas;
            cardsContainer.add(card, gbc);

            visibleIndex++;
        }

        // glue para fixar no topo/esquerda e permitir rolagem vertical natural
        gbc.gridx = 0;
        gbc.gridwidth = colunas;
        gbc.weightx = 1;
        gbc.weighty = 1;
        cardsContainer.add(Box.createGlue(), gbc);

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // Aplico o tema recursivamente nos componentes e nos GameCards
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
        if (comp instanceof GameCard) ((GameCard) comp).applyThemeNow();

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                applyThemeRecursive(child);
            }
        }
        getContentPane().setBackground(theme.bg());
    }
}
