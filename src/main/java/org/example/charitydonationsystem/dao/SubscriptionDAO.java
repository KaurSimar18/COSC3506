package org.example.charitydonationsystem.dao;

import org.example.charitydonationsystem.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionDAO {

    public static void subscribe(int userId, int campaignId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO subscriptions (user_id, campaign_id, subscription_type, start_date, is_active) VALUES (?, ?, 'campaign', ?, 1)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, campaignId);
            stmt.setString(3, LocalDate.now().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unsubscribe(int userId, int campaignId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM subscriptions WHERE user_id = ? AND campaign_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, campaignId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSubscribed(int userId, int campaignId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT 1 FROM subscriptions WHERE user_id = ? AND campaign_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, campaignId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Set<Integer> getSubscribersForCampaign(int campaignId) {
        Set<Integer> subs = new HashSet<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT user_id FROM subscriptions WHERE campaign_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, campaignId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subs.add(rs.getInt("user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subs;
    }
}
