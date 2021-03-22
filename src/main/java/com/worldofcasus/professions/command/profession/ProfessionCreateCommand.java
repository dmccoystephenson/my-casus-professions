package com.worldofcasus.professions.command.profession;

import com.rpkit.core.exception.UnregisteredServiceException;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class ProfessionCreateCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /profession create [name]";
    private static final String PROFESSION_SERVICE_NOT_REGISTERED_ERROR = RED + "No profession service registered.";
    private static final String NO_PERMISSION = RED + "You do not have permission to create professions.";

    private final CasusProfessions plugin;

    public ProfessionCreateCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.profession.create")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String professionName = args[0];
        ProfessionService professionService;
        try {
            professionService = plugin.core.getServiceManager().getServiceProvider(ProfessionService.class);
        } catch (UnregisteredServiceException e) {
            sender.sendMessage(PROFESSION_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        Profession profession = new Profession(professionName);
        professionService.addProfession(profession);
        sender.sendMessage(professionCreated(profession));
        return true;
    }

    private String professionCreated(Profession profession) {
        return GREEN + "Profession " + profession.getName() + " created.";
    }
}
