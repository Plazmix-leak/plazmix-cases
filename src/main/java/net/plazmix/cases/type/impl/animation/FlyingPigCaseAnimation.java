package net.plazmix.cases.type.impl.animation;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.NonNull;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CaseOpeningProcess;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.protocollib.entity.impl.FakePig;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class FlyingPigCaseAnimation extends BaseCaseAnimation {

    private FakePig fakePig;

    public FlyingPigCaseAnimation() {
        super(20, "Летающая свинья", new ItemStack(Material.MONSTER_EGG, 1, EntityType.PIG.getTypeId()));
    }

    @Override
    protected void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType) {

        // Play sounds.
        caseBox.getLocation().getWorld().playSound(caseBox.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1, 1);

        // Create a pig.
        fakePig = new FakePig(caseBox.getLocation().getBlock().getRelative(getRightFace(caseBox.getLocation()), 9)
                .getLocation().clone().add(0, 1.25, 0));

        fakePig.setInvisible(true);

        fakePig.getEntityEquipment().setEquipment(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.GOLD_HELMET));
        fakePig.spawn();
    }


    private final Collection<SimpleHolographic> wtfHolographicCollection = new ArrayList<>();

    private long tickCounter = 0;
    private int animationPhase = 0;

    private SimpleHolographic itemHolographic;

    @Override
    public void onAnimationTick(@NonNull Player player, @NonNull CaseOpeningProcess caseOpeningProcess) {
        Location caseLocation = caseOpeningProcess.getCaseBox().getLocation().clone();

        tickCounter++;
        switch (animationPhase) {

            // Появление этого приза игроку.
            case 0: {
                if (itemHolographic == null) {
                    itemHolographic = playResultItem(player, caseOpeningProcess.getCaseBox().getLocation().clone().add(0, 1.25, 0), caseOpeningProcess.getCaseBoxType().getCaseItems());
                    itemHolographic.spawn();

                    break;
                }


                if (tickCounter % 40 == 0) {
                    animationPhase++;
                }

                break;
            }

            // Появление свиньи, которая пиздит этот приз.
            case 1: {

                if (fakePig.isInvisible()) {
                    caseLocation.getWorld().createExplosion(fakePig.getLocation(), 1, false);

                    fakePig.setInvisible(false);
                }

                if (tickCounter % 2 != 0) {
                    break;
                }

                BlockFace blockFace = getLeftFace(caseLocation);
                Location nextLocation = fakePig.getLocation()
                        .getBlock()
                        .getRelative(blockFace)
                        .getLocation()

                        .clone()
                        .add(0.5, 0, 0.5);

                fakePig.look(caseLocation.getYaw() - 180, 0);
                fakePig.teleport(nextLocation);

                fakePig.getLocation().getWorld().playSound(fakePig.getLocation(), Sound.ENTITY_PIG_AMBIENT, 1, 1);
                fakePig.getLocation().getWorld().spawnParticle(Particle.CLOUD, fakePig.getLocation(), 5, 0.1F, 0.1F, 0.1F, 0.1F);

                if (nextLocation.distance(caseLocation.clone().add(0, 1.25, 0)) <= 1) {
                    itemHolographic.remove();

                    fakePig.getLocation().getWorld().playSound(fakePig.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2, 1);
                    fakePig.getLocation().getWorld().spawnParticle(Particle.BARRIER, fakePig.getLocation(), 1);
                }

                if (nextLocation.distance(caseLocation.clone().add(0, 1.25, 0)) >= 10) {
                    fakePig.look(fakePig.getLocation().getBlock().getRelative(getRightFace(caseLocation)).getLocation());

                    fakePig.getLocation().getWorld().createExplosion(fakePig.getLocation(), 1, false);
                    fakePig.setInvisible(true);

                    animationPhase++;
                }

                break;
            }

            // Кейс стоит в ахуе и не понимает поч приз спиздили(((
            case 2: {
                if (tickCounter % 20 != 0) {
                    break;
                }

                SimpleHolographic simpleHolographic = new SimpleHolographic(caseLocation);
                simpleHolographic.addOriginalHolographicLine("§cWTF?");

                simpleHolographic.spawn();
                wtfHolographicCollection.add(simpleHolographic);

                if (wtfHolographicCollection.size() > 3) {

                    for (SimpleHolographic wtfHolographic : wtfHolographicCollection) {
                        wtfHolographic.remove();
                    }

                    wtfHolographicCollection.clear();
                    animationPhase++;
                }

                break;
            }

            // Дикий свин передумал, пролетая снова мимо кейса, и высрал приз обратно.
            case 3: {
                if (fakePig.isInvisible()) {

                    caseLocation.getWorld().playSound(fakePig.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1, 1);
                    caseLocation.getWorld().createExplosion(fakePig.getLocation(), 1, false);

                    fakePig.setInvisible(false);
                    fakePig.teleport(fakePig.getLocation().getBlock().getRelative(getRightFace(caseLocation)).getLocation());
                }

                if (tickCounter % 2 != 0) {
                    break;
                }

                BlockFace blockFace = getRightFace(caseLocation);
                Location nextLocation = fakePig.getLocation()
                        .getBlock()
                        .getRelative(blockFace)
                        .getLocation()

                        .clone()
                        .add(0.5, 0, 0.5);

                fakePig.look(caseLocation.getYaw(), 0);
                fakePig.teleport(nextLocation);

                fakePig.getLocation().getWorld().playSound(fakePig.getLocation(), Sound.ENTITY_PIG_AMBIENT, 1, 0);
                fakePig.getLocation().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, fakePig.getLocation(), 1);
                fakePig.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, fakePig.getLocation(), 5, 0.1F, 0.01F, 0.1F, 0.1F);

                if (nextLocation.distance(caseLocation.clone().add(0, 1.25, 0)) <= 1) {
                    itemHolographic.spawn();

                    fakePig.getLocation().getWorld().playSound(fakePig.getLocation(), Sound.ENTITY_LLAMA_HURT, 2, 1.5f);
                    fakePig.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, fakePig.getLocation(), 5, 0.1F, 0.1F, 0.1F, 0.1F);
                }

                if (nextLocation.distance(caseLocation.clone().add(0, 1.25, 0)) >= 10) {

                    fakePig.getLocation().getWorld().createExplosion(fakePig.getLocation(), 1, false);
                    fakePig.remove();

                    animationPhase++;
                }

                break;
            }

            // Охуевший кейс ебать как охуел и радуется что дикий свин поимел совесть (в рот) и отдал приз короче.
            case 4: {
                if (tickCounter % 20 != 0) {
                    break;
                }

                SimpleHolographic simpleHolographic = new SimpleHolographic(caseLocation);
                simpleHolographic.addOriginalHolographicLine("§aWOW!");

                simpleHolographic.spawn();
                wtfHolographicCollection.add(simpleHolographic);

                if (wtfHolographicCollection.size() > 3) {

                    for (SimpleHolographic wtfHolographic : wtfHolographicCollection) {
                        wtfHolographic.remove();
                    }

                    wtfHolographicCollection.clear();
                    itemHolographic.remove();

                    cancel(caseOpeningProcess.getCaseBox());
                }

                break;
            }
        }
    }

    private BlockFace getLeftFace(@NonNull Location caseLocation) {
        return LocationUtil.yawToFace(caseLocation.getYaw() - 180, false);
    }

    private BlockFace getRightFace(@NonNull Location caseLocation) {
        return LocationUtil.yawToFace(caseLocation.getYaw(), false);
    }

}
