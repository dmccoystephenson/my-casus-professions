package com.worldofcasus.professions.listener;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterProvider;
import com.rpkit.core.exception.UnregisteredServiceException;
import com.rpkit.players.bukkit.profile.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.RPKMinecraftProfileProvider;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeItem;
import com.worldofcasus.professions.node.NodeService;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionService;
import com.worldofcasus.professions.stamina.StaminaService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class PlayerInteractListener implements Listener {

    private static final String NO_STAMINA = RED + "You feel completely exhausted. Please rest for a while!";
    private static final String NO_ITEMS = RED + "That node has no drops set up.";
    private static final String NO_ITEM_DROPPED = RED + "Bad luck, you didn't get an item this time.";

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
        ProfessionService professionService;
        try {
            professionService = plugin.core.getServiceManager().getServiceProvider(ProfessionService.class);
        } catch (UnregisteredServiceException e) {
            return;
        }
        RPKMinecraftProfileProvider minecraftProfileService;
        try {
            minecraftProfileService = plugin.core.getServiceManager().getServiceProvider(RPKMinecraftProfileProvider.class);
        } catch (UnregisteredServiceException e) {
            return;
        }
        Player player = event.getPlayer();
        RPKMinecraftProfile minecraftProfile = minecraftProfileService.getMinecraftProfile(player);
        if (minecraftProfile == null) {
            return;
        }
        RPKCharacterProvider characterService;
        try {
            characterService = plugin.core.getServiceManager().getServiceProvider(RPKCharacterProvider.class);
        } catch (UnregisteredServiceException e) {
            return;
        }
        RPKCharacter character = characterService.getActiveCharacter(minecraftProfile);
        if (character == null) {
            return;
        }
        NodeService nodeService;
        try {
            nodeService = plugin.core.getServiceManager().getServiceProvider(NodeService.class);
        } catch (UnregisteredServiceException e) {
            return;
        }
        List<Node> nodes = nodeService.getNodesAt(event.getClickedBlock().getLocation());
        if (!nodes.isEmpty()) {
            event.setCancelled(true);
        }
        CompletableFuture<Optional<Profession>> professionFuture = professionService.getProfession(character);
        professionFuture.thenAccept((profession) -> {
            StaminaService staminaService;
            try {
                staminaService = plugin.core.getServiceManager().getServiceProvider(StaminaService.class);
            } catch (UnregisteredServiceException e) {
                return;
            }
            for (Node node : nodes) {
                harvest(
                    staminaService,
                    player,
                    character,
                    profession.orElse(null),
                    node,
                    block.getRelative(event.getBlockFace()).getLocation()
                );
            }

        });
    }

    private CompletableFuture<Void> harvest(StaminaService staminaService, Player player, RPKCharacter character, Profession profession, Node node, Location dropLocation) {
        if (profession == null) {
            player.sendMessage(noProfession(node.getRequiredProfession()));
            return CompletableFuture.completedFuture(null);
        }
        if (!node.getRequiredProfession().getId().equals(profession.getId())) {
            player.sendMessage(wrongProfession(node.getRequiredProfession(), profession));
            return CompletableFuture.completedFuture(null);
        }
        return staminaService.getAndUpdateStamina(character, (ctx, stamina) -> {
            int harvestCost = plugin.getConfig().getInt("stamina.harvest-cost");
            if (stamina - harvestCost < 0) {
                player.sendMessage(NO_STAMINA);
                return;
            }
            List<NodeItem> items = node.getItems();
            if (items.isEmpty()) {
                player.sendMessage(NO_ITEMS);
                return;
            }
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
            final NodeItem finalChosenItem = chosenItem;
            staminaService.setStamina(ctx, character, stamina - harvestCost).join();
            if (chosenItem.getItem().getType().isItem()) {
                player.sendMessage(itemDropped(finalChosenItem.getItem()));
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        player.getWorld().dropItemNaturally(dropLocation, finalChosenItem.getItem())
                );
            } else {
                player.sendMessage(NO_ITEM_DROPPED);
            }
        });
    }

    private String wrongProfession(Profession requiredProfession, Profession profession) {
        return RED + "This node requires you to be a " + requiredProfession.getName() + ", you are a " + profession.getName() + ".";
    }

    private String noProfession(Profession requiredProfession) {
        return RED + "This node requires you to be a " + requiredProfession.getName() + ", you do not yet have a profession. Set one with /profession set [profession]";
    }

    private String itemDropped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String name = null;
        if (meta != null) {
            if (meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }
        }
        if (name == null) {
            name = item.getType().toString().toLowerCase().replace('_', ' ');
        }
        return GREEN + "You got " + item.getAmount() + " x " + name;
    }

}
