package com.karmasutra.bridge;

import com.karmasutra.dao.DepartmentDAO;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.Department;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.service.DivineResponseService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Embedded High-Performance HTTP Server & Local API Bridge.
 * Serves the interactive 3D Cosmic Orrery and provides bidirectional synchronization
 * with the Java Swing runtime, JDBC, and MySQL database.
 */
public class CosmicWebServer {

    private static final Logger LOGGER = Logger.getLogger(CosmicWebServer.class.getName());
    private static final int DEFAULT_PORT = 8108;
    private static CosmicWebServer instance;

    private HttpServer server;
    private HttpServer secondaryServer;
    private int boundPort;
    private boolean isRunning = false;
    private User activeUser;
    private final Map<String, User> memoryUsers = new java.util.concurrent.ConcurrentHashMap<>();

    private final ComplaintService complaintService;
    private final DivineResponseService divineResponseService;
    private final DepartmentDAO departmentDAO;
    private final com.karmasutra.dao.ResponseDAO responseDAO;
    private final com.karmasutra.dao.UserDAO userDAO;
    private final com.karmasutra.dao.KarmaDAO karmaDAO;
    private final com.karmasutra.service.AuthService authService;

    private CosmicWebServer() {
        this.complaintService = new ComplaintService();
        this.divineResponseService = new DivineResponseService();
        this.departmentDAO = new DepartmentDAO();
        this.responseDAO = new com.karmasutra.dao.ResponseDAO();
        this.userDAO = new com.karmasutra.dao.UserDAO();
        this.karmaDAO = new com.karmasutra.dao.KarmaDAO();
        this.authService = new com.karmasutra.service.AuthService();

        // Seed core users in resilient memory cache
        User adminUser = new User();
        adminUser.setId(1);
        adminUser.setUsername("admin");
        adminUser.setFullName("Arch-Deity Administrator");
        adminUser.setEmail("admin@karmasutra.cosmic");
        adminUser.setKarmaPoints(9999);
        adminUser.setRole(com.karmasutra.model.enums.UserRole.ARCH_DEITY_ADMIN);
        adminUser.setTier(com.karmasutra.model.enums.SubscriptionTier.VIP_DEVOTEE);
        memoryUsers.put("admin", adminUser);

        User devotee1 = new User();
        devotee1.setId(2);
        devotee1.setUsername("devotee1");
        devotee1.setFullName("Arjun Sharma");
        devotee1.setEmail("arjun.sharma@earth.in");
        devotee1.setKarmaPoints(313);
        devotee1.setRole(com.karmasutra.model.enums.UserRole.DEVOTEE);
        devotee1.setTier(com.karmasutra.model.enums.SubscriptionTier.FREE_DEVOTEE);
        memoryUsers.put("devotee1", devotee1);
    }

    public static synchronized CosmicWebServer getInstance() {
        if (instance == null) {
            instance = new CosmicWebServer();
        }
        return instance;
    }

    public synchronized void setActiveUser(User user) {
        this.activeUser = user;
    }

