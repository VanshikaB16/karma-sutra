package com.karmasutra.service;

import com.karmasutra.dao.SubscriptionDAO;
import com.karmasutra.model.SimulatedPayment;
import com.karmasutra.model.Subscription;
import com.karmasutra.model.User;
import com.karmasutra.model.enums.SubscriptionTier;

import java.util.List;

public class SubscriptionService {

    private final SubscriptionDAO subscriptionDAO;
    private final KarmaService karmaService;

    public SubscriptionService() {
        this.subscriptionDAO = new SubscriptionDAO();
        this.karmaService = new KarmaService();
    }

    public boolean upgradeTier(User user, SubscriptionTier targetTier, String paymentMethod) {
        if (user == null) {
            return false;
        }

        boolean success = subscriptionDAO.processSubscriptionUpgrade(user.getId(), targetTier, paymentMethod);
        if (success) {
            user.setTier(targetTier);
            // Award bonus karma points for subscription upgrade!
            int bonus = targetTier == SubscriptionTier.VIP_DEVOTEE ? 250 : 100;
            karmaService.awardKarma(user.getId(), bonus, "Karmic Bonus for " + targetTier.getDisplayName() + " Upgrade");
        }
        return success;
    }

    public Subscription getActiveSubscription(int userId) {
        return subscriptionDAO.findActiveByUserId(userId);
    }

    public List<SimulatedPayment> getPaymentHistory(int userId) {
        return subscriptionDAO.listPaymentsByUserId(userId);
    }

    public List<SimulatedPayment> getAllPayments() {
        return subscriptionDAO.listAllPayments();
    }
}
