package net.plazmix.cases.listener;

import lombok.RequiredArgsConstructor;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.PlazmixCasesPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public final class BoxOpeningListener implements Listener {

    private final PlazmixCasesPlugin casesPlugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null) {
            BaseCaseBox baseCaseBox = casesPlugin.getCaseBox(clickedBlock);

            if (baseCaseBox != null) {
                baseCaseBox.onInteract(player);

                event.setCancelled(true);
            }
        }
    }

}
