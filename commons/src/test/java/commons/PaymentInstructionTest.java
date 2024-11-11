package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInstructionTest {

    Participant p1, p2, p3, p4, p5, p6, p7;
    String name1, name2;
    String email1, email2;
    String iban1, iban2;
    String bic1, bic2;
    int debt1, debt2;
    Event event;
    long id;
    int amount;
    PaymentInstruction payment1, payment2, payment3;

    @BeforeEach
    void setUp() {

        name1 = "Alex";
        email1 = "alex@gmail.com";
        iban1 = "NL91ABNA0417164300";
        bic1 = "INGBNL2A";
        debt1 = 1337;

        amount = 12;
        id = 1234;

        name2 = "Boris";
        email2 = "boris@yahoo.info";
        iban2 = "DE75512108001245126199";
        bic2 = "ABNANL2A";
        debt2 = 228;
        event = new Event("Party");

        p1 = new Participant(id, name1, email1, iban1, bic1, debt1, event);
        p2 = new Participant(id, name1, email2, iban2, bic1, debt1, event);
        p3 = new Participant(id, name2, email1, iban2, bic1, debt1, event);


        payment1 = new PaymentInstruction(event, p1, p2, amount);
        payment2 = new PaymentInstruction(event, p2, p3, amount);
        payment3 = new PaymentInstruction(event, p1, p2, amount);

    }

    @Test
    void testEmptyConstructor() {
        assertNotNull(new PaymentInstruction());
    }
    @Test
    void testConstructor() {
        assertNotNull(new PaymentInstruction(event, p1, p1, amount));
    }

    @Test
    void getEvent() {
        assertEquals(event, payment1.getEvent());
    }

    @Test
    void setEvent() {
        payment1.setEvent(null);
        assertEquals(null, payment1.getEvent());
    }

    @Test
    void getFrom() {
        assertEquals(p1, payment1.getFrom());
    }

    @Test
    void setFrom() {
        payment1.setFrom(null);
        assertEquals(null, payment1.getFrom());
    }

    @Test
    void getTo() {
        assertEquals(p2, payment1.getTo());
    }

    @Test
    void setTo() {
        payment1.setTo(null);
        assertEquals(null, payment1.getTo());
    }

    @Test
    void getAmount() {
        assertEquals(12, payment1.getAmount());
    }

    @Test
    void setAmount() {
        payment1.setAmount(123);
        assertEquals(123, payment1.getAmount());

    }
    @Test
    void testEquals() {
        assertEquals(payment1, payment1);
        assertEquals(payment1, payment3);
        assertNotEquals(payment1, payment2);
        assertNotEquals(payment1, null);

    }

    @Test
    void testHashCode() {
        assertEquals(payment1.hashCode(), payment1.hashCode());
        assertEquals(payment1.hashCode(), payment3.hashCode());
        assertNotEquals(payment1.hashCode(), payment2.hashCode());
    }
}