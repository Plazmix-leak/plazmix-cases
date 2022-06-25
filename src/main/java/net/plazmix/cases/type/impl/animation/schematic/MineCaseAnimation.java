package net.plazmix.cases.type.impl.animation.schematic;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.type.BaseCaseBoxType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class MineCaseAnimation extends SchematicCaseAnimation {

    public MineCaseAnimation() {
        super(13, "mine_casebox", "Шахта", new ItemStack(Material.GOLD_ORE),
                Particle.FALLING_DUST);
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.AMBIENT_CAVE, 1, 1);
    }

}
