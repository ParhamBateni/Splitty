package server.api;

import commons.Event;
import commons.Participant;
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
import server.services.ListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    @Mock
    private EventRepository repo;
    @Mock
    private ListenerService listenerService;
    @Mock
    private SimpMessagingTemplate msgs;
    @InjectMocks
    private ParticipantController controller;

    private Event event;

    private List<Participant> participants;

    @BeforeEach
    void setUp() {
        controller = new ParticipantController(repo, listenerService, msgs);
        participants = new ArrayList<>();
        event = new Event(50,
                "lul",
                new ArrayList<>(),
                new ArrayList<>());
        participants.add(new Participant(123, "Alex", "alex@gmail.com",
                "NL91ABNA0417164300", "INGBNL2A", 100, event));
        participants.add(new Participant(228, "Boris", "boris@yahoo.info",
                "DE75512108001245126199", "ABNANL2A", -100, event));
        event.setParticipants(participants);
    }

    @Test
    void updateParticipantIsNullTest() {
        ResponseEntity<Participant> e = controller.updateById(50, 123, null);
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void updateEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        ResponseEntity<Participant> e = controller.updateById(51, 123, participants.get(0));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void updateParticipantNotExistsTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        ResponseEntity<Participant> e = controller.updateById(50, 100, participants.get(0));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());
    }

//    @Test
//    void updateSuccessfulTest() {
//        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
//        participants.get(0).setName("Test title");
//        ResponseEntity<Participant> e = controller.updateById(50, 123, participants.get(0));
//        assertEquals(HttpStatus.OK, e.getStatusCode());
//        assertEquals(participants.stream().filter(x -> x.getId() == 123).
//                findFirst().get(), controller.getById(50, 123).getBody());
//    }

    @Test
    void getByIdEventNotExistsTest() {

        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        // Act
        ResponseEntity<Participant> e = controller.getById(51, 100);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void getByIdParticipantNotExistsTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        // Act
        ResponseEntity<Participant> e = controller.getById(50, 124);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());
    }

    @Test
    void getByIdSuccessfulTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        ResponseEntity<Participant> e = controller.getById(50, 123);
        // Assert
        assertEquals(HttpStatus.OK, e.getStatusCode());
        assertEquals(participants.stream().filter(x -> x.getId() == 123)
                .findFirst().get(), e.getBody());
    }

    @Test
    void deleteByIdEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        // Act
        ResponseEntity<Participant> e = controller.deleteById(51, 100);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());

    }

    @Test
    void deleteByIdParticipantNotExistsTest() {
        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
        // Act
        ResponseEntity<Participant> e = controller.deleteById(50, 100);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertNull(e.getBody());

    }

//    @Test
//    void deleteByIdSuccessfulTest() {
//        Mockito.when(repo.findById(50L)).thenReturn(Optional.of(event));
//        // Act
//        ResponseEntity<Participant> e = controller.deleteById(50, 228);
//        // Assert
//        assertEquals(HttpStatus.NO_CONTENT, e.getStatusCode());
//        assertNull(e.getBody());
//    }

//    @Test
//    void addSuccessfulTest() {
//        Mockito.when(repo.findById(50L)).thenReturn(Optional.ofNullable(event));
//        ResponseEntity<Participant> e = controller.add(50, participants.getFirst());
//        assertEquals(HttpStatus.OK, e.getStatusCode());
//        assertEquals(participants.getFirst(), e.getBody());
//    }

    @Test
    void addEventNotExistsTest() {
        Mockito.when(repo.findById(51L)).thenReturn(Optional.empty());
        ResponseEntity<Participant> e = controller.add(51, participants.getFirst());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertEquals(null, e.getBody());
    }

    @Test
    void addParticipantIsNullTest() {
        ResponseEntity<Participant> e = controller.add(50, null);
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        assertEquals(null, e.getBody());
    }

//    @Test
//    void getUpdatesTest() {
//        Participant testParticipant = participants.getFirst();
//        DeferredResult<ResponseEntity<Participant>> result = controller.getUpdates();
//        Consumer<Participant> consumer = controller.listeners.get(result.hashCode());
//        consumer.accept(testParticipant);
//        assertEquals(result.getResult(), ResponseEntity.ok(testParticipant));
//    }
}