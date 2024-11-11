/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import client.SplittyConfig;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.PaymentInstruction;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * ServerUtils class
 */
public class ServerUtils {
    public static String HOST;

    static {
        String host1 = "";
        System.out.println("HOST: " + SplittyConfig.getServerUrl());
        host1 = SplittyConfig.getServerUrl();
        if (host1 == null) {
            System.out.println("SEVER URL not found/not valid, redirecting to localhost");
            host1 = "localhost:8080/";
        }
        HOST = host1;
    }

    private final String SERVER = "http://" + HOST;
    private final String WEBSOCKET = "ws://" + HOST + "websocket";

    //establishes a session with the websocket
    private StompSession session;
    public Long pollingId = 0L;

    public Event addEvent(Event event) {
        event.setDateCreated(new Date());
        event.setLastAction(new Date());
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(event, APPLICATION_JSON), Event.class);

    }

    public Event getEventById(long id) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{id}")
                .resolveTemplate("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Event event = response.readEntity(Event.class);
            event.getParticipants().forEach(p -> p.setEvent(event));
            return event;
        }
        return null;
    }

    public Event getEventByLink(String link) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/")
                .resolveTemplate("link", link)
                .queryParam("link", link)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON).get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(Event.class);
        }
        return null;
    }

    public Event updateEvent(Event event) {
        event.setLastAction(new Date());
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{id}")
                .resolveTemplate("id", event.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    public Event deleteEvent(long id) {
        if (getEventById(id) == null) {
            return null;
        }
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{id}")
                .resolveTemplate("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(Event.class);
    }

    public List<Event> getEvents() {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            List<Event> events = response.readEntity(new GenericType<List<Event>>() {
            });
            return events;
        }
        return new ArrayList<>();
    }


    public Participant addParticipant(Participant participant) {
        Event event = getEventById(participant.getEvent().getId());
        Participant p = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{eventId}/participant/")
                .resolveTemplate("eventId", event.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(participant, APPLICATION_JSON), Participant.class);
        event = getEventById(participant.getEvent().getId());
        event.setLastAction(new Date());
        updateEvent(event);
        return p;
    }

    public int check() {
        try {
            Response r = ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER)
                    .path("/admin/check")
                    .request()
                    .get();
            return r.getStatus();
        } catch (ProcessingException e) {
            return -1;
        }
    }

    public Participant updateParticipant(Participant participant) {
        Event event = getEventById(participant.getEvent().getId());
        System.out.println("Participant ID: " + participant.getId());
        System.out.println("EVENT ID: " + participant.getEvent().getId());
        Participant p = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{eid}/participant/")
                .resolveTemplate("eid", participant.getEvent().getId())
                .queryParam("id", participant.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(participant, APPLICATION_JSON), Participant.class);
        event = event = getEventById(participant.getEvent().getId());
        event.setLastAction(new Date());
        updateEvent(event);
        return p;
    }

    public void deleteParticipant(Participant participant) {
        Event event = getEventById(participant.getEvent().getId());
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{eventId}/participant/")
                .resolveTemplate("eventId", participant.getEvent().getId())
                .queryParam("id", participant.getId())
                .request()
                .accept(APPLICATION_JSON)
                .delete();
        event = event = getEventById(participant.getEvent().getId());
        event.setLastAction(new Date());
        updateEvent(event);
    }

    /**
     * Executes a long pooling request
     *
     * @param id       event id
     * @param consumer Consumer of participants
     */
    public void registerForUpdates(String target, Long id, Consumer<Participant> consumer,
                                   String source) {

        var callback = new InvocationCallback<Response>() {
            private final Long currentId = pollingId;

            @Override
            public void completed(Response response) {
                System.out.println(currentId + " " + pollingId + " " + source);
                if (!currentId.equals(pollingId)) {
                    return;
                }
                if (response.getStatus() == 200) {
                    Participant p = response.readEntity(Participant.class);
                    // save the object into the consumer
                    consumer.accept(p);
                }
                if (response.getStatus() == 200 || response.getStatus() == 204) {
                    loopRequest();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                System.out.println("Long polling failed : " + throwable.toString());
            }

            public void loopRequest() {
                ClientBuilder.newClient(new ClientConfig())
                        .target(SERVER).path(target)
                        .resolveTemplate("eventId", id).request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .async()
                        .get(this);
            }
        };

        callback.loopRequest();
    }

    public Expense addExpense(Expense expense) {
        Event event = getEventById(expense.getEvent().getId());
        Expense e = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{eventId}/expense/")
                .resolveTemplate("eventId", expense.getEvent().getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
        event = event = getEventById(expense.getEvent().getId());
        event.setLastAction(new Date());
        updateEvent(event);
        return e;

    }

    public Expense updateExpense(Expense expense) {
        Event event = getEventById(expense.getEvent().getId());
        System.out.println("EXPENSE ID: " + expense.getId());
        System.out.println("EVENT ID: " + expense.getEvent().getId());
        Expense e = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{eid}/expense/")
                .resolveTemplate("eid", expense.getEvent().getId())
                .queryParam("id", expense.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(expense, APPLICATION_JSON), Expense.class);
        event = event = getEventById(expense.getEvent().getId());
        event.setLastAction(new Date());
        updateEvent(event);
        return e;
    }

    public void deleteExpense(Expense expense) {
        Event event = getEventById(expense.getEvent().getId());;
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/event/{eventId}/expense/")
                .resolveTemplate("eventId", expense.getEvent().getId())
                .queryParam("id", expense.getId())
                .request()
                .accept(APPLICATION_JSON)
                .delete();
        event = event = getEventById(expense.getEvent().getId());
        event.setLastAction(new Date());
        updateEvent(event);
    }

    public List<PaymentInstruction> getPaymentInstructionsByEventId(long id) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER)
                .path("event/" + id + "/instructions")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if (response.getStatus() != 200) {
            // Handle error response
            return new ArrayList<>();
        }
        List<PaymentInstruction> instructions = response.readEntity(
                new GenericType<>() {
                });
        response.close();
        return instructions;
    }

    public boolean checkAdminPassword(String password) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/admin")
                .queryParam("password", password)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Boolean.class);

    }

    /**
     * Finds the correct smtp server and port for the email domain
     *
     * @return the correct server and port
     * @throws IOException throws an error if there is a problem with reading form config file
     */
    public String[] getDomain(String email) {
        String host;
        String port;
        Scanner scanner = new Scanner(email);
        scanner.useDelimiter("@");
        scanner.next();
        String domain = scanner.next();
        if (domain.contains("outlook.com")) {
            host = "smtp-mail.outlook.com";
            port = "587";
        } else if (domain.contains("gmail.com")) {
            host = "smtp.gmail.com";
            port = "587";
        } else if (domain.contains("yahoo.com")) {
            host = "smtp.mail.yahoo.com";
            port = "587";
        } else if (domain.contains("student.tudelft.nl")) {
            host = "mx11.surfmailfilter.nl";
            port = "10";
        } else {
            host = "vds";
            port = "153";
        }
        String[] result = new String[2];
        result[0] = host;
        result[1] = port;
        return result;
    }

    /**
     * Sends invitation emails to all entries in the emails list
     *
     * @param emails list of emails
     * @param event  the current event
     */
    public void sendInviteToEmails(List<String> emails, Event event, String sourceEmail,
                                   String password) {
        for (String email : emails) {
            Properties props = System.getProperties();
            String username = sourceEmail;
            String[] name = email.split("@");
            String[] hostName = username.split("@");
            String topic = "Hello " + name[0] + "!";
            String body = "You have been invited to join an event on the Splitty server "
                    + HOST + ". With invite link: " +
                    event.getLink() +
                    " by " + hostName[0];
            String[] to = {email};
            String[] domain = getDomain(sourceEmail);
            String host = domain[0];
            String port = domain[1];
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(username));
                InternetAddress[] toAddress = new InternetAddress[to.length];
                // To get the array of addresses
                for (int i = 0; i < to.length; i++) {
                    toAddress[i] = new InternetAddress(to[i]);
                }
                for (InternetAddress address : toAddress) {
                    message.addRecipient(Message.RecipientType.TO, address);
                }
                message.setSubject(topic);
                message.setText(body);
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(
                        username));
                message.saveChanges();
                Transport transport = session.getTransport("smtp");
                System.out.println("get protocol");
                transport.connect(host, sourceEmail, password);
                System.out.println("get host,from and password");
                transport.sendMessage(message, message.getAllRecipients());
                System.out.println("get recipients");
                transport.close();
                System.out.println("close");
                System.out.println("Email Sent Successfully to " + email + "!");
            } catch (AuthenticationFailedException e) {
                System.out.println("Incorrect email or password!");
            } catch (MessagingException e) {
                System.out.println("There is a problem sending email to " + email);
            } finally {
                System.out.println("Complete Process");
            }
        }
    }

    public Map<String, Double> getExchangeRates(String date) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER)
                .path("/rates/{date}")
                .resolveTemplate("date", date)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON).get();
        if (response.getStatus() != 200) {
            return null;
        }
        Map<String, Double> rates = response.readEntity(Map.class);
        return rates;
    }

    public StompSession connect(String url) {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {

            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        if (session == null) {
            session = connect(WEBSOCKET);
        }
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    // sends to the Stomp endpoint object o to the destination path - dest
    public void send(String dest, Object o) {
        if (session == null) {
            session = connect(WEBSOCKET);
        }
        session.send(dest, o);
    }
}