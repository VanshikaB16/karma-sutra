package com.karmasutra;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.dao.ComplaintDAO;
import com.karmasutra.dao.DepartmentDAO;
import com.karmasutra.dao.ResponseDAO;
import com.karmasutra.dao.StatusHistoryDAO;
import com.karmasutra.dao.UserDAO;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.ComplaintStatusHistory;
import com.karmasutra.model.Department;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.complaint.Grievance;
import com.karmasutra.model.complaint.PersonalDilemma;
import com.karmasutra.model.complaint.Prayer;
import com.karmasutra.model.complaint.Wish;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.model.enums.UserRole;
import com.karmasutra.service.AuthService;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.service.KarmaService;
import com.karmasutra.service.SubscriptionService;
import com.karmasutra.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KarmaSutraSystemTest {

    private static AuthService authService;
    private static ComplaintService complaintService;
    private static KarmaService karmaService;
    private static SubscriptionService subscriptionService;

    private static User testUser;
    private static Complaint testComplaint;

    @BeforeAll
    static void setup() {
        boolean initialized = DatabaseConfig.initializeDatabase();
        assertTrue(initialized, "Database schema should initialize successfully on MySQL.");

        authService = new AuthService();
        complaintService = new ComplaintService();
        karmaService = new KarmaService();
        subscriptionService = new SubscriptionService();
    }

    @Test
    @Order(1)
    @DisplayName("Verify Cryptographic Password Hashing (PBKDF2)")
    void testPasswordSecurity() {
        String password = "SecretPassphrase@108";
        String salt = PasswordUtil.generateSalt();
        assertNotNull(salt);
        assertEquals(32, salt.length(), "Salt should be 16 bytes (32 hex characters)");

        String hash = PasswordUtil.hashPassword(password, salt);
        assertNotNull(hash);

        assertTrue(PasswordUtil.verifyPassword(password, salt, hash), "Password should verify against valid hash");
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", salt, hash), "Wrong password must be rejected");
    }

    @Test
    @Order(2)
    @DisplayName("Verify User Registration & Initial 108 Karma Grant")
    void testUserRegistration() {
        String username = "tester_" + System.currentTimeMillis();
        String email = username + "@cosmic.test";

        testUser = authService.register(username, email, "Test Petitioner", "Password@123");
        assertNotNull(testUser);
        assertTrue(testUser.getId() > 0);
        assertEquals(108, testUser.getKarmaPoints(), "Devotee should receive initial 108 Karma points");
        assertEquals(SubscriptionTier.FREE_DEVOTEE, testUser.getTier());
    }

    @Test
    @Order(3)
    @DisplayName("Verify OOP Polymorphism across Complaint Subclasses")
    void testComplaintPolymorphism() {
        Grievance g = new Grievance();
        g.setSeverity(Severity.COSMIC_CRITICAL);
        assertTrue(g.getBureaucraticClassification().contains("GRIEVANCE"));
        assertEquals(60, g.calculateKarmaWeight());

        Prayer p = new Prayer();
        p.setSeverity(Severity.HIGH);
        assertTrue(p.getBureaucraticClassification().contains("DEVOTIONAL"));
        assertEquals(35, p.calculateKarmaWeight());

        Wish w = new Wish();
        w.setLongTermWish(true);
        assertTrue(w.getBureaucraticClassification().contains("ASPIRATIONAL-ROADMAP"));
        assertEquals(50, w.calculateKarmaWeight());

        PersonalDilemma pd = new PersonalDilemma();
        pd.setSeverity(Severity.MEDIUM);
        assertTrue(pd.getBureaucraticClassification().contains("ETHICAL-TRIAGE"));
        assertEquals(50, pd.calculateKarmaWeight());
    }

    @Test
    @Order(4)
    @DisplayName("Verify Divine Grievance Submission & Automatic Department Routing")
    void testGrievanceSubmission() {
        assertNotNull(testUser);

        testComplaint = complaintService.submitComplaint(
                testUser.getId(),
                "Professor assigned unexpected quiz on Monday morning",
                "I stayed up all night and studied chapter 4, but he asked questions from chapter 9 without notice.",
                ComplaintCategory.ACADEMIC_SUFFERING,
                ComplaintType.COMPLAINT,
                Severity.HIGH,
                "Retroactive grading curvature and formal cosmic apology"
        );

        assertNotNull(testComplaint);
        assertTrue(testComplaint.getId() > 0);
        assertTrue(testComplaint.getTicketNumber().startsWith("KS-2026-"));
        assertEquals(TicketStatus.RECEIVED_BY_HEAVEN, testComplaint.getStatus());
        assertTrue(testComplaint.getKarmaPointsAwarded() > 0);
    }

    @Test
    @Order(5)
    @DisplayName("Verify Divine Response Generation & Persistence")
    void testDivineResponseGeneration() {
        assertNotNull(testComplaint);

        ComplaintResponse response = complaintService.generateAIResponse(testComplaint, 1);
        assertNotNull(response);
        assertTrue(response.getId() > 0);
        assertNotNull(response.getResponseText());
        assertFalse(response.getResponseText().isBlank());

        // Verify ticket status transition in MySQL
        Complaint reloaded = complaintService.getComplaintById(testComplaint.getId());
        assertEquals(TicketStatus.UNDER_DIVINE_REVIEW, reloaded.getStatus());

        // Verify status audit history
        List<ComplaintStatusHistory> history = complaintService.getStatusHistory(testComplaint.getId());
        assertFalse(history.isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Verify Long-Term Wish Classification & Milestone Creation")
    void testLongTermWishClassification() {
        Complaint aspirationalWish = complaintService.submitComplaint(
                testUser.getId(),
                "Founding a sustainable deep tech venture within 10 years",
                "I aspire to build a quantum computing startup and scale it across 5 continents before 2035.",
                ComplaintCategory.DESTINY_AND_LIFE_DECISIONS,
                ComplaintType.WISH,
                Severity.HIGH,
                "Favorable celestial alignments and patient capital"
        );

        assertTrue(aspirationalWish.isLongTermWish(), "Aspiration should be detected as long term");

        // Verify milestones created in MySQL
        var milestones = complaintService.getMilestones(aspirationalWish.getId());
        assertNotNull(milestones);
        assertEquals(3, milestones.size(), "Should auto-generate 3 destiny milestones");
    }

    @Test
    @Order(7)
    @DisplayName("Verify Simulated Premium Upgrade (JDBC Transaction & ACID Test)")
    void testSimulatedSubscriptionUpgrade() {
        assertNotNull(testUser);

        int karmaBefore = testUser.getKarmaPoints();
        boolean upgraded = subscriptionService.upgradeTier(testUser, SubscriptionTier.KARMA_SUTRA_PRO, "COSMIC_UPI");
        assertTrue(upgraded, "Subscription upgrade transaction should commit successfully");

        assertEquals(SubscriptionTier.KARMA_SUTRA_PRO, testUser.getTier());

        // Verify simulated payment logged
        var payments = subscriptionService.getPaymentHistory(testUser.getId());
        assertFalse(payments.isEmpty());
        assertEquals(49.0, payments.get(0).getAmount());

        // Verify onboarding karma bonus was awarded
        assertTrue(testUser.getKarmaPoints() > karmaBefore);
    }

    @Test
    @Order(8)
    @DisplayName("Verify Arch-Deity Administrative Adjudication")
    void testAdminAdjudication() {
        assertNotNull(testComplaint);

        complaintService.updateTicketByAdmin(
                testComplaint.getId(),
                TicketStatus.RESOLVED,
                1, // Department ID
                1, // Admin user ID
                "Order of the Arch-Deity: Exam curve shall be adjusted by 4.5 marks across all affected sections."
        );

        Complaint updated = complaintService.getComplaintById(testComplaint.getId());
        assertEquals(TicketStatus.RESOLVED, updated.getStatus());

        List<ComplaintResponse> responses = complaintService.getResponsesForComplaint(testComplaint.getId());
        assertTrue(responses.size() >= 2, "Should include original divine response plus administrative response");
    }
}
