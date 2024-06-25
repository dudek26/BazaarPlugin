package com.dudko.bazaar.command;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.shop.GlassDisplayShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveShopCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Bazaar plugin = Bazaar.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length < 1) {
            sender.sendMessage(mm.deserialize(plugin.translatedString("message.command.usage"),
                                              Placeholder.unparsed("usage", command.getUsage())));
            return true;
        }

        boolean success = GlassDisplayShop.remove(args[0]);
        if (success) sender.sendMessage(parsedComponent("message.shop.removed", args[0]));
        else sender.sendMessage(parsedComponent("message.shop.removed-error", args[0]));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of();
    }

    private Component parsedComponent(String key, String id) {
        return mm.deserialize(plugin.translatedString(key), Placeholder.unparsed("id", id));
    }
}
