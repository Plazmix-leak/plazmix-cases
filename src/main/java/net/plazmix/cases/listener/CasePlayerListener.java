package net.plazmix.cases.listener;

import net.plazmix.cases.player.CasePlayer;
import net.plazmix.coreconnector.core.network.NetworkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class CasePlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        int playerId = NetworkManager.INSTANCE.getPlayerId(player.getName());

        CasePlayer casePlayer = new CasePlayer(playerId);
        casePlayer.loadPlayer();

        CasePlayer.CASE_PLAYER_MAP.put(playerId, casePlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CasePlayer.of(event.getPlayer().getName()).savePlayer();
    }

}
