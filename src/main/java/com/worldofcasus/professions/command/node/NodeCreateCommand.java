package com.worldofcasus.professions.command.node;

import com.rpkit.core.bukkit.location.LocationsKt;
import com.rpkit.core.service.Services;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfileService;
import com.rpkit.selection.bukkit.selection.RPKSelectionService;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeService;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class NodeCreateCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = "Usage: /node create [name] [required profession]";
    private static final String NODE_SERVICE_NOT_REGISTERED_ERROR = RED + "No node service registered.";
    private static final String SELECTION_SERVICE_NOT_REGISTERED_ERROR = RED + "No selection service registered.";
    private static final String MINECRAFT_PROFILE_SERVICE_NOT_REGISTERED_ERROR = RED + "No Minecraft profile service registered.";
    private static final String PROFESSION_SERVICE_NOT_REGISTERED_ERROR = RED + "No profession service registered.";
    private static final String MUST_BE_A_PLAYER = RED + "You must be a player to use this command.";
    private static final String NO_MINECRAFT_PROFILE = RED + "You need a Minecraft profile to be able to use this command.";
    private static final String NO_PERMISSION = RED + "You do not have permission to create nodes.";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.node.create")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String name = args[0];
        NodeService nodeService = Services.INSTANCE.get(NodeService.class);
        if (nodeService == null) {
            sender.sendMessage(NODE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        RPKMinecraftProfileService minecraftProfileService = Services.INSTANCE.get(RPKMinecraftProfileService.class);
        if (minecraftProfileService == null) {
            sender.sendMessage(MINECRAFT_PROFILE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        RPKSelectionService selectionService = Services.INSTANCE.get(RPKSelectionService.class);
        if (selectionService == null) {
            sender.sendMessage(SELECTION_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        ProfessionService professionService = Services.INSTANCE.get(ProfessionService.class);
        if (professionService == null) {
            sender.sendMessage(PROFESSION_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        String professionName = args[1];
        CompletableFuture<Optional<Profession>> requiredProfessionFuture = professionService.getProfession(professionName);
        requiredProfessionFuture.thenAccept((requiredProfession) -> {
            if (!requiredProfession.isPresent()) {
                sender.sendMessage(professionNotFound(professionName));
                return;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(MUST_BE_A_PLAYER);
                return;
            }
            Player player = (Player) sender;
            RPKMinecraftProfile minecraftProfile = minecraftProfileService.getPreloadedMinecraftProfile(player);
            if (minecraftProfile == null) {
                sender.sendMessage(NO_MINECRAFT_PROFILE);
                return;
            }
            selectionService.getSelection(minecraftProfile).thenAccept(selection -> {
                Location minLocation = LocationsKt.toBukkitBlock(selection.getMinimumPoint()).getLocation();
                Location maxLocation = LocationsKt.toBukkitBlock(selection.getMaximumPoint()).getLocation();
                nodeService.addNode(new Node(
                        name,
                        minLocation,
                        maxLocation,
                        requiredProfession.get(),
                        new ArrayList<>()
                )).thenAccept((node) -> sender.sendMessage(nodeCreated(name, minLocation, maxLocation)));
            });

        });
        return true;
    }

    private String nodeCreated(String name, Location minLocation, Location maxLocation) {
        return GREEN + "Node " + name + " created in world " +
                minLocation.getWorld().getName() + " between " +
                minLocation.getBlockX() + "," +
                minLocation.getBlockY() + "," +
                minLocation.getBlockZ() + " and " +
                maxLocation.getBlockX() + "," +
                maxLocation.getBlockY() + "," +
                maxLocation.getBlockZ();
    }

    private String professionNotFound(String professionName) {
        return RED + "Profession " + professionName + " not found.";
    }

}
