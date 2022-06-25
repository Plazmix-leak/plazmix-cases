package net.plazmix.cases.type.impl.animation.schematic;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.type.BaseCaseBoxType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class EndCaseAnimation extends SchematicCaseAnimation {

    public EndCaseAnimation() {
        super(25, "end_casebox", "Конец?", new ItemStack(Material.OBSIDIAN),
                Particle.DRAGON_BREATH);
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.ENTITY_ENDERMEN_DEATH, 1, 0);
    }

}
