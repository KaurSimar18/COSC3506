package org.example.charitydonationsystem.dao;

import org.example.charitydonationsystem.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public static void makeDonation(int donorId, int campaignId, double amount) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // Insert donation
            String donateSQL = "INSERT INTO donations (donor_id, campaign_id, amount, payment_method) VALUES (?, ?, ?, ?)";
            PreparedStatement donateStmt = conn.prepareStatement(donateSQL);
            donateStmt.setInt(1, donorId);
            donateStmt.setInt(2, campaignId);
            donateStmt.setDouble(3, amount);
            donateStmt.setString(4, "card"); // hardcoded method
            donateStmt.executeUpdate();

            // Update campaign amount raised
            String updateSQL = "UPDATE campaigns SET amount_raised = amount_raised + ? WHERE campaign_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, campaignId);
            updateStmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

