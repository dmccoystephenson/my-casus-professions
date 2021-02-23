package com.worldofcasus.professions.listener;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeItem;
import com.worldofcasus.professions.node.NodeService;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import com.worldofcasus.professions.stamina.StaminaService;
import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterService;
import com.rpkit.core.service.Services;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.minecraft.RPKMinecraftProfileService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class PlayerInteractListener implements Listener {

    private static final String NO_STAMINA = RED + "You feel completely exhausted. Please rest for a while!";

    private final CasusProfessions plugin;
    private final Random random;

    public PlayerInteractListener(CasusProfessions plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) return;
        ProfessionService professionService = Services.INSTANCE.get(ProfessionService.class);
        if (professionService == null) {
            return;
        }
        RPKMinecraftProfileService minecraftProfileService = Services.INSTANCE.get(RPKMinecraftProfileService.class);
        if (minecraftProfileService == null) {
            return;
        }
        Player player = event.getPlayer();
        RPKMinecraftProfile minecraftProfile = minecraftProfileService.getMinecraftProfile(player);
        if (minecraftProfile == null) {
            return;
        }
        RPKCharacterService characterService = Services.INSTANCE.get(RPKCharacterService.class);
        if (characterService == null) {
            return;
        }
        RPKCharacter character = characterService.getActiveCharacter(minecraftProfile);
        if (character == null) {
            return;
        }
        Optional<Profession> profession = professionService.getProfession(character);
        if (!profession.isPresent()) {
            return;
        }
        NodeService nodeService = Services.INSTANCE.get(NodeService.class);
        if (nodeService == null) {
            return;
        }
        StaminaService staminaService = Services.INSTANCE.get(StaminaService.class);
        if (staminaService == null) {
            return;
        }
        List<Node> nodes = nodeService.getNodesAt(event.getClickedBlock().getLocation());
        if (!nodes.isEmpty()) {
            event.setCancelled(true);
        }
        for (Node node : nodes) {
            harvest(staminaService, player, character, profession.get(), node, block.getRelative(event.getBlockFace()).getLocation());
        }
    }

    private void harvest(StaminaService staminaService, Player player, RPKCharacter character, Profession profession, Node node, Location dropLocation) {
        if (!node.getRequiredProfession().getId().equals(profession.getId())) return;
        int stamina = staminaService.getStamina(character);
        if (stamina <= 0) {
            player.sendMessage(NO_STAMINA);
            return;
        }
        List<NodeItem> items = node.getItems();
        if (items.isEmpty()) return;
        int chanceSum = items.stream().map(NodeItem::getChance).reduce(0, Integer::sum);
        int choice = random.nextInt(chanceSum);
        int sum = 0;
        NodeItem chosenItem = null;
        for (NodeItem nodeItem : items) {
            sum += nodeItem.getChance();
            if (sum > choice) {
                chosenItem = nodeItem;
                break;
            }
        }
        if (chosenItem == null) return;
        player.getWorld().dropItemNaturally(dropLocation, chosenItem.getItem());
        staminaService.setStamina(character, stamina - plugin.getConfig().getInt("stamina.harvest-cost"));
    }

}
