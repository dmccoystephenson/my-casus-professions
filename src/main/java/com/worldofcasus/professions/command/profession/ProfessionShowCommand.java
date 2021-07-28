package com.worldofcasus.professions.command.profession;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterService;
import com.rpkit.core.service.Services;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfileService;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class ProfessionShowCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to show your profession.";
    private static final String NO_PERMISSION_OTHER = RED + "You do not have permission to view other players' professions.";
    private static final String PROFESSION_SHOW_USAGE = RED + "Usage: /profession show (player)";
    private static final String NO_MINECRAFT_PROFILE_SERVICE = RED + "There is no Minecraft profile service registered.";
    private static final String NO_MINECRAFT_PROFILE_SELF = RED + "You do not have a Minecraft profile. Try relogging, and if the problem persists, contact a developer.";
    private static final String NO_MINECRAFT_PROFILE_OTHER = RED + "That player does not currently have a Minecraft profile.";
    private static final String NO_CHARACTER_SERVICE = RED + "There is no character service registered.";
    private static final String NO_CHARACTER_SELF = RED + "You do not currently have an active character.";
    private static final String NO_CHARACTER_OTHER = RED + "That player does not currently have an active character.";
    private static final String NO_PROFESSION_SERVICE = RED + "There is no profession service registered.";
    private static final String NO_PROFESSION_SELF = GREEN + "You do not currently have a profession.";

    private final CasusProfessions plugin;

    public ProfessionShowCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.profession.show")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        Player target = null;
        if (args.length > 0) {
            if (!sender.hasPermission("worldofcasus.professions.command.profession.show.other")) {
                sender.sendMessage(NO_PERMISSION_OTHER);
                return true;
            }
            target = plugin.getServer().getPlayer(args[0]);
        }
        if (target == null) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(PROFESSION_SHOW_USAGE);
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
        professionService.getProfession(character).thenAccept(profession -> {
            if (profession.isPresent()) {
                if (sender == finalTarget) {
                    sender.sendMessage(professionSelf(profession.get()));
                } else {
                    sender.sendMessage(professionOther(character, profession.get()));
                }
            } else {
                if (sender == finalTarget) {
                    sender.sendMessage(NO_PROFESSION_SELF);
                } else {
                    sender.sendMessage(noProfessionOther(character));
                }
            }
        });
        return true;
    }

    private String professionSelf(Profession profession) {
        return GREEN + "Your profession is " + profession.getName() + ".";
    }

    private String professionOther(RPKCharacter character, Profession profession) {
        return GREEN + character.getName() + "'s profession is " + profession.getName() + ".";
    }

    private String noProfessionOther(RPKCharacter character) {
        return GREEN + character.getName() + " does not currently have a profession.";
    }

}
