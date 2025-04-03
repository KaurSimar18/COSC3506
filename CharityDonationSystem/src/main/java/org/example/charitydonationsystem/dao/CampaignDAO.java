package org.example.charitydonationsystem.dao;

import org.example.charitydonationsystem.DBUtil;
import org.example.charitydonationsystem.models.Campaign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CampaignDAO {

    public static List<Campaign> getCampaignsByFundraiser(int userId) {
        List<Campaign> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM campaigns WHERE fundraiser_id = ?";
            assert conn != null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Campaign(
                        rs.getInt("campaign_id"),
                        rs.getInt("fundraiser_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("goal_amount"),
                        rs.getDouble("amount_raised"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addCampaign(Campaign c) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO campaigns (fundraiser_id, title, description, goal_amount, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            assert conn != null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, c.getFundraiserId());
            stmt.setString(2, c.getTitle());
            stmt.setString(3, c.getDescription());
            stmt.setDouble(4, c.getGoalAmount());
            stmt.setString(5, c.getStartDate());
            stmt.setString(6, c.getEndDate());
            stmt.setString(7, c.getStatus());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCampaign(Campaign c) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE campaigns SET title = ?, description = ?, goal_amount = ?, end_date = ?, status = ? WHERE campaign_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setDouble(3, c.getGoalAmount());
            stmt.setString(4, c.getEndDate());
            stmt.setString(5, c.getStatus());
            stmt.setInt(6, c.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteCampaign(int campaignId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM campaigns WHERE campaign_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, campaignId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Campaign> getAllActiveCampaigns() {
        List<Campaign> campaigns = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM campaigns WHERE status = 'active'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                campaigns.add(new Campaign(
                        rs.getInt("campaign_id"),
                        rs.getInt("fundraiser_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("goal_amount"),
                        rs.getDouble("amount_raised"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return campaigns;
    }

    public static List<String> getAllCampaignSummaries() {
        List<String> summaries = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = """
                        SELECT c.title, c.goal_amount, c.amount_raised, c.status, u.full_name AS fundraiser
                        FROM campaigns c
                        JOIN users u ON c.fundraiser_id = u.user_id
                        ORDER BY c.campaign_id DESC
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String summary = String.format(
                        "• %s by %s | Goal: $%.2f | Raised: $%.2f | Status: %s",
                        rs.getString("title"),
                        rs.getString("fundraiser"),
                        rs.getDouble("goal_amount"),
                        rs.getDouble("amount_raised"),
                        rs.getString("status")
                );
                summaries.add(summary);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return summaries;
    }

}

