package com.karmasutra.service.ai;

import com.karmasutra.config.AppConfig;
import com.karmasutra.exception.DivineServiceUnavailableException;
import com.karmasutra.model.Department;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.Severity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Native Google Gemini API implementation (gemini-1.5-flash / gemini-2.5-flash).
 * Zero external heavy SDKs, pure java.net.http.HttpClient.
 */
public class GeminiProvider implements LLMProvider {

    private static final Logger LOGGER = Logger.getLogger(GeminiProvider.class.getName());
    private static final String DEFAULT_MODEL = "gemini-1.5-flash";
    private final HttpClient httpClient;

    public GeminiProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(12))
                .build();
    }

    private String getApiKey() {
        return AppConfig.get("gemini.api.key", AppConfig.get("GEMINI_API_KEY", ""));
    }

    private String getModel() {
        return AppConfig.get("ai.model", DEFAULT_MODEL);
    }

    @Override
    public String getProviderName() {
        return "Google Gemini API (" + getModel() + ")";
    }

    @Override
    public boolean isAvailable() {
        String key = getApiKey();
        return key != null && !key.isBlank() && !key.equalsIgnoreCase("your_gemini_api_key_here");
    }

    @Override
    public DivineAIResult generateDivineResponse(Complaint complaint, Department department, int iterationNumber)
            throws DivineServiceUnavailableException {
        if (!isAvailable()) {
            throw new DivineServiceUnavailableException("Gemini API key is not configured in .env or settings.");
        }

        String deptName = department != null ? department.getName() : "Universal Docket Office";
        String deptHead = department != null ? department.getDivineHead() : "Cosmic Registry Official";

        String systemInstruction = """
            You are the official spokesperson and senior administrative clerk for Karma Sutra™: Divine Grievance & Resolution Office.
            Brand Tagline: "The universe has a ticketing system." | "You do the karma. We handle the drama."
            
            Personality & Directives:
            1. SATIRE & COMEDY (CORE RULE): The replies must be genuinely hilarious, razor-sharp, laugh-out-loud funny, and satirical.
               Treat cosmic reality like a dysfunctional, high-tech corporate bureaucracy (SCRUM masters, Akashic server racks, ISO-9001 karma audits, Chitragupta Proctoring).
            2. AUDACIOUS & LAZY REQUESTS: When a petitioner asks for absurd or unearned things (e.g. "I want to be a billionaire without doing anything", passing exams without opening books, wanting an ex back, complaining about midnight Swiggy orders):
               ROAST THEM THOROUGHLY. Cite fictional clauses like "Section 0.00 of the Akashic Penal Code (Pure Unadulterated Audacity)", note that deities choked on their celestial espresso, calculate the zero biological effort expended, and demand at least 14 minutes of sitting upright before further petitions.
            3. SERIOUS MATTERS: For genuinely serious human distress (suicide, self-harm, grief, abuse, severe clinical depression):
               DROP ALL SARCASM INSTANTLY. Respond with profound warmth, sacred empathy, and practical guidance to seek earthly professional help.
            4. TONE & VOCABULARY:
               Use corporate-cosmic jargon: "Cosmic Docket", "First Law of Karmic Thermodynamics", "Bandwidth throttling", "Akashic Telemetry", "Transit of Saturn delay".
               Understand Hinglish, desi humor (Sharma Ji's son, Shaadi aunties, WhatsApp forwards) naturally without forced memes.
            5. FORMAT:
               Start with a bureaucratic dispatch header (Official Docket #, Presiding Deity, Department).
               Address the petitioner's exact words directly.
               Give concrete "Mandatory Remedial Actions".
               Sign off formally with the presiding divine authority.
            """;

        String userPrompt = String.format("""
            TICKET NUMBER: %s
            DEPARTMENT: %s (Head: %s)
            CATEGORY: %s
            TYPE: %s
            SEVERITY: %s
            SUBJECT: %s
            DESCRIPTION: %s
            DESIRED RESOLUTION: %s
            ITERATION: Perspective #%d
            
            Generate a personalized, contextual official cosmic response for this petitioner.
            """,
            complaint.getTicketNumber(),
            deptName,
            deptHead,
            complaint.getCategory().getLabel(),
            complaint.getType().getLabel(),
            complaint.getSeverity().getLabel(),
            complaint.getSubject(),
            complaint.getDescription(),
            complaint.getDesiredResolution() != null ? complaint.getDesiredResolution() : "General cosmic intervention",
            iterationNumber
        );

        try {
            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray parts = new JSONArray();

            parts.put(new JSONObject().put("text", systemInstruction + "\n\n" + userPrompt));
            contentObj.put("parts", parts);
            contents.put(contentObj);
            body.put("contents", contents);

            String endpoint = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                    getModel(), getApiKey()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray candidates = json.optJSONArray("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    JSONObject first = candidates.getJSONObject(0);
                    JSONObject candContent = first.optJSONObject("content");
                    if (candContent != null) {
                        JSONArray candParts = candContent.optJSONArray("parts");
                        if (candParts != null && !candParts.isEmpty()) {
                            String text = candParts.getJSONObject(0).optString("text", "").trim();
                            return new DivineAIResult(
                                    deptHead,
                                    text,
                                    detectTone(complaint.getSubject() + " " + complaint.getDescription()),
                                    complaint.isLongTermWish(),
                                    department != null ? department.getCode() : "DEPT_MISC"
                            );
                        }
                    }
                }
            } else if (response.statusCode() == 429) {
                throw new DivineServiceUnavailableException("Cosmic Gateway Rate Limit Exceeded (HTTP 429). The deities are overwhelmed with prayers.");
            } else if (response.statusCode() == 403 || response.statusCode() == 400) {
                throw new DivineServiceUnavailableException("Invalid or unauthorized API key for Gemini (HTTP " + response.statusCode() + ").");
            } else {
                throw new DivineServiceUnavailableException("Divine Gateway returned status " + response.statusCode() + ": " + response.body());
            }
        } catch (DivineServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Gemini API invocation failed", e);
            throw new DivineServiceUnavailableException("Connection to Cosmic AI Gateway failed: " + e.getMessage(), e);
        }

        throw new DivineServiceUnavailableException("Empty response received from Celestial AI Core.");
    }

    @Override
    public ClassificationResult classifySubmission(String subject, String description) {
        String combined = (subject + " " + description).toLowerCase();
        boolean isLongTerm = combined.contains("year") || combined.contains("startup") ||
                             combined.contains("company") || combined.contains("future") ||
                             combined.contains("career") || combined.contains("decades") ||
                             combined.contains("retire") || combined.contains("long-term");

        boolean isSerious = combined.contains("abuse") || combined.contains("suicide") ||
                            combined.contains("die") || combined.contains("depressed") ||
                            combined.contains("death") || combined.contains("hospital") ||
                            combined.contains("grief");

        if (isSerious) {
            return new ClassificationResult(
                    "Inappropriate submission",
                    false,
                    ComplaintCategory.OTHER_DIVINE_MATTERS,
                    Severity.COSMIC_CRITICAL,
                    "COMPASSIONATE_EMPATHIC",
                    1
            );
        }

        boolean isHilarious = combined.contains("attendance") || combined.contains("crush") ||
                ((combined.contains("exam") || combined.contains("test")) && (combined.contains("disappear") || combined.contains("cancel"))) ||
                combined.contains("billionaire") || combined.contains("rich without") || combined.contains("calorie");

        if (isHilarious) {
            return new ClassificationResult("Funny / hilarious wish", false, ComplaintCategory.CAREER_AND_UNEMPLOYMENT, Severity.MEDIUM, "WITTY_ROAST", 1);
        }

        if (combined.contains("alien") || combined.contains("pigeon") || combined.contains("gravity") || combined.contains("teleport")) {
            return new ClassificationResult("Unusual or absurd wish", true, ComplaintCategory.DESTINY_AND_LIFE_DECISIONS, Severity.MEDIUM, "SURREAL_CURIOSITY", 5);
        }

        if (combined.contains("prayer") || combined.contains("bless") || combined.contains("peace") || combined.contains("health") || isLongTerm) {
            return new ClassificationResult("Sincere wish or prayer", true, ComplaintCategory.DESTINY_AND_LIFE_DECISIONS, Severity.LOW, "REVERENT_BLESSING", 3);
        }

        return new ClassificationResult("Genuine grievance", false, ComplaintCategory.OTHER_DIVINE_MATTERS, Severity.HIGH, "STRUCTURED_DETERMINATION", 1);
    }

    private String detectTone(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("suicide") || lower.contains("abuse") || lower.contains("depressed") || lower.contains("grief")) {
            return "SERIOUS_EMPATHIC";
        }
        if (lower.contains("startup") || lower.contains("career") || lower.contains("dream")) {
            return "ASPIRATIONAL_BUREAUCRATIC";
        }
        return "WITTY_BUREAUCRATIC";
    }
}
