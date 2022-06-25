package net.plazmix.cases.type.impl.type;

import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.cases.type.BaseCaseItem;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CoinsBoxType extends BaseCaseBoxType {

    public CoinsBoxType() {
        super(0, 5, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTg1NTk0Yjc4ZThmYjNlM2JkN2NlM2NlMmJmYTk0ZjEzOGE5ZWJjMWJhZWQ2NzlhZWJmNWY2M2QxOTYxYzhkZSJ9fX0=",

                "Сокровищница",
                "Данный набор содержит в себе",
                "разновидность некоторых сумм, которые Вы",
                "можете получить в виде монеток");

        for (int coinCount : new int[]{250, 500, 690, 1000, 1550, 2000, 2560}) {
            BaseCaseItem baseCaseItem = new BaseCaseItem(ChatColor.YELLOW + (NumberUtil.spaced(coinCount) + " монет"), new ItemStack(Material.GOLD_NUGGET),
                    player -> PlazmixUser.of(player).addCoins(coinCount));

            addItem(baseCaseItem);
        }
    }

}
