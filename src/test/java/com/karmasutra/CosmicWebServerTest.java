package com.karmasutra;

import com.karmasutra.bridge.CosmicWebServer;
import com.karmasutra.model.User;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CosmicWebServerTest {

    private static CosmicWebServer server;
    private static HttpClient client;

    @BeforeAll
    static void setup() {
        server = CosmicWebServer.getInstance();
        User testDevotee = new User(1, "devotee1", "devotee1@karmasutra.org", "hash", "salt", "Arjun Sharma", UserRole.DEVOTEE, SubscriptionTier.FREE_DEVOTEE, 108, null);
        server.setActiveUser(testDevotee);
        server.start();

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @DisplayName("Verify Cosmic Web Server status API endpoint")
    void testStatusEndpoint() throws Exception {
        assertTrue(server.isRunning(), "Server should be marked as running");
        int port = server.getPort();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/status"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status API should return 200 OK");
        String body = response.body();
        assertNotNull(body);
        assertTrue(body.contains("ONLINE"), "Response should indicate ONLINE status");
        assertTrue(body.contains("Arjun Sharma") || body.contains("devotee1"), "Response should reflect active devotee");
    }

    @Test
    @DisplayName("Verify static index.html serving for 3D Cosmic Orrery")
    void testStaticIndexServing() throws Exception {
        int port = server.getPort();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/index.html"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Static index.html should return 200 OK");
        String body = response.body();
        assertNotNull(body);
        assertTrue(body.contains("Karma Sutra") || body.contains("Orrery"), "HTML should contain Orrery branding");
    }
}
