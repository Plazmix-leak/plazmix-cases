package net.plazmix.cases;

import lombok.NonNull;
import net.plazmix.cases.listener.BoxOpeningListener;
import net.plazmix.cases.listener.CasePlayerListener;
import net.plazmix.cases.player.CasePlayer;
import net.plazmix.cases.type.impl.animation.FireworkCaseAnimation;
import net.plazmix.cases.type.impl.animation.FlyingPigCaseAnimation;
import net.plazmix.cases.type.impl.animation.schematic.*;
import net.plazmix.cases.type.impl.type.CoinsBoxType;
import net.plazmix.cases.type.impl.type.ExperienceBoxType;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class PlazmixCasesPlugin extends JavaPlugin {

    private final Map<Block, BaseCaseBox> baseCasesMap = new HashMap<>();

    @Override
    public void onEnable() {

        // Save configuration.
        saveDefaultConfig();

        // Initialize cases.
        spawnAllCases();

        CasesManager.INSTANCE.registerCaseType(new CoinsBoxType());
        CasesManager.INSTANCE.registerCaseType(new ExperienceBoxType());

        CasesManager.INSTANCE.registerCaseAnimation(0, FireworkCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(1, FlyingPigCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(2, OceanCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(3, NetherCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(4, EndCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(5, SandCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(6, MineCaseAnimation::new);
        CasesManager.INSTANCE.registerCaseAnimation(7, FarmCaseAnimation::new);

        // Register listeners.
        getServer().getPluginManager().registerEvents(new CasePlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BoxOpeningListener(this), this);

        // Create Mysql tables.
        CoreConnector.getInstance().getMysqlConnection().createTable(true, "LobbyCaseAnimations", "`Id` INT NOT NULL, `AnimationId` INT NOT NULL");
        CoreConnector.getInstance().getMysqlConnection().createTable(true, "LobbyPlayerCases", "`Id` INT NOT NULL, `CaseId` INT NOT NULL, `Keys` INT NOT NULL");
    }

    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            CasePlayer.of(player.getName()).savePlayer();
        }
    }


    public List<Location> getBoxesLocations() {
        return getConfig().getStringList("boxes-locations")
                .stream()
                .map(LocationUtil::stringToLocation)
                .collect(Collectors.toList());
    }

    public BaseCaseBox getCaseBox(@NonNull Block block) {
        return baseCasesMap.get(block);
    }

    private void spawnAllCases() {
        for (Location location : getBoxesLocations()) {

            BaseCaseBox baseCaseBox = new BaseCaseBox(location);
            baseCaseBox.resetDefaults();

            baseCasesMap.put(location.getBlock(), baseCaseBox);
        }
    }

}
