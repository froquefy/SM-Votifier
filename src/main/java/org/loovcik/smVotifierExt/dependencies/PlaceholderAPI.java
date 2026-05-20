package org.loovcik.smVotifierExt.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.loovcik.smVotifierExt.SMVotifierExt;

public class PlaceholderAPI {
   private PlaceholderAPIHook hook;

   public boolean isEnabled() {
      return this.hook != null;
   }

   public String process(OfflinePlayer op, String input) {
      return this.isEnabled() ? this.hook.process(op, input) : input;
   }

   public String process(String input) {
      return this.process(null, input);
   }

   public void register() {
      if (this.isEnabled()) {
         this.hook.placeholders.register();
      }
   }

   public void unregister() {
      if (this.isEnabled()) {
         this.hook.placeholders.unregister();
      }
   }

   public PlaceholderAPI(SMVotifierExt plugin) {
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         this.hook = new PlaceholderAPIHook(plugin);
         Bukkit.getConsoleSender()
            .sendMessage(
               "[SMVotifierExt] PlaceholderAPI support: "
                  + ChatColor.GREEN
                  + "Yes "
                  + ChatColor.RESET
                  + "("
                  + Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion()
                  + ")"
            );
      } else {
         Bukkit.getConsoleSender().sendMessage("[SMVotifierExt] PlaceholderAPI support: " + ChatColor.RED + "No");
      }
   }
}
