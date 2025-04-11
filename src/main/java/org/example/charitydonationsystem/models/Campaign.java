package org.example.charitydonationsystem.models;

/**
 * This class represents a fundraising campaign.
 * It holds information about the campaign's goal, status, dates, and fundraiser details.
 */
public class Campaign {

    // Unique ID of the campaign (auto-generated in DB)
    private int id;

    // ID of the fundraiser who created this campaign
    private int fundraiserId;

    // Basic campaign details
    private String title;
    private String description;

    // Goal and progress
    private double goalAmount;
    private double amountRaised;

    // Start and end dates (as String for simplicity)
    private String startDate;
    private String endDate;

    // Status can be 'active', 'completed', or 'cancelled'
    private String status;

    /**
     * Constructor used when creating a campaign or loading it from the database.
     */
    public Campaign(int id, int fundraiserId, String title, String description,
                    double goalAmount, double amountRaised,
                    String startDate, String endDate, String status) {
        this.id = id;
        this.fundraiserId = fundraiserId;
        this.title = title;
        this.description = description;
        this.goalAmount = goalAmount;
        this.amountRaised = amountRaised;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // ===== Getters & Setters =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFundraiserId() {
        return fundraiserId;
    }

    public void setFundraiserId(int fundraiserId) {
        this.fundraiserId = fundraiserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(double goalAmount) {
        this.goalAmount = goalAmount;
    }

    public double getAmountRaised() {
        return amountRaised;
    }

    public void setAmountRaised(double amountRaised) {
        this.amountRaised = amountRaised;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
