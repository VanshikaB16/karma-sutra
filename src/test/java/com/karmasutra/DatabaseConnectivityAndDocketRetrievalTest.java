package com.karmasutra;

import com.karmasutra.bridge.CosmicWebServer;
import com.karmasutra.config.AppConfig;
import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.dao.ComplaintDAO;
import com.karmasutra.dao.DepartmentDAO;
import com.karmasutra.dao.ResponseDAO;
import com.karmasutra.dao.UserDAO;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.Department;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.service.ComplaintService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseConnectivityAndDocketRetrievalTest {

    private static User testDevotee;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
        boolean ok = DatabaseConfig.initializeDatabase();
        assertTrue(ok, "Database initialization must succeed");

        UserDAO udao = new UserDAO();
        testDevotee = udao.findByUsername("devotee1");
        if (testDevotee == null) {
            testDevotee = udao.findById(2);
        }
        assertNotNull(testDevotee, "Devotee with complaints must exist");
    }

    @Test
    @Order(1)
    @DisplayName("1. Verify MySQL JDBC Connection & Metadata Parameters")
    void testJdbcConnectionParameters() throws Exception {
        System.out.println("JDBC URL: " + DatabaseConfig.getJdbcUrl());
        System.out.println("DB User: " + DatabaseConfig.getDbUser());

        assertTrue(DatabaseConfig.testConnection(), "DatabaseConfig.testConnection() probe must return true");

        try (Connection conn = DatabaseConfig.getConnection()) {
            assertNotNull(conn, "Connection must not be null");
            assertFalse(conn.isClosed(), "Connection must be active");
            assertTrue(conn.isValid(3), "Connection must validate within 3 seconds");

            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Connected to: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());
            assertEquals("MySQL", meta.getDatabaseProductName());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Verify Independent Complaints Table Query Execution")
    void testIndependentComplaintsQuery() throws Exception {
        String sql = "SELECT COUNT(*) FROM complaints WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testDevotee.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Result set must have a count row");
                int count = rs.getInt(1);
                System.out.println("Total complaints for devotee (ID " + testDevotee.getId() + "): " + count);
                assertTrue(count > 0, "Devotee must have at least 1 complaint in the database");
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("3. Verify ComplaintService & DAO Ticket Retrieval")
    void testComplaintServiceRetrieval() {
        ComplaintService complaintService = new ComplaintService();
        List<Complaint> complaints = complaintService.getComplaintsForUser(testDevotee.getId(), null, null, null);

        assertNotNull(complaints, "Complaints list must not be null");
        assertFalse(complaints.isEmpty(), "Complaints list must not be empty");
        System.out.println("Retrieved " + complaints.size() + " complaints for devotee " + testDevotee.getUsername());

        for (Complaint c : complaints) {
            assertNotNull(c.getTicketNumber(), "Ticket number must not be null");
            assertTrue(c.getTicketNumber().startsWith("KS-2026-"), "Ticket number format valid");
            System.out.println(" - " + c.getTicketNumber() + " | " + c.getSubject() + " | " + c.getCategory().getLabel());
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. Verify Divine AI Decrees & Responses Associated with Tickets")
    void testResponseRetrieval() {
        ResponseDAO responseDAO = new ResponseDAO();
        ComplaintService complaintService = new ComplaintService();
        List<Complaint> complaints = complaintService.getComplaintsForUser(testDevotee.getId(), null, null, null);

        boolean foundAtLeastOneDecree = false;
        for (Complaint c : complaints) {
            ComplaintResponse r = responseDAO.getLatestByComplaintId(c.getId());
            if (r != null) {
                foundAtLeastOneDecree = true;
                System.out.println("Ticket " + c.getTicketNumber() + " has decree signed by: " + r.getDivinePersona());
                assertNotNull(r.getResponseText());
                assertFalse(r.getResponseText().isBlank());
            }
        }
        assertTrue(foundAtLeastOneDecree, "At least one ticket must have a signed divine response decree");
    }

    @Test
    @Order(5)
    @DisplayName("5. Verify Cosmic Web Server /api/dockets & /api/departments Endpoints")
    void testCosmicWebServerApiEndpoints() throws Exception {
        CosmicWebServer server = CosmicWebServer.getInstance();
        server.setActiveUser(testDevotee);
        server.start();

        int port = server.getPort();
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        // Test 1: /api/dockets
        HttpRequest reqDockets = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/dockets"))
                .GET()
                .build();

        HttpResponse<String> respDockets = client.send(reqDockets, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respDockets.statusCode());
        assertNotNull(respDockets.body());

        JSONArray docketsArr = new JSONArray(respDockets.body());
        System.out.println("HTTP /api/dockets returned " + docketsArr.length() + " items");
        assertTrue(docketsArr.length() > 0, "/api/dockets must return non-empty array of dockets");

        JSONObject first = docketsArr.getJSONObject(0);
        assertTrue(first.has("ticketNumber"));
        assertTrue(first.has("subject"));
        assertTrue(first.has("planetId"));
        System.out.println("First ticket: " + first.getString("ticketNumber") + " -> Planet: " + first.getString("planetId"));

        // Test 2: /api/departments
        HttpRequest reqDepts = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/departments"))
                .GET()
                .build();

        HttpResponse<String> respDepts = client.send(reqDepts, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respDepts.statusCode());

        JSONArray deptsArr = new JSONArray(respDepts.body());
        System.out.println("HTTP /api/departments returned " + deptsArr.length() + " departments");
        assertTrue(deptsArr.length() >= 7, "/api/departments must return at least 7 divine bureaus");
    }
}
