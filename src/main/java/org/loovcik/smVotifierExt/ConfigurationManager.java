package org.loovcik.smVotifierExt;

import java.util.Arrays;
import org.bukkit.configuration.file.FileConfiguration;
import org.loovcik.smVotifierExt.utils.Time;

public class ConfigurationManager {
   private final SMVotifierExt plugin;
   private FileConfiguration config;
   public Time purgeTime;

   public FileConfiguration getConfig() {
      return this.config;
   }

   public void load() {
      this.purgeTime = this.getTime("purgeTime", "45d", "Czas, po którym dane zostaną usunięte");
   }

   public void loadConfig() {
      this.plugin.reloadConfig();
      this.config = this.plugin.getConfig();
      this.config.options().copyDefaults(true);
      this.load();
      this.plugin.saveConfig();
   }

   public Time getTime(String path, String def, String comment) {
      this.config.addDefault(path, def);
      this.addComment(path, comment);
      return Time.of(this.config.getString(path, def));
   }

   public void addComment(String path, String comment) {
      if (comment != null && !comment.isEmpty()) {
         this.config.setComments(path, Arrays.asList(comment.split("\n")));
      }
   }

   public ConfigurationManager(SMVotifierExt plugin) {
      this.plugin = plugin;
      plugin.saveDefaultConfig();
   }
}
