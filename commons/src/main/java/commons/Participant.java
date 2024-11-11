package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
public class Participant implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String email;
    private String iban;
    private String bic;
    // debt amount in cents
    // debt > 0 - the person owes, debt < 0 - person is owed to
    private int debt;
    private int expenseShare;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    public static final String emailPattern = "^.{3,20}@.{3,20}$";

    /**
     * Constructor with no parameters
     */
    public Participant() {

    }

    public Participant(long id, String name, String email, String iban,
                       String bic, int debt, int expenseShare, Event event) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
        this.debt = debt;
        this.expenseShare = expenseShare;
        this.event = event;
    }

    public Participant(long id, String name, String email, String iban,
                       String bic, int debt, Event event) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
        this.debt = debt;
        this.event = event;
    }
    public Participant ( String email,  Event event) {
        this.name = email.split("@")[0];
        this.email = email;
        this.event = event;
    }

    public Participant(long id, String name, String email, String iban,
                       String bic, Event event) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
        this.event = event;
    }

    public Participant(String name, String email, String iban,
                       String bic, Event event) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
        this.event = event;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * Setter for iban
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Getter for bic
     */
    public String getBic() {
        return bic;
    }

    /**
     * Setter for bic
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Getter for debt
     */
    public int getDebt() {
        return debt;
    }

    /**
     * Setter for debt
     */
    public void setDebt(int debt) {
        this.debt = debt;
    }

    public void addDebt(int amount) {
        this.debt += amount;
    }

    public void resolveDebt(int amount) {
        this.debt -= amount;
    }

    public int getExpenseShare() {
        return expenseShare;
    }

    public void setExpenseShare(int expenseShare) {
        this.expenseShare = expenseShare;
    }

    public void addExpenseShare(int amount) {
        this.expenseShare += amount;
    }

    public void resolveExpenseShare(int amount) {
        this.expenseShare -= amount;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    /***
     * Check if name is valid
     */
    public static boolean isNameValid(String name) {
        Pattern pattern = Pattern.compile("^.{3,15}$");
        return pattern.matcher(name).matches();
    }


    /***
     * Check if email is valid
     */
    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(emailPattern);
        return pattern.matcher(email).matches();
    }

    /***
     * Check if iban is valid
     */
    public static boolean isIbanValid(String iban) {
        //TODO: apply a better validation method for iban:
        // https://en.wikipedia.org/wiki/International_Bank_Account_Number
        Pattern pattern = Pattern.compile("^[A-Z]{2}.{13,31}$");
        return iban.isEmpty() || pattern.matcher(iban).matches();
    }

    /***
     * Check if bic is valid
     */
    public static boolean isBicValid(String bic) {
        Pattern pattern = Pattern.compile("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2,5}$");
        return bic.isEmpty() || pattern.matcher(bic).matches();
    }

    /**
     * Check if two participants are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Participant that = (Participant) o;
        return id == that.id
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(iban, that.iban)
                && Objects.equals(bic, that.bic)
                && debt == that.debt
                && expenseShare == that.expenseShare;
    }

    /**
     * Create a hash code fore participant
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, iban, bic, debt, expenseShare);
    }

    /**
     * Return a string representing the participant
     */
    @Override
    public String toString() {
        return name + " is registered with id: " + id
                + " using email: " + email
                + " with IBAN: " + iban
                + " BIC: " + bic
                + " debt: " + debt
                + " and expense share: " + expenseShare;
    }
}
