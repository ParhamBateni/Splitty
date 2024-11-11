package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private EventRepository repo;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private ListenerService listenerService;
    @Mock
    private SimpMessagingTemplate msgs;

    @InjectMocks
    private ExpenseController sut;

    private Event event;

    private List<Expense> testExpenses;

    @BeforeEach
    void setUp() {
        sut = new ExpenseController(repo, expenseService, listenerService, msgs);
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
        testExpenses.getFirst().setId(100L);
        event.setExpenses(testExpenses);

//        Mockito.doNothing().when(Mockito.any(ExpenseService.class));
    }

    @Test
    void getByIdEventNotExistsTest() {
        // Arrange
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Expense> e = sut.getById(51, 100);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertEquals(null, e.getBody());
    }

    @Test
    void getByIdExpenseNotExistsTest() {
        // Arrange
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));

        // Act
        ResponseEntity<Expense> e = sut.getById(50, 101L);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertEquals(null, e.getBody());
    }

    @Test
    void getByIdSuccessfulTest() {
        // Arrange
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));

        // Act
        ResponseEntity<Expense> e = sut.getById(50, 100);
        // Assert
        assertEquals(HttpStatus.OK, e.getStatusCode());
        assertEquals(testExpenses.stream().filter(x -> x.getId() == 100)
                .findFirst().get(), e.getBody());
    }


    @Test
    void updateExpenseIsNullTest() {
        ResponseEntity<Expense> e = sut.updateById(50, 123, null);
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void updateEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        ResponseEntity<Expense> e = sut.updateById(51, 123, testExpenses.getFirst());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void updateExpenseNotExistsTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        ResponseEntity<Expense> e = sut.updateById(50, 123, testExpenses.getFirst());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());
    }

//    @Test
//    void updateSuccessfulTest() {
//        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
//        testExpenses.getFirst().setTitle("Test title");
//        ResponseEntity<Expense> e = sut.updateById(50, 100L, testExpenses.getFirst());
//        assertEquals(HttpStatus.NO_CONTENT, e.getStatusCode());
//        assertNull(e.getBody());
//        assertEquals(testExpenses.stream().filter(x -> x.getId() == 100).
//                findFirst().get(), sut.getById(50, 100).getBody());
//    }

    @Test
    void deleteByIdEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        // Act
        ResponseEntity<Expense> e = sut.deleteById(51, 100);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void deleteByIdExpenseNotExistsTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        // Act
        ResponseEntity<Expense> e = sut.deleteById(50, 101);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void deleteByIdSuccessfulTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        // Act
        ResponseEntity<Expense> e = sut.deleteById(50, 100);
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Disabled
    @Test
    void addSuccessfulTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.ofNullable(event));
        ResponseEntity<Expense> e = sut.add(50, testExpenses.getFirst());
        assertEquals(HttpStatus.OK, e.getStatusCode());
        assertEquals(testExpenses.getFirst(), e.getBody());
    }

    @Test
    void addEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        ResponseEntity<Expense> e = sut.add(51, testExpenses.getFirst());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertEquals(null, e.getBody());
    }

    @Test
    void addExpenseIsNullTest() {
        ResponseEntity<Expense> e = sut.add(50, null);
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertEquals(null, e.getBody());
    }

    @Test
    void getAllSuccessfulTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        ResponseEntity<List<Expense>> expenses = sut.getExpensesByEventId(50L);
        assertEquals(HttpStatus.OK, expenses.getStatusCode());
        assertEquals(testExpenses.size(), expenses.getBody().size());
    }

    @Test
    void getAllEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        ResponseEntity<List<Expense>> expenses = sut.getExpensesByEventId(51L);
        assertEquals(HttpStatus.NOT_FOUND, expenses.getStatusCode());
        assertEquals(null, expenses.getBody());
    }
//
//    @Test
//    void websocketAdd() {
//        Expense expense = testExpenses.getFirst();
//        assertEquals(expense, sut.addExpense(expense.getId(), expense));
//    }
}