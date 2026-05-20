package pl.ibcgames.smvotifier.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.loovcik.smVotifierExt.SMVotifierExt;
import org.loovcik.smVotifierExt.SmVotifierAPI;
import pl.ibcgames.smvotifier.Consts;
import pl.ibcgames.smvotifier.Utils;
import pl.ibcgames.smvotifier.Votifier;
import pl.ibcgames.smvotifier.response.UserVoteResponse;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class Reward implements CommandExecutor {

    private final Votifier plugin;
    private final ConcurrentHashMap<String, Date> timeouts = new ConcurrentHashMap<>();

    public Reward(Votifier plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        this.plugin.scheduleAsync(() -> {
            try {
                var config = this.plugin.getConfiguration();
                if (Utils.sendTokenInvalid(config, sender)) {
                    return;
                }

                if (this.timeouts.containsKey(sender.getName())) {
                    Date d = this.timeouts.get(sender.getName());
                    long diff = (long) Math.floor((new Date().getTime() / 1000) - (d.getTime() / 1000));

                    if (diff < 60) {
                        long remaining = 60 - diff;
                        sender.sendMessage(Utils.textComponent(Consts.COMMAND_TIMEOUT_MESSAGE_1, NamedTextColor.RED)
                                .append(Utils.textComponent(Long.toString(remaining), NamedTextColor.GREEN))
                                .append(Utils.textComponent(Consts.COMMAND_TIMEOUT_MESSAGE_2, NamedTextColor.RED)));

                        return;
                    }
                }

                sender.sendMessage(Utils.textComponent(Consts.CHECKING_VOTE_MESSAGE, NamedTextColor.GREEN));
                this.timeouts.put(sender.getName(), new Date());

                var response = Utils.sendRequest(Consts.WEBPAGE_URL + "/api/server-by-key/" + config.getToken() + "/get-vote/" + sender.getName(), UserVoteResponse.class);
                execute(response, sender);
            } catch (Exception e) {
                this.plugin.getSLF4JLogger().warn(Consts.ERROR_DOWNLOAD_USER_VOTE_DATA_MESSAGE, sender.getName(), e);
                sender.sendMessage(Utils.textComponent(Consts.ERROR_DOWNLOAD_USER_VOTE_DATA_PLAYER_MESSAGE, NamedTextColor.RED));
            }
        });

        return true;
    }

    public void reload() {
        this.timeouts.clear();
    }

    private void execute(UserVoteResponse response, CommandSender sender) {
        var error = response.error();
        if (error != null && !error.isEmpty()) {
            sender.sendMessage(Utils.message(error));
            return;
        }

        if (!response.canClaimReward()) {
            sender.sendMessage(Utils.textComponent(Consts.USER_CAN_NOT_CLAIM_REWARD_MESSAGE, NamedTextColor.RED));
            return;
        }

        Utils.executeCommands(this.plugin, sender);

        if (sender instanceof Player player && SMVotifierExt.getInstance() != null) {
            new SmVotifierAPI().addVote(player);
        }
    }
}
