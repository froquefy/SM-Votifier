package org.loovcik.smVotifierExt.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.loovcik.smVotifierExt.Placeholders;
import org.loovcik.smVotifierExt.SMVotifierExt;

class PlaceholderAPIHook {
   public final Placeholders placeholders;

   public String getVersion() {
      return Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion();
   }

   public String process(OfflinePlayer op, String input) {
      return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(op, input);
   }

   public PlaceholderAPIHook(SMVotifierExt plugin) {
      this.placeholders = new Placeholders(plugin);
   }
}
