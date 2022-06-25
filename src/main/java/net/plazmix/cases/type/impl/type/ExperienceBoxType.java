package net.plazmix.cases.type.impl.type;

import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.cases.type.BaseCaseItem;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExperienceBoxType extends BaseCaseBoxType {

    public ExperienceBoxType() {
        super(1, 7, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTI4Mzk2NTkyM2MwZTNlYWQwZmU4NzYwZTA4Y2JhODk4MmUzM2E2YWNkYjg5MWVjYzZmNmRmZTZkNWQxODBiYyJ9fX0=",

                "Игровой опыт",
                "Данный набор содержит в себе",
                "разновидность некоторых сумм, которые Вы",
                "можете получить в виде игрового опыта");

        for (int coinCount : new int[]{250, 500, 690, 1000, 1550, 2000, 2560}) {
            BaseCaseItem baseCaseItem = new BaseCaseItem(ChatColor.AQUA + (NumberUtil.spaced(coinCount) + " EXP"), new ItemStack(Material.EXP_BOTTLE),
                    player -> PlazmixUser.of(player).addExperience(coinCount));

            addItem(baseCaseItem);
        }
    }
}
