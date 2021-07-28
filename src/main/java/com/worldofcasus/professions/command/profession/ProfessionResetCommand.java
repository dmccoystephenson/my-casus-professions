package com.worldofcasus.professions.command.profession;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterService;
import com.rpkit.core.service.Services;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfileService;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.profession.ProfessionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class ProfessionResetCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to reset your profession.";
    private static final String NO_PERMISSION_OTHER = RED + "You do not have permission to reset other players' professions.";
    private static final String PROFESSION_RESET_USAGE = RED + "Usage: /profession reset (player)";
    private static final String NO_MINECRAFT_PROFILE_SERVICE = RED + "There is no Minecraft profile service registered.";
    private static final String NO_MINECRAFT_PROFILE_SELF = RED + "You do not have a Minecraft profile. Try relogging, and if the problem persists, contact a developer.";
    private static final String NO_MINECRAFT_PROFILE_OTHER = RED + "That player does not currently have a Minecraft profile.";
    private static final String NO_CHARACTER_SERVICE = RED + "There is no character service registered.";
    private static final String NO_CHARACTER_SELF = RED + "You do not currently have an active character.";
    private static final String NO_CHARACTER_OTHER = RED + "That player does not currently have an active character.";
    private static final String NO_PROFESSION_SERVICE = RED + "There is no profession service registered.";
    private static final String PROFESSION_RESET_SELF = GREEN + "Your profession has been reset.";

    private final CasusProfessions plugin;

    public ProfessionResetCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.profession.reset")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        Player target = null;
        if (args.length > 0) {
            if (!sender.hasPermission("worldofcasus.professions.command.profession.reset.other")) {
                sender.sendMessage(NO_PERMISSION_OTHER);
                return true;
            }
            target = plugin.getServer().getPlayer(args[0]);
        }
        if (target == null) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(PROFESSION_RESET_USAGE);
                return true;
            }
        }
        RPKMinecraftProfileService minecraftProfileService = Services.INSTANCE.get(RPKMinecraftProfileService.class);
        if (minecraftProfileService == null) {
            sender.sendMessage(NO_MINECRAFT_PROFILE_SERVICE);
            return true;
        }
        RPKMinecraftProfile minecraftProfile = minecraftProfileService.getPreloadedMinecraftProfile(target);
        if (minecraftProfile == null) {
            if (sender == target) {
                sender.sendMessage(NO_MINECRAFT_PROFILE_SELF);
            } else {
                sender.sendMessage(NO_MINECRAFT_PROFILE_OTHER);
            }
            return true;
        }
        RPKCharacterService characterService = Services.INSTANCE.get(RPKCharacterService.class);
        if (characterService == null) {
            sender.sendMessage(NO_CHARACTER_SERVICE);
            return true;
        }
        RPKCharacter character = characterService.getPreloadedActiveCharacter(minecraftProfile);
        if (character == null) {
            if (sender == target) {
                sender.sendMessage(NO_CHARACTER_SELF);
            } else {
                sender.sendMessage(NO_CHARACTER_OTHER);
            }
            return true;
        }
        ProfessionService professionService = Services.INSTANCE.get(ProfessionService.class);
        if (professionService == null) {
            sender.sendMessage(NO_PROFESSION_SERVICE);
            return true;
        }
        Player finalTarget = target;
        professionService.unsetProfession(character).thenRun(() -> {
            if (sender == finalTarget) {
                sender.sendMessage(PROFESSION_RESET_SELF);
            } else {
                sender.sendMessage(professionResetOther(character));
                finalTarget.sendMessage(PROFESSION_RESET_SELF);
            }
        });
        return true;
    }

    private String professionResetOther(RPKCharacter character) {
        return GREEN + character.getName() + "'s profession was reset.";
    }

}
