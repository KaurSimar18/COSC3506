package org.example.charitydonationsystem.dao;

import org.example.charitydonationsystem.DBUtil;
import org.example.charitydonationsystem.models.Campaign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * CampaignDAO handles all database interactions related to fundraising campaigns.
 * This includes creating, updating, deleting, and querying campaigns.
 */
public class CampaignDAO {

    /**
     * Returns all campaigns created by a specific fundraiser.
     *
     * @param userId fundraiser's user ID
     * @return list of Campaign objects
     */
    public static List<Campaign> getCampaignsByFundraiser(int userId) {
        List<Campaign> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM campaigns WHERE fundraiser_id = ?";
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

    /**
     * Adds a new campaign to the database.
     *
     * @param c Campaign object to insert
     */
    public static void addCampaign(Campaign c) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            if (conn == null) return;
            conn.setAutoCommit(false);

            // 1. Insert campaign
            String sql = "INSERT INTO campaigns (fundraiser_id, title, description, goal_amount, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, c.getFundraiserId());
                stmt.setString(2, c.getTitle());
                stmt.setString(3, c.getDescription());
                stmt.setDouble(4, c.getGoalAmount());
                stmt.setString(5, c.getStartDate());
                stmt.setString(6, c.getEndDate());
                stmt.setString(7, c.getStatus());
                stmt.executeUpdate();
            }

            // 2. Get fundraiser name
            String fundraiserName = "";
            try (PreparedStatement nameStmt = conn.prepareStatement("SELECT full_name FROM users WHERE user_id = ?")) {
                nameStmt.setInt(1, c.getFundraiserId());
                ResultSet rs = nameStmt.executeQuery();
                if (rs.next()) {
                    fundraiserName = rs.getString("full_name");
                }
                rs.close();
            }

            // 3. Notify all donors using same connection
            String message = "New campaign launched: \"" + c.getTitle() + "\" by " + fundraiserName;
            try (PreparedStatement donorsStmt = conn.prepareStatement("SELECT user_id FROM users WHERE role = 'donor'")) {
                ResultSet donorRs = donorsStmt.executeQuery();
                while (donorRs.next()) {
                    int donorId = donorRs.getInt("user_id");
                    try (PreparedStatement notifyStmt = conn.prepareStatement("INSERT INTO notifications (user_id, message) VALUES (?, ?)")) {
                        notifyStmt.setInt(1, donorId);
                        notifyStmt.setString(2, message);
                        notifyStmt.executeUpdate();
                    }
                }
                donorRs.close();
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    /**
     * Updates a campaign's details based on its ID.
     *
     * @param c Campaign object with updated data
     */
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

    /**
     * Deletes a campaign from the database based on its ID.
     *
     * @param campaignId ID of the campaign to delete
     */
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

    /**
     * Returns all currently active campaigns.
     *
     * @return list of Campaigns with status 'active'
     */
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

    /**
     * Gets a summary list of all campaigns with fundraiser name and stats.
     * Used for Admin overview.
     *
     * @return list of formatted strings summarizing campaign data
     */
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
