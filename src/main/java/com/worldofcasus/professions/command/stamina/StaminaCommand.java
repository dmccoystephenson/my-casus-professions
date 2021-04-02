package com.worldofcasus.professions.command.stamina;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterProvider;
import com.rpkit.core.exception.UnregisteredServiceException;
import com.rpkit.players.bukkit.profile.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.RPKMinecraftProfileProvider;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.stamina.StaminaService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class StaminaCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to view stamina.";
    private static final String INVALID_TARGET = RED + "There is no player by that name online.";
    private static final String NO_PERMISSION_OTHER = RED + "You do not have permission to view other players' stamina.";
    private static final String STAMINA_USAGE = RED + "Usage: /stamina [player]";
    private static final String NO_MINECRAFT_PROFILE_SERVICE = RED + "There is no Minecraft profile service registered.";
    private static final String NO_MINECRAFT_PROFILE_SELF = RED + "You do not have a Minecraft profile. Try relogging, and contact a developer if the problem persists.";
    private static final String NO_MINECRAFT_PROFILE_OTHER = RED + "That player does not have a Minecraft profile.";
    private static final String NO_CHARACTER_SERVICE = RED + "There is no character service registered.";
    private static final String NO_CHARACTER_SELF = RED + "You do not have an active character.";
    private static final String NO_CHARACTER_OTHER = RED + "That player does not have an active character.";
    private static final String NO_STAMINA_SERVICE = RED + "There is no stamina service registered.";

    private final CasusProfessions plugin;
    private final StaminaSetCommand staminaSetCommand;

    public StaminaCommand(CasusProfessions plugin) {
        this.plugin = plugin;
        staminaSetCommand = new StaminaSetCommand(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.stamina")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("set")) {
                return staminaSetCommand.onCommand(sender, command, label, Arrays.stream(args).skip(1).toArray(String[]::new));
            } else if (sender.hasPermission("worldofcasus.professions.command.stamina.other")) {
                Player target = plugin.getServer().getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(INVALID_TARGET);
                    return true;
                }
                displayStamina(sender, target);
            } else {
                sender.sendMessage(NO_PERMISSION_OTHER);
            }
        } else {
            if (sender instanceof Player) {
                displayStamina(sender, (Player) sender);
            } else {
                sender.sendMessage(STAMINA_USAGE);
            }
        }
        return true;
    }

    private void displayStamina(CommandSender sender, Player target) {
        RPKMinecraftProfileProvider minecraftProfileService;
        try {
            minecraftProfileService = plugin.core.getServiceManager().getServiceProvider(RPKMinecraftProfileProvider.class);
        } catch (UnregisteredServiceException exception) {
            sender.sendMessage(NO_MINECRAFT_PROFILE_SERVICE);
            return;
        }
        RPKMinecraftProfile minecraftProfile = minecraftProfileService.getMinecraftProfile(target);
        if (minecraftProfile == null) {
            if (sender == target) {
                sender.sendMessage(NO_MINECRAFT_PROFILE_SELF);
            } else {
                sender.sendMessage(NO_MINECRAFT_PROFILE_OTHER);
            }
            return;
        }
        RPKCharacterProvider characterService;
        try {
            characterService = plugin.core.getServiceManager().getServiceProvider(RPKCharacterProvider.class);
        } catch (UnregisteredServiceException exception) {
            sender.sendMessage(NO_CHARACTER_SERVICE);
            return;
        }
        RPKCharacter character = characterService.getActiveCharacter(minecraftProfile);
        if (character == null) {
            if (sender == target) {
                sender.sendMessage(NO_CHARACTER_SELF);
            } else {
                sender.sendMessage(NO_CHARACTER_OTHER);
            }
            return;
        }
        StaminaService staminaService;
        try {
            staminaService = plugin.core.getServiceManager().getServiceProvider(StaminaService.class);
        } catch (UnregisteredServiceException exception) {
            sender.sendMessage(NO_STAMINA_SERVICE);
            return;
        }
        staminaService.getStamina(character).thenAccept(stamina -> {
            if (sender == target) {
                sender.sendMessage(staminaMessageSelf(stamina));
            } else {
                sender.sendMessage(staminaMessageOther(character, stamina));
            }
        });
    }

    private String staminaMessageOther(RPKCharacter character, int stamina) {
        return GREEN + character.getName() + " has " + stamina + " stamina.";
    }

    private String staminaMessageSelf(int stamina) {
        return GREEN + "You have " + stamina + " stamina.";
    }

}