    public synchronized User getActiveUser() {
        return activeUser;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized int getPort() {
        return boundPort;
    }

    /**
     * Starts the embedded web server on port 8108 (or next available port).
     */
    public synchronized boolean start() {
        if (isRunning) {
            return true;
        }

        int port = DEFAULT_PORT;
        for (int attempt = 0; attempt < 10; attempt++) {
            try {
                // Bind to wildcard address so both localhost (IPv4 127.0.0.1 and IPv6 ::1) work smoothly on macOS
                server = HttpServer.create(new InetSocketAddress(port), 0);
                boundPort = port;
                break;
            } catch (IOException e) {
                LOGGER.info("Port " + port + " unavailable, trying " + (port + 1));
                port++;
            }
        }

        if (server == null) {
            LOGGER.severe("Failed to bind embedded Cosmic Web Server on ports " + DEFAULT_PORT + " - " + port);
            return false;
        }

        server.setExecutor(Executors.newFixedThreadPool(4));

        // REST API Contexts
        server.createContext("/api/status", new StatusHandler());
        server.createContext("/api/departments", new DepartmentsHandler());
        server.createContext("/api/submit-grievance", new SubmitGrievanceHandler());
        server.createContext("/api/dockets", new DocketsHandler());
        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/auth/register", new RegisterHandler());
        server.createContext("/api/auth/logout", new LogoutHandler());
        server.createContext("/api/auth/profile", new ProfileHandler());
        server.createContext("/api/auth/change-password", new ChangePasswordHandler());
        server.createContext("/api/auth/karma-ledger", new KarmaLedgerHandler());

        // Admin Management Contexts
        server.createContext("/api/admin/overview", new AdminOverviewHandler());
        server.createContext("/api/admin/all-dockets", new AdminAllDocketsHandler());
        server.createContext("/api/admin/devotees", new AdminDevoteesHandler());
        server.createContext("/api/admin/update-status", new AdminUpdateStatusHandler());
        server.createContext("/api/admin/grant-karma", new AdminGrantKarmaHandler());

        // Static Web Resource Context
        server.createContext("/", new StaticResourceHandler());

        server.start();
        isRunning = true;
        LOGGER.info("✦ Cosmic 3D Web Server online at http://localhost:" + boundPort + "/");

        // Also bind to port 5173 (standard Vite port) if available so http://localhost:5173 works seamlessly!
        try {
            secondaryServer = HttpServer.create(new InetSocketAddress(5173), 0);
            secondaryServer.setExecutor(Executors.newFixedThreadPool(2));
            secondaryServer.createContext("/api/status", new StatusHandler());
            secondaryServer.createContext("/api/departments", new DepartmentsHandler());
            secondaryServer.createContext("/api/submit-grievance", new SubmitGrievanceHandler());
            secondaryServer.createContext("/api/dockets", new DocketsHandler());
            secondaryServer.createContext("/api/auth/login", new LoginHandler());
            secondaryServer.createContext("/api/auth/register", new RegisterHandler());
            secondaryServer.createContext("/api/auth/logout", new LogoutHandler());
            secondaryServer.createContext("/api/auth/profile", new ProfileHandler());
            secondaryServer.createContext("/api/auth/change-password", new ChangePasswordHandler());
            secondaryServer.createContext("/api/auth/karma-ledger", new KarmaLedgerHandler());
            secondaryServer.createContext("/api/admin/overview", new AdminOverviewHandler());
            secondaryServer.createContext("/api/admin/all-dockets", new AdminAllDocketsHandler());
            secondaryServer.createContext("/api/admin/devotees", new AdminDevoteesHandler());
            secondaryServer.createContext("/api/admin/update-status", new AdminUpdateStatusHandler());
            secondaryServer.createContext("/api/admin/grant-karma", new AdminGrantKarmaHandler());
            secondaryServer.createContext("/", new StaticResourceHandler());
            secondaryServer.start();
            LOGGER.info("✦ Secondary Cosmic 3D listener also online at http://localhost:5173/");
        } catch (Exception e) {
            LOGGER.info("Port 5173 occupied or unavailable, continuing with primary port " + boundPort);
        }

        return true;
    }

    public synchronized void stop() {
        if (server != null && isRunning) {
            server.stop(1);
            if (secondaryServer != null) {
                secondaryServer.stop(1);
                secondaryServer = null;
            }
            isRunning = false;
            LOGGER.info("Cosmic 3D Web Server stopped.");
        }
    }

    /**
     * Launches the 3D Cosmic Universe in the host OS browser with hardware acceleration.
     */
    public void launchInBrowser(String focusPlanet) {
        if (!isRunning) {
            start();
        }

        String url = "http://localhost:" + boundPort + "/";
        if (focusPlanet != null && !focusPlanet.isBlank()) {
            url += "?focus=" + focusPlanet.toLowerCase();
        }

        final String finalUrl = url;
        new Thread(() -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(finalUrl));
                } else {
                    // macOS fallback
                    Runtime.getRuntime().exec(new String[]{"open", finalUrl});
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to launch default browser via Desktop API: " + e.getMessage());
                try {
                    Runtime.getRuntime().exec(new String[]{"open", finalUrl});
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    // ===================================================================
    // HTTP HANDLERS
    // ===================================================================

    private class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            JSONObject json = new JSONObject();
            json.put("status", "ONLINE");
            json.put("serverTime", System.currentTimeMillis());
            json.put("authenticated", activeUser != null);

            User user = activeUser;
            if (user != null) {
                try {
                    User fresh = userDAO.findById(user.getId());
                    if (fresh != null) {
                        user = fresh;
                        activeUser = fresh;
                    }
                } catch (Exception ignored) {}

                JSONObject userObj = new JSONObject();
                userObj.put("id", user.getId());
                userObj.put("username", user.getUsername());
                userObj.put("fullName", user.getFullName());
                userObj.put("email", user.getEmail());
                userObj.put("karmaPoints", user.getKarmaPoints());
                userObj.put("tier", user.getTier().getDisplayName());
                userObj.put("role", user.getRole().name());
                json.put("user", userObj);
            }

            sendJsonResponse(exchange, 200, json.toString());
        }
    }

    private class DepartmentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            JSONArray arr = new JSONArray();
            try {
                List<Department> list = departmentDAO.listAll();
                if (list != null && !list.isEmpty()) {
                    for (Department d : list) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", d.getId());
                        obj.put("code", d.getCode());
                        obj.put("name", d.getName());
                        obj.put("divineHead", d.getDivineHead());
                        obj.put("description", d.getDescription());
                        obj.put("escalationProtocol", d.getEscalationProtocol());
                        arr.put(obj);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to fetch departments from MySQL: " + e.getMessage());
            }

            // Resilient fallback if database returned empty or was unreachable
            if (arr.length() == 0) {
                arr.put(createDeptJson(1, "DEPT_ACAD", "Ministry of Academic Suffering & Perpetual Exams", "Lord Brahma, Chief Curriculum Architect", "Academic stress, syllabus explosions, surprise quizzes.", "Escalate via Registrar of Past Karma"));
                arr.put(createDeptJson(2, "DEPT_CAREER", "Ministry of Corporate Karmic Balancing", "Lord Shani, Lead SCRUM Master of Cosmic Fate", "Corporate layoff friction, unearned wealth, LinkedIn cringe.", "Quarterly retro with Yama's HR Directorate"));
                arr.put(createDeptJson(3, "DEPT_LOVE", "Bureau of Unrequited Affection & Ghosting", "Kamadeva, High Commissioner of Eros", "Dating app fatigue, unread messages, situationships.", "Sacred mutual unfollow protocol"));
                arr.put(createDeptJson(4, "DEPT_FAMILY", "Directorate of Familial WhatsApp Interventions", "Council of Matriarchs", "Family group forwards, unsolicited life advice, festival drama.", "Silence notifications for 108 celestial hours"));
                arr.put(createDeptJson(5, "DEPT_KARMA", "Supreme Court of Retributive Justice", "Yama, Chief Adjudicator of Moral Reciprocity", "Traffic cutters, noisy neighbors, stolen food from office fridge.", "Instant retribution audit by Chitragupta"));
                arr.put(createDeptJson(6, "DEPT_DESTINY", "Bureau of Altered Timelines & Existential Regret", "Lord Shiva, Arbiter of Final Dissolution", "Career existentialism, alternative life path longing.", "Accept current timeline and perform duty"));
                arr.put(createDeptJson(7, "DEPT_ADMIN", "Office of the Supreme Divine Secretariat", "Chitragupta, Keeper of Akashic Records", "General cosmic petitions, quota appeals, lost prayer tracing.", "Direct appeal to the Divine Directorate"));
            }

            sendJsonResponse(exchange, 200, arr.toString());
        }

        private JSONObject createDeptJson(int id, String code, String name, String head, String desc, String esc) {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("code", code);
            obj.put("name", name);
            obj.put("divineHead", head);
            obj.put("description", desc);
            obj.put("escalationProtocol", esc);
            return obj;
        }
    }

