package commons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    Participant p1, p2, p3, p4, p5, p6, p7;
    String name1, name2;
    String email1, email2;
    String iban1, iban2;
    String bic1, bic2;
    int debt1, debt2;

    Event event;

    long id;

    @BeforeEach
    void setUp() {
        name1 = "Alex";
        email1 = "alex@gmail.com";
        iban1 = "NL91ABNA0417164300";
        bic1 = "INGBNL2A";
        debt1 = 1337;

        id = 1234;

        name2 = "Boris";
        email2 = "boris@yahoo.info";
        iban2 = "DE75512108001245126199";
        bic2 = "ABNANL2A";
        debt2 = 228;
        event = new Event("Party");

        p1 = new Participant(id, name1, email1, iban1, bic1, debt1, event);
        p2 = new Participant(id, name1, email1, iban1, bic1, debt1, event);
        p3 = new Participant(id, name2, email1, iban1, bic1, debt1, event);
        p4 = new Participant(id, name1, email2, iban1, bic1, debt1, event);
        p5 = new Participant(id, name1, email1, iban2, bic1, debt1, event);
        p6 = new Participant(id, name1, email1, iban1, bic2, debt2, event);
        p7 = new Participant(id, name1, email1, iban1, bic1, debt2, event);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void emptyConstructorTest() {
        Participant empty = new Participant();
        assertNotNull(empty);
    }

    @Test
    void constructorTest() {
        assertNotNull(new Participant(name1, email1, iban1, bic1, event));
    }

    @Test
    void testGetEvent() {
        assertEquals(event, p1.getEvent());
    }

    @Test
    void testSetEvent() {
        p1.setEvent(null);
        assertEquals(null, p1.getEvent());
    }

    @Test
    void getName() {
        assertEquals(name1, p1.getName());
    }

    @Test
    void setName() {
        p1.setName(name2);
        assertEquals(name2, p1.getName());
    }

    @Test
    void getEmail() {
        assertEquals(email1, p1.getEmail());
    }

    @Test
    void setEmail() {
        p1.setEmail(email2);
        assertEquals(email2, p1.getEmail());
    }

    @Test
    void getIban() {
        assertEquals(iban1, p1.getIban());
    }

    @Test
    void setIban() {
        p1.setIban(iban2);
        assertEquals(iban2, p1.getIban());
    }

    @Test
    void getBic() {
        assertEquals(bic1, p1.getBic());
    }

    @Test
    void setBic() {
        p1.setBic(bic2);
        assertEquals(bic2, p1.getBic());
    }

    @Test
    void getDebt() {
        assertEquals(debt1, p1.getDebt());
    }

    @Test
    void setDebt() {
        p1.setDebt(debt2);
        assertEquals(debt2, p1.getDebt());
    }

    @Test
    void changeDebt() {
        p1.setDebt(0);
        p1.addDebt(57);
        assertEquals(57, p1.getDebt());
        p1.addDebt(43);
        assertEquals(100, p1.getDebt());
        p1.resolveDebt(200);
        assertEquals(-100, p1.getDebt());
    }

    @Test
    void testEquals() {
        assertEquals(p1, p1);
        assertNotEquals(p1, null);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, p4);
        assertNotEquals(p1, p5);
        assertNotEquals(p1, p6);
        assertNotEquals(p1, p7);
    }

    @Test
    void testGetId() {
        assertEquals(1234, p2.getId());

    }

    @Test
    void testSetId() {
        p1.setId(12345);
        assertEquals(12345, p1.getId());

    }

    @Test
    void testIsNameValid() {
        assertTrue(Participant.isNameValid("parham"));
        assertFalse(Participant.isNameValid("a"));
        assertFalse(Participant.isEmailValid("bb"));
        assertTrue(Participant.isNameValid("Federico"));
    }

    @Test
    void testIsEmailValid() {
        assertTrue(Participant.isEmailValid("parham@gmail.com"));
        assertFalse(Participant.isEmailValid("parham.com"));
        assertTrue(Participant.isEmailValid("parham@yahoo.com"));
        assertFalse(Participant.isEmailValid("abc"));
    }

    @Test
    void testIsIbanValid() {
        assertTrue(Participant.isIbanValid("IE12BOFI90000112345678"));
        assertFalse(Participant.isIbanValid("NL1234"));
        assertFalse(Participant.isIbanValid("123NL"));
        assertTrue(Participant.isIbanValid("GB82WEST12345698765432"));
    }

    @Test
    void testIsBicValid() {
        assertTrue(Participant.isBicValid("ABNANL2A"));
        assertFalse(Participant.isBicValid("NL1234"));
        assertFalse(Participant.isBicValid("123NL"));
        assertTrue(Participant.isBicValid("HBUKGB4B"));
    }

    @Test
    void testHashCode() {
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
        assertNotEquals(p1.hashCode(), p4.hashCode());
        assertNotEquals(p1.hashCode(), p5.hashCode());
        assertNotEquals(p1.hashCode(), p6.hashCode());
    }

    @Test
    void testToString() {
        assertTrue(p1.toString().contains(name1));
        assertTrue(p1.toString().contains(email1));
        assertTrue(p1.toString().contains(iban1));
        assertTrue(p1.toString().contains(bic1));
        assertTrue(p1.toString().contains(Integer.toString(debt1)));
    }
}