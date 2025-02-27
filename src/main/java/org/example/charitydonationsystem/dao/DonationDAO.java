package org.example.charitydonationsystem.dao;

import org.example.charitydonationsystem.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DonationDAO handles all database operations related to donations.
 * This includes making donations, retrieving donor history, and admin-level reporting.
 */
public class DonationDAO {

    /**
     * Inserts a new donation and updates the related campaign's amount_raised.
     * This method uses a transaction to ensure both inserts/updates happen together.
     */
    public static void makeDonation(int donorId, int campaignId, double amount) {
        String campaignTitle = "";
        double goalAmount = 0;
        double newRaisedAmount = 0;
        int fundraiserId = -1;

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insert donation
            String donateSQL = "INSERT INTO donations (donor_id, campaign_id, amount, payment_method) VALUES (?, ?, ?, ?)";
            try (PreparedStatement donateStmt = conn.prepareStatement(donateSQL)) {
                donateStmt.setInt(1, donorId);
                donateStmt.setInt(2, campaignId);
                donateStmt.setDouble(3, amount);
                donateStmt.setString(4, "card");
                donateStmt.executeUpdate();
            }

            // 2. Update campaign raised amount
            String updateSQL = "UPDATE campaigns SET amount_raised = amount_raised + ? WHERE campaign_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, campaignId);
                updateStmt.executeUpdate();
            }

            // 3. Get campaign info
            String titleSQL = "SELECT title, goal_amount, amount_raised, fundraiser_id FROM campaigns WHERE campaign_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(titleSQL)) {
                stmt.setInt(1, campaignId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    campaignTitle = rs.getString("title");
                    goalAmount = rs.getDouble("goal_amount");
                    newRaisedAmount = rs.getDouble("amount_raised");
                    fundraiserId = rs.getInt("fundraiser_id");
                }
                rs.close();
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Donor: Donation confirmation
        if (!campaignTitle.isEmpty()) {
            String message = "You donated $" + amount + " to \"" + campaignTitle + "\".";
            NotificationDAO.addNotification(donorId, message);
        }

        // 5. Fundraiser: Someone donated to their campaign
        if (fundraiserId != -1) {
            String message = "🎉 You received a new donation of $" + amount + " for \"" + campaignTitle + "\".";
            NotificationDAO.addNotification(fundraiserId, message);
        }

        // 6. Campaign goal reached → notify subscribers + fundraiser
        if (newRaisedAmount >= goalAmount && goalAmount > 0) {
            String goalMsg = "🎯 The campaign \"" + campaignTitle + "\" has reached its goal!";

            // Notify subscribers
            for (int subscriberId : SubscriptionDAO.getSubscribersForCampaign(campaignId)) {
                NotificationDAO.addNotification(subscriberId, goalMsg);
            }

            // Notify fundraiser too
            if (fundraiserId != -1) {
                NotificationDAO.addNotification(fundraiserId, goalMsg);
            }
        }
    }

    /**
     * Returns a list of formatted donation strings for a specific user.
     * Used in donor's "Donation History" screen.
     */
    public static List<String> getDonationsByUser(int userId) {
        List<String> donations = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = """
                        SELECT d.amount, d.donation_date, c.title
                        FROM donations d
                        JOIN campaigns c ON d.campaign_id = c.campaign_id
                        WHERE d.donor_id = ?
                        ORDER BY d.donation_date DESC
                    """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String campaignTitle = rs.getString("title");
                double amount = rs.getDouble("amount");
                String date = rs.getString("donation_date");
                donations.add("• " + campaignTitle + " — $" + amount + " on " + date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return donations;
    }

    /**
     * Returns the total amount donated by a specific user.
     */
    public static double getTotalDonatedByUser(int userId) {
        double total = 0;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT SUM(amount) AS total FROM donations WHERE donor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    // ===========================
    // Admin Reporting Methods
    // ===========================

    /**
     * Returns the total amount donated across all users and campaigns.
     * Used in Admin reports.
     */
    public static double getTotalDonations() {
        double total = 0;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT SUM(amount) AS total FROM donations";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Returns donation statistics per campaign (count and total raised).
     */
    public static List<String> getDonationStatsByCampaign() {
        List<String> stats = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = """
                        SELECT c.title, COUNT(d.donation_id) AS total_donations, SUM(d.amount) AS total_amount
                        FROM campaigns c
                        LEFT JOIN donations d ON c.campaign_id = d.campaign_id
                        GROUP BY c.campaign_id
                        ORDER BY total_amount DESC
                    """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String line = String.format(
                        "• %s | Donations: %d | Total Raised: $%.2f",
                        rs.getString("title"),
                        rs.getInt("total_donations"),
                        rs.getDouble("total_amount")
                );
                stats.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Returns the top campaigns based on total donation amount.
     *
     * @param limit number of top campaigns to return
     */
    public static List<String> getTopCampaigns(int limit) {
        List<String> top = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = """
                        SELECT c.title, SUM(d.amount) AS raised
                        FROM campaigns c
                        JOIN donations d ON c.campaign_id = d.campaign_id
                        GROUP BY c.campaign_id
                        ORDER BY raised DESC
                        LIMIT ?
                    """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String result = String.format("• %s — $%.2f", rs.getString("title"), rs.getDouble("raised"));
                top.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return top;
    }
}
