package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {
    Expense e1, e2, e3, e4;

    Participant p1, p2, p3, p4, p5;

    String title1, title2;
    String date1, date2;
    String code1, code2;
    int amount1, amount2;
    List<Participant> participants1, participants2;

    Event event;

    Currency currency2;
    long id1, id2;


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
        event = new Event("Party");

        p1 = new Participant(id1, name1, email1, iban1, bic1, event);
        p2 = new Participant(id1, name1, email1, iban1, bic1, event);
        p3 = new Participant(id1, name2, email1, iban1, bic1, event);
        p4 = new Participant(id1, name1, email2, iban1, bic1, event);
        p5 = new Participant(id1, name1, email1, iban2, bic1, event);

        amount1 = 250;
        title1 = "Restaurant";
        date1 = "19 set 2024";
        participants1 = List.of(p2, p3);
        id1 = 1234;
        code1 = "USD";
        Currency currency1 = Currency.getInstance(code1);

        amount2 = 350;
        title2 = "Cinema";
        date2 = "20 set 2024";
        participants2 = List.of(p1, p3);
        id2 = 5678;
        code2 = "AUD";
        currency2 = Currency.getInstance(code2);

        e1 = new Expense(p1, currency1, amount1, title1, date1, participants1);
        e2 = new Expense(p2, currency2, amount2, title2, date2, participants2);
        e3 = new Expense(p1, currency1, amount1, title1, date1, participants1);
        e4 = new Expense(p1, currency1, amount1, title1, date1, participants1, event, false);

    }

    @Test
    void emptyConstructorTest() {
        assertNotNull(new Expense());
    }

    @Test
    void constructorTest() {
        assertNotNull(new Expense(p1, currency2, amount2,
                title2, date2, List.of(p1, p2), event, false));
    }

    @Test
    void testGetEvent() {
        assertEquals(event, e4.getEvent());
    }

    @Test
    void testSetEvent() {
        e4.setEvent(null);
        assertEquals(null, e4.getEvent());
    }


    @Test
    void getCurrency() {
        assertEquals("USD", e1.getCurrency().getCurrencyCode());
    }

    @Test
    void getAmount() {
        assertEquals(250, e1.getAmount());
    }

    @Test
    void getTitle() {
        assertEquals("Restaurant", e1.getTitle());

    }

    @Test
    void getDate() {
        assertEquals("19 set 2024", e1.getDate());

    }

    @Test
    void getPayer() {
        assertEquals(p1, e1.getPayer());
    }

    @Test
    void getParticipants() {
        assertEquals(participants1, e1.getParticipants());
    }

    @Test
    void getId() {
        assertEquals(0, e1.getId());
    }

    @Test
    void setCurrency() {
        e1.setCurrency(Currency.getInstance("GBP"));
        assertEquals(Currency.getInstance("GBP"), e1.getCurrency());
    }

    @Test
    void setAmount() {
        e1.setAmount(100);
        assertEquals(100, e1.getAmount());
    }

    @Test
    void setTitle() {
        e1.setTitle("Drinks");
        assertEquals("Drinks", e1.getTitle());
    }

    @Test
    void setDate() {
        e1.setDate("19 ott 2024");
        assertEquals("19 ott 2024", e1.getDate());
    }

    @Test
    void setPayer() {
        e1.setPayer(p2);
        assertEquals(p2, e1.getPayer());
    }

    @Test
    void setParticipants() {
        e1.setParticipants(participants2);
        assertEquals(participants2, e1.getParticipants());
    }

    @Test
    void setId() {
        e1.setId(123);
        assertEquals(123, e1.getId());
    }

    @Test
    void testEquals() {
        assertEquals(e1, e3);
        assertEquals(e1, e1);
        assertNotEquals(e1, null);
        assertNotEquals(e1, e2);

    }

    @Test
    void testHashCode() {
        assertEquals(e1.hashCode(), e3.hashCode());
        assertEquals(e1.hashCode(), e1.hashCode());
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testToString() {
        assertTrue(e1.toString().contains(Long.toString(e1.getId())));
        assertTrue(e1.toString().contains(code1));
        assertTrue(e1.toString().contains(Integer.toString(amount1)));
        assertTrue(e1.toString().contains(title1));
        assertTrue(e1.toString().contains(date1));
        assertTrue(e1.toString().contains(p1.toString()));
        assertTrue(e1.toString().contains(List.of(p2, p3).toString()));
    }
}