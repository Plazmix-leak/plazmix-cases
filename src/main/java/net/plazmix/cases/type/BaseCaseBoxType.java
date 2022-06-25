package net.plazmix.cases.type;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public abstract class BaseCaseBoxType {

    private final int id;
    private final int goldsPrice;

    private final String titleName;
    private final String[] description;

    private final ItemStack itemIcon;
    private final Collection<BaseCaseItem> caseItems = new ArrayList<>();

    public BaseCaseBoxType(int id, int goldsPrice, String skullTexture, String titleName, String... description) {
        this.id = id;
        this.goldsPrice = goldsPrice;

        this.titleName = titleName;
        this.description = description;

        this.itemIcon = ItemUtil.newBuilder(Material.SKULL_ITEM)
                .setDurability(3)
                .setTextureValue(skullTexture)
                .build();
    }

    public void addItem(@NonNull BaseCaseItem baseCaseItem) {
        caseItems.add(baseCaseItem);
    }

}
