package com.dudko.bazaar.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public enum Sounds {

    ERROR(Sound.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1, 0.5F),
    BUY(Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 2F);

    private final Sound sound;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;

    Sounds(Sound sound, SoundCategory category, float volume, float pitch) {
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player player) {
        player.playSound(player, sound, category, volume, pitch);
    }
    public void play(Location location) {
        location.getWorld().playSound(location, sound, category, volume, pitch);
    }

}
