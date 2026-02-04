package ldj.data;

import java.util.*;
import ldj.model.Game;
// NOVO:
import ldj.storege.Storege;

public class GameData {

    public static List<Game> all() {
        List<Game> list = new ArrayList<>();

        list.add(new Game(
            "Valorant (Riot)",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqEJcsyEWxiLe_Fv5dOyYAK1tWOAjjPcMlvg&s",
            "https://playvalorant.com/pt-br/",
            "Portal Riot",
            "https://www.riotgames.com/pt-br"
        ));

        list.add(new Game(
            "League of Legends (Riot)",
            "https://wallpapers.com/images/hd/league-of-legends-background-o9ymho1kg8vmx6p6.jpg",
            "https://www.leagueoflegends.com/pt-br/",
            "Portal Riot",
            "https://www.riotgames.com/pt-br"
        ));

        list.add(new Game(
            "Minecraft (Microsoft)",
            "https://fazendoanossafesta.com.br/wp-content/uploads/2021/12/Adesivo-Capa-de-Caderno-Horizontal-Minecraft.jpg",
            "https://www.minecraft.net/pt-br/login",
            "Conta Microsoft",
            "https://account.microsoft.com/"
        ));

        list.add(new Game(
            "Counter-Strike 2 (Steam)",
            "https://s2-techtudo.glbimg.com/nc_YPmxU6MvKilsIimFWdect8pI=/0x0:2400x1350/984x0/smart/filters:strip_icc()/i.s3.glbimg.com/v1/AUTH_08fbf48bc0524877943fe86e43087e7a/internal_photos/bs/2023/1/1/q88ar8T2WQF2YEGOkKPw/counter-strike-2-ingles.png",
            "https://store.steampowered.com/app/730/CounterStrike_2/",
            "Login Steam",
            "https://store.steampowered.com/login/"
        ));

        list.add(new Game(
            "Roblox",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQAQC5JXPPhD0JKAh4fT0d5Cu4bWfLobUVcoQ&s",
            "https://www.roblox.com/pt/login/",
            "Gerenciar Conta",
            "https://www.roblox.com/pt/account/settings"
        ));

        list.add(new Game(
            "Overwatch 2 (Blizzard)",
            "https://i.ytimg.com/vi/GKXS_YA9s7E/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLBf5464tjEyqWFjaV2Y3nVgTws32Q",
            "https://overwatch.blizzard.com/pt-br/",
            "Login Battle.net",
            "https://account.battle.net/"
        ));

        list.add(new Game(
            "Call of Duty: Warzone",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS5J7WHjZdKtXe24y79P1DPZ-gw4Gk-iyL09w&s",
            "https://www.callofduty.com/br/pt/playnow/warzone",
            "Portal CoD",
            "https://www.callofduty.com/br/pt/"
        ));

        list.add(new Game(
            "Rust (FacePunch)",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSryWGle0uIbFC6UdE-8LaY-ld1lU3YNTO9XA&s",
            "https://rust.facepunch.com/",
            "Portal FacePunch",
            "https://store.steampowered.com/login/"
        ));

        list.add(new Game(
            "Fortnite (Epic Games)",
            "https://f.i.uol.com.br/fotografia/2020/05/18/15898209045ec2bde8d74a0_1589820904_3x2_md.jpg",
            "https://www.fortnite.com/download",
            "Portal Epic Games",
            "https://www.epicgames.com/store/pt-BR/"
        ));

        list.add(new Game(
            "Clash Royale (Supercell)",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSohecYfbqTVqkh731_XQopykss2kfJJrWb3A&s",
            "https://supercell.com/en/games/clashroyale/",
            "Portal Supercell",
            "https://supercell.com/en/"
        ));

        // === NOVO: adiciona os jogos cadastrados pelo administrador ===
        List<Game> custom = Storege.getCustomGames();
        list.addAll(custom);

        // Ordena A→Z
        list.sort(Comparator.comparing(g -> g.title.toLowerCase(Locale.ROOT)));
        return list;
    }
}