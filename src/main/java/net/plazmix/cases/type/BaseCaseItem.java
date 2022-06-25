package net.plazmix.cases.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.holographic.impl.SimpleHolographic;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class BaseCaseItem {

    private final String displayName;
    private final ItemStack itemStack;

    private final Consumer<Player> onItemWon;

    public SimpleHolographic createEntity(@NonNull Location location) {
        SimpleHolographic simpleHolographic = new SimpleHolographic(location.clone().subtract(0, 2, 0));

        simpleHolographic.addOriginalHolographicLine(ChatColor.YELLOW + displayName);
        simpleHolographic.addItemHolographicLine(itemStack);

        return simpleHolographic;
    }

    public void playItemPass(@NonNull Player player) {
        onItemWon.accept(player);
    }

}
