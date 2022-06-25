package net.plazmix.cases.inventory;

import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CasesManager;
import net.plazmix.cases.player.CasePlayer;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BoxSelectAnimationInventory extends BasePaginatedInventory {

    private final BaseCaseBox baseCaseBox;
    private final BaseCaseBoxType baseCaseBoxType;

    public BoxSelectAnimationInventory(BaseCaseBox baseCaseBox, BaseCaseBoxType baseCaseBoxType) {
        super("Анимация открытия", 6);

        this.baseCaseBox = baseCaseBox;
        this.baseCaseBoxType = baseCaseBoxType;
    }

    @Override
    public void drawInventory(Player player) {
        addRowToMarkup(3, 2);
        addRowToMarkup(4, 2);

        // Set frame items.
        ItemStack yellowFrameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(4)
                .setName(ChatColor.RESET.toString())
                .build();

        setOriginalItem(1, yellowFrameItem);
        setOriginalItem(9, yellowFrameItem);

        setOriginalItem(10, yellowFrameItem);
        setOriginalItem(18, yellowFrameItem);

        setOriginalItem(19, yellowFrameItem);
        setOriginalItem(27, yellowFrameItem);

        setOriginalItem(28, yellowFrameItem);
        setOriginalItem(36, yellowFrameItem);

        setOriginalItem(37, yellowFrameItem);
        setOriginalItem(45, yellowFrameItem);

        setOriginalItem(46, yellowFrameItem);
        setOriginalItem(54, yellowFrameItem);


        ItemStack blackFrameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(15)
                .setName(ChatColor.RESET.toString())
                .build();

        for (int i = 0; i < 7; i++) {
            setOriginalItem(2 + i, blackFrameItem);
            setOriginalItem(47 + i, blackFrameItem);
        }

        // Add cancel item.
        setClickItem(50, ItemUtil.newBuilder(Material.BARRIER)
                        .setName("§cОтмена")
                        .addLore("§7Нажмите, чтобы отменить открытие ключа!")
                        .build(),

                (player1, inventoryClickEvent) -> new BoxSelectTypeInventory(baseCaseBox).openInventory(player));

        // Add box animations.
        for (Supplier<BaseCaseAnimation> caseAnimationSupplier : CasesManager.INSTANCE.getRegisteredCaseAnimations().valueCollection()
                .stream()
                .sorted(Comparator.comparingInt(value -> CasesManager.INSTANCE.getAnimationId(value.get())))
                .collect(Collectors.toCollection(LinkedHashSet::new))) {

            BaseCaseAnimation caseAnimation = caseAnimationSupplier.get();
            addClickItemToMarkup(toItemIcon(player.getName(), caseAnimation),
                    (player1, inventoryClickEvent) -> {

                        if (caseAnimation.isPurchased(player)) {
                            baseCaseBox.startOpening(player, baseCaseBoxType, caseAnimation);
                            return;
                        }

                        PlazmixUser plazmixUser = PlazmixUser.of(player);

                        if (plazmixUser.getGolds() < caseAnimation.getGoldsPrice()) {
                            player.sendMessage("§d§lPlazmix §8:: §cОшибка, у Вас недостаточно средств для покупки данной анимации!");
                            return;
                        }

                        plazmixUser.removeGolds(caseAnimation.getGoldsPrice());
                        caseAnimation.purchase(player);

                        updateInventory(player);
                    });
        }
    }

    private ItemStack toItemIcon(String playerName, BaseCaseAnimation caseAnimation) {
        CasePlayer casePlayer = CasePlayer.of(playerName);

        if (caseAnimation.isPurchased(casePlayer.getBukkitHandle())) {
            ItemUtil.ItemBuilder itemBuilder = ItemUtil.newBuilder(caseAnimation.getItemIcon());

            itemBuilder.setName("§d§lАнимация '" + caseAnimation.getTitleName() + "'");
            itemBuilder.addLore("");
            itemBuilder.addLore("§aПриобретено!");
            itemBuilder.addLore("§e▸ Нажмите, чтобы использовать!");

            return itemBuilder.build();

        } else {

            ItemUtil.ItemBuilder itemBuilder = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE);
            itemBuilder.setDurability(14);

            itemBuilder.setName("§cАнимация '" + caseAnimation.getTitleName() + "'");
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Цена покупки: §c" + NumberUtil.formattingSpaced(caseAnimation.getGoldsPrice(), "золото", "золота", "золота"));
            itemBuilder.addLore("");
            itemBuilder.addLore("§c▸ Нажмите, чтобы приобрести!");

            return itemBuilder.build();
        }
    }

}
