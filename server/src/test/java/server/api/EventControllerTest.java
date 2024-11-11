package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.services.ExpenseService;
import server.services.ListenerService;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventRepository repo;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private ListenerService listenerService;
    @Mock
    private SimpMessagingTemplate msgs;

    @InjectMocks
    private EventController sut;

    private Event event;

    private List<Expense> testExpenses;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(repo);
        sut = new EventController(repo, expenseService, listenerService, msgs);
        testExpenses = new ArrayList<>();
        event = new Event(50,
                "lul",
                new ArrayList<>(),
                new ArrayList<>());
        testExpenses.add(new Expense(
                new Participant(),
                Currency.getInstance("USD"),
                10,
                "food",
                "10 10 2020",
                new ArrayList<>()));
        event.setExpenses(testExpenses);
        event.setId(50L);
    }

    @Test
    void updateEventTest() {

        ArrayList t2 = new ArrayList<>();
        Event event2 = new Event(50, // different id
                "Flying event",
                new ArrayList<>(),
                new ArrayList<>());
        t2.add(new Expense(
                new Participant(),
                Currency.getInstance("USD"),
                100,
                "tickets",
                "20 10 2020",
                new ArrayList<>()));
        event2.setExpenses(t2);
        event2.setId(50L);
        Mockito.doNothing().when(msgs).convertAndSend(Mockito.anyString(), (Object) Mockito.any());
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        Mockito.when(repo.save(event)).thenReturn(event2);
        ResponseEntity<Event> e = sut.updateEvent(50L, event2);

        assertEquals(event2, e.getBody());

    }

    @Test
    void updateDifferentEventTest() {
        ArrayList t2 = new ArrayList<>();
        Event event2 = new Event(10, // different id
                "Flying event",
                new ArrayList<>(),
                new ArrayList<>());
        t2.add(new Expense(
                new Participant(),
                Currency.getInstance("USD"),
                100,
                "tickets",
                "20 10 2020",
                new ArrayList<>()));
        event2.setExpenses(t2);

        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));

        ResponseEntity<Event> e = sut.updateEvent(50L, event2);

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void updateEventEmptyTest() {

        ResponseEntity<Event> e = sut.updateEvent(50L, null);
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void updateEventNotExistingTest() {

        ArrayList t2 = new ArrayList<>();
        Event event2 = new Event(50,
                "Flying event",
                new ArrayList<>(),
                new ArrayList<>());
        t2.add(new Expense(
                new Participant(),
                Currency.getInstance("USD"),
                100,
                "tickets",
                "20 10 2020",
                new ArrayList<>()));
        event2.setExpenses(t2);

        Mockito.when(repo.findById(50L)).thenReturn(Optional.empty());

        ResponseEntity<Event> e = sut.updateEvent(50L, event2);

        assertNull(e.getBody());


    }

//    @Test
//    void addTest() throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(event);
//
//        Mockito.when(repo.save(Mockito.any(Event.class))).thenReturn(event);
//
//        ResponseEntity<Event> e = sut.add(json);
//        assertEquals(event, e.getBody());
//
//    }

    @Test
    void addNothingTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(null);

        ResponseEntity<Event> e = sut.add(json);
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void addBadTest() {

        assertThrows(RuntimeException.class, () -> {
            ResponseEntity<Event> e = sut.add("invalid");
        });
    }

    @Test
    void deleteByEventIdEmptyTest() {

        Mockito.when(repo.findById(50L)).thenReturn(Optional.empty());
        // Act
        ResponseEntity<Event> e = sut.deleteById(50L);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void deleteByEventIdTest() {
        Optional<Event> optionalEvent = Optional.of(event);
        Mockito.when(repo.findById(50L)).thenReturn(optionalEvent);
        Mockito.doNothing().when(repo).deleteById(50L);
        // Act
        ResponseEntity<Event> e = sut.deleteById(50L);
        // Assert
        assertNull(e.getBody());
    }

    @Test
    void getByIdTest() {

        Optional<Event> optionalEvent = Optional.of(event);
        Mockito.when(repo.findById(50L)).thenReturn(optionalEvent);

        // Act
        ResponseEntity<Event> e = sut.getById(50L);
        // Assert
        assertEquals(event, e.getBody());

    }

    @Test
    void getByIdEmptyTest() {

        Mockito.when(repo.findById(50L)).thenReturn(Optional.empty());
        // Act
        ResponseEntity<Event> e = sut.getById(50L);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void getByLinkTest() {

        List<Event> testArr = new ArrayList<>();
        testArr.add(event);

        Mockito.when(repo.findByLink("")).thenReturn(testArr);
        // Act
        ResponseEntity<Event> e = sut.getByLink("");
        // Assert
        assertEquals(event, e.getBody());
    }

    @Test
    void getByLinkEmptyTest() {

        Mockito.when(repo.findByLink("")).thenReturn(new ArrayList<>());
        // Act
        ResponseEntity<Event> e = sut.getByLink("");
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());

    }

}
