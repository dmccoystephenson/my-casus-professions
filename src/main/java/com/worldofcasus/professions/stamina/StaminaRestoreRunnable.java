package com.worldofcasus.professions.stamina;

import com.worldofcasus.professions.CasusProfessions;
import com.rpkit.core.service.Services;
import org.bukkit.scheduler.BukkitRunnable;

public final class StaminaRestoreRunnable extends BukkitRunnable {

    private final CasusProfessions plugin;

    public StaminaRestoreRunnable(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        StaminaService staminaService = Services.INSTANCE.get(StaminaService.class);
        if (staminaService == null) {
            return;
        }
        staminaService.restoreStamina();
    }
}
