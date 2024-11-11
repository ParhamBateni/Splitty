package server.api;


import commons.Event;
import commons.Expense;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;
import server.services.ExpenseService;
import server.services.ListenerService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Controller
@RequestMapping("/event/{eventId}/expense")
public class ExpenseController {
    private final EventRepository eventRepository;
    private ExpenseService expenseService;
    private SimpMessagingTemplate msgs;

    private ListenerService listenerService;

    /**
     * Expense Controller class constructor
     */
    @Autowired
    ExpenseController(EventRepository e,
                      ExpenseService expenseService, ListenerService listenerService,
                      SimpMessagingTemplate msgs) {
        this.eventRepository = e;
        this.expenseService = expenseService;
        this.listenerService = listenerService;
        this.msgs = msgs;
    }

    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Expense>> getUpdates(@PathVariable("eventId")
                                                                  long eventId) {

        // establish the default response
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Expense>>(10000L, noContent);

        Consumer<Object> callback = p -> {
            res.setResult(ResponseEntity.ok(null));
        };
        listenerService.addListener(eventId, callback);
        res.onCompletion(() -> {
            listenerService.removeListener(eventId, callback);
        });

        return res;
    }

    /**
     * API endpoint: get expense by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable("eventId") long eventId,
                                           @PathVariable("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Expense expense = optionalEvent.get()
                    .getExpenses()
                    .stream()
                    .filter(e -> e.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (expense == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(expense);
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * API endpoint: delete expense by id
     */
    @DeleteMapping("/")
    public ResponseEntity<Expense> deleteById(@PathVariable("eventId") long eventId,
                                              @RequestParam("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Event event = optionalEvent.get();
            List<Expense> list = event.getExpenses();

            for (Expense e : list) {
                if (e.getId() == id) {
                    expenseService.rollbackExpense(e);
                    list.remove(e);
                    event.setExpenses(list);
                    eventRepository.save(event);
                    listenerService.notify(eventId);
                    msgs.convertAndSend("/events/modifier", event);
                    return ResponseEntity.noContent().build();
                }
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * API endpoint: post expense
     */
    @PostMapping("/")
    public ResponseEntity<Expense> add(@PathVariable("eventId") long eventId,
                                       @RequestBody Expense expenseRequest) {
        if (expenseRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            Hibernate.initialize(event.getExpenses());
            expenseRequest.setEvent(event);
            List<Expense> expenses = event.getExpenses();
            expenses.add(expenseRequest);
            event.setExpenses(expenses);
            event = eventRepository.save(event);
            listenerService.notify(eventId);
            if (event != null) {
                expenseService.applyExpense(event.getExpenses().getLast());
            }
            msgs.convertAndSend("/events/modifier", event);
            return ResponseEntity.ok(event.getExpenses().getLast());
        }
        return ResponseEntity.notFound().build();

    }

    /**
     * API endpoint: update expense by id
     */

    @PutMapping("/")
    public ResponseEntity<Expense> updateById(@PathVariable("eventId") long eventId,
                                              @RequestParam("id") long id,
                                              @RequestBody Expense updatedExpense) {
        if (updatedExpense == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Event event = optionalEvent.get();
            List<Expense> list = event.getExpenses();

            for (Expense existingExpense : list) {
                if (existingExpense.getId() == id) {
                    expenseService.rollbackExpense(existingExpense);
                    existingExpense.setPayer(updatedExpense.getPayer());
                    existingExpense.setAmount(updatedExpense.getAmount());
                    existingExpense.setCurrency(updatedExpense.getCurrency());
                    existingExpense.setTitle(updatedExpense.getTitle());
                    existingExpense.setDate(updatedExpense.getDate());
                    existingExpense.setParticipants(updatedExpense.getParticipants());
                    existingExpense.setEvent(event);
                    Expense updated = existingExpense;
                    expenseService.applyExpense(updated);
                    listenerService.notify(eventId);
                    list.remove(existingExpense);
                    list.add(updated);
                    event.setExpenses(list);
                    event = eventRepository.save(event);
                    msgs.convertAndSend("/events/modifier", event);
                    return ResponseEntity.ok(event.getExpenses().getLast());
                }
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();

    }

    /**
     * API endpoint: return all expenses from an event given the event id
     */
    @GetMapping("/all")
    public ResponseEntity<List<Expense>> getExpensesByEventId(@PathVariable("eventId")
                                                              long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        return optionalEvent.map(event ->
                        ResponseEntity.ok(event.getExpenses()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
