package com.karmasutra;

import com.karmasutra.model.Department;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.complaint.Grievance;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.service.ai.CosmicResilientProvider;
import com.karmasutra.service.ai.DivineAIResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DivineResponseHumorTest {

    @Test
    @DisplayName("Verify hilarious roast for 'billionaire without doing anything'")
    void testBillionaireLazyRoast() {
        CosmicResilientProvider provider = new CosmicResilientProvider();
        Department dept = new Department(2, "DEPT_CAREER", "Ministry of Corporate Karmic Balancing & Unearned Opulence", "Lord Shani, Lead SCRUM Master of Cosmic Fate", "Oversees mortal productivity", "None", null);

        Complaint complaint = com.karmasutra.model.complaint.ComplaintFactory.createComplaint(com.karmasutra.model.enums.ComplaintType.COMPLAINT);
        complaint.setId(1);
        complaint.setTicketNumber("KS-2026-TEST");
        complaint.setUserId(1);
        complaint.setDepartmentId(2);
        complaint.setCategory(ComplaintCategory.CAREER_AND_UNEMPLOYMENT);
        complaint.setSeverity(Severity.HIGH);
        complaint.setSubject("I want to be a billionaire without doing anything at all");
        complaint.setDescription("I want to be a billionaire without doing anything at all");
        complaint.setDesiredResolution("Give me 10 billion dollars immediately");

        DivineAIResult result = provider.generateDivineResponse(complaint, dept, 1);
        assertNotNull(result);
        String text = result.responseText();

        System.out.println("=== GENERATED HILARIOUS ROAST ===");
        System.out.println(text);
        System.out.println("=================================");

        assertTrue(text.contains("espresso") || text.contains("Audacity") || text.contains("sofa") || text.contains("biological activity"),
                "Response should roast user's laziness with hilarious cosmic bureaucracy");
    }
}
