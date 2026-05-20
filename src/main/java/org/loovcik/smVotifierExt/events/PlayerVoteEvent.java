package org.loovcik.smVotifierExt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.loovcik.smVotifierExt.VotePlayer;

public class PlayerVoteEvent extends Event {
   public static HandlerList handlers = new HandlerList();
   private VotePlayer votePlayer;

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public VotePlayer getPlayer() {
      return this.votePlayer;
   }

   public PlayerVoteEvent(VotePlayer player) {
      this.votePlayer = player;
   }
}
