package com.karmasutra.service;

import com.karmasutra.dao.KarmaDAO;
import com.karmasutra.dao.UserDAO;
import com.karmasutra.model.KarmaTransaction;
import com.karmasutra.model.User;

import java.util.List;

public class KarmaService {

    private final KarmaDAO karmaDAO;
    private final UserDAO userDAO;

    public KarmaService() {
        this.karmaDAO = new KarmaDAO();
        this.userDAO = new UserDAO();
    }

    public List<KarmaTransaction> getHistory(int userId) {
        return karmaDAO.listByUserId(userId);
    }

    public KarmaTransaction awardKarma(int userId, int points, String reason) {
        KarmaTransaction tx = new KarmaTransaction();
        tx.setUserId(userId);
        tx.setAmount(points);
        tx.setTransactionType(points >= 0 ? "EARNED" : "DEDUCTED");
        tx.setReason(reason);
        KarmaTransaction saved = karmaDAO.createTransaction(tx);

        // Refresh current user in session if matches
        User current = AuthService.getCurrentUser();
        if (current != null && current.getId() == userId) {
            current.setKarmaPoints(current.getKarmaPoints() + points);
        }

        return saved;
    }

    /**
     * Generates a witty, personalized Karma Summary based on real user points.
     */
    public String getPersonalizedKarmaSummary(User user) {
        int points = user != null ? user.getKarmaPoints() : 108;
        if (points > 500) {
            return String.format("Current Balance: %d Karma Points. Celestial Rating: Enlightened VIP. The cosmos is actively looking out for your Wi-Fi signals and parking spots.", points);
        } else if (points >= 200) {
            return String.format("Current Balance: %d Karma Points. Celestial Rating: Solvent Devotee. Your cosmic audit has passed without any pending reincarnation penalties.", points);
        } else if (points >= 100) {
            return String.format("Current Balance: %d Karma Points. Celestial Rating: Standard Earthling. Moderate cosmic solvency; avoid arguing in family WhatsApp groups to preserve balance.", points);
        } else if (points >= 50) {
            return String.format("Current Balance: %d Karma Points. Celestial Rating: Low Karmic Liquidity. Please perform at least one unconditional good deed before submitting further requests.", points);
        } else {
            return String.format("Current Balance: %d Karma Points. Celestial Rating: Karmic Overdraft. The cosmic court recommends immediate charity or returning your borrowed Tupperware.", points);
        }
    }
}
