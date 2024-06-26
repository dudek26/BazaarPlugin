package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.market.Market;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;

public class MarketGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Bazaar plugin = Bazaar.getPlugin();

    public Gui marketItems(Market market) {

    }

    public void display(Player player) {
        Gui gui = TabGui.normal()
    }

}
