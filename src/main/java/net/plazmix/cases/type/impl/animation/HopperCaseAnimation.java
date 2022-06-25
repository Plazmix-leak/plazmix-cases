package net.plazmix.cases.type.impl.animation;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CaseOpeningProcess;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.holographic.impl.SimpleHolographic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HopperCaseAnimation extends BaseCaseAnimation {

    public HopperCaseAnimation() {
        super(6, "Воронка", new ItemStack(Material.HOPPER));
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.UI_TOAST_OUT, 2, 0);
    }


    private final double radian = Math.pow(2, 3);

    private final double height = 0.05;
    private final double radius = 1;

    private final long startTicker = 10;

    private long leftTicks = startTicker + 1;
    private long endTicksCounter = 60L;


    private SimpleHolographic wonItemHolographic;

    @Override
    public void onAnimationTick(@NonNull Player player, @NonNull CaseOpeningProcess caseOpeningProcess) {
        BaseCaseBox baseCaseBox = caseOpeningProcess.getCaseBox();

        if (hasResultItem()) {
            endTicksCounter--;

            if (endTicksCounter < 0) {
                wonItemHolographic.remove();

                cancel(baseCaseBox);
                return;
            }

            return;
        }

        this.leftTicks--;

        if (leftTicks % 2 != 0) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 2f - (2f / startTicker) * leftTicks);

        for (double y = radius; y <= radius * 2; y += height) {
            for (double t = 0; t <= 360; t += Math.PI / radian) {

                double x = Math.abs(y - radius) * Math.cos(t);
                double z = Math.abs(y - radius) * Math.sin(t);

                baseCaseBox.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, baseCaseBox.getLocation().clone().add(x, y, z), 1,
                        0, 0, 0, 0);
            }
        }

        baseCaseBox.getLocation().getWorld().spawnParticle(Particle.FLAME, baseCaseBox.getLocation().clone().add(0, 1.25, 0), 5, 2, 2, 2, 2);

        if (leftTicks <= 0) {
            Location location = baseCaseBox.getLocation().clone().add(0, 1.25, 0);

            this.wonItemHolographic = playResultItem(player, location, caseOpeningProcess.getCaseBoxType().getCaseItems());
            this.wonItemHolographic.spawn();

            baseCaseBox.getLocation().getWorld().createExplosion(location, 1, false);
        }
    }
}
