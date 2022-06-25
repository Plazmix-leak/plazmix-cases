package net.plazmix.cases.inventory;

import net.plazmix.cases.BaseCaseBox;
import net.plazmix.cases.CasesManager;
import net.plazmix.cases.player.CasePlayer;
import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BoxSelectTypeInventory extends BasePaginatedInventory {

    private final BaseCaseBox baseCaseBox;

    public BoxSelectTypeInventory(BaseCaseBox baseCaseBox) {
        super("Мистический сундук", 6);

        this.baseCaseBox = baseCaseBox;
    }

    @Override
    public void drawInventory(Player player) {
        addRowToMarkup(3, 2);
        addRowToMarkup(4, 2);

        // Set frame items.
        ItemStack purpleFrameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(10)
                .setName(ChatColor.RESET.toString())
                .build();

        setOriginalItem(1, purpleFrameItem);
        setOriginalItem(9, purpleFrameItem);

        setOriginalItem(10, purpleFrameItem);
        setOriginalItem(18, purpleFrameItem);

        setOriginalItem(19, purpleFrameItem);
        setOriginalItem(27, purpleFrameItem);

        setOriginalItem(28, purpleFrameItem);
        setOriginalItem(36, purpleFrameItem);

        setOriginalItem(37, purpleFrameItem);
        setOriginalItem(45, purpleFrameItem);

        setOriginalItem(46, purpleFrameItem);
        setOriginalItem(54, purpleFrameItem);


        ItemStack blackFrameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(15)
                .setName(ChatColor.RESET.toString())
                .build();

        for (int i = 0; i < 7; i++) {
            setOriginalItem(2 + i, blackFrameItem);
            setOriginalItem(47 + i, blackFrameItem);
        }

        // Add info items.
        setOriginalItem(50, ItemUtil.newBuilder(Material.BOOK)
                .setName("§eПолезная информация")
                .addLore("")
                .addLore("§7Чтобы открыть кейс, приобретите")
                .addLore("§7ключ, нажав §cПКМ §7по кейсу!")
                .build());


        // Add box types.
        int typeCounter = 0;

        for (BaseCaseBoxType caseBoxType : CasesManager.INSTANCE.getRegisteredCaseTypes()) {
            int finalTypeCounter = typeCounter;

            addClickItemToMarkup(toItemIcon(player.getName(), caseBoxType), (player1, inventoryClickEvent) -> {

                // Purchasing box keys.
                if (inventoryClickEvent.isRightClick()) {
                    PlazmixUser plazmixUser = PlazmixUser.of(player);

                    if (plazmixUser.getGolds() < caseBoxType.getGoldsPrice()) {
                        player.sendMessage("§d§lPlazmix §8:: §cОшибка, у Вас недостаточно средств для покупки данного ключа!");
                        return;
                    }

                    plazmixUser.removeGolds(caseBoxType.getGoldsPrice());

                    int currentKeys = CasePlayer.of(player.getName()).getKeysCount(caseBoxType);
                    CasePlayer.of(player.getName()).setKeysCount(caseBoxType, currentKeys + 1);

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

                    // Update item.
                    getBukkitInventory().setItem(getPageSlots().get(finalTypeCounter) - 1, toItemIcon(player.getName(), caseBoxType));
                    return;
                }

                // Opening box keys.
                if (inventoryClickEvent.isLeftClick()) {
                    int currentKeys = CasePlayer.of(player.getName()).getKeysCount(caseBoxType);

                    if (currentKeys <= 0) {
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                        return;
                    }

                    new BoxSelectAnimationInventory(baseCaseBox, caseBoxType)
                            .openInventory(player);
                }
            });

            typeCounter++;
        }
    }

    private ItemStack toItemIcon(String playerName, BaseCaseBoxType caseBoxType) {
        int keysCount = CasePlayer.of(playerName).getKeysCount(caseBoxType);

        ItemUtil.ItemBuilder itemBuilder = ItemUtil.newBuilder(keysCount > 0 ? caseBoxType.getItemIcon() : new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14));
        itemBuilder.setAmount(Math.max(1, keysCount));

        itemBuilder.setName("§d§l" + caseBoxType.getTitleName());
        itemBuilder.addLore("");

        for (String description : caseBoxType.getDescription()) {
            itemBuilder.addLore(ChatColor.WHITE + description);
        }

        itemBuilder.addLore("");
        itemBuilder.addLore("§cВНИМАНИЕ! При получении уже существующего");
        itemBuilder.addLore("§cпредмета, Вы получаете монетки в качестве");
        itemBuilder.addLore("§cкомпенсанции потерянного ключа");
        itemBuilder.addLore("");

        if (keysCount > 0) {
            itemBuilder.addLore("§7Доступно для открытия: §d" + keysCount);

        } else {

            itemBuilder.addLore("§cУ Вас нет ни одного ключа в наличии!");
        }

        itemBuilder.addLore("§7Цена покупки: §6" + NumberUtil.formattingSpaced(caseBoxType.getGoldsPrice(), "плазма", "плазмы", "плазмы"));
        itemBuilder.addLore("");

        itemBuilder.addLore("§e▸ Нажмите ЛКМ, чтобы открыть!");
        itemBuilder.addLore("§e▸ Нажмите ПКМ, чтобы приобрести!");

        return itemBuilder.build();
    }
}
