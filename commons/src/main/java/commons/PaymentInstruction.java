package commons;

import java.util.Objects;

public class PaymentInstruction implements java.io.Serializable {
    private Event event;
    private Participant from, to;
    private Integer amount;

    public PaymentInstruction() {

    }

    public PaymentInstruction(Event event, Participant from, Participant to, Integer amount) {
        this.event = event;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Participant getFrom() {
        return from;
    }

    public void setFrom(Participant from) {
        this.from = from;
    }

    public Participant getTo() {
        return to;
    }

    public void setTo(Participant to) {
        this.to = to;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentInstruction that = (PaymentInstruction) o;
        return Objects.equals(event, that.event) && Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, from, to, amount);
    }
}
