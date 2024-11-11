package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    Participant p1, p2, p3, p4, p5, p6;
    Expense e1, e2;
    String title1, title2;
    String date1, date2;
    int amount1, amount2;
    long id;

    Event event1, event2, event3, event4, event5;


    @BeforeEach
    void setUp() {
        String name1 = "Alex";
        String email1 = "alex@gmail.com";
        String iban1 = "NL91ABNA0417164300";
        String bic1 = "INGBNL2A";

        String name2 = "Boris";
        String email2 = "boris@yahoo.info";
        String iban2 = "DE75512108001245126199";
        String bic2 = "ABNANL2A";
        Event event = new Event("Party");

        id = 123456789;

        p1 = new Participant(id, name1, email1, iban1, bic1, event);
        p2 = new Participant(id, name1, email1, iban1, bic1, event);
        p3 = new Participant(id, name2, email1, iban1, bic1, event);
        p4 = new Participant(id, name1, email2, iban1, bic1, event);
        p5 = new Participant(id, name1, email1, iban2, bic1, event);
        p6 = new Participant(id, name1, email1, iban1, bic2, event);


        Currency currency1 = Currency.getInstance("USD");
        amount1 = 250;
        title1 = "Restaurant";
        date1 = "19 set 2024";
        Participant payer1 = p1;
        long id1 = 1234;

        Currency currency2 = Currency.getInstance("AUD");
        amount2 = 350;
        title2 = "Cinema";
        date2 = "20 set 2024";
        Participant payer2 = p2;
        long id2 = 5678;

        e2 = new Expense(payer1, currency1, amount1, title1, date1, List.of(p2, p3));
        e1 = new Expense(payer2, currency2, amount2, title2, date2, List.of(p1));

        event1 = new Event(title1, List.of(p1, p2, p3), List.of(e1));
        event2 = new Event(title1, List.of(p1, p2, p3), List.of(e1));
        event3 = new Event(title2, List.of(p1, p2, p3), List.of(e1));
        event4 = new Event(title1, List.of(p1, p2, p3), List.of(e1, e2));
        event5 = new Event(title1, List.of(p1), List.of(e1));



    }

    @Test
    void testEmptyConstructor() {
        assertNotNull(new Event());
    }

    @Test
    void testConstructorWithId () {
        assertNotNull(new Event(id, title1, List.of(p1, p2, p3), List.of(e1)));
    }

    @Test
    void testGetDateCreated() {
        String dateString = "12 Sep 2024";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
            System.out.println(date); // Prints: Wed Sep 12 00:00:00 UTC 2024
        } catch (ParseException e) {
            e.printStackTrace();
        }

        event1.setDateCreated(date);
        assertEquals(date, event1.getDateCreated());
    }

    @Test
    void testGetLastAction() {
        String dateString = "12 Sep 2024";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
            System.out.println(date); // Prints: Wed Sep 12 00:00:00 UTC 2024
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event1.setLastAction(date);
        assertEquals( date, event1.getLastAction());
    }

    @Test
    void testAddParticipant() {
        event5.addParticipant(p2);
        assertEquals(List.of(p1, p2), event5.getParticipants());
    }

    @Test
    void testRemoveParticipant() {
        event5.removeParticipant(p1);
        assertEquals(List.of(), event5.getParticipants());
    }

    @Test
    void testAddExpense() {
        event5.addExpense(e2);
        assertEquals(List.of(e1, e2), event5.getExpenses());
    }

    @Test
    void testRemoveExpense() {
        event5.removeExpense(e1);
        assertEquals(List.of(), event5.getExpenses());
    }

    @Test
    void getId() {
        assertEquals(0, event1.getId());
    }

    @Test
    void setId() {
        event1.setId(24);
        assertEquals(24, event1.getId());
    }

    @Test
    void getTitle() {
        assertEquals("Restaurant", event1.getTitle());
    }

    @Test
    void setTitle() {
        event1.setTitle("Bar");
        assertEquals("Bar", event1.getTitle());
    }

    @Test
    void getParticipants() {
        assertEquals(List.of(p1, p2, p3), event1.getParticipants());
    }

    @Test
    void setParticipants() {
        event1.setParticipants(List.of());
        assertEquals(List.of(), event1.getParticipants());
    }

    @Test
    void getExpenses() {
        assertEquals(List.of(e1), event1.getExpenses());
    }

    @Test
    void setExpenses() {
        event1.setExpenses(List.of());
        assertEquals(List.of(), event1.getExpenses());
    }

    @Test
    void testEquals() {

        assertEquals(event1, event1);
        assertNotEquals(event1, null);
        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertNotEquals(event2, event4);
    }

    @Test
    void testHashCode() {
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1.hashCode(), event3.hashCode());
        assertNotEquals(event2.hashCode(), event4.hashCode());
    }

    @Test
    void testToString() {
        assertTrue(event1.toString().contains(Long.toString(event1.getId())));
        assertTrue(event1.toString().contains(title1));
        assertTrue(event1.toString().contains(List.of(p1, p2, p3).toString()));
        assertTrue(event1.toString().contains(List.of(e1).toString()));
    }
}