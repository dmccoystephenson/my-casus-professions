package com.worldofcasus.professions.command.profession;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterService;
import com.rpkit.core.service.Services;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfileService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class ProfessionSetCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /profession set [name]";
    private static final String PROFESSION_SERVICE_NOT_REGISTERED_ERROR = RED + "No profession service registered.";
    private static final String MINECRAFT_PROFILE_SERVICE_NOT_REGISTERED_ERROR = RED + "No Minecraft profile service registered.";
    private static final String CHARACTER_SERVICE_NOT_REGISTERED_ERROR = RED + "No character service registered.";
    private static final String MUST_BE_A_PLAYER = RED + "You must be a player to perform this command.";
    private static final String NO_MINECRAFT_PROFILE = RED + "You need a Minecraft profile to be able to use this command.";
    private static final String PROFESSION_ALREADY_SET = RED + "Your profession has already been set.";

    private final CasusProfessions plugin;

    public ProfessionSetCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String professionName = args[0];
        ProfessionService professionService = Services.INSTANCE.get(ProfessionService.class);
        if (professionService == null) {
            sender.sendMessage(PROFESSION_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(MUST_BE_A_PLAYER);
            return true;
        }
        RPKMinecraftProfileService minecraftProfileService = Services.INSTANCE.get(RPKMinecraftProfileService.class);
        if (minecraftProfileService == null) {
            sender.sendMessage(MINECRAFT_PROFILE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        Player player = (Player) sender;
        RPKMinecraftProfile minecraftProfile = minecraftProfileService.getMinecraftProfile(player);
        if (minecraftProfile == null) {
            sender.sendMessage(NO_MINECRAFT_PROFILE);
            return true;
        }
        RPKCharacterService characterService = Services.INSTANCE.get(RPKCharacterService.class);
        if (characterService == null) {
            sender.sendMessage(CHARACTER_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        RPKCharacter character = characterService.getActiveCharacter(minecraftProfile);
        Optional<Profession> characterProfession = professionService.getProfession(character);
        if (characterProfession.isPresent()) {
            sender.sendMessage(PROFESSION_ALREADY_SET);
            return true;
        }
        Optional<Profession> profession = professionService.getProfession(professionName);
        if (profession.isPresent()) {
            Profession value = profession.get();
            professionService.setProfession(character, value);
            sender.sendMessage(professionSet(value));
        } else {
            sender.sendMessage(professionNotFound(professionName));
        }
        return true;
    }

    private String professionSet(Profession profession) {
        return GREEN + "Profession set to " + profession.getName() + ".";
    }

    private String professionNotFound(String professionName) {
        return RED + "Profession " + professionName + " not found.";
    }

}
