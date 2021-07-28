package com.worldofcasus.professions.command.stamina;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterService;
import com.rpkit.core.service.Services;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfileService;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.stamina.StaminaService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class StaminaSetCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to set stamina.";
    private static final String NO_STAMINA_SERVICE = RED + "There is no stamina service currently registered.";
    private static final String STAMINA_SET_USAGE = RED + "Usage: /stamina set [player] [stamina]";
    private static final String INVALID_TARGET = RED + "There is no player by that name online.";
    private static final String NO_MINECRAFT_PROFILE_SERVICE = RED + "There is no Minecraft profile service currently registered.";
    private static final String NO_MINECRAFT_PROFILE = RED + "That player does not have a Minecraft profile.";
    private static final String NO_CHARACTER_SERVICE = RED + "There is no character service currently registered.";
    private static final String NO_CHARACTER = RED + "That player does not have an active character.";

    private final CasusProfessions plugin;

    public StaminaSetCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.stamina.set")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        StaminaService staminaService = Services.INSTANCE.get(StaminaService.class);
        if (staminaService == null) {
            sender.sendMessage(NO_STAMINA_SERVICE);
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(STAMINA_SET_USAGE);
            return true;
        }
        Player player = plugin.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(INVALID_TARGET);
            return true;
        }
        RPKMinecraftProfileService minecraftProfileService = Services.INSTANCE.get(RPKMinecraftProfileService.class);
        if (minecraftProfileService == null) {
            sender.sendMessage(NO_MINECRAFT_PROFILE_SERVICE);
            return true;
        }
        RPKMinecraftProfile minecraftProfile = minecraftProfileService.getPreloadedMinecraftProfile(player);
        if (minecraftProfile == null) {
            sender.sendMessage(NO_MINECRAFT_PROFILE);
            return true;
        }
        int stamina;
        try {
            stamina = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(STAMINA_SET_USAGE);
            return true;
        }
        RPKCharacterService characterService = Services.INSTANCE.get(RPKCharacterService.class);
        if (characterService == null) {
            sender.sendMessage(NO_CHARACTER_SERVICE);
            return true;
        }
        RPKCharacter character = characterService.getPreloadedActiveCharacter(minecraftProfile);
        if (character == null) {
            sender.sendMessage(NO_CHARACTER);
            return true;
        }
        staminaService.setStamina(character, stamina).thenRun(() -> {
            sender.sendMessage(staminaSet(character, stamina));
        });
        return true;
    }

    private String staminaSet(RPKCharacter character, int stamina) {
        return GREEN + character.getName() + "'s stamina set to " + stamina;
    }
}
