package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public User getOrCreateUser(long chatId, String name) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT points FROM users WHERE chat_id = ?");
            ps.setLong(1, chatId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("points");

                // Carica NationDex
                List<String> nationDex = new ArrayList<>();
                PreparedStatement ps2 = conn.prepareStatement("SELECT nation FROM nationdex WHERE chat_id = ?");
                ps2.setLong(1, chatId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    nationDex.add(rs2.getString("nation"));
                }

                User user = new User(chatId, name);
                user.addPoints(points);
                nationDex.forEach(user::addNation);
                return user;
            } else {
                PreparedStatement insert = conn.prepareStatement("INSERT INTO users (chat_id, name) VALUES (?, ?)");
                insert.setLong(1, chatId);
                insert.setString(2, name);
                insert.executeUpdate();
                return new User(chatId, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new User(chatId, name);
        }
    }

    public void updatePoints(User user) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET points = ? WHERE chat_id = ?");
            ps.setInt(1, user.getTotalPoints());
            ps.setLong(2, user.getChatId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNationToDex(User user, String nation) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO nationdex (chat_id, nation) VALUES (?, ?)");
            ps.setLong(1, user.getChatId());
            ps.setString(2, nation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        user.addNation(nation);
    }

    public String getLeaderboardString() {
        StringBuilder sb = new StringBuilder("🏆 Classifica globale:\n");
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users ORDER BY points DESC");
            ResultSet rs = ps.executeQuery();

            int rank = 1;
            while (rs.next()&&rank<11) {
                String name = rs.getString("name");
                int points = rs.getInt("points");
                sb.append(rank).append(". ").append(name).append(" - ").append(points).append(" punti\n");
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "⚠️ Errore nel caricamento della leaderboard!";
        }

        if (sb.toString().equals("🏆 Classifica globale:\n")) {
            return "La leaderboard è vuota!";
        }
        return sb.toString();
    }

    public void updateUsername(User user, String newName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ? WHERE chat_id = ?");
            ps.setString(1, newName);
            ps.setLong(2, user.getChatId());
            ps.executeUpdate();

            // Aggiorna anche l’oggetto in memoria
            user = getOrCreateUser(user.getChatId(), newName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
