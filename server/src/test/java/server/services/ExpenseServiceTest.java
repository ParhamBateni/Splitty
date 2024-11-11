package server.services;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    ExpenseService expenseService;
    Event event, tevent;
    Participant p1, p2, p3, tp1, tp2, tp3;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(eventRepository);
        p1 = new Participant(1, "Alex", "alex@gmail.com", "iban1", "bic1", 100, null);
        tp1 = new Participant(1, "Alex", "alex@gmail.com", "iban1", "bic1", 100, null);
        p2 = new Participant(2, "Bob", "bob@yahoo.com", "iban2", "bic2", -150, null);
        tp2 = new Participant(2, "Bob", "bob@yahoo.com", "iban2", "bic2", -150, null);
        p3 = new Participant(3, "Charlie", "charlie@mail.info", "iban3", "bic3", 50, null);
        tp3 = new Participant(3, "Charlie", "charlie@mail.info", "iban3", "bic3", 50, null);
        event = new Event("cool event", List.of(p1, p2, p3), new ArrayList<Expense>());
        tevent = new Event("cool event", List.of(tp1, tp2, tp3), new ArrayList<Expense>());
    }

    @Test
    void getDistributionTest() {
        Currency currency = Currency.getInstance("EUR");
        Expense expense = new Expense(p1, currency, 100, "bla", "bla", List.of(p1, p2, p3));
        expense.setAmountInEuro(100);
        List<Integer> distribution = expenseService.getDistribution(expense);
        assertEquals(3, distribution.size());
        int sum = distribution.stream().mapToInt(Integer::intValue).sum();
        assertEquals(100, sum);
    }


    @Test
    void getPaymentInstructionsTest() {
        when(eventRepository.getReferenceById(anyLong())).thenReturn(tevent);

        List<PaymentInstruction> instructions = expenseService.getPaymentInstructions(123);
        for (var i : instructions) {
            long from = ((Participant) i.getFrom()).getId();
            long to = ((Participant) i.getTo()).getId();
            Integer amount = (Integer) i.getAmount();
            Participant pfrom = event.getParticipants().stream()
                    .filter(x -> x.getId() == from).toList().get(0);
            Participant pto = event.getParticipants().stream()
                    .filter(x -> x.getId() == to).toList().get(0);
            pfrom.resolveDebt(amount);
            pto.addDebt(amount);
        }
        assertEquals(0, p1.getDebt());
        assertEquals(0, p2.getDebt());
        assertEquals(0, p3.getDebt());
        assertTrue(instructions.size() < event.getParticipants().size());
    }

    @Test
    void rollbackExpenseTestShares() {
        when(eventRepository.getReferenceById(anyLong())).thenReturn(tevent);

        Expense expense = new Expense(p1, Currency.getInstance("EUR"), 99,
                "bla", "bla", List.of(p1, p2, p3), event, false);
        expense.setAmountInEuro(99);

        event.addExpense(expense);

        expenseService.applyExpense(expense);

        assertEquals(0, p1.getExpenseShare());
        assertEquals(0, p2.getExpenseShare());
        assertEquals(0, p3.getExpenseShare());

        expenseService.rollbackExpense(expense);

        assertEquals(0, p1.getExpenseShare());
        assertEquals(0, p2.getExpenseShare());
        assertEquals(0, p3.getExpenseShare());
    }

    @Test
    void rollbackExpenseTestEvent() {

        when(eventRepository.getReferenceById(anyLong())).thenReturn(tevent);

        Expense expense = new Expense(p1, Currency.getInstance("EUR"), 99,
                "bla", "bla", List.of(p1, p2, p3), event, false);
        expense.setAmountInEuro(99);

        event.addExpense(expense);

        expenseService.applyExpense(expense);

        List<Expense> result = new ArrayList<>(List.of(expense));

        assertEquals(result, event.getExpenses());

        expenseService.rollbackExpense(expense);

        assertEquals(result, event.getExpenses());
    }

    @Test
    void applyExpenseTest() {
        when(eventRepository.getReferenceById(anyLong())).thenReturn(tevent);

        Expense expense = new Expense(p1, Currency.getInstance("EUR"), 99,
                "bla", "bla", List.of(p1, p2, p3), event, false);
        expense.setAmountInEuro(99);

//        expenseService.applyExpense(expense);
//        assertEquals(0, p1.getExpenseShare());
//        assertEquals(0, p2.getExpenseShare());
//        assertEquals(0, p3.getExpenseShare());

        event.addExpense(expense);

        expenseService.applyExpense(expense);
        assertEquals(0, p1.getExpenseShare());
        assertEquals(0, p2.getExpenseShare());
        assertEquals(0, p3.getExpenseShare());
    }
}