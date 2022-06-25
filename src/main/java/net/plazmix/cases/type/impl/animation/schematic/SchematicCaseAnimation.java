package net.plazmix.cases.type.impl.animation.schematic;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CaseOpeningProcess;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.schematic.BaseSchematic;
import net.plazmix.schematic.BaseSchematicBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SchematicCaseAnimation extends BaseCaseAnimation {

    private final BaseSchematic baseSchematic;
    private final Particle particle;

    public SchematicCaseAnimation(int goldsPrice,

                                  @NonNull String schematicName,
                                  @NonNull String titleName,

                                  @NonNull ItemStack itemIcon,
                                  @NonNull Particle particle) {

        super(goldsPrice, titleName, itemIcon);

        this.particle = particle;
        this.baseSchematic = new BaseSchematic(schematicName);
    }


    // Particle animation.
    private long maxTicksCount = 0;
    private long tickCounter = 0;

    private double t = 0;

    // Schematic animation
    private int blockCounter = 0;

    private List<BaseSchematicBlock> schematicBlocks;
    private final Map<Block, MaterialData> previousWorldBlocks = new HashMap<>();

    // End ticks.
    private boolean animationAllow = true;
    private long endsTicksCounter = 0;

    // Result item.
    private SimpleHolographic itemHolographic;

    @Override
    @SneakyThrows
    public void onAnimationTick(@NonNull Player player, @NonNull CaseOpeningProcess caseOpeningProcess) {
        tickCounter++;

        BaseCaseBox baseCaseBox = caseOpeningProcess.getCaseBox();
        Location location = baseCaseBox.getLocation().clone();

        // Play particle animation.
        if ((maxTicksCount > 0 && maxTicksCount - tickCounter <= 60) && animationAllow) {

            t += Math.PI / 16;
            location.getWorld().playSound(location, Sound.BLOCK_NOTE_BELL, 1, Math.max(2f, (((float) tickCounter) - 20f) / 20f));

            for (double p = 0; p <= Math.PI * 2; p += Math.PI / 2) {

                double x = 0.3 * (4 * Math.PI - t) * Math.cos(t + p);
                double y = 0.2 * t;
                double z = 0.3 * (4 * Math.PI - t) * Math.sin(t + p);

                Location particleLocation = location.clone().add(x, y - 1, z);
                location.getWorld().spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);

                if (t >= Math.PI * 4) {
                    location.getWorld().spawnParticle(Particle.CLOUD, particleLocation, 1, 0, 0, 0, 50);

                    if (animationAllow) {
                        animationAllow = false;

                        location.getWorld().createExplosion(particleLocation, 1, false);

                        itemHolographic = playResultItem(player, particleLocation, caseOpeningProcess.getCaseBoxType().getCaseItems());
                        itemHolographic.spawn();
                    }
                }
            }
        }

        // Give result item
        if (!animationAllow && schematicBlocks != null && blockCounter >= schematicBlocks.size()) {
            if (endsTicksCounter >= 20 * 3) {

                if (itemHolographic != null) {
                    itemHolographic.remove();
                }

                cancel(baseCaseBox);
                backward();
            }

            endsTicksCounter++;
            return;
        }

        // Build schematic
        if (schematicBlocks == null) {
            schematicBlocks = baseSchematic.reader().read()
                    .stream()
                    .sorted(Collections.reverseOrder(Comparator.comparing(schematicBlock -> schematicBlock.getBlock(location.clone()).getLocation().distance(baseCaseBox.getLocation()))))
                    .collect(Collectors.toCollection(LinkedList::new));
        }

        if (blockCounter >= schematicBlocks.size()) {
            return;
        }

        if (maxTicksCount <= 0) {
            maxTicksCount = schematicBlocks.size();
        }

        BaseSchematicBlock schematicBlock = schematicBlocks.get(blockCounter);
        Block worldBlock = schematicBlock.getBlock(location.clone());

        previousWorldBlocks.put(worldBlock, worldBlock.getState().getData());

        MaterialData schematicBlockData = schematicBlock.getMaterialData();

        for (Player receiver : Bukkit.getOnlinePlayers()) {
            receiver.sendBlockChange(worldBlock.getLocation().clone(), schematicBlockData.getItemTypeId(), schematicBlockData.getData());
        }

        worldBlock.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, worldBlock.getLocation(), 5, 1, 1, 1, 1);
        worldBlock.getWorld().playSound(worldBlock.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);

        blockCounter++;
    }

    private void backward() {
        previousWorldBlocks.forEach((block, materialData) -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendBlockChange(block.getLocation(), materialData.getItemTypeId(), materialData.getData());
            }
        });
    }

}
