package org.example.charitydonationsystem.dao;

import org.example.charitydonationsystem.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public static void addNotification(int userId, String message) {
        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null) {
                String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, message);
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getNotificationsByUser(int userId) {
        List<String> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT message, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add("• " + rs.getString("message") + " (" + rs.getString("created_at") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

