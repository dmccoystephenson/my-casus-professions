package com.worldofcasus.professions.command.node;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeId;
import com.worldofcasus.professions.node.NodeItem;
import com.worldofcasus.professions.node.NodeService;
import com.rpkit.core.service.Services;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.bukkit.ChatColor.*;

public final class NodeAddItemCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /node additem [node]";
    private static final String MUST_BE_A_PLAYER = RED + "You must be a player to use this command.";
    private static final String NODE_SERVICE_NOT_REGISTERED_ERROR = RED + "No node service registered.";
    private static final String CHANCE_PROMPT_TEXT = WHITE + "What would you like the chance for getting that item to be (percentage)? " + GRAY + "(Type \"cancel\" to cancel)";
    private static final String NODE_INVALID = RED + "Could not find a node by that ID or name.";
    private static final String NO_PERMISSION = RED + "You do not have permission to add items to nodes.";

    private final CasusProfessions plugin;
    private final ConversationFactory conversationFactory;

    public NodeAddItemCommand(CasusProfessions plugin) {
        this.plugin = plugin;
        this.conversationFactory = new ConversationFactory(plugin)
                .withFirstPrompt(new ChancePrompt(plugin))
                .withEscapeSequence("cancel");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("professions.command.node.additem")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(MUST_BE_A_PLAYER);
            return true;
        }
        Player player = (Player) sender;
        NodeService nodeService = Services.INSTANCE.get(NodeService.class);
        if (nodeService == null) {
            sender.sendMessage(NODE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        Optional<Node> node;
        try {
            int nodeId = Integer.parseInt(args[0]);
            node = nodeService.getNode(new NodeId(nodeId));
        } catch (NumberFormatException exception) {
            String nodeName = args[0];
            node = nodeService.getNode(nodeName);
        }
        if (node.isPresent()) {
            ItemStack item = player.getInventory().getItemInMainHand();
            Map<Object, Object> sessionData = new HashMap<>();
            sessionData.put("node", node.get());
            sessionData.put("item", item);
            conversationFactory.withInitialSessionData(sessionData).buildConversation(player).begin();
        } else {
            sender.sendMessage(NODE_INVALID);
        }
        return true;
    }

    private static class ChancePrompt extends NumericPrompt {

        private final CasusProfessions plugin;

        public ChancePrompt(CasusProfessions plugin) {
            this.plugin = plugin;
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
            context.setSessionData("chance", input.intValue());
            return new ItemAddedPrompt(plugin);
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context, @NotNull Number input) {
            int chance = input.intValue();
            return chance >= 1 && chance <= 100;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return CHANCE_PROMPT_TEXT;
        }

    }

    private static class ItemAddedPrompt extends MessagePrompt {

        private final CasusProfessions plugin;

        public ItemAddedPrompt(CasusProfessions plugin) {
            this.plugin = plugin;
        }

        @Override
        protected @Nullable Prompt getNextPrompt(@NotNull ConversationContext context) {
            NodeService nodeService = Services.INSTANCE.get(NodeService.class);
            if (nodeService == null) {
                return END_OF_CONVERSATION;
            }
            nodeService.addNodeItem(
                    (Node) context.getSessionData("node"),
                    new NodeItem(
                            (ItemStack) context.getSessionData("item"),
                            (int) context.getSessionData("chance")
                    )
            );
            return END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return itemAdded((ItemStack) requireNonNull(context.getSessionData("item")),
                    (Node) requireNonNull(context.getSessionData("node")),
                    (int) requireNonNull(context.getSessionData("chance")));
        }

        private String itemAdded(ItemStack item, Node node, int chance) {
            return GREEN + "Added item " + item.getType().toString().toLowerCase().replace('_', ' ') +
                    " x " + item.getAmount() +
                    " to node " + node.getName() +
                    " with chance " + chance + "%";
        }
    }
}
