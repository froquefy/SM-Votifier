package org.loovcik.smVotifierExt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.loovcik.smVotifierExt.events.PlayerVoteEvent;
import org.loovcik.smVotifierExt.utils.Time;

public class VotePlayer {
   private final UUID uuid;
   private final SMVotifierExt plugin;
   private final List<Long> votes;
   private String name;

   public UUID getUuid() {
      return this.uuid;
   }

   public OfflinePlayer getPlayer() {
      return Bukkit.getOfflinePlayer(this.uuid);
   }

   public String getName() {
      return this.name == null ? "" : this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void addVote() {
      this.votes.add(System.currentTimeMillis());
      this.plugin.addTotalVotes();
      this.save();
      Bukkit.getServer().getPluginManager().callEvent(new PlayerVoteEvent(this));
   }

   public void addVote(Long time) {
      this.votes.add(time);
      this.plugin.addTotalVotes();
      this.save();
      Bukkit.getServer().getPluginManager().callEvent(new PlayerVoteEvent(this));
   }

   public int count() {
      return this.votes.size();
   }

   public int count(Time time) {
      return (int)this.votes.stream().filter(entry -> {
         Long expiresAt = System.currentTimeMillis() - time.toMilliseconds();
         return entry > expiresAt;
      }).count();
   }

   public int count(Long time) {
      return this.count(Time.ofMillis(time));
   }

   public int count(String time) {
      return this.count(Time.of(time));
   }

   public List<Long> getVotes() {
      return this.votes;
   }

   public Long getLastDate() {
      long last = 0L;

      for (Long date : this.votes) {
         if (date > last) {
            last = date;
         }
      }

      return last;
   }

   public void reset() {
      int totalVotes = this.votes.size();
      this.votes.clear();
      this.plugin.subtractTotalVotes(totalVotes);
      this.save();
   }

   public void purge(Time time) {
      Long expiresAt = System.currentTimeMillis() - time.toMilliseconds();
      this.votes.removeIf(value -> value < expiresAt);
      this.save();
   }

   private void loadVotes() {
      this.votes.clear();

      try {
         File file = new File(this.plugin.getDataFolder() + "/data", this.uuid + ".yml");
         if (file.exists()) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            this.votes.addAll(data.getLongList("data"));
            this.setName(data.getString("name"));
         }
      } catch (Exception var3) {
         this.plugin.getLogger().severe("Unable to load '" + this.getName() + "' player data");
      }
   }

   private void save() {
      File file = new File(this.plugin.getDataFolder() + "/data", this.uuid + ".yml");
      if (!file.exists()) {
         try {
            if (!file.createNewFile()) {
               this.plugin.getLogger().severe("Failed to create file " + file.getName());
            }
         } catch (IOException var5) {
            this.plugin.getLogger().severe("Failed to create file " + file.getName());
         }
      }

      YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
      data.set("name", this.getName());
      data.set("data", this.votes);

      try {
         data.save(file);
      } catch (Exception var4) {
         this.plugin.getLogger().severe("Unable to save '" + this.getName() + "' player data");
      }
   }

   public VotePlayer(SMVotifierExt plugin, UUID uuid) {
      this.plugin = plugin;
      this.uuid = uuid;
      this.votes = new ArrayList<>();
      this.loadVotes();
      if (this.name == null) {
         OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
         if (player.getName() != null) {
            this.name = player.getName();
         }
      }
   }
}
