package commons;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Event implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private Date dateCreated;
    private Date lastAction;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Participant> participants;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Expense> expenses;

    private final String link = UUID.randomUUID().toString().substring(0, 6);

    public Event(long id, String title, List<Participant> participants,
                 List<Expense> expenses) {
        this.id = id;
        this.title = title;
        this.participants = new ArrayList<>(participants);
        this.expenses = new ArrayList<>(expenses);
    }

    public Event(String title, List<Participant> participants,
                 List<Expense> expenses) {
        this.title = title;
        this.participants = new ArrayList<>(participants);
        this.expenses = new ArrayList<>(expenses);
    }

    public Event(String title) {
        this.title = title;
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }


    //TODO: check if we really need empty constructor

    public Event() {
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        if (this.participants == participants) {
            return;
        }
        this.participants.clear();
        this.participants.addAll(participants);
    }

    public void addParticipant(Participant participant) {
        if (participants != null) {
            participants.add(participant);
        }
    }

    public void removeParticipant(Participant participant) {
        if (participants != null && participants.contains(participant)) {
            participants.remove(participant);
        }
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        if (this.expenses == expenses) {
            return;
        }
        this.expenses.clear();
        this.expenses.addAll(expenses);
    }

    public void addExpense(Expense expense) {
        if (expenses != null) {
            expenses.add(expense);
        }
    }

    public void removeExpense(Expense expense) {
        if (expenses != null) {
            expenses.remove(expense);
        }
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return id == event.id && Objects.equals(title, event.title)
                && Objects.equals(participants, event.participants)
                && Objects.equals(expenses, event.expenses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, participants, expenses);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", participants=" + participants +
                ", expenses=" + expenses +
                '}';
    }


}