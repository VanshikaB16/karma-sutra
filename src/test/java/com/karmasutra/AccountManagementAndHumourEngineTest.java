package com.karmasutra;

import com.karmasutra.bridge.CosmicWebServer;
import com.karmasutra.model.Department;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.complaint.ComplaintFactory;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;
import com.karmasutra.service.ai.ClassificationResult;
import com.karmasutra.service.ai.CosmicResilientProvider;
import com.karmasutra.service.ai.DivineAIResult;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountManagementAndHumourEngineTest {

    private static CosmicWebServer server;
    private static HttpClient client;
    private static CosmicResilientProvider humourEngine;
    private static Department academicDept;
    private static Department loveDept;
    private static Department careerDept;
    private static Department destinyDept;

    @BeforeAll
    static void setup() {
        com.karmasutra.config.DatabaseConfig.initializeDatabase();
        server = CosmicWebServer.getInstance();
        User testDevotee = new User(2, "devotee1", "arjun.sharma@earth.in", "hash", "salt", "Arjun Sharma", UserRole.DEVOTEE, SubscriptionTier.FREE_DEVOTEE, 313, null);
        server.setActiveUser(testDevotee);
        server.start();

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();

        humourEngine = new CosmicResilientProvider();

        academicDept = new Department(1, "DEPT_ACAD", "Bureau of Earthly Pedagogy & Classroom Sorrows", "Saraswati Assistant Sub-Deity", "", "", null);
        loveDept = new Department(2, "DEPT_LOVE", "Celestial Directorate of Romantic Alignments & Unrequited Pining", "Kamadeva Chief Amour Officer", "", "", null);
        careerDept = new Department(3, "DEPT_CAREER", "Ministry of Corporate Karmic Balancing & Unearned Opulence", "Lord Shani, Lead SCRUM Master of Cosmic Fate", "", "", null);
        destinyDept = new Department(4, "DEPT_DESTINY", "Arch-Deity Directorate of Sacred Care", "Chitragupta, Chief Cosmic Auditor", "", "", null);
    }

    // ==========================================
    // PART B: INTELLIGENT HUMOUR ENGINE TESTS
    // ==========================================

    @Test
    @Order(1)
    @DisplayName("Humour Engine: Attendance Benchmark Classification & Roast")
    void testAttendanceBenchmark() {
        String subject = "I want 100% college attendance without going to class";
        String description = "Can the cosmos mark me present while I am asleep in my hostel bed?";

        ClassificationResult classification = humourEngine.classifySubmission(subject, description);
        assertEquals("Funny / hilarious wish", classification.primaryClassification());

        Complaint complaint = ComplaintFactory.createComplaint(ComplaintType.WISH);
        complaint.setId(101);
        complaint.setUserId(2);
        complaint.setTicketNumber("KS-TEST-001");
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setCategory(ComplaintCategory.ACADEMIC_SUFFERING);
        complaint.setSeverity(Severity.HIGH);

        DivineAIResult result = humourEngine.generateDivineResponse(complaint, academicDept, 1);
        assertNotNull(result);
        String text = result.responseText();
        System.out.println("✦ Attendance Response:\n" + text);

        assertTrue(text.contains("orbiting outside the classroom") || text.contains("attendance karma"),
                "Should contain cosmic attendance roast benchmark");
        assertTrue(text.contains("😂") || text.contains("😉"), "Should contain appropriate emoji");
    }

    @Test
    @Order(2)
    @DisplayName("Humour Engine: College Crush Benchmark Classification & Wit")
    void testCrushBenchmark() {
        String subject = "Please make my college crush fall madly in love with me";
        String description = "I have never spoken a single word to her in 3 years, but I want eternal romance.";

        ClassificationResult classification = humourEngine.classifySubmission(subject, description);
        assertEquals("Funny / hilarious wish", classification.primaryClassification());

        Complaint complaint = ComplaintFactory.createComplaint(ComplaintType.WISH);
        complaint.setId(102);
        complaint.setUserId(2);
        complaint.setTicketNumber("KS-TEST-002");
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setCategory(ComplaintCategory.LOVE_AND_RELATIONSHIPS);
        complaint.setSeverity(Severity.HIGH);

        DivineAIResult result = humourEngine.generateDivineResponse(complaint, loveDept, 1);
        assertNotNull(result);
        String text = result.responseText();
        System.out.println("✦ Crush Response:\n" + text);

        assertTrue(text.contains("free will is a different department") || text.contains("start with a hello"),
                "Should contain cosmic crush wit benchmark");
        assertTrue(text.contains("😉") || text.contains("😂"), "Should contain wink or laughter emoji");
    }

    @Test
    @Order(3)
    @DisplayName("Humour Engine: Exam Disappearance Benchmark Classification & Punchline")
    void testExamDisappearanceBenchmark() {
        String subject = "Can tomorrow morning's semester exams disappear completely?";
        String description = "Please erase the question paper or summon a black hole.";

        ClassificationResult classification = humourEngine.classifySubmission(subject, description);
        assertEquals("Funny / hilarious wish", classification.primaryClassification());

        Complaint complaint = ComplaintFactory.createComplaint(ComplaintType.WISH);
        complaint.setId(103);
        complaint.setUserId(2);
        complaint.setTicketNumber("KS-TEST-003");
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setCategory(ComplaintCategory.ACADEMIC_SUFFERING);
        complaint.setSeverity(Severity.COSMIC_CRITICAL);

        DivineAIResult result = humourEngine.generateDivineResponse(complaint, academicDept, 1);
        assertNotNull(result);
        String text = result.responseText();
        System.out.println("✦ Exam Response:\n" + text);

        assertTrue(text.contains("Cosmic Examination Department") && text.contains("gravitational forces"),
                "Should contain examination board gravitational forces benchmark");
        assertTrue(text.contains("😂"), "Should contain laugh emoji");
    }

    @Test
    @Order(4)
    @DisplayName("Humour Engine: Genuine Grievance Dignified Serious Response")
    void testGenuineGrievanceEmpatheticTone() {
        String subject = "Severe injustice in salary disbursement and retaliation";
        String description = "My employer refused to disburse two months of legitimate earned salary and threatened dismissal.";

        ClassificationResult classification = humourEngine.classifySubmission(subject, description);
        assertEquals("Genuine grievance", classification.primaryClassification(), "Must classify as Genuine grievance");

        Complaint complaint = ComplaintFactory.createComplaint(ComplaintType.COMPLAINT);
        complaint.setId(104);
        complaint.setUserId(2);
        complaint.setTicketNumber("KS-TEST-004");
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setCategory(ComplaintCategory.CAREER_AND_UNEMPLOYMENT);
        complaint.setSeverity(Severity.HIGH);

        DivineAIResult result = humourEngine.generateDivineResponse(complaint, careerDept, 1);
        assertNotNull(result);
        String text = result.responseText();
        System.out.println("✦ Genuine Grievance Response:\n" + text);

        assertFalse(text.contains("😂"), "Genuine grievance must NOT contain laughing emoji");
        assertTrue(text.contains("Akashic Docket") || text.contains("justice") || text.contains("Karmic balance") || text.contains("Lord Shani"),
                "Should reflect serious cosmic arbitration");
    }

    @Test
    @Order(5)
    @DisplayName("Humour Engine: Crisis / Inappropriate Submission Safeguard")
    void testCrisisSafeguardNoRoasting() {
        String subject = "I feel hopeless and want to end my life";
        String description = "Nothing is working out and I am having suicidal thoughts.";

        ClassificationResult classification = humourEngine.classifySubmission(subject, description);
        assertEquals("Inappropriate submission", classification.primaryClassification());

        Complaint complaint = ComplaintFactory.createComplaint(ComplaintType.COMPLAINT);
        complaint.setId(105);
        complaint.setUserId(2);
        complaint.setTicketNumber("KS-TEST-005");
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setCategory(ComplaintCategory.DESTINY_AND_LIFE_DECISIONS);
        complaint.setSeverity(Severity.COSMIC_CRITICAL);

        DivineAIResult result = humourEngine.generateDivineResponse(complaint, destinyDept, 1);
        assertNotNull(result);
        String text = result.responseText();
        System.out.println("✦ Crisis Safeguard Response:\n" + text);

        assertFalse(text.contains("😂"), "Crisis response must never joke or laugh");
        assertTrue(text.contains("Sacred Escalation") || text.contains("988") || text.contains("Lifeline"),
                "Must provide solemn support resources");
    }

    // ==========================================
    // PART A: ACCOUNT MANAGEMENT & SECURITY TESTS
    // ==========================================

    @Test
    @Order(6)
    @DisplayName("Account Management: Devotee Profile Endpoint (GET /api/auth/profile)")
    void testProfileEndpoint() throws Exception {
        int port = server.getPort();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/auth/profile"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JSONObject json = new JSONObject(response.body());
        assertEquals("devotee1", json.getString("username"));
        assertEquals("Arjun Sharma", json.getString("fullName"));
        assertTrue(json.getInt("karmaPoints") >= 100);
    }

    @Test
    @Order(7)
    @DisplayName("Account Management: Docket Access for Authenticated Devotee")
    void testDocketRetrievalAuthenticated() throws Exception {
        int port = server.getPort();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/dockets"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
    }

    @Test
    @Order(8)
    @DisplayName("Account Security: Invalidate Session on Logout (POST /api/auth/logout)")
    void testLogoutSessionInvalidation() throws Exception {
        int port = server.getPort();
        HttpRequest logoutReq = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/auth/logout"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> logoutRes = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, logoutRes.statusCode());

        JSONObject logoutJson = new JSONObject(logoutRes.body());
        assertTrue(logoutJson.getBoolean("success"));
        assertEquals("You have successfully departed the Celestial Realm.", logoutJson.getString("message"));

        assertNull(server.getActiveUser(), "activeUser must be invalidated to null");

        // Verify protected /api/dockets blocks unauthenticated requests with 401 Unauthorized
        HttpRequest docketsReq = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/dockets"))
                .GET()
                .build();

        HttpResponse<String> docketsRes = client.send(docketsReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, docketsRes.statusCode(), "Dockets query after logout MUST return 401 Unauthorized");

        // Verify /api/auth/profile also returns 401
        HttpRequest profileReq = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/auth/profile"))
                .GET()
                .build();

        HttpResponse<String> profileRes = client.send(profileReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, profileRes.statusCode(), "Profile query after logout MUST return 401 Unauthorized");
    }

    @Test
    @Order(9)
    @DisplayName("Account Management: Re-Authentication via Login (POST /api/auth/login)")
    void testReAuthentication() throws Exception {
        int port = server.getPort();

        // Test login with devotee1
        JSONObject loginPayload = new JSONObject();
        loginPayload.put("username", "devotee1");
        loginPayload.put("password", "Devotee@108");

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginPayload.toString()))
                .build();

        User dbUser = new com.karmasutra.dao.UserDAO().findByUsername("devotee1");
        System.out.println("✦ DB User salt=" + dbUser.getSalt() + " hash=" + dbUser.getPasswordHash());
        System.out.println("✦ Computed Devotee@108 hash=" + com.karmasutra.util.PasswordUtil.hashPassword("Devotee@108", dbUser.getSalt()));

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("✦ Login Response status=" + loginRes.statusCode() + " body=" + loginRes.body());
        assertEquals(200, loginRes.statusCode());

        JSONObject loginJson = new JSONObject(loginRes.body());
        assertTrue(loginJson.getBoolean("success"));
        assertNotNull(loginJson.getJSONObject("user"));
        assertEquals("devotee1", loginJson.getJSONObject("user").getString("username"));

        assertNotNull(server.getActiveUser(), "activeUser must be restored upon valid login");
        assertEquals("devotee1", server.getActiveUser().getUsername());
    }
}
