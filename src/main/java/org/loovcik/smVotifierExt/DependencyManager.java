package org.loovcik.smVotifierExt;

import org.loovcik.smVotifierExt.dependencies.PlaceholderAPI;

public class DependencyManager {
   private final SMVotifierExt plugin;
   public PlaceholderAPI placeholderAPI;

   private void create() {
      this.placeholderAPI = new PlaceholderAPI(this.plugin);
   }

   public DependencyManager(SMVotifierExt plugin) {
      this.plugin = plugin;
      this.create();
   }
}
