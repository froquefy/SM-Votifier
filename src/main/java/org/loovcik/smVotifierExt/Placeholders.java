package org.loovcik.smVotifierExt;

import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.loovcik.smVotifierExt.utils.Time;

public class Placeholders extends PlaceholderExpansion {
   private final SMVotifierExt plugin;

   public Placeholders(SMVotifierExt plugin) {
      this.plugin = plugin;
   }

   @NotNull
   public String getIdentifier() {
      return "smvotifierext";
   }

   @NotNull
   public String getAuthor() {
      return String.join(", ", this.plugin.getDescription().getAuthors());
   }

   @NotNull
   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onRequest(OfflinePlayer player, @NotNull String params) {
      List<String> parts = List.of(params.split("_"));
      String result = "N/A";
      if (!parts.isEmpty()) {
         String var5 = parts.get(0);

         result = switch (var5) {
            case "total" -> {
               if (parts.get(1).equalsIgnoreCase("all")) {
                  yield String.valueOf(this.plugin.getTotalVotes());
               } else {
                  Time time = Time.of(parts.get(1));
                  yield String.valueOf(this.plugin.players.count(time));
               }
            }
            case "player" -> {
               VotePlayer votePlayer = new VotePlayer(this.plugin, player.getUniqueId());
               if (parts.get(1).equalsIgnoreCase("all")) {
                  yield String.valueOf(votePlayer.count());
               } else {
                  Time time = Time.of(parts.get(1));
                  yield String.valueOf(votePlayer.count(time));
               }
            }
            case "last" -> {
               String kind = parts.get(1);
               yield switch (kind) {
                  case "name" -> {
                     VotePlayer last = this.plugin.players.getLast();
                     yield last != null ? last.getName() : "---";
                  }
                  case "date" -> {
                     VotePlayer last = this.plugin.players.getLast();
                     yield last != null ? Time.ofMillis(last.getLastDate()).formatDate() : "---";
                  }
                  case "player" -> {
                     VotePlayer self = this.plugin.players.getPlayer(player.getUniqueId());
                     yield self != null ? Time.ofMillis(self.getLastDate()).formatDate() : "---";
                  }
                  default -> "---";
               };
            }
            case "database" -> String.valueOf(this.plugin.players.count());
            default -> "N/A";
         };
      }

      return result;
   }
}
