package org.loovcik.smVotifierExt;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import pl.ibcgames.smvotifier.Votifier;

/**
 * SM-VotifierExt module, originally a standalone plugin by Loovcik
 * (org.loovcik:SM-VotifierExt:1.0). Embedded into SM-Votifier as a component
 * so the host plugin can record per-player vote history and expose
 * %smvotifierext_*% placeholders.
 *
 * The class no longer extends JavaPlugin — it holds a reference to the host
 * Votifier plugin and proxies the JavaPlugin-flavored calls the rest of the
 * ext code makes (getDataFolder, getLogger, getConfig, ...). Data files now
 * live under plugins/SM-Votifier/ instead of plugins/SM-VotifierExt/.
 */
public final class SMVotifierExt {
   private static SMVotifierExt instance;
   private final Votifier host;
   public ConfigurationManager config;
   public PlayersManager players;
   public DependencyManager dependencies;
   private int totalVotes;

   public SMVotifierExt(Votifier host) {
      this.host = host;
      instance = this;
   }

   public static SMVotifierExt getInstance() {
      return instance;
   }

   public File getDataFolder() {
      return host.getDataFolder();
   }

   public Logger getLogger() {
      return host.getLogger();
   }

   public void saveDefaultConfig() {
      host.saveDefaultConfig();
   }

   public void reloadConfig() {
      host.reloadConfig();
   }

   public FileConfiguration getConfig() {
      return host.getConfig();
   }

   public void saveConfig() {
      host.saveConfig();
   }

   public PluginDescriptionFile getDescription() {
      return host.getDescription();
   }

   public void enable() {
      Bukkit.getConsoleSender().sendMessage("[SMVotifierExt] " + ChatColor.YELLOW + "Author: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Loovcik");
      Bukkit.getConsoleSender().sendMessage("[SMVotifierExt] " + ChatColor.YELLOW + "Version: " + ChatColor.GRAY + this.getDescription().getVersion());
      this.saveDefaultConfig();
      this.config = new ConfigurationManager(this);
      this.config.loadConfig();
      this.loadStatistics();
      this.players = new PlayersManager(this);
      this.players.purge(this.config.purgeTime);
      this.dependencies = new DependencyManager(this);
      this.dependencies.placeholderAPI.register();
      new SmVotifierAPI(this);
      this.getLogger().info("SMVotifierExt module loaded");
   }

   public void disable() {
      this.saveStatistics();
      if (this.dependencies != null && this.dependencies.placeholderAPI.isEnabled()) {
         this.dependencies.placeholderAPI.unregister();
      }
      this.getLogger().info("SMVotifierExt module disabled");
   }

   public int getTotalVotes() {
      return this.totalVotes;
   }

   public void addTotalVotes() {
      this.totalVotes++;
      this.saveStatistics();
   }

   public void subtractTotalVotes(int amount) {
      this.totalVotes -= amount;
      if (this.totalVotes < 0) {
         this.totalVotes = 0;
      }

      this.saveStatistics();
   }

   public void resetTotalVotes() {
      this.totalVotes = 0;
      this.saveStatistics();
   }

   public void reset() {
      this.totalVotes = 0;
      this.saveStatistics();

      try {
         File file = new File(this.getDataFolder(), "data");
         deleteDirectory(file);
         this.players.clear();
      } catch (Exception ignored) {
      }
   }

   private static void deleteDirectory(File file) {
      if (file != null && file.listFiles() != null) {
         for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
               deleteDirectory(subfile);
            }

            subfile.delete();
         }
      }
   }

   private void loadStatistics() {
      try {
         File f = new File(this.getDataFolder(), "stats.yml");
         YamlConfiguration stats = YamlConfiguration.loadConfiguration(f);
         this.totalVotes = stats.getInt("totalVotes");
      } catch (Exception e) {
         this.getLogger().severe(e.getMessage());
      }
   }

   private void saveStatistics() {
      try {
         File f = new File(this.getDataFolder(), "stats.yml");
         YamlConfiguration stats = YamlConfiguration.loadConfiguration(f);
         stats.set("totalVotes", this.totalVotes);
         stats.save(f);
      } catch (Exception e) {
         this.getLogger().severe(e.getMessage());
      }
   }
}
