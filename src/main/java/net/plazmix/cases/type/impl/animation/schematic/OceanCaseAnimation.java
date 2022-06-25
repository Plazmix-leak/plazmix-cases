package net.plazmix.cases.type.impl.animation.schematic;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.type.BaseCaseBoxType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class OceanCaseAnimation extends SchematicCaseAnimation {

    public OceanCaseAnimation() {
        super(20, "ocean_casebox", "Морское царство", new ItemStack(Material.PRISMARINE_SHARD),
                Particle.SPELL_WITCH);
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1, 0);
    }

}
