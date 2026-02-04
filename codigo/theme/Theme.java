
package ldj.theme;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Cursor;
import javax.swing.border.EmptyBorder;

public class Theme {
    public boolean dark = true;

    // Dark
    public final Color darkBg       = new Color(0x0f172a);
    public final Color darkFg       = new Color(0xe2e8f0);
    public final Color darkCardBg   = new Color(0x1f2937);
    public final Color darkBorder   = new Color(0x374151);
    public final Color darkInputBg  = new Color(0x1f2937);

    // Light
    public final Color lightBg       = new Color(0xf9fafb);
    public final Color lightFg       = new Color(0x111111);
    public final Color lightCardBg   = new Color(0xe5e7eb);
    public final Color lightBorder   = new Color(0xCCCCCC);
    public final Color lightInputBg  = Color.WHITE;

    // Primary
    public final Color primary      = new Color(0x3b82f6);

    public Color bg()      { return dark ? darkBg      : lightBg; }
    public Color fg()      { return dark ? darkFg      : lightFg; }
    public Color cardBg()  { return dark ? darkCardBg  : lightCardBg; }
    public Color border()  { return dark ? darkBorder  : lightBorder; }
    public Color inputBg() { return dark ? darkInputBg : lightInputBg; }

    public void stylePrimaryButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(primary);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(),
                new EmptyBorder(8, 12, 8, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}

