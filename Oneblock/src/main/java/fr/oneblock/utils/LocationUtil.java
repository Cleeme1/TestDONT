package fr.oneblock.utils;

import org.bukkit.Location;

public class LocationUtil {

    public static boolean areLocationsEqual(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ() &&
                loc1.getWorld().equals(loc2.getWorld());
    }
}