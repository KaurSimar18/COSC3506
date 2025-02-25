package org.example.charitydonationsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBUtil {
    private static final String DB_URL = "jdbc:sqlite:charity.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement()) {
                // Users Table
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS users (
                                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                username TEXT UNIQUE NOT NULL,
                                password TEXT NOT NULL,
                                full_name TEXT NOT NULL,
                                email TEXT UNIQUE,
                                role TEXT CHECK(role IN ('donor', 'fundraiser', 'admin')) NOT NULL
                            );
                        """);

                // Campaigns Table
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS campaigns (
                                campaign_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                fundraiser_id INTEGER NOT NULL,
                                title TEXT NOT NULL,
                                description TEXT,
                                goal_amount REAL NOT NULL,
                                amount_raised REAL DEFAULT 0,
                                start_date TEXT NOT NULL,
                                end_date TEXT,
                                status TEXT CHECK(status IN ('active', 'completed', 'cancelled')) DEFAULT 'active',
                                FOREIGN KEY (fundraiser_id) REFERENCES users(user_id)
                            );
                        """);

                // Donations Table
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS donations (
                                donation_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                donor_id INTEGER NOT NULL,
                                campaign_id INTEGER NOT NULL,
                                amount REAL NOT NULL,
                                donation_date TEXT DEFAULT CURRENT_TIMESTAMP,
                                payment_method TEXT,
                                FOREIGN KEY (donor_id) REFERENCES users(user_id),
                                FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id)
                            );
                        """);

                // Notifications Table
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS notifications (
                                notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                user_id INTEGER NOT NULL,
                                message TEXT NOT NULL,
                                is_read INTEGER DEFAULT 0,
                                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(user_id)
                            );
                        """);

                // Subscriptions Table
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS subscriptions (
                                subscription_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                user_id INTEGER NOT NULL,
                                campaign_id INTEGER,
                                subscription_type TEXT,
                                start_date TEXT NOT NULL,
                                end_date TEXT,
                                is_active INTEGER DEFAULT 1,
                                FOREIGN KEY (user_id) REFERENCES users(user_id),
                                FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id)
                            );
                        """);

                // Reports Table
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS reports (
                                report_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                generated_by INTEGER NOT NULL,
                                report_type TEXT,
                                generated_on TEXT DEFAULT CURRENT_TIMESTAMP,
                                content TEXT,
                                FOREIGN KEY (generated_by) REFERENCES users(user_id)
                            );
                        """);

                System.out.println("Database initialized successfully");

                // Predefined users
                stmt.executeUpdate("""
                            INSERT OR IGNORE INTO users (username, password, full_name, email, role)
                            VALUES
                                ('admin1', 'admin123', 'Admin One', 'admin1@example.com', 'admin'),
                                ('donor1', 'donor123', 'Donor One', 'donor1@example.com', 'donor'),
                                ('fundraiser1', 'fund123', 'Fundraiser One', 'fund1@example.com', 'fundraiser');
                        """);

                System.out.println("Predefined users populated");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
