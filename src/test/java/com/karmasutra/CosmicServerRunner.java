package com.karmasutra;

import com.karmasutra.bridge.CosmicWebServer;
import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.dao.UserDAO;
import com.karmasutra.model.User;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;

public class CosmicServerRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting dedicated CosmicWebServer daemon...");

        // Verify MySQL connectivity and initialize tables if needed
        boolean dbOk = DatabaseConfig.initializeDatabase();
        System.out.println("Database connectivity verified: " + dbOk);

        User devotee = null;
        try {
            UserDAO udao = new UserDAO();
            devotee = udao.findByUsername("devotee1");
            if (devotee == null) {
                devotee = udao.findByUsername("Draupadi");
            }
        } catch (Exception e) {
            System.out.println("Devotee lookup note: " + e.getMessage());
        }

        if (devotee == null) {
            devotee = new User(2, "devotee1", "devotee1@karmasutra.org", "hash", "salt", "Arjun Sharma", UserRole.DEVOTEE, SubscriptionTier.FREE_DEVOTEE, 108, null);
        }

        System.out.println("Active cosmic devotee: " + devotee.getFullName() + " (ID: " + devotee.getId() + ")");

        CosmicWebServer server = CosmicWebServer.getInstance();
        server.setActiveUser(devotee);
        server.start();

        System.out.println("✦ CosmicWebServer running on port " + server.getPort() + " (and 5173). Keeping alive...");
        Thread.currentThread().join();
    }
}
