package org.loovcik.smVotifierExt;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.loovcik.smVotifierExt.utils.Time;

public class PlayersManager {
   private final SMVotifierExt plugin;
   private HashMap<UUID, VotePlayer> data;

   public void addVote(UUID uuid) {
      if (!this.isRegistered(uuid)) {
         this.register(uuid);
      }

      VotePlayer votePlayer = this.getPlayer(uuid);
      votePlayer.addVote();
   }

   public void addVote(UUID uuid, Long time) {
      if (!this.isRegistered(uuid)) {
         this.register(uuid);
      }

      VotePlayer votePlayer = this.getPlayer(uuid);
      votePlayer.addVote(time);
   }

   private void register(UUID uuid) {
      if (!this.isRegistered(uuid)) {
         this.data.put(uuid, new VotePlayer(this.plugin, uuid));
      }
   }

   private boolean isRegistered(UUID uuid) {
      return this.data.containsKey(uuid);
   }

   public VotePlayer getPlayer(UUID uuid) {
      return !this.isRegistered(uuid) ? null : this.data.get(uuid);
   }

   public void clear() {
      this.data.clear();
   }

   public int count() {
      int result = 0;

      for (Entry<UUID, VotePlayer> entry : this.data.entrySet()) {
         result += entry.getValue().count();
      }

      return result;
   }

   public int count(Time time) {
      int result = 0;

      for (Entry<UUID, VotePlayer> entry : this.data.entrySet()) {
         result += entry.getValue().count(time);
      }

      return result;
   }

   public VotePlayer getLast() {
      long time = 0L;
      VotePlayer result = null;

      for (Entry<UUID, VotePlayer> entry : this.data.entrySet()) {
         if (entry.getValue().getLastDate() > time) {
            time = entry.getValue().getLastDate();
            result = entry.getValue();
         }
      }

      return result;
   }

   public void purge(Time time) {
      this.plugin.getLogger().info("Purging...");
      HashMap<UUID, VotePlayer> copy = new HashMap<>(this.data);

      for (Entry<UUID, VotePlayer> entries : copy.entrySet()) {
         VotePlayer votePlayer = entries.getValue();
         votePlayer.purge(time);
         if (votePlayer.count() == 0) {
            this.data.remove(votePlayer.getUuid());
            File file = new File(this.plugin.getDataFolder() + "/data", votePlayer.getUuid() + ".yml");
            file.delete();
         }
      }
   }

   private void loadData() {
      this.data.clear();
      List<File> files = this.listFiles();
      YamlConfiguration loader = new YamlConfiguration();

      for (File file : files) {
         try {
            if (file.exists()) {
               UUID uuid = UUID.fromString(file.getName().replaceAll(".yml", ""));
               loader.load(file);
               VotePlayer votePlayer = new VotePlayer(this.plugin, uuid);
               this.data.put(uuid, votePlayer);
            }
         } catch (Exception var7) {
            this.plugin.getLogger().severe("Unable to load file " + file.getName());
         }
      }
   }

   private List<File> listFiles() {
      File files = new File(this.plugin.getDataFolder(), "data");
      File[] f = files.listFiles();
      return f != null ? Arrays.asList(f) : List.of();
   }

   public PlayersManager(SMVotifierExt plugin) {
      this.plugin = plugin;
      this.data = new HashMap<>();
      this.loadData();
   }
}
