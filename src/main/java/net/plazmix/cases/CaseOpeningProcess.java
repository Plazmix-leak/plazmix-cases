package net.plazmix.cases;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
@Getter
public class CaseOpeningProcess extends BukkitRunnable {

    private final Player player;

    private final BaseCaseBox caseBox;
    private final BaseCaseBoxType caseBoxType;

    private final BaseCaseAnimation caseAnimation;

    @Override
    public void run() {
        caseAnimation.onAnimationTick(player, this);

        // Near players velocity
        Location caseLocation = caseBox.getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(this.player))
                continue;

            if (player.getWorld().equals(caseLocation.getWorld()) && player.getLocation().distance(caseLocation) <= 8) {

                Vector velocity = player.getLocation().getDirection().add(new Vector(0, -0.1, 0)).multiply(0.1);
                player.setVelocity( player.getVelocity().subtract(velocity) );
            }
        }
    }

}
