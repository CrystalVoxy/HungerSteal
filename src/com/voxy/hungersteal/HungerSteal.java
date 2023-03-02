package com.voxy.hungersteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerSteal extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        System.out.println("HungerSteal plugin has been enabled!");
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setFoodLevel(20);
            player.setSaturation(20);
        });

        // Register command and event listeners
        getCommand("hunger").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);

        // Add recipe for Hunger item
        ItemStack hungerItem = new ItemStack(Material.HONEY_BOTTLE);
        ShapedRecipe recipe = new ShapedRecipe(hungerItem);
        recipe.shape("OOO", "OGO", "OGO");
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("hunger")) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("withdraw")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can withdraw hunger!");
                return true;
            }
            Player player = (Player) sender;
            int foodLevel = player.getFoodLevel();
            int maxFoodLevel = player.getFoodLevel();
            int amountToWithdraw = maxFoodLevel - foodLevel;
            if (amountToWithdraw <= 0) {
                player.sendMessage(ChatColor.RED + "You are already at full hunger!");
                return true;
            }
            player.setFoodLevel(maxFoodLevel);
            player.sendMessage(ChatColor.GREEN + "You have withdrawn " + ChatColor.YELLOW + amountToWithdraw + ChatColor.GREEN + " hunger bars!");
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            if (sender instanceof Player && !((Player) sender).isOp()) {
                sender.sendMessage(ChatColor.RED + "Only ops can give hunger to players!");
                return true;
            }
            if (!isInteger(args[1])) {
                sender.sendMessage(ChatColor.RED + "Please enter a valid number for the hunger amount!");
                return true;
            }
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Please enter a positive number for the hunger amount!");
                return true;
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                int foodLevel = player.getFoodLevel();
                int maxFoodLevel = player.getFoodLevel();
                int amountToAdd = Math.min(maxFoodLevel - foodLevel, amount);
                if (amountToAdd > 0) {
                    player.setFoodLevel(foodLevel + amountToAdd);
                    player.sendMessage(ChatColor.GREEN + "You have received " + ChatColor.YELLOW + amountToAdd + ChatColor.GREEN + " hunger bars from " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + "!");
                }
            });
            sender.sendMessage(ChatColor.GREEN + "All players have received " + ChatColor.YELLOW + amount + ChatColor.GREEN + " hunger bars!");
            return true;
        } else {
        sender.sendMessage(ChatColor.RED + "Invalid command syntax! Usage: /hunger <withdraw|give> [amount]");
        return true;
        }
        }

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int foodLevel = player.getFoodLevel();
        if (foodLevel > 0) {
        player.setFoodLevel(foodLevel / 2);
        player.sendMessage(ChatColor.RED + "You lost " + ChatColor.YELLOW + (foodLevel / 2) + ChatColor.RED + " hunger bars upon death!");
        }
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.HONEY_BOTTLE && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Hunger")) {
        int foodLevel = player.getFoodLevel();
        int maxFoodLevel = player.getFoodLevel();
        int amountToAdd = Math.min(maxFoodLevel - foodLevel, 10);
        if (amountToAdd > 0) {
        player.setFoodLevel(foodLevel + amountToAdd);
        player.sendMessage(ChatColor.GREEN + "You have gained " + ChatColor.YELLOW + amountToAdd + ChatColor.GREEN + " hunger bars from the Hunger bottle!");
        player.getInventory().removeItem(item);
        } else {
        player.sendMessage(ChatColor.RED + "You are already at full hunger!");
        }
        }
        }

        private boolean isInteger(String s) {
        try {
        Integer.parseInt(s);
        return true;
        } catch (NumberFormatException ex) {
        return false;
        }
        }
        }

        //end of code.
