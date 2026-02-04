package ldj.ui.dialogs;

import ldj.model.HistoryEntry;
import ldj.storege.Storege;
import ldj.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
// imports específicos de AWT (eu prefiro sem wildcard aqui)
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Component;
// imports utilitários explícitos
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HistoryDialog extends JDialog {
    private final Theme theme;
    private final JPanel listPanel = new JPanel();
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // altura fixa para todas as linhas do histórico
    private static final int ROW_HEIGHT = 64;

    public HistoryDialog(JFrame parent, Theme theme) {
        super(parent, " Histórico de Acessos", true);
        this.theme = theme;

        setLayout(new BorderLayout());
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);

        // >>> Garantir barra de rolagem vertical e desabilitar horizontal
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // rolagem mais suave
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        // botão fechar (eu mantenho)
        JButton close = new JButton("Fechar");
        theme.stylePrimaryButton(close);
        close.addActionListener(e -> dispose());

        // botão novo para limpar o histórico de uma vez
        JButton clear = new JButton("🗑️ Limpar histórico");
        theme.stylePrimaryButton(clear);
        clear.addActionListener(e -> onClearHistory());

        // barra superior com os botões
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        top.add(close);
        top.add(clear);
        add(top, BorderLayout.NORTH);

        setPreferredSize(new Dimension(600, 480));
        applyTheme(scroll);
        reload();
        pack();
        setLocationRelativeTo(parent);
    }

    private void applyTheme(JScrollPane scroll) {
        getContentPane().setBackground(theme.bg());
        listPanel.setBackground(theme.bg());
        // manter o fundo do viewport consistente com o tema
        if (scroll != null && scroll.getViewport() != null) {
            scroll.getViewport().setBackground(theme.bg());
        }
    }

    private void reload() {
        listPanel.removeAll();

        // eu busco o histórico já com o timestamp certo (epochMillis)
        List<HistoryEntry> hist = Storege.getHistory();

        if (hist.isEmpty()) {
            JLabel empty = new JLabel("Nenhuma abertura registrada.");
            empty.setForeground(theme.fg());
            empty.setBorder(new EmptyBorder(16,16,16,16));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(empty);
        } else {
            // mostro o mais recente primeiro
            List<HistoryEntry> copy = new ArrayList<>(hist);
            Collections.reverse(copy);

            for (HistoryEntry h : copy) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(theme.cardBg());
                row.setBorder(new EmptyBorder(12, 12, 12, 12));

                // >>> Tamanho fixo de altura para todas as linhas
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                row.setMinimumSize(new Dimension(0, ROW_HEIGHT));
                row.setPreferredSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));

                String time = fmt.format(Date.from(Instant.ofEpochMilli(h.time)));
                JLabel label = new JLabel(h.game + " — " + time);
                label.setForeground(theme.fg());
                label.setFont(label.getFont().deriveFont(14f));

                row.add(label, BorderLayout.CENTER);
                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(8));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // quando eu clico para limpar, eu confirmo antes de apagar tudo
    private void onClearHistory() {
        int op = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja limpar todo o histórico?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (op == JOptionPane.YES_OPTION) {
            Storege.clearHistory();
            reload();
            JOptionPane.showMessageDialog(this, "Histórico limpo com sucesso.");
        }
    }
}