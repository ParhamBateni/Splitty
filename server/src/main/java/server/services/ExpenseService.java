package server.services;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.PaymentInstruction;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

import java.util.*;

@Service
public class ExpenseService {
    private final EventRepository eventRepository;

    @Autowired
    public ExpenseService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Integer> getDistribution(Expense expense) {
        Random random = new Random(expense.getId());

        ArrayList<Integer> distribution = new ArrayList<Integer>();
        int sz = expense.getParticipants().size();
        int rem = expense.getAmountInEuro() % sz;

        for (int i = 0; i < rem; ++i) {
            distribution.add(expense.getAmountInEuro() / sz + 1);
        }
        for (int i = 0; i < sz - rem; ++i) {
            distribution.add(expense.getAmountInEuro() / sz);
        }

        Collections.shuffle(distribution, random);

        return distribution;
    }

    /**
     * Distributes the debts among participants of an expense
     *
     * @param expense expense to be processed
     */
    @Transactional
    public void applyExpense(Expense expense) {
        System.out.println(expense.getEvent());
        long id = expense.getEvent().getId();
        Event event = eventRepository.getReferenceById(id);
        List<Integer> distribution = getDistribution(expense);
        List<Participant> participants = expense.getParticipants();
        for (int i = 0; i < participants.size(); ++i) {
            int finalI = i;
            Participant p = event.getParticipants().stream()
                    .filter(x -> x.getId() == participants.get(finalI).getId()).findFirst().get();
            int delta = distribution.get(i);
            p.addDebt(delta);
            if (!expense.getIsSettlement()) {
                p.addExpenseShare(delta);
//                participants.get(i).addExpenseShare(delta);
            }
        }
        Participant p = event.getParticipants().stream()
                .filter(x -> x.getId() == expense.getPayer().getId()).findFirst().get();
        p.resolveDebt(expense.getAmountInEuro());
    }

    /**
     * Rollbacks the debts added by a certain expense
     *
     * @param expense expense to be processed
     */
    @Transactional
    public void rollbackExpense(Expense expense) {
        System.out.println(expense.getEvent());
        long id = expense.getEvent().getId();
        Event event = eventRepository.getReferenceById(id);
        List<Integer> distribution = getDistribution(expense);
        List<Participant> participants = expense.getParticipants();
        for (int i = 0; i < participants.size(); ++i) {
            int finalI = i;
            Participant p = event.getParticipants().stream()
                    .filter(x -> x.getId() == participants.get(finalI).getId()).findFirst().get();
            int delta = distribution.get(i);
            p.resolveDebt(delta);
            if (!expense.getIsSettlement()) {
                p.resolveExpenseShare(delta);
//                participants.get(i).resolveExpenseShare(delta);
            }
        }
        Participant p = event.getParticipants().stream()
                .filter(x -> x.getId() == expense.getPayer().getId()).findFirst().get();
        p.addDebt(expense.getAmountInEuro());
    }

    /**
     * Creates payment instructions to settle all debts in event with id eventId
     * Guarantees at most n-1 instructions
     *
     * @param eventId id of the event where instructions are requested
     * @return list of lists of size 3:
     * first - person who should pay,
     * second - person to whom they should pay,
     * third - integer amount which should be paid
     */
    public List<PaymentInstruction> getPaymentInstructions(long eventId) {
        List<Participant> positive = new ArrayList<>();
        List<Participant> negative = new ArrayList<>();
        Event event = eventRepository.getReferenceById(eventId);
        for (var p : event.getParticipants()) {
            if (p.getDebt() > 0) {
                positive.add(p);
            } else if (p.getDebt() < 0) {
                negative.add(p);
            }
        }
        List<PaymentInstruction> result = new ArrayList<>();
        int ptr1 = 0, ptr2 = 0;
        while (ptr1 < positive.size() && ptr2 < negative.size()) {
            Participant p1 = positive.get(ptr1);
            Participant p2 = negative.get(ptr2);
            int settle = Math.min(p1.getDebt(), -p2.getDebt());
            result.add(new PaymentInstruction(event, p1, p2, settle));
            p1.resolveDebt(settle);
            p2.addDebt(settle);
            if (p1.getDebt() == 0) {
                ++ptr1;
            }
            if (p2.getDebt() == 0) {
                ++ptr2;
            }
        }
        return result;
    }
}
