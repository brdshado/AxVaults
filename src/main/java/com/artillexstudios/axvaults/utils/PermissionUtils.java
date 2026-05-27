package com.artillexstudios.axvaults.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axvaults.AxVaults.CONFIG;

public class PermissionUtils {

    public static boolean hasPermission(@NotNull Player player, int vault) {
        if (CONFIG.getInt("permission-mode", 0) == 0) return player.hasPermission("axvaults.vault." + vault);
        if (player.isOp()) return true;

        int max = player.hasPermission("axvaults.vault.1") ? 1 : 0;
        for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            if (!effectivePermission.getValue()) continue;
            if (effectivePermission.getPermission().equals("*")) return true;

            if (!effectivePermission.getPermission().startsWith("axvaults.vault.")) continue;

            int value = Integer.parseInt(effectivePermission.getPermission().substring(effectivePermission.getPermission().lastIndexOf('.') + 1));

            if (value > max) max = value;
        }

        return vault <= max;
    }

    public static int getPermissionBase(@NotNull Player player) {
        int maxVal = 0;
        int maxAllowedAmount = CONFIG.getInt("max-vault-amount", 100);
        if (maxAllowedAmount == -1) maxAllowedAmount = 200; // default cap for iteration
        
        if (player.isOp()) return maxAllowedAmount;
        
        for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            if (!effectivePermission.getValue()) continue;
            if (effectivePermission.getPermission().equals("*")) return maxAllowedAmount;
        }

        for (int i = 1; i <= maxAllowedAmount; i++) {
            if (player.hasPermission("axvaults.vault." + i)) {
                if (i > maxVal) maxVal = i;
            }
        }
        return maxVal;
    }
}
