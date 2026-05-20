package org.loovcik.smVotifierExt;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.loovcik.smVotifierExt.utils.Time;

public class SmVotifierAPI {
   private static SMVotifierExt plugin;

   public SmVotifierAPI(SMVotifierExt plugin) {
      SmVotifierAPI.plugin = plugin;
   }

   public void addVote(OfflinePlayer player) {
      if (player.isOnline()) {
         plugin.players.addVote(player.getUniqueId());
      }
   }

   public void addVote(OfflinePlayer player, Long time) {
      plugin.players.addVote(player.getUniqueId(), time);
   }

   public int getCount(OfflinePlayer player, String time) {
      VotePlayer votePlayer = plugin.players.getPlayer(player.getUniqueId());
      return votePlayer == null ? 0 : votePlayer.count(time);
   }

   public int getCount() {
      return plugin.getTotalVotes();
   }

   public List<Long> getVotes(OfflinePlayer player, String time) {
      VotePlayer votePlayer = plugin.players.getPlayer(player.getUniqueId());
      if (votePlayer == null) {
         return List.of();
      } else {
         Long timeLimit = Time.of(time).toMilliseconds();
         List<Long> votes = votePlayer.getVotes();
         List<Long> result = new ArrayList<>();

         for (Long vote : votes) {
            if (vote >= timeLimit) {
               result.add(vote);
            }
         }

         return result;
      }
   }

   public void reset(OfflinePlayer player) {
      VotePlayer votePlayer = plugin.players.getPlayer(player.getUniqueId());
      if (votePlayer != null) {
         votePlayer.reset();
      }
   }

   public void reset() {
      plugin.reset();
   }

   public SmVotifierAPI() {
   }
}
