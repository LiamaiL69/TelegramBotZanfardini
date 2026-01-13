package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Recupera o crea utente dal DB
    public User getOrCreateUser(long chatId, String name) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT name, points, quiz_played, quiz_won FROM users WHERE chat_id = ?");
            ps.setLong(1, chatId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("points");
                int quizPlayed = rs.getInt("quiz_played");
                int quizWon = rs.getInt("quiz_won");

                // Carica NationDex
                List<String> nationDex = new ArrayList<>();
                PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT nation FROM nationdex WHERE chat_id = ?");
                ps2.setLong(1, chatId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    nationDex.add(rs2.getString("nation"));
                }

                User user = new User(chatId, rs.getString("name"));
                user.addPoints(points);
                user.setQuizPlayed(quizPlayed);   // Imposta correttamente dal DB
                user.setQuizWon(quizWon);         // Imposta correttamente dal DB
                nationDex.forEach(user::addNation);

                return user;

            } else {
                // Inserimento nuovo utente
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO users (chat_id, name, points, quiz_played, quiz_won) VALUES (?, ?, 0, 0, 0)");
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

    // Aggiorna punti e statistiche quiz
    public void updateStats(User user) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET points = ?, quiz_played = ?, quiz_won = ? WHERE chat_id = ?");
            ps.setInt(1, user.getTotalPoints());
            ps.setInt(2, user.getQuizPlayed());
            ps.setInt(3, user.getQuizWon());
            ps.setLong(4, user.getChatId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Aggiunge nazione al NationDex e DB
    public void addNationToDex(User user, String nation) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO nationdex (chat_id, nation) VALUES (?, ?)");
            ps.setLong(1, user.getChatId());
            ps.setString(2, nation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        user.addNation(nation);
    }

    // Reset NationDex
    public void resetNationDex(User user) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM nationdex WHERE chat_id = ?");
            ps.setLong(1, user.getChatId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        user.resetNationDex();
    }

    // Aggiorna username
    public void updateUsername(User user, String newName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET name = ? WHERE chat_id = ?");
            ps.setString(1, newName);
            ps.setLong(2, user.getChatId());
            ps.executeUpdate();
            user.setName(newName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leaderboard
    public String getLeaderboardString() {
        StringBuilder sb = new StringBuilder("🏆 *Classifica globale:*\n");
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users ORDER BY points DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                int points = rs.getInt("points");
                String medal = rank == 1 ? "🥇 " : rank == 2 ? "🥈 " : rank == 3 ? "🥉 " : "";
                sb.append(medal).append(rank).append(". ").append(name)
                        .append(" - ").append(points).append(" punti\n");
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "⚠️ Errore nel caricamento della leaderboard!";
        }

        if (sb.toString().equals("🏆 *Classifica globale:*\n")) {
            return "_La leaderboard è vuota!_";
        }
        return sb.toString();
    }

    // Posizione dell’utente nella leaderboard
    public int getUserRank(User user) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT chat_id FROM users ORDER BY points DESC");
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                long id = rs.getLong("chat_id");
                if (id == user.getChatId()) return rank;
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // se non trovato
    }
}
