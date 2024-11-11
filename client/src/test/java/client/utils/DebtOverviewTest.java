package client.utils;

import client.DebtOverview;
import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DebtOverviewTest {

    @Test
    void constructor() {
        Event event = new Event("Event");
        Participant participant = new Participant("Alice", event);
        DebtOverview debtOverview = new DebtOverview(participant, 100, 100);
        Assertions.assertNotNull(debtOverview);
    }

    @Test
    void getParticipant() {
        Event event = new Event("Event");
        Participant participant = new Participant("Alice", event);
        DebtOverview debtOverview = new DebtOverview(participant, 100, 100);
        Assertions.assertEquals(participant, debtOverview.getParticipant());
    }

    @Test
    void getDebt() {
        Event event = new Event("Event");
        Participant participant = new Participant("Alice", event);
        DebtOverview debtOverview = new DebtOverview(participant, 100, 100);
        Assertions.assertEquals(100, debtOverview.getDebt());
    }

    @Test
    void getDebtPercentage() {
        Event event = new Event("Event");
        Participant participant = new Participant("Alice", event);
        DebtOverview debtOverview = new DebtOverview(participant, 100, 100);
        Assertions.assertEquals(100, debtOverview.getDebtPercentage());
    }

    @Test
    void testToString() {
        Event event = new Event("Event");
        Participant participant = new Participant("Alice", event);
        DebtOverview debtOverview = new DebtOverview(participant, 100, 100);
        Assertions.assertEquals("DebtOverview{participant=" +
                "Alice is registered with id: 0 using email: " +
                "Alice with IBAN: null BIC: null debt: 0 and expense share: " +
                "0, debt=1.00, debtPercentage=100}", debtOverview.toString());
    }
}