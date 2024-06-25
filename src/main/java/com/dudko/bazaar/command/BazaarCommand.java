package com.dudko.bazaar.command;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.item.ItemManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class BazaarCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Bazaar plugin = Bazaar.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        //noinspection DataFlowIssue
        sender.sendMessage(mm.deserialize("""
                                                                                                   \s
                                                  <gold>Bazaar</gold> <gray>by</gray> <yellow>%author%</yellow>
                                                  <gray>%description%
                                                                                                   \s
                                                  Version: </gray>%version%
                                                 \s"""
                                                  .replace("%author%", plugin.getPluginMeta().getAuthors().getFirst())
                                                  .replace("%description%", plugin.getPluginMeta().getDescription())
                                                  .replace("%version%", plugin.getPluginMeta().getVersion())));

        if (sender instanceof Player p) {
            p.getInventory().addItem(ItemManager.GLASS_DISPLAY_SHOP);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of();
    }
}