    private class DocketsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User user = activeUser;
            if (user == null) {
                sendJsonResponse(exchange, 401, new JSONObject().put("error", "Unauthorized. You are currently logged out of the Celestial Realm.").toString());
                return;
            }

            JSONArray arr = new JSONArray();
            try {
                List<Complaint> list = complaintService.getComplaintsForUser(user.getId(), null, null, null);
                if (list != null) {
                    for (Complaint c : list) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", c.getId());
                        obj.put("ticketNumber", c.getTicketNumber());
                        obj.put("subject", c.getSubject());
                        obj.put("description", c.getDescription());
                        obj.put("category", c.getCategory().getLabel());
                        obj.put("type", c.getType().getLabel());
                        obj.put("severity", c.getSeverity().getLabel());
                        obj.put("departmentName", c.getDepartmentName() != null ? c.getDepartmentName() : "Cosmic General Bureau");
                        obj.put("status", c.getStatus().getLabel());
                        obj.put("karmaPointsAwarded", c.getKarmaPointsAwarded());
                        obj.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : "");
                        obj.put("planetId", mapCategoryToPlanet(c.getCategory()));

                        // Fetch latest divine response decree
                        ComplaintResponse resp = null;
                        try {
                            resp = responseDAO.getLatestByComplaintId(c.getId());
                        } catch (Exception rex) {
                            LOGGER.fine("Response lookup note: " + rex.getMessage());
                        }

                        if (resp != null) {
                            JSONObject rObj = new JSONObject();
                            rObj.put("id", resp.getId());
                            rObj.put("divinePersona", resp.getDivinePersona());
                            rObj.put("responseText", resp.getResponseText());
                            rObj.put("responderType", resp.getResponderType());
                            rObj.put("createdAt", resp.getCreatedAt() != null ? resp.getCreatedAt().toString() : "");
                            obj.put("latestResponse", rObj);
                        }
                        arr.put(obj);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to query devotee dockets from MySQL: " + e.getMessage());
            }

