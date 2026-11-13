package com.karmasutra.service;

import com.karmasutra.dao.KarmaDAO;
import com.karmasutra.dao.UserDAO;
import com.karmasutra.exception.AuthenticationException;
import com.karmasutra.model.KarmaTransaction;
import com.karmasutra.model.User;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;
import com.karmasutra.util.PasswordUtil;

import java.util.logging.Logger;

/**
 * Authentication and Session Management Service.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;
    private final KarmaDAO karmaDAO;
    private static User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.karmaDAO = new KarmaDAO();
    }

    public synchronized User register(String username, String email, String fullName, String password) {
        if (username == null || username.trim().length() < 3) {
            throw new AuthenticationException("Username must contain at least 3 characters.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new AuthenticationException("Please enter a valid earthly email address.");
        }
        if (password == null || password.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters long.");
        }

        if (userDAO.findByUsername(username.trim()) != null) {
            throw new AuthenticationException("A devotee with username '" + username.trim() + "' is already registered in cosmic archives.");
        }
        if (userDAO.findByEmail(email.trim()) != null) {
            throw new AuthenticationException("A devotee with email '" + email.trim() + "' already holds a cosmic ticket account.");
        }

        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setFullName(fullName != null && !fullName.isBlank() ? fullName.trim() : username.trim());
        newUser.setPasswordHash(passwordHash);
        newUser.setSalt(salt);
        newUser.setRole(UserRole.DEVOTEE);
        newUser.setTier(SubscriptionTier.FREE_DEVOTEE);
        newUser.setKarmaPoints(108);

        User created = userDAO.create(newUser);

        // Record initial karma endowment
        KarmaTransaction initialTx = new KarmaTransaction();
        initialTx.setUserId(created.getId());
        initialTx.setAmount(108);
        initialTx.setTransactionType("EARNED");
        initialTx.setReason("Initial Cosmic Devotee Welcome Grant");
        karmaDAO.createTransaction(initialTx);

        currentUser = created;
        LOGGER.info("Registered new devotee: " + created.getUsername());
        return created;
    }

    public synchronized User login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Please enter both username and password.");
        }

        String identifier = usernameOrEmail.trim();
        User user = identifier.contains("@") ? userDAO.findByEmail(identifier.toLowerCase()) : userDAO.findByUsername(identifier);

        if (user == null) {
            throw new AuthenticationException("No cosmic record found for '" + identifier + "'. Please check your credentials.");
        }

        boolean valid = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash());
        if (!valid) {
            throw new AuthenticationException("Invalid password. The cosmic gatekeeper rejected your entry.");
        }

        currentUser = user;
        LOGGER.info("User logged in successfully: " + user.getUsername() + " (" + user.getRole() + ")");
        return user;
    }

    public synchronized void logout() {
        if (currentUser != null) {
            LOGGER.info("Devotee logged out: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    public static synchronized boolean isAuthenticated() {
        return currentUser != null;
    }

    public static synchronized boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
