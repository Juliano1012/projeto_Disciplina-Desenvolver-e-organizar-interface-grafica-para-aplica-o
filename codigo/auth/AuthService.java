package ldj.auth;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private static final Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("admin", "1234");
        USERS.put("juliano", "ldj2026");
    }

    public static boolean authenticate(String user, String pass) {
        if (user == null || pass == null) return false;
        return pass.equals(USERS.get(user));
    }

    // === NOVO: identifica se o usuário é administrador ===
    public static boolean isAdmin(String user) {
        return user != null && user.equalsIgnoreCase("admin");
    }
}
