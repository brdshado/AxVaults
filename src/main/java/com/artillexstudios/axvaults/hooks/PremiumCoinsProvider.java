package com.artillexstudios.axvaults.hooks;

import net.bharatmc.sync.api.BharatMCAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.CompletableFuture;

public class PremiumCoinsProvider {
    private static BharatMCAPI api = null;

    public static boolean isHooked() {
        if (Bukkit.getPluginManager().getPlugin("BharatSync") == null) return false;
        if (api != null) return true;
        
        RegisteredServiceProvider<BharatMCAPI> rsp = Bukkit.getServer().getServicesManager().getRegistration(BharatMCAPI.class);
        if (rsp != null) {
            api = rsp.getProvider();
            return true;
        }
        return false;
    }

    public static CompletableFuture<Double> getBalance(Player player) {
        if (!isHooked()) return CompletableFuture.completedFuture(0.0);
        return api.getBalance(player);
    }

    public static CompletableFuture<Boolean> takeCoins(Player player, double amount, String reason) {
        if (!isHooked()) return CompletableFuture.completedFuture(false);
        return api.takeCoins(player, amount, reason).thenApply(result -> result.isSuccess());
    }
}
