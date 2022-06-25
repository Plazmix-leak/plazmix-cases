package net.plazmix.cases;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.cases.inventory.BoxSelectTypeInventory;
import net.plazmix.cases.player.CasePlayer;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.protocollib.packet.ProtocolPacketFactory;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.EnderChest;
import org.bukkit.scheduler.BukkitRunnable;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

@Getter
public class BaseCaseBox {

    private final Location location;

    private CaseOpeningProcess currentOpeningProcess;

    private final SimpleHolographic caseHolographic;
    private BukkitRunnable waitingAnimationTask;


    public BaseCaseBox(Location location) {
        this.location = location;
        this.caseHolographic = new SimpleHolographic(location.clone().subtract(0, 0.5, 0));
    }

    private void resetWaitAnimation() {
        this.waitingAnimationTask = new BukkitRunnable() {

            private final double radian = Math.pow(2, 4);
            private final double radius = 1.25;

            private double t = 0;

            @Override
            public void run() {
                t += Math.PI / radian;

                if (t >= Math.PI * 2) {
                    t = 0;
                }

                double x = radius * Math.cos(t);
                double z = radius * Math.sin(t);

                location.getWorld().spawnParticle(Particle.SPELL_INSTANT, location.clone().add(z, x, x), 1, 0, 0, 0, 0);
                location.getWorld().spawnParticle(Particle.SPELL_INSTANT, location.clone().add(z, z, x), 1, 0, 0, 0, 0);
                location.getWorld().spawnParticle(Particle.SPELL_INSTANT, location.clone().add(x, z, z), 1, 0, 0, 0, 0);
            }
        };
    }

    public void resetDefaults() {

        // Reset case opening process.
        if (currentOpeningProcess != null) {

            currentOpeningProcess.cancel();
            currentOpeningProcess = null;
        }

        // Reset case block.
        BlockFace blockFace = LocationUtil.yawToFace(location.getYaw() + 90, false);

        EnderChest enderChest = new EnderChest(blockFace);
        location.getBlock().setTypeIdAndData(enderChest.getItemTypeId(), enderChest.getData(), true);

        // Close the chest.
        ProtocolPacketFactory.createBlockActionPacket(location.getBlock(), 1, 0)
                .broadcastPacket();

        // Reset case holographic.
        if (caseHolographic.getHolographicLines().isEmpty()) {

            caseHolographic.addLangHolographicLine(localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_CASES_LINE_1"));
            caseHolographic.addLangHolographicLine(localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_CASES_LINE_2"));
            caseHolographic.addLangHolographicLine(localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_CASES_LINE_3"));
        }

        caseHolographic.spawn();

        // Reset waiting animation.
        resetWaitAnimation();
        waitingAnimationTask.runTaskTimer(PlazmixCasesPlugin.getPlugin(PlazmixCasesPlugin.class), 0, 1);

        // Play sounds
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        location.getWorld().createExplosion(location, 1.75f, false);
    }

    public void startOpening(@NonNull Player player,
                             @NonNull BaseCaseBoxType caseBoxType,
                             @NonNull BaseCaseAnimation caseAnimation) {

        if (hasOpeningProcess()) {
            return;
        }

        CasePlayer.of(player.getName()).setKeysCount(caseBoxType, CasePlayer.of(player.getName()).getKeysCount(caseBoxType) - 1);
        player.closeInventory();

        // Announce to the server
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage("§d§lМистический сундук §8:: " + PlazmixUser.of(player).getDisplayName() + " §fоткрывает коробку §e(" + caseBoxType.getTitleName() + ")");
        }

        // Open the chest.
        ProtocolPacketFactory.createBlockActionPacket(location.getBlock(), 1, 1)
                .broadcastPacket();

        // Start case opening process.
        currentOpeningProcess = new CaseOpeningProcess(player, this, caseBoxType, caseAnimation);

        caseAnimation.play(currentOpeningProcess);
        currentOpeningProcess.runTaskTimer(PlazmixCasesPlugin.getPlugin(PlazmixCasesPlugin.class), 1, 1);

        // Cancel the waiting task.
        waitingAnimationTask.cancel();

        // Remove holographic
        caseHolographic.remove();
    }

    public boolean hasOpeningProcess() {
        return currentOpeningProcess != null;
    }

    public void onInteract(Player player) {
        if (hasOpeningProcess()) {
            player.sendMessage("§d§lPlazmix §8:: §cМистический сундук уже открывается кем-то другим...");

            return;
        }

        new BoxSelectTypeInventory(this).openInventory(player);
    }

}
