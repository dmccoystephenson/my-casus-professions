package com.worldofcasus.professions.command.profession;

import com.rpkit.core.service.Services;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

public final class ProfessionListCommand implements CommandExecutor {

    private static final String PROFESSION_SERVICE_NOT_REGISTERED_ERROR = RED + "No profession service registered.";
    private static final String PROFESSION_LIST_TITLE = WHITE + "Professions:";

    private final CasusProfessions plugin;

    public ProfessionListCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ProfessionService professionService = Services.INSTANCE.get(ProfessionService.class);
        if (professionService == null) {
            sender.sendMessage(PROFESSION_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        professionService.getProfessions().thenAccept((professions) -> {
            sender.sendMessage(PROFESSION_LIST_TITLE);
            for (Profession profession : professions) {
                sender.sendMessage(professionListItem(profession));
            }
        });
        return true;
    }

    private String professionListItem(Profession profession) {
        return WHITE + " - " + profession.getName();
    }
}
