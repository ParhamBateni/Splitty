package client;

import commons.Participant;

public class DebtOverview {
    Participant participant;
    int debt;
    int debtPercentage;

    public DebtOverview(Participant participant, int debt, int debtPercentage) {
        this.participant = participant;
        this.debt = debt;
        this.debtPercentage = debtPercentage;
    }

    public Participant getParticipant() {
        return participant;
    }

    public int getDebt() {
        return debt;
    }

    public int getDebtPercentage() {
        return debtPercentage;
    }

    @Override
    public String toString() {
        return "DebtOverview{" +
                "participant=" + participant +
                ", debt=" + String.format("%.2f", debt / 100.0) +
                ", debtPercentage=" + debtPercentage +
                '}';
    }
}
