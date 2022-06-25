package net.plazmix.cases.type.impl.animation;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CaseOpeningProcess;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.holographic.impl.SimpleHolographic;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkCaseAnimation extends BaseCaseAnimation {

    public FireworkCaseAnimation() {
        super(0, "Фейерверк", new ItemStack(Material.FIREWORK));
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1, 0);
    }


    private final double radian = Math.pow(2, 3);

    private double radius = 1.5;

    private double y = 0;
    private double t = 0;

    private long tickCounter = 0;


    private SimpleHolographic wonItemHolographic;

    @Override
    public void onAnimationTick(@NonNull Player player, @NonNull CaseOpeningProcess caseOpeningProcess) {
        BaseCaseBox baseCaseBox = caseOpeningProcess.getCaseBox();
        t += Math.PI / radian;

        if (t >= Math.PI * 2) {
            t = 0;
        }

        double x = radius * Math.cos(t);
        double z = radius * Math.sin(t);

        Location location = baseCaseBox.getLocation().clone();
        location = location.clone().add(x, y, z);

        if (radius <= 0) {
            if (tickCounter <= 0) {

                // Create firework explosion
                Firework firework = location.getWorld().spawn(location, Firework.class);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                fireworkMeta.setPower(1);
                fireworkMeta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withColor(Color.PURPLE, Color.WHITE)
                        .build());

                firework.setFireworkMeta(fireworkMeta);

                // Add item holo.
                wonItemHolographic = playResultItem(player, location, caseOpeningProcess.getCaseBoxType().getCaseItems());
                wonItemHolographic.spawn();
            }

            if (tickCounter >= (20 * 3)) {
                wonItemHolographic.remove();

                cancel(baseCaseBox);
            }

            tickCounter++;
        } else {

            location.getWorld().spawnParticle(Particle.FALLING_DUST, location, 5, 0, 0, 0, 0);
            location.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 10, 0, 0, 0, 0);

            this.radius -= 0.05;
            this.y += 0.1;
        }
    }
}
