package com.codeitforyou.votes.storage.type;

import com.codeitforyou.votes.Votes;
import com.codeitforyou.votes.storage.ObjectMapper;
import com.codeitforyou.votes.storage.StorageType;
import com.codeitforyou.votes.storage.object.VoteHistory;
import com.codeitforyou.votes.storage.object.VoteUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class MySQLMapper implements StorageType {

    private Votes plugin = Votes.getPlugin();
    private Connection connectionSource;

    private String userTable;
    private String historyTable;

    public MySQLMapper(String prefix, String host, int port, String database, String username, String password) {
        this.userTable = prefix + "users";
        this.userTable = prefix + "history";
        try {
            connectionSource = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            Statement statement = connectionSource.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + database + ".`" + userTable + "` ( `id` INT NOT NULL AUTO_INCREMENT , `uuid` VARCHAR(255) NULL DEFAULT NULL , `votes` INT NULL DEFAULT '0' , PRIMARY KEY (`id`), UNIQUE (`uuid`));");
        } catch (SQLException e) {
            plugin.getLogger().warning("==================================================");
            plugin.getLogger().warning("Failed to connect to MySQL database!");
            plugin.getLogger().warning("If you believe this is an error, send the below stacktrace to the developer..");
            e.printStackTrace();
            plugin.getLogger().warning("==================================================");
        }
    }

    @Override
    public void pullUser(final UUID target) {
        try {
            PreparedStatement stmt = connectionSource.prepareStatement("SELECT * FROM " + userTable + " WHERE uuid = ?");
            stmt.setString(1, target.toString());
            ResultSet rs = stmt.executeQuery();

            ObjectMapper<VoteUser> objectMapper = new ObjectMapper<>(VoteUser.class);
            VoteUser voteUser = objectMapper.map(rs);

            if (voteUser == null) {
                voteUser = new VoteUser(target, 0);
            }
            plugin.getUserManager().addUser(voteUser);

            Player player = Bukkit.getPlayer(voteUser.getVoter());
            if (player != null)
                plugin.getLogger().info("Pulled user " + player.getName() + " with " + voteUser.getVotes() + " vote(s)!");
        } catch (SQLException e) {
            plugin.getLogger().warning("==================================================");
            plugin.getLogger().warning("Failed to pull user data of " + target.toString() + "!");
            plugin.getLogger().warning("If you believe this is an error, send the below stacktrace to the developer..");
            e.printStackTrace();
            plugin.getLogger().warning("==================================================");
        }
    }

    @Override
    public void pushUser(final UUID target) {
        VoteUser voteUser = plugin.getUserManager().getUser(target);

        try {
            //
            PreparedStatement stmt = connectionSource.prepareStatement("INSERT INTO " + userTable + " (uuid, votes) VALUES (?, ?) ON DUPLICATE KEY UPDATE votes = ?");
            stmt.setString(1, voteUser.getVoter().toString());
            stmt.setInt(2, voteUser.getVotes());
            stmt.setInt(3, voteUser.getVotes());

            stmt.executeUpdate();

            plugin.getLogger().info("Pushed user " + Bukkit.getPlayer(voteUser.getVoter()).getName() + " with " + voteUser.getVotes() + " vote(s)!");
            plugin.getUserManager().removeUser(target);
        } catch (SQLException e) {
            plugin.getLogger().warning("==================================================");
            plugin.getLogger().warning("Failed to push user data of " + target.toString() + "!");
            plugin.getLogger().warning("If you believe this is an error, send the below stacktrace to the developer..");
            e.printStackTrace();
            plugin.getLogger().warning("==================================================");
        }
    }

    @Override
    public void pushHistory(final UUID target, final VoteHistory history) {

        try {
            //
            PreparedStatement stmt = connectionSource.prepareStatement("INSERT INTO " + historyTable + " (uuid, service, time) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE service = ?, time = ?");
//            stmt.setString(1, voteUser.getVoter().toString());
//            stmt.setInt(2, voteUser.getVotes());
//            stmt.setInt(3, voteUser.getVotes());

            stmt.executeUpdate();

//            plugin.getLogger().info("Pushed user " + Bukkit.getPlayer(voteUser.getVoter()).getName() + " with " + voteUser.getVotes() + " vote(s)!");
            plugin.getUserManager().removeUser(target);
        } catch (SQLException e) {
            plugin.getLogger().warning("==================================================");
            plugin.getLogger().warning("Failed to push vote history of " + target.toString() + "!");
            plugin.getLogger().warning("If you believe this is an error, send the below stacktrace to the developer..");
            e.printStackTrace();
            plugin.getLogger().warning("==================================================");
        }
    }
}
