package com.karmasutra.model.complaint;

import com.karmasutra.model.enums.ComplaintType;

/**
 * PersonalDilemma represents ethical crossroads, difficult choices,
 * relationship quandaries, or existential paralysis.
 */
public class PersonalDilemma extends Complaint {

    private String choiceA;
    private String choiceB;
    private boolean moralCrisis;

    public PersonalDilemma() {
        super();
        setType(ComplaintType.PERSONAL_DILEMMA);
    }

    @Override
    public String getBureaucraticClassification() {
        return "ETHICAL-TRIAGE-PARADOX-7 [Philosophical Council]";
    }

    @Override
    public int calculateKarmaWeight() {
        return 25 * getSeverity().getLevel();
    }

    @Override
    public String getSuggestedEscalationLevel() {
        return "Referral to High Philosophical Council of Equanimity";
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public boolean isMoralCrisis() {
        return moralCrisis;
    }

    public void setMoralCrisis(boolean moralCrisis) {
        this.moralCrisis = moralCrisis;
    }
}
