package net.plazmix.cases.player;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.cases.CasesManager;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.network.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CasePlayer {

    public static final TIntObjectMap<CasePlayer> CASE_PLAYER_MAP = new TIntObjectHashMap<>();

    public static final String PLAYER_KEYS_LOAD_QUERY    = "SELECT * FROM `LobbyPlayerCases` WHERE `Id`=?";
    public static final String PLAYER_KEYS_INSERT_QUERY  = "INSERT INTO `LobbyPlayerCases` VALUES (?, ?, ?)";
    public static final String PLAYER_KEYS_SAVE_QUERY    = "UPDATE `LobbyPlayerCases` SET `Keys`=? WHERE `Id`=? AND `CaseId`=?";

    public static final String PLAYER_ANIMATIONS_LOAD_QUERY    = "SELECT * FROM `LobbyCaseAnimations` WHERE `Id`=?";
    public static final String PLAYER_ANIMATIONS_INSERT_QUERY  = "INSERT INTO `LobbyCaseAnimations` VALUES (?, ?)";

    public static CasePlayer of(int playerId) {
        return CASE_PLAYER_MAP.get(playerId);
    }

    public static CasePlayer of(@NonNull String playerName) {
        return of(NetworkManager.INSTANCE.getPlayerId(playerName));
    }


    private final int playerId;

    private final TIntList purchasedAnimationIds = new TIntArrayList();
    private final TIntIntMap typesKeysMap = new TIntIntHashMap();

    public void loadPlayer() {

        // Box cases load.
        CoreConnector.getInstance().getMysqlConnection().executeQuery(false, PLAYER_KEYS_LOAD_QUERY, resultSet -> {

            while (resultSet.next()) {
                int boxTypeId = resultSet.getInt("CaseId");
                int keys = resultSet.getInt("Keys");

                typesKeysMap.put(boxTypeId, keys);
            }

            for (BaseCaseBoxType baseCaseBoxType : CasesManager.INSTANCE.getRegisteredCaseTypes()) {
                int boxTypeId = baseCaseBoxType.getId();

                if (typesKeysMap.containsKey(boxTypeId)) {
                    continue;
                }

                CoreConnector.getInstance().getMysqlConnection().execute(false, PLAYER_KEYS_INSERT_QUERY, playerId, boxTypeId, 0);
                typesKeysMap.put(boxTypeId, 0);
            }

            return null;
        }, playerId);

        // Box animations load.
        CoreConnector.getInstance().getMysqlConnection().executeQuery(false, PLAYER_ANIMATIONS_LOAD_QUERY, resultSet -> {

            while (resultSet.next()) {
                purchasedAnimationIds.add(resultSet.getInt("AnimationId"));
            }

            return null;
        }, playerId);
    }

    public void savePlayer() {
        typesKeysMap.forEachEntry((caseId, keysCount) -> {

            CoreConnector.getInstance().getMysqlConnection().execute(false, PLAYER_KEYS_SAVE_QUERY, keysCount, playerId, caseId);
            return true;
        });
    }

    public int getKeysCount(@NonNull BaseCaseBoxType baseCaseBoxType) {
        return typesKeysMap.get(baseCaseBoxType.getId());
    }

    public void setKeysCount(@NonNull BaseCaseBoxType baseCaseBoxType, int keysCount) {
        typesKeysMap.put(baseCaseBoxType.getId(), keysCount);
    }


    public void addAnimation(int animationId) {
        purchasedAnimationIds.add(animationId);

        CoreConnector.getInstance().getMysqlConnection().execute(true, PLAYER_ANIMATIONS_INSERT_QUERY, playerId, animationId);
    }

    public boolean hasAnimation(int animationId) {
        return purchasedAnimationIds.contains(animationId);
    }

    public Player getBukkitHandle() {
        return Bukkit.getPlayer( NetworkManager.INSTANCE.getPlayerName(playerId) );
    }

}
