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
 * Groq Cloud AI provider (e.g. llama-3.3-70b-versatile or llama-3.1-8b-instant).
 */
public class GroqProvider implements LLMProvider {

    private static final Logger LOGGER = Logger.getLogger(GroqProvider.class.getName());
    private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";
    private final HttpClient httpClient;

    public GroqProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private String getApiKey() {
        return AppConfig.get("groq.api.key", AppConfig.get("GROQ_API_KEY", ""));
    }

    @Override
    public String getProviderName() {
        return "Groq Cloud AI (" + DEFAULT_MODEL + ")";
    }

    @Override
    public boolean isAvailable() {
        String key = getApiKey();
        return key != null && !key.isBlank() && !key.equalsIgnoreCase("your_groq_api_key_here");
    }

    @Override
    public DivineAIResult generateDivineResponse(Complaint complaint, Department department, int iterationNumber)
            throws DivineServiceUnavailableException {
        if (!isAvailable()) {
            throw new DivineServiceUnavailableException("Groq API key is not configured.");
        }

        String deptHead = department != null ? department.getDivineHead() : "Cosmic Registry Official";
        String deptName = department != null ? department.getName() : "Universal Docket Office";

        try {
            JSONObject body = new JSONObject();
            body.put("model", DEFAULT_MODEL);
            JSONArray messages = new JSONArray();

            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", """
                        You are the official spokesperson for Karma Sutra™: Divine Grievance & Resolution Office.
                        Tagline: "The universe has a ticketing system." | "You do the karma. We handle the drama."
                        The replies must be genuinely hilarious, satirical, razor-sharp, and laugh-out-loud funny.
                        When someone asks for unearned wealth, miracles without work, or absurd wishes (e.g. billionaire without doing anything), roast their pure audacity using corporate-cosmic jargon (Section 0.00 of Akashic Penal Code, SCRUM master deities, zero biological effort expended).
                        For genuinely serious human suffering or self-harm, drop all sarcasm and respond with profound empathy.
                        """));

            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", String.format("Ticket #%s\nDepartment: %s\nSubject: %s\nDescription: %s\nProvide official divine response.",
                            complaint.getTicketNumber(), deptName, complaint.getSubject(), complaint.getDescription())));

            body.put("messages", messages);
            body.put("temperature", 0.7);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getApiKey())
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject resJson = new JSONObject(response.body());
                JSONArray choices = resJson.getJSONArray("choices");
                if (!choices.isEmpty()) {
                    String content = choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
                    return new DivineAIResult(deptHead, content, "WITTY_BUREAUCRATIC", complaint.isLongTermWish(), department != null ? department.getCode() : "DEPT_MISC");
                }
            } else {
                throw new DivineServiceUnavailableException("Groq Gateway Error (HTTP " + response.statusCode() + "): " + response.body());
            }
        } catch (DivineServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Groq API call failed", e);
            throw new DivineServiceUnavailableException("Groq Divine Gateway unreachable: " + e.getMessage(), e);
        }

        throw new DivineServiceUnavailableException("No response generated by Groq Gateway.");
    }

    @Override
    public ClassificationResult classifySubmission(String subject, String description) {
        // Leverages consistent heuristics
        boolean isLongTerm = subject.toLowerCase().contains("year") || description.toLowerCase().contains("company") ||
                             description.toLowerCase().contains("startup");
        return new ClassificationResult(
                isLongTerm ? "Long-Term Aspiration" : "Complaint",
                isLongTerm,
                isLongTerm ? ComplaintCategory.DESTINY_AND_LIFE_DECISIONS : ComplaintCategory.OTHER_DIVINE_MATTERS,
                Severity.MEDIUM,
                "NEUTRAL_BUREAUCRATIC",
                isLongTerm ? 5 : 1
        );
    }
}
