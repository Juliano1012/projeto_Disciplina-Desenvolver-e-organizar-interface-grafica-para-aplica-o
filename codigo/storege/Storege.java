package ldj.storege;

import java.util.*;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import ldj.model.HistoryEntry;
// NOVO:
import ldj.model.Game;

public class Storege {

    // mantenho o mesmo nó de preferências
    private static final Preferences PREFS =
        Preferences.userNodeForPackage(Storege.class);

    // chaves padronizadas
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_HISTORY   = "history";
    // NOVO: armazenamento de jogos customizados
    private static final String KEY_CUSTOM_GAMES = "custom_games";

    // separador simples entre "nome" e "timestamp" no histórico
    private static final String SEP = "|";

    // === NOVO: delimitador para serializar campos de Game ===
    private static final String GAME_DELIM = ":::"; // título:::img:::open:::portalLabel:::portalUrl

    // ---------- Favoritos ----------

    public static void addFavorite(String game) {
        List<String> fav = getFavorites();
        if (!fav.contains(game)) {
            fav.add(game);
            save(KEY_FAVORITES, fav);
        }
    }

    public static List<String> getFavorites() {
        return load(KEY_FAVORITES);
    }

    public static void removeFavorite(String game) {
        List<String> fav = getFavorites();
        fav.remove(game);
        save(KEY_FAVORITES, fav);
    }

    // ---------- Histórico ----------

    public static void addHistory(String game) {
        long now = System.currentTimeMillis();
        List<String> hist = load(KEY_HISTORY);
        hist.add(game + SEP + now);
        save(KEY_HISTORY, hist);
    }

    public static List<HistoryEntry> getHistory() {
        List<HistoryEntry> list = new ArrayList<>();
        for (String raw : load(KEY_HISTORY)) {
            HistoryEntry entry = parseHistoryLine(raw);
            if (entry != null) {
                list.add(entry);
            }
        }
        return list;
    }

    public static void clearHistory() {
        save(KEY_HISTORY, new ArrayList<>()); // salvo vazio e pronto
    }

    private static HistoryEntry parseHistoryLine(String raw) {
        if (raw == null || raw.isEmpty()) return null;

        // 1) formato novo: "nome|1738100275123"
        int idx = raw.lastIndexOf(SEP);
        if (idx > 0) {
            String name = raw.substring(0, idx).trim();
            String tail = raw.substring(idx + SEP.length()).trim();
            if (isNumeric(tail)) {
                try {
                    long millis = Long.parseLong(tail);
                    return new HistoryEntry(name, millis);
                } catch (NumberFormatException ignore) {
                    // tenta formato antigo
                }
            }
        }

        // 2) formato antigo: "nome | Mon Jan 26 21:46:19 BRT 2026"
        int idxOld = raw.indexOf(" | ");
        if (idxOld > 0) {
            String name = raw.substring(0, idxOld).trim();
            String dateStr = raw.substring(idxOld + 3).trim();

            Long millis = tryParseLegacyDate(dateStr);
            if (millis != null) {
                return new HistoryEntry(name, millis);
            } else {
                return new HistoryEntry(name, System.currentTimeMillis());
            }
        }

        // 3) se não couber em nenhum formato, ignora
        return null;
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    private static Long tryParseLegacyDate(String s) {
        try {
            long millis = Date.parse(s);
            return millis;
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- Jogos customizados (admin) ----------

    // Salva jogo customizado, evitando duplicatas por título (case-insensitive).
    public static void addCustomGame(Game g) {
        if (g == null || g.title == null || g.title.trim().isEmpty()) return;

        List<String> lines = load(KEY_CUSTOM_GAMES);

        // remove entradas com mesmo título (case-insensitive)
        String titleLower = g.title.trim().toLowerCase(Locale.ROOT);
        lines.removeIf(line -> {
            Game parsed = decodeGame(line);
            return parsed != null && parsed.title.trim().toLowerCase(Locale.ROOT).equals(titleLower);
        });

        // adiciona a nova versão
        lines.add(encodeGame(g));
        save(KEY_CUSTOM_GAMES, lines);
    }

    public static List<Game> getCustomGames() {
        List<Game> out = new ArrayList<>();
        for (String raw : load(KEY_CUSTOM_GAMES)) {
            Game g = decodeGame(raw);
            if (g != null) out.add(g);
        }
        return out;
    }

    private static String encodeGame(Game g) {
        // substitui quebras de linha para não quebrar o armazenamento por linhas
        String t  = safe(g.title);
        String im = safe(g.imageUrl);
        String op = safe(g.openUrl);
        String pl = safe(g.portalLabel);
        String pu = safe(g.portalUrl);
        return String.join(GAME_DELIM, t, im, op, pl, pu);
    }

    private static Game decodeGame(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        String[] p = raw.split(Pattern.quote(GAME_DELIM), -1);
        if (p.length < 5) return null;
        String t  = unsafe(p[0]);
        String im = unsafe(p[1]);
        String op = unsafe(p[2]);
        String pl = unsafe(p[3]);
        String pu = unsafe(p[4]);
        return new Game(t, im, op, pl, pu);
    }

    private static String safe(String s) {
        if (s == null) return "";
        // remove quebras de linha e aparas
        String x = s.replace("\r", " ").replace("\n", " ").trim();
        // não usamos escape de GAME_DELIM; assumimos que não será digitado
        return x;
    }
    private static String unsafe(String s) {
        return s; // sem transformações no momento
    }

    // ---------- Persistência crua (string com quebras de linha) ----------

    private static List<String> load(String key) {
        String data = PREFS.get(key, "");
        if (data.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(data.split("\n")));
    }

    private static void save(String key, List<String> values) {
        PREFS.put(key, String.join("\n", values));
    }
}