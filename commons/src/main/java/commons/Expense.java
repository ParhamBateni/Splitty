package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Currency;
import java.util.List;
import java.util.Objects;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Currency currency;
    private int amount;
    private int amountInEuro;
    private String title;
    private String date;
    private boolean isSettlement;
    @ManyToOne(fetch = FetchType.EAGER)
    private Participant payer;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Participant> participants;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private Event event;

    public Expense(Participant payer, Currency currency,
                   int amount, String title, String date,
                   List<Participant> participants, boolean isSettlement) {
        this.payer = payer;
        this.currency = currency;
        this.amount = amount;
        this.title = title;
        this.date = date;
        this.participants = participants;
        this.isSettlement = isSettlement;
    }

    public Expense(Participant payer, Currency currency,
                   int amount, String title, String date,
                   List<Participant> participants) {
        this.payer = payer;
        this.currency = currency;
        this.amount = amount;
        this.title = title;
        this.date = date;
        this.participants = participants;
        this.isSettlement = false;
    }

    public Expense(Participant payer, Currency currency,
                   int amount, String title, String date,
                   List<Participant> participants, Event event, boolean isSettlement) {
        this.payer = payer;
        this.currency = currency;
        this.amount = amount;
        this.title = title;
        this.date = date;
        this.participants = participants;
        this.event = event;
        this.isSettlement = isSettlement;
    }

    @SuppressWarnings("unused")
    public Expense() {

    }

    /**
     * Getter method for this.currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Setter method for this.currency
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Getter method for this.amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Setter method for this.amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
        if (currency.getCurrencyCode().equals("EUR")) {
            setAmountInEuro(amount);
        }
    }

    /**
     * Getter method for this.title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter method for this.title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter method for this.date
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter method for this.date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Getter method for this.payer
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Setter method for this.payer
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Getter method for participants
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * Setter for participants
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Getter method for this.id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter method for this.id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter method for this.isSettlement
     */
    public boolean getIsSettlement() {
        return isSettlement;
    }

    /**
     * Setter method for this.isSettlement
     */
    public void setIsSettlement(boolean isSettlement) {
        this.isSettlement = isSettlement;
    }

    /**
     * Equals method: compares if two expenses are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Expense expense = (Expense) o;
        return amount == expense.amount &&
                Objects.equals(currency, expense.currency) &&
                Objects.equals(title, expense.title) &&
                Objects.equals(date, expense.date) &&
                Objects.equals(payer, expense.payer) &&
                Objects.equals(participants, expense.participants);
    }

    /**
     * Hashcode method: returns int value of the hashcode of the entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(currency, amount, title, date, payer, participants, id, isSettlement);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "currency=" + currency +
                ", amount=" + amount +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", payer=" + payer +
                ", participants=" + participants +
                ", id=" + id +
                ", isSettlement=" + isSettlement +
                "}";
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getAmountInEuro() {
        return amountInEuro;
    }

    public void setAmountInEuro(int amountInEuro) {
        this.amountInEuro = amountInEuro;
    }
}
