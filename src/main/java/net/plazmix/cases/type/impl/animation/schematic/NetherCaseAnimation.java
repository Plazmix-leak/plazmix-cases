package net.plazmix.cases.type.impl.animation.schematic;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.type.BaseCaseBoxType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class NetherCaseAnimation extends SchematicCaseAnimation {

    public NetherCaseAnimation() {
        super(25, "nether_casebox", "Преисподня", new ItemStack(Material.BLAZE_POWDER),
                Particle.VILLAGER_ANGRY);
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
    }

}
