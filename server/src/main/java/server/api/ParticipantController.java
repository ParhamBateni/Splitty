package server.api;

import commons.Event;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;
import server.services.ListenerService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/event/{eventId}/participant")
public class ParticipantController {


    private final EventRepository eventRepository;

    private SimpMessagingTemplate msgs;
    private ListenerService listenerService;

    @Autowired
    ParticipantController(EventRepository e, ListenerService listenerService,
                          SimpMessagingTemplate msgs) {
        this.eventRepository = e;
        this.listenerService = listenerService;
        this.msgs = msgs;
    }


    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Participant>> getUpdates(@PathVariable("eventId")
                                                                      long eventId) {

        // establish the default response
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Participant>>(10000L, noContent);

        Consumer<Object> callback = p -> {
            res.setResult(ResponseEntity.ok(null));
        };
        listenerService.addListener(eventId, callback);
        res.onCompletion(() -> {
            listenerService.removeListener(eventId, callback);
        });

        return res;
    }

    @GetMapping("/")
    public ResponseEntity<Participant> getById(@PathVariable("eventId") long eventId,
                                               @RequestParam("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Participant p = optionalEvent.get()
                    .getParticipants()
                    .stream()
                    .filter(participant -> participant.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (p == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/")
    public ResponseEntity<Participant> updateById(@PathVariable("eventId") long eventId,
                                                  @RequestParam("id") long id,
                                                  @RequestBody Participant updatedParticipant) {
        if (updatedParticipant == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Event event = optionalEvent.get();
            List<Participant> list = event.getParticipants();

            for (Participant p : list) {
                if (p.getId() == id) {
                    p.setName(updatedParticipant.getName());
                    p.setEmail(updatedParticipant.getEmail());
                    p.setIban(updatedParticipant.getIban());
                    p.setBic(updatedParticipant.getBic());
                    p.setEvent(event);
                    Participant updated = p;
                    list.remove(p);
                    list.add(updated);
                    event.setParticipants(list);
                    eventRepository.save(event);
                    listenerService.notify(eventId);
                    msgs.convertAndSend("/events/modifier", event);
                    return ResponseEntity.ok(updated);
                }
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/")
    public ResponseEntity<Participant> deleteById(@PathVariable("eventId") long eventId,
                                                  @RequestParam("id") long id) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Event event = optionalEvent.get();
            List<Participant> list = event.getParticipants();

            for (Participant p : list) {
                if (p.getId() == id) {
                    list.remove(p);
                    event.setParticipants(list);
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

    @PostMapping("/")
    public ResponseEntity<Participant> add(@PathVariable("eventId") long eventId,
                                           @RequestBody Participant participantRequest) {
        if (participantRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isEmpty()) {
            Event event = optionalEvent.get();
            participantRequest.setEvent(event);
            event.addParticipant(participantRequest);

            // when a participant is added, notify the listener
//            listeners.forEach((k, l) -> l.accept(participantRequest));
            listenerService.notify(eventId);

            eventRepository.save(event);
            msgs.convertAndSend("/events/modifier", event);
            return ResponseEntity.ok(participantRequest);
        }
        return ResponseEntity.notFound().build();

    }

}
