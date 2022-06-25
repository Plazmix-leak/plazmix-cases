package net.plazmix.cases.type.impl.animation.schematic;

import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.type.BaseCaseBoxType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class SandCaseAnimation extends SchematicCaseAnimation {

    public SandCaseAnimation() {
        super(15, "sand_casebox", "Пустыня", new ItemStack(Material.SAND),
                Particle.TOTEM);
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
    }

}
