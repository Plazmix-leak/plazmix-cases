package net.plazmix.cases.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CaseOpeningProcess;
import net.plazmix.cases.CasesManager;
import net.plazmix.cases.player.CasePlayer;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
public abstract class BaseCaseAnimation {

    private final int goldsPrice;

    private final String titleName;
    private final ItemStack itemIcon;


    protected abstract void onPlay(@NonNull BaseCaseBox caseBox, @NonNull BaseCaseBoxType caseBoxType);

    public abstract void onAnimationTick(@NonNull Player player, @NonNull CaseOpeningProcess caseOpeningProcess);


    public void play(@NonNull CaseOpeningProcess caseOpeningProcess) {
        onPlay(caseOpeningProcess.getCaseBox(), caseOpeningProcess.getCaseBoxType());
    }

    public void cancel(@NonNull BaseCaseBox caseBox) {
        caseBox.resetDefaults();

        if (playerWinner != null && resultCaseItem != null) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage("§d§lМистический сундук §8:: " + PlazmixUser.of(playerWinner).getDisplayName() + " §fвыпало из коробки - §e" + resultCaseItem.getDisplayName());
            }
        }
    }


    public void purchase(@NonNull Player player) {
        CasePlayer.of(player.getName()).addAnimation( CasesManager.INSTANCE.getAnimationId(this) );
    }

    public boolean isPurchased(@NonNull Player player) {
        return goldsPrice <= 0 || CasePlayer.of(player.getName()).hasAnimation( CasesManager.INSTANCE.getAnimationId(this) );
    }


    // Give random item
    private BaseCaseItem resultCaseItem;
    private Player playerWinner;

    public SimpleHolographic playResultItem(@NonNull Player player, @NonNull Location location,
                                            @NonNull Collection<BaseCaseItem> caseItems) {

        this.playerWinner = player;
        this.resultCaseItem = caseItems.stream()
                .skip((long) (Math.random() * caseItems.size()))
                .findFirst()
                .orElse(null);

        if (resultCaseItem != null) {

            resultCaseItem.playItemPass(player);
            return resultCaseItem.createEntity(location);
        }

        return null;
    }

    public boolean hasResultItem() {
        return resultCaseItem != null;
    }

}
