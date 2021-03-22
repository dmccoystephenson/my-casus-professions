package com.worldofcasus.professions.stamina;

import com.rpkit.core.exception.UnregisteredServiceException;
import com.worldofcasus.professions.CasusProfessions;
import org.bukkit.scheduler.BukkitRunnable;

public final class StaminaRestoreRunnable extends BukkitRunnable {

    private final CasusProfessions plugin;

    public StaminaRestoreRunnable(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        StaminaService staminaService;
        try {
            staminaService = plugin.core.getServiceManager().getServiceProvider(StaminaService.class);
        } catch (UnregisteredServiceException e) {
            return;
        }
        staminaService.restoreStamina();
    }
}