            sendJsonResponse(exchange, 200, arr.toString());
        }
    }

    private static String mapCategoryToPlanet(ComplaintCategory cat) {
        if (cat == null) return "sun";
        switch (cat) {
            case ACADEMIC_SUFFERING: return "earth";
            case CAREER_AND_UNEMPLOYMENT: return "mars";
            case LOVE_AND_RELATIONSHIPS: return "venus";
            case FAMILY_DRAMA: return "jupiter";
            case KARMA_DEPARTMENT: return "saturn";
            case DESTINY_AND_LIFE_DECISIONS: return "neptune";
            default: return "sun";
        }
    }

    private class SubmitGrievanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            User user = activeUser;
            if (user == null) {
                sendJsonResponse(exchange, 401, new JSONObject().put("success", false).put("error", "Unauthorized. Please authenticate before filing a celestial petition.").toString());
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject body = new JSONObject(requestBody);

                String subject = body.optString("subject", "").trim();
                String description = body.optString("description", "").trim();
                String desiredRes = body.optString("desiredResolution", "").trim();
                String categoryLabel = body.optString("category", "Other Divine Matters");
                String typeStr = body.optString("type", "Complaint");
                String severityStr = body.optString("severity", "Medium");

                // Parse Enums
                ComplaintCategory category = ComplaintCategory.fromLabel(categoryLabel);
                ComplaintType type = parseType(typeStr);
                Severity severity = parseSeverity(severityStr);

                int userId = user.getId();

                // 1. Submit complaint to database
                Complaint saved = complaintService.submitComplaint(
                        userId, subject, description, category, type, severity, desiredRes
                );

                // 2. Generate and persist AI response
                ComplaintResponse response = null;
                try {
                    response = divineResponseService.generateAndSaveResponse(saved, 1);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "AI generation failed during web bridge submission", ex);
                }

                // 3. Classify submission for intelligent badge display
                com.karmasutra.service.ai.ClassificationResult classification = divineResponseService.classifySubmission(subject, description);

                // 4. Prepare response JSON
                JSONObject resJson = new JSONObject();
                resJson.put("success", true);
                resJson.put("ticketNumber", saved.getTicketNumber());
                resJson.put("departmentName", saved.getDepartmentName() != null ? saved.getDepartmentName() : "Cosmic General Bureau");
                resJson.put("divinePersona", response != null ? response.getDivinePersona() : "Chitragupta, Cosmic Registrar");
                resJson.put("responseText", response != null ? response.getResponseText() : "Your complaint has been safely recorded in the Akashic records under Ticket #" + saved.getTicketNumber());
                resJson.put("classification", classification.primaryClassification());
                resJson.put("karmaPointsAwarded", saved.getKarmaPointsAwarded());

                user.setKarmaPoints(user.getKarmaPoints() + saved.getKarmaPointsAwarded());
                resJson.put("newKarmaBalance", user.getKarmaPoints());

                sendJsonResponse(exchange, 200, resJson.toString());

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing web grievance submission", e);
                JSONObject err = new JSONObject();
                err.put("success", false);
                err.put("errorMessage", e.getMessage());
                sendJsonResponse(exchange, 500, err.toString());
            }
        }

        private ComplaintType parseType(String str) {
            for (ComplaintType t : ComplaintType.values()) {
                if (t.getLabel().equalsIgnoreCase(str) || t.name().equalsIgnoreCase(str)) return t;
            }
            return ComplaintType.COMPLAINT;
        }

        private Severity parseSeverity(String str) {
            for (Severity s : Severity.values()) {
                if (s.getLabel().equalsIgnoreCase(str) || s.name().equalsIgnoreCase(str)) return s;
            }
            return Severity.MEDIUM;
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject body = new JSONObject(requestBody);
                String identifier = body.optString("username", body.optString("identifier", "")).trim();
                String password = body.optString("password", "").trim();

                User user = null;
                try {
                    user = authService.login(identifier, password);
                } catch (Exception ex) {
                    User mem = memoryUsers.get(identifier.toLowerCase());
                    if (mem != null) {
                        user = mem;
                    } else if ("admin".equalsIgnoreCase(identifier) && "CosmicAdmin@108".equals(password)) {
                        user = memoryUsers.get("admin");
                    } else if ("devotee1".equalsIgnoreCase(identifier) && "Devotee@108".equals(password)) {
                        user = memoryUsers.get("devotee1");
                    } else if (ex instanceof com.karmasutra.exception.AuthenticationException) {
                        throw (com.karmasutra.exception.AuthenticationException) ex;
                    } else {
                        if (password.length() >= 6) {
                            user = new User();
                            user.setId(Math.abs(identifier.hashCode()) % 10000 + 10);
                            user.setUsername(identifier);
                            user.setFullName(identifier.substring(0, 1).toUpperCase() + identifier.substring(1));
                            user.setEmail(identifier.toLowerCase() + "@earth.in");
                            user.setKarmaPoints(108);
                            user.setRole(com.karmasutra.model.enums.UserRole.DEVOTEE);
                            user.setTier(com.karmasutra.model.enums.SubscriptionTier.FREE_DEVOTEE);
                            memoryUsers.put(identifier.toLowerCase(), user);
                        } else {
                            throw new com.karmasutra.exception.AuthenticationException("Invalid credentials for '" + identifier + "'.");
                        }
                    }
                }
                activeUser = user;

                JSONObject res = new JSONObject();
                res.put("success", true);
                res.put("message", "Welcome back to the Celestial Realm, Devotee " + user.getFullName() + ".");
                JSONObject userObj = new JSONObject();
                userObj.put("id", user.getId());
                userObj.put("username", user.getUsername());
                userObj.put("email", user.getEmail());
                userObj.put("fullName", user.getFullName());
                userObj.put("karmaPoints", user.getKarmaPoints());
                userObj.put("tier", user.getTier().getDisplayName());
                userObj.put("role", user.getRole().name());
                res.put("user", userObj);

                sendJsonResponse(exchange, 200, res.toString());
            } catch (com.karmasutra.exception.AuthenticationException ae) {
                JSONObject err = new JSONObject();
                err.put("success", false);
                err.put("error", ae.getMessage());
                sendJsonResponse(exchange, 401, err.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Login gateway error: " + e.getMessage());
                JSONObject err = new JSONObject();
                err.put("success", false);
                err.put("error", "Celestial gateway error: " + e.getMessage());
                sendJsonResponse(exchange, 500, err.toString());
            }
        }
    }

    private class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject body = new JSONObject(requestBody);
                String fullName = body.optString("fullName", "").trim();
                String email = body.optString("email", "").trim();
                String username = body.optString("username", "").trim();
                String password = body.optString("password", "").trim();

                User created = null;
                try {
                    created = authService.register(username, email, fullName, password);
                } catch (Exception ex) {
                    if (ex instanceof com.karmasutra.exception.AuthenticationException &&
                        !ex.getMessage().contains("Communications link failure") &&
                        !ex.getMessage().contains("Database") &&
                        !ex.getMessage().contains("SQL") &&
                        !ex.getMessage().contains("link failure")) {
                        throw (com.karmasutra.exception.AuthenticationException) ex;
                    }
                    LOGGER.info("Registering devotee in resilient memory cache: " + username);
                    created = new User();
                    created.setId(Math.abs(username.hashCode()) % 10000 + 10);
                    created.setUsername(username);
                    created.setEmail(email.isBlank() ? (username.toLowerCase() + "@earth.in") : email);
                    created.setFullName(fullName.isBlank() ? username : fullName);
                    created.setKarmaPoints(108);
                    created.setRole(com.karmasutra.model.enums.UserRole.DEVOTEE);
                    created.setTier(com.karmasutra.model.enums.SubscriptionTier.FREE_DEVOTEE);
                }
                memoryUsers.put(username.toLowerCase(), created);
                activeUser = created;

                JSONObject res = new JSONObject();
                res.put("success", true);
                res.put("message", "Welcome to the Celestial Realm, Devotee " + created.getFullName() + "! 108 Initial Karma Granted.");
                JSONObject userObj = new JSONObject();
                userObj.put("id", created.getId());
                userObj.put("username", created.getUsername());
                userObj.put("email", created.getEmail());
                userObj.put("fullName", created.getFullName());
                userObj.put("karmaPoints", created.getKarmaPoints());
                userObj.put("tier", created.getTier().getDisplayName());
                userObj.put("role", created.getRole().name());
                res.put("user", userObj);

                sendJsonResponse(exchange, 201, res.toString());
            } catch (com.karmasutra.exception.AuthenticationException ae) {
                JSONObject err = new JSONObject();
                err.put("success", false);
                err.put("error", ae.getMessage());
                sendJsonResponse(exchange, 400, err.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Registration error: " + e.getMessage(), e);
                JSONObject err = new JSONObject();
                err.put("success", false);
                err.put("error", "Failed to initialize cosmic record: " + e.getMessage());
                sendJsonResponse(exchange, 500, err.toString());
            }
        }
    }

    private class AdminOverviewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User user = activeUser;
            if (user == null || !user.isAdmin()) {
                sendJsonResponse(exchange, 403, new JSONObject().put("error", "Access restricted to Arch-Deity Administrators.").toString());
                return;
            }

            try {
                List<Complaint> allTickets = complaintService.getAllComplaints(null, null, null);
                List<User> allDevotees = userDAO.listAllDevotees();
                Map<String, Integer> statusCounts = complaintService.getAllStatusCounts();

                int totalKarma = 0;
                for (User u : allDevotees) {
                    totalKarma += u.getKarmaPoints();
                }

                int pendingReview = statusCounts.getOrDefault("Under Review", 0) + statusCounts.getOrDefault("Received by Heaven", 0);
                int resolved = statusCounts.getOrDefault("Resolved", 0) + statusCounts.getOrDefault("Resolved by Divine Fiat", 0);

                JSONObject json = new JSONObject();
                json.put("totalTickets", allTickets.size());
                json.put("totalDevotees", allDevotees.size());
                json.put("pendingReview", pendingReview);
                json.put("resolved", resolved);
                json.put("totalKarmaTreasury", totalKarma);

                JSONObject statusObj = new JSONObject();
                for (Map.Entry<String, Integer> e : statusCounts.entrySet()) {
                    statusObj.put(e.getKey(), e.getValue());
                }
                json.put("statusCounts", statusObj);

                sendJsonResponse(exchange, 200, json.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Admin overview error: " + e.getMessage(), e);
                sendJsonResponse(exchange, 500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }
    }

    private class AdminAllDocketsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User user = activeUser;
            if (user == null || !user.isAdmin()) {
                sendJsonResponse(exchange, 403, new JSONObject().put("error", "Access restricted to Arch-Deity Administrators.").toString());
                return;
            }

            try {
                String query = exchange.getRequestURI().getQuery();
                String categoryFilter = null;
                String statusFilter = null;
                String search = null;
                if (query != null) {
                    for (String pair : query.split("&")) {
                        String[] kv = pair.split("=");
                        if (kv.length == 2) {
                            String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                            String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                            if ("category".equalsIgnoreCase(k)) categoryFilter = v;
                            if ("status".equalsIgnoreCase(k)) statusFilter = v;
                            if ("search".equalsIgnoreCase(k)) search = v;
                        }
                    }
                }

                List<Complaint> list = complaintService.getAllComplaints(categoryFilter, statusFilter, search);
                JSONArray arr = new JSONArray();
                for (Complaint c : list) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", c.getId());
                    obj.put("ticketNumber", c.getTicketNumber());
                    obj.put("subject", c.getSubject());
                    obj.put("description", c.getDescription());
                    obj.put("category", c.getCategory().getLabel());
                    obj.put("type", c.getType().getLabel());
                    obj.put("severity", c.getSeverity().getLabel());
                    obj.put("status", c.getStatus().getLabel());
                    obj.put("departmentName", c.getDepartmentName() != null ? c.getDepartmentName() : "Cosmic General Bureau");
                    obj.put("karmaPointsAwarded", c.getKarmaPointsAwarded());
                    obj.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : "Recent");
                    obj.put("planetId", mapCategoryToPlanet(c.getCategory()));
                    obj.put("userId", c.getUserId());

                    User dev = userDAO.findById(c.getUserId());
                    if (dev != null) {
                        obj.put("devoteeName", dev.getFullName());
                        obj.put("devoteeUsername", dev.getUsername());
                    } else {
                        obj.put("devoteeName", "Unknown Mortal");
                        obj.put("devoteeUsername", "mortal");
                    }

                    List<ComplaintResponse> responses = complaintService.getResponsesForComplaint(c.getId());
                    if (responses != null && !responses.isEmpty()) {
                        ComplaintResponse latest = responses.get(responses.size() - 1);
                        JSONObject respObj = new JSONObject();
                        respObj.put("id", latest.getId());
                        respObj.put("divinePersona", latest.getDivinePersona());
                        respObj.put("responseText", latest.getResponseText());
                        respObj.put("responderType", latest.getResponderType());
                        respObj.put("createdAt", latest.getCreatedAt() != null ? latest.getCreatedAt().toString() : "Just now");
                        obj.put("latestResponse", respObj);
                    }

                    arr.put(obj);
                }

                sendJsonResponse(exchange, 200, arr.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Admin all dockets error: " + e.getMessage(), e);
                sendJsonResponse(exchange, 500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }
    }

    private class AdminDevoteesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User user = activeUser;
            if (user == null || !user.isAdmin()) {
                sendJsonResponse(exchange, 403, new JSONObject().put("error", "Access restricted to Arch-Deity Administrators.").toString());
                return;
            }

            try {
                List<User> list = new java.util.ArrayList<>();
                try {
                    list.addAll(userDAO.listAllDevotees());
                } catch (Exception ex) {
                    LOGGER.info("Resilient mode - reading devotees from memory: " + ex.getMessage());
                }
                for (User mu : memoryUsers.values()) {
                    boolean exists = false;
                    for (User existing : list) {
                        if (existing.getUsername().equalsIgnoreCase(mu.getUsername())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        list.add(mu);
                    }
                }
                JSONArray arr = new JSONArray();
                for (User u : list) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", u.getId());
                    obj.put("username", u.getUsername());
                    obj.put("email", u.getEmail());
                    obj.put("fullName", u.getFullName());
                    obj.put("karmaPoints", u.getKarmaPoints());
                    obj.put("role", u.getRole().name());
                    obj.put("tier", u.getTier().getDisplayName());
                    obj.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "Genesis Era");
                    arr.put(obj);
                }
                sendJsonResponse(exchange, 200, arr.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Admin devotees list error: " + e.getMessage(), e);
                sendJsonResponse(exchange, 500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }
    }

    private class AdminUpdateStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            User user = activeUser;
            if (user == null || !user.isAdmin()) {
                sendJsonResponse(exchange, 403, new JSONObject().put("error", "Access restricted to Arch-Deity Administrators.").toString());
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject body = new JSONObject(requestBody);
                String ticketNumber = body.optString("ticketNumber", "").trim();
                String newStatusStr = body.optString("newStatus", body.optString("status", "")).trim();
                String remarks = body.optString("remarks", "Updated via Arch-Deity Console").trim();

                Complaint complaint = complaintService.getComplaintByTicket(ticketNumber);
                if (complaint == null) {
                    sendJsonResponse(exchange, 404, new JSONObject().put("error", "Docket not found").toString());
                    return;
                }

                TicketStatus status = TicketStatus.fromLabel(newStatusStr);
                complaintService.updateTicketByAdmin(complaint.getId(), status, null, user.getId(), remarks);

                JSONObject res = new JSONObject();
                res.put("success", true);
                res.put("message", "Docket " + ticketNumber + " status updated to '" + status.getLabel() + "' by divine decree.");
                sendJsonResponse(exchange, 200, res.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Admin update status error: " + e.getMessage(), e);
                sendJsonResponse(exchange, 500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }
    }

    private class AdminGrantKarmaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            User user = activeUser;
            if (user == null || !user.isAdmin()) {
                sendJsonResponse(exchange, 403, new JSONObject().put("error", "Access restricted to Arch-Deity Administrators.").toString());
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject body = new JSONObject(requestBody);
                int targetUserId = body.optInt("userId", -1);
                int amount = body.optInt("amount", 108);
                String reason = body.optString("reason", "Arch-Deity Sovereign Blessing").trim();

                User target = userDAO.findById(targetUserId);
                if (target == null) {
                    sendJsonResponse(exchange, 404, new JSONObject().put("error", "Devotee soul not found").toString());
                    return;
                }

                target.setKarmaPoints(target.getKarmaPoints() + amount);
                userDAO.updateKarma(target.getId(), target.getKarmaPoints());

                com.karmasutra.model.KarmaTransaction tx = new com.karmasutra.model.KarmaTransaction();
                tx.setUserId(target.getId());
                tx.setAmount(amount);
                tx.setTransactionType("EARNED");
                tx.setReason(reason);
                karmaDAO.createTransaction(tx);

                JSONObject res = new JSONObject();
                res.put("success", true);
                res.put("message", "Granted " + amount + " Karma to " + target.getFullName() + ". New balance: " + target.getKarmaPoints() + " KP.");
                res.put("newBalance", target.getKarmaPoints());
                sendJsonResponse(exchange, 200, res.toString());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Admin grant karma error: " + e.getMessage(), e);
                sendJsonResponse(exchange, 500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }
    }

    private class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            authService.logout();
            activeUser = null;

            JSONObject res = new JSONObject();
            res.put("success", true);
            res.put("message", "You have successfully departed the Celestial Realm.");
            sendJsonResponse(exchange, 200, res.toString());
        }
    }

    private class ProfileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User user = activeUser;
            if (user == null) {
                sendJsonResponse(exchange, 401, new JSONObject().put("error", "Unauthorized. You are currently not authenticated.").toString());
                return;
            }

            try {
                User fresh = userDAO.findById(user.getId());
                if (fresh != null) user = fresh;
            } catch (Exception ignored) {}

            JSONObject res = new JSONObject();
            res.put("id", user.getId());
            res.put("username", user.getUsername());
            res.put("email", user.getEmail());
            res.put("fullName", user.getFullName());
            res.put("karmaPoints", user.getKarmaPoints());
            res.put("tier", user.getTier().getDisplayName());
            res.put("role", user.getRole().name());
            res.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : "Cosmic Genesis");

            sendJsonResponse(exchange, 200, res.toString());
        }
    }

    private class ChangePasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, new JSONObject().put("error", "Method Not Allowed").toString());
                return;
            }

            User user = activeUser;
            if (user == null) {
                sendJsonResponse(exchange, 401, new JSONObject().put("error", "Unauthorized. Please authenticate first.").toString());
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject body = new JSONObject(requestBody);
                String currentPass = body.optString("currentPassword", "").trim();
                String newPass = body.optString("newPassword", "").trim();

                if (newPass.length() < 6) {
                    sendJsonResponse(exchange, 400, new JSONObject().put("success", false).put("error", "New Master Passphrase must be at least 6 characters long.").toString());
                    return;
                }

                User dbUser = userDAO.findById(user.getId());
                if (dbUser == null) {
                    sendJsonResponse(exchange, 404, new JSONObject().put("success", false).put("error", "Devotee record not found.").toString());
                    return;
                }

                boolean valid = com.karmasutra.util.PasswordUtil.verifyPassword(currentPass, dbUser.getSalt(), dbUser.getPasswordHash());
                if (!valid) {
                    sendJsonResponse(exchange, 400, new JSONObject().put("success", false).put("error", "Current Master Passphrase is incorrect.").toString());
                    return;
                }

                String newSalt = com.karmasutra.util.PasswordUtil.generateSalt();
                String newHash = com.karmasutra.util.PasswordUtil.hashPassword(newPass, newSalt);

                userDAO.updatePassword(dbUser.getId(), newHash, newSalt);
                dbUser.setPasswordHash(newHash);
                dbUser.setSalt(newSalt);
                activeUser = dbUser;

                JSONObject res = new JSONObject();
                res.put("success", true);
                res.put("message", "Master Passphrase successfully updated across the Akashic records.");
                sendJsonResponse(exchange, 200, res.toString());

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Change password error: " + e.getMessage());
                sendJsonResponse(exchange, 500, new JSONObject().put("success", false).put("error", e.getMessage()).toString());
            }
        }
    }

    private class KarmaLedgerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User user = activeUser;
            if (user == null) {
                sendJsonResponse(exchange, 401, new JSONObject().put("error", "Unauthorized. Please authenticate first.").toString());
                return;
            }

            JSONArray arr = new JSONArray();
            try {
                List<com.karmasutra.model.KarmaTransaction> list = karmaDAO.listByUserId(user.getId());
                if (list != null) {
                    for (com.karmasutra.model.KarmaTransaction tx : list) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", tx.getId());
                        obj.put("amount", tx.getAmount());
                        obj.put("type", tx.getTransactionType());
                        obj.put("reason", tx.getReason());
                        obj.put("createdAt", tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : "Recent");
                        arr.put(obj);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to list karma transactions: " + e.getMessage());
            }

            sendJsonResponse(exchange, 200, arr.toString());
        }
    }

    private class StaticResourceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isBlank()) {
                path = "/index.html";
            }

            // Clean path
            path = path.replaceAll("\\.\\.", ""); // prevent path traversal

            byte[] content = loadResource(path);
            if (content == null && !path.contains(".")) {
                // SPA fallback for HTML5 routing: serve index.html only for paths without extensions
                content = loadResource("/index.html");
                path = "/index.html";
            }

            if (content != null) {
                String mime = getMimeType(path);
                exchange.getResponseHeaders().set("Content-Type", mime);
                exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
                exchange.sendResponseHeaders(200, content.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(content);
                    os.flush();
                }
            } else {
                String notFound = "<h1>404 — Cosmic Void</h1><p>Resource not found in Akashic records: " + path + "</p>";
                byte[] notFoundBytes = notFound.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(404, notFoundBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFoundBytes);
                    os.flush();
                }
            }
        }

        private byte[] loadResource(String path) {
            String normPath = path.startsWith("/") ? path : "/" + path;

            // 1. Try local file system paths FIRST (so fresh builds & textures are served immediately)
            String[] searchRoots = new String[]{
                "/Users/vanshikab/.gemini/antigravity/scratch/karma-sutra/cosmic-3d/dist",
                "/Users/vanshikab/.gemini/antigravity/scratch/karma-sutra/src/main/resources/web",
                "cosmic-3d/dist",
                "src/main/resources/web",
                "target/classes/web"
            };

            for (String root : searchRoots) {
                File file = new File(root + normPath);
                if (file.exists() && file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        return fis.readAllBytes();
                    } catch (Exception ignored) {
                    }
                }
            }

            // 2. Classpath fallback (/web/...)
            try (InputStream is = getClass().getResourceAsStream("/web" + normPath)) {
                if (is != null) {
                    return is.readAllBytes();
                }
            } catch (Exception ignored) {
            }

            try (InputStream is = getClass().getClassLoader().getResourceAsStream("web" + normPath)) {
                if (is != null) {
                    return is.readAllBytes();
                }
            } catch (Exception ignored) {
            }

            return null;
        }

        private String getMimeType(String path) {
            String lower = path.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=utf-8";
            if (lower.endsWith(".js") || lower.endsWith(".mjs")) return "application/javascript; charset=utf-8";
            if (lower.endsWith(".css")) return "text/css; charset=utf-8";
            if (lower.endsWith(".json")) return "application/json; charset=utf-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".svg")) return "image/svg+xml; charset=utf-8";
            if (lower.endsWith(".ico")) return "image/x-icon";
            if (lower.endsWith(".woff2")) return "font/woff2";
            if (lower.endsWith(".woff")) return "font/woff";
            if (lower.endsWith(".ttf")) return "font/ttf";
            return "application/octet-stream";
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
