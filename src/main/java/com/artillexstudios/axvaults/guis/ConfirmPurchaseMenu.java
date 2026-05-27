package com.artillexstudios.axvaults.guis;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axvaults.hooks.PremiumCoinsProvider;
import com.artillexstudios.axvaults.utils.SoundUtils;
import com.artillexstudios.axvaults.vaults.VaultPlayer;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

import static com.artillexstudios.axvaults.AxVaults.CONFIG;
import static com.artillexstudios.axvaults.AxVaults.MESSAGES;
import static com.artillexstudios.axvaults.AxVaults.MESSAGEUTILS;

public class ConfirmPurchaseMenu {
    private final Player player;
    private final VaultPlayer vaultPlayer;
    private final int vaultNum;

    public ConfirmPurchaseMenu(Player player, VaultPlayer vaultPlayer, int vaultNum) {
        this.player = player;
        this.vaultPlayer = vaultPlayer;
        this.vaultNum = vaultNum;
    }

    public void open() {
        final double price = CONFIG.getDouble("slot-purchase-price", 350.0);

        final Gui gui = Gui.gui()
                .title(StringUtils.format("&8Confirm Purchase: Vault #" + vaultNum))
                .rows(3)
                .disableAllInteractions()
                .create();

        // 1. Confirm Button (Green)
        ItemStack confirmItem = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName(StringUtils.formatToString("&#2eff2e&lCONFIRM PURCHASE"));
            confirmMeta.setLore(Collections.singletonList(StringUtils.formatToString("&7Click to purchase Vault #" + vaultNum + " for " + price + " coins.")));
            confirmItem.setItemMeta(confirmMeta);
        }
        GuiItem confirmGuiItem = new GuiItem(confirmItem);
        confirmGuiItem.setAction(event -> {
            player.closeInventory();
            PremiumCoinsProvider.getBalance(player).thenAccept(balance -> {
                if (balance < price) {
                    MESSAGEUTILS.sendLang(player, "errors.insufficient-coins");
                    return;
                }
                PremiumCoinsProvider.takeCoins(player, price, "Purchased Vault #" + vaultNum).thenAccept(success -> {
                    if (success) {
                        vaultPlayer.addPurchasedSlot();
                        MESSAGEUTILS.sendLang(player, "vault.purchased-success");
                        SoundUtils.playSound(player, MESSAGES.getString("sounds.purchase-success"));
                    } else {
                        MESSAGEUTILS.sendLang(player, "errors.transaction-failed");
                    }
                });
            });
        });
        gui.setItem(2, 3, confirmGuiItem);

        // 2. Info Button (Book)
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(StringUtils.formatToString("&#55ffff&lPurchase Information"));
            infoMeta.setLore(java.util.Arrays.asList(
                    StringUtils.formatToString("&7Item: &fVault Slot #" + vaultNum),
                    StringUtils.formatToString("&7Price: &e" + price + " Premium Coins"),
                    StringUtils.formatToString(""),
                    StringUtils.formatToString("&7Your slots will persist forever.")
            ));
            infoItem.setItemMeta(infoMeta);
        }
        gui.setItem(2, 5, new GuiItem(infoItem));

        // 3. Cancel Button (Red)
        ItemStack cancelItem = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName(StringUtils.formatToString("&#ff2e2e&lCANCEL"));
            cancelMeta.setLore(Collections.singletonList(StringUtils.formatToString("&7Click to cancel and go back.")));
            cancelItem.setItemMeta(cancelMeta);
        }
        GuiItem cancelGuiItem = new GuiItem(cancelItem);
        cancelGuiItem.setAction(event -> {
            new VaultSelector(player, vaultPlayer).open();
        });
        gui.setItem(2, 7, cancelGuiItem);

        gui.open(player);
    }
}
