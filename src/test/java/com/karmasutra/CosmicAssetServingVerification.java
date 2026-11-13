package com.karmasutra;

import com.karmasutra.bridge.CosmicWebServer;
import com.karmasutra.model.User;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CosmicAssetServingVerification {

    public static void main(String[] args) {
        System.out.println("✦ Launching CosmicWebServer verification...");
        CosmicWebServer server = CosmicWebServer.getInstance();
        User testDevotee = new User(1, "vanshika108", "vanshika@karmasutra.org", "hash", "salt", "Vanshika Bishnoi", UserRole.DEVOTEE, SubscriptionTier.FREE_DEVOTEE, 138, null);
        server.setActiveUser(testDevotee);
        boolean started = server.start();
        if (!started) {
            System.err.println("FAILED to start CosmicWebServer!");
            System.exit(1);
        }

        int port = server.getPort();
        System.out.println("CosmicWebServer started on port " + port);

        try {
            // 1. Test index.html
            testEndpoint("http://127.0.0.1:" + port + "/", 200, "text/html", "Karma Sutra™ — Interactive 3D Cosmic Orrery");

            // 2. Test assets/index.css
            testEndpoint("http://127.0.0.1:" + port + "/assets/index.css", 200, "text/css", ".cosmic-app-container");

            // 3. Test assets/index.js
            testEndpoint("http://127.0.0.1:" + port + "/assets/index.js", 200, "application/javascript", "createRoot");

            // 4. Test api/status
            testEndpoint("http://127.0.0.1:" + port + "/api/status", 200, "application/json", "ONLINE");

            // 5. Test 404 for unknown asset
            testEndpoint("http://127.0.0.1:" + port + "/assets/nonexistent.png", 404, "text/html", "Cosmic Void");

            System.out.println("\n🎉 ALL STATIC ASSETS & API ENDPOINTS VERIFIED 100% PERFECTLY!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            server.stop();
        }
    }

    private static void testEndpoint(String urlStr, int expectedCode, String expectedMimePrefix, String expectedSnippet) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        int code = conn.getResponseCode();
        String contentType = conn.getContentType();
        InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        byte[] bytes = is.readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);

        System.out.printf("GET %s -> Code: %d, Content-Type: %s, Length: %d bytes%n", urlStr, code, contentType, bytes.length);

        if (code != expectedCode) {
            throw new AssertionError("Expected HTTP " + expectedCode + " but got " + code + " for " + urlStr);
        }
        if (contentType == null || !contentType.toLowerCase().contains(expectedMimePrefix.toLowerCase())) {
            throw new AssertionError("Expected Content-Type containing '" + expectedMimePrefix + "' but got '" + contentType + "' for " + urlStr);
        }
        if (!body.contains(expectedSnippet)) {
            throw new AssertionError("Expected snippet '" + expectedSnippet + "' not found in body of " + urlStr);
        }
        System.out.println("  ✓ PASS: Snippet and MIME matched.");
    }
}
