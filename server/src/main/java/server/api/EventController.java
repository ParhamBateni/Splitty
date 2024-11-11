package server.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;

import commons.Expense;
import commons.Participant;
import commons.PaymentInstruction;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;
import server.services.ExpenseService;
import server.services.ListenerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/event")
public class EventController {


    private EventRepository eventRepository;
    private ExpenseService expenseService;
    private ListenerService listenerService;

    private SimpMessagingTemplate msgs;

    @Autowired
    EventController(EventRepository e, ExpenseService expenseService,
                    ListenerService listenerService, SimpMessagingTemplate msgs) {
        this.eventRepository = e;
        this.expenseService = expenseService;
        this.listenerService = listenerService;
        this.msgs = msgs;
    }


    @GetMapping("/{id}/updates")
    public DeferredResult<ResponseEntity<Event>> getUpdates(@PathVariable("id")
                                                            long eventId) {

        // establish the default response
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Event>>(10000L, noContent);

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
     * API endpoint: get event by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (!optionalEvent.isEmpty()) {
            return ResponseEntity.ok(optionalEvent.get());
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAll() {
        List<Event> eventList = eventRepository.findAll();
        if (eventList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventList);
    }

    @GetMapping("/")
    public ResponseEntity<Event> getByLink(@RequestParam(value = "link", required = false)
                                           String link) {
        List<Event> eventList = eventRepository.findByLink(link);
        if (eventList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventList.get(0));
    }

    @MessageMapping("event/terminator")
    public void deleteEvent(long eventId) {
        // when an expense request is received, create the expense and return it for the topic
        deleteById(eventId);
    }


    /**
     * API endpoint: update event by id
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable("id") long id,
                                             @RequestBody Event updatedEvent) {

        if (updatedEvent == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Event existingEvent = optionalEvent.get();

        if (existingEvent.getId() != updatedEvent.getId()) {
            return ResponseEntity.badRequest().build();
        }

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDateCreated(updatedEvent.getDateCreated());
        existingEvent.setLastAction(updatedEvent.getLastAction());
        existingEvent.setExpenses(updatedEvent.getExpenses());
        existingEvent.setParticipants(updatedEvent.getParticipants());

        Event saved = eventRepository.save(existingEvent);
        listenerService.notify(id);
        msgs.convertAndSend("/events/modifier", saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * API endpoint: delete event by id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Event> deleteById(@PathVariable("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        eventRepository.deleteById(id);
        listenerService.notify(id);
        msgs.convertAndSend("/events/terminator", optionalEvent.get());
        msgs.convertAndSend("/events/terminator/event", id);
        return ResponseEntity.ok().build();
    }

    /**
     * API endpoint: get payment instructions by event id
     */
    @GetMapping("/{id}/instructions")
    public ResponseEntity<List<PaymentInstruction>> getPaymentInstructions(
            @PathVariable("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (!optionalEvent.isEmpty()) {
            return ResponseEntity.ok(expenseService.getPaymentInstructions(
                    optionalEvent.get().getId()));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * API endpoint: post event
     */
    @PostMapping("/")
    public ResponseEntity<Event> add(@RequestBody String jsonEvent) {
        // TODO add more conditions if needed to verify event
        ObjectMapper objectMapper = new ObjectMapper();
        Event event = null;
        try {
            event = objectMapper.readValue(jsonEvent, Event.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (event == null) {
            return ResponseEntity.badRequest().build();
        }
        Event eventCopy = new Event(event.getTitle());
        eventCopy.setDateCreated(new Date());
        eventCopy.setLastAction(new Date());
        eventCopy = eventRepository.save(eventCopy);
        eventCopy = eventRepository.findById(eventCopy.getId()).get();
        for (var p : event.getParticipants()) {
            Participant participantCopy = new Participant(p.getName(), p.getEmail(), p.getIban(),
                    p.getBic(), eventCopy);
            participantCopy.setDebt(p.getDebt());
            participantCopy.setExpenseShare(p.getExpenseShare());
            eventCopy.addParticipant(participantCopy);
        }
        eventCopy = eventRepository.findById(eventCopy.getId()).get();
        BiFunction<Long, Event, Integer> findIndex = (index, event_) ->
                IntStream.range(0, event_.getParticipants().size())
                        .filter(i -> event_.getParticipants().get(i).getId() == index)
                        .findFirst().orElse(-1);
        for (var e : event.getExpenses()) {
            Expense expenseCopy = new Expense(eventCopy.getParticipants()
                    .get(findIndex.apply(e.getPayer().getId(), event)),
                    e.getCurrency(), e.getAmount(), e.getTitle(), e.getDate(),
                    new ArrayList<>(), e.getIsSettlement());
            expenseCopy.setAmountInEuro(e.getAmountInEuro());
            List<Participant> participants = new ArrayList<Participant>();
            for (var p : e.getParticipants()) {
                participants.add(eventCopy.getParticipants().get(findIndex.apply(p.getId(),
                        event)));
            }
            expenseCopy.setParticipants(participants);
            expenseCopy.setEvent(eventCopy);

            eventCopy.addExpense(expenseCopy);
        }
        eventCopy = eventRepository.save(eventCopy);
        Event saved = eventRepository.getReferenceById(eventCopy.getId());
        listenerService.notify(saved.getId());
        msgs.convertAndSend("/events/collector", event);
        return ResponseEntity.ok(saved);
    }

}
