<div style="width: fit-content; margin: 0px auto;text-align: center">
<h1> Week 8 Agenda </h1>
<table>
<tr> 
    <th style="padding: 15px"> Date:</th>
    <td> 03/04/2024</td>
</tr>
<tr>
    <th style="padding: 15px"> Time:</th>
    <td> 13:45-14:30</td>
</tr>
<tr>
    <th style="padding: 15px"> Location:</th>
    <td> DW PC1 Cubicle 13 </td>
</tr>
<tr>
    <th style="padding: 15px"> Chair:</th>
    <td> Teodor Damyanov</td>
</tr>
<tr>
    <th style="padding: 15px"> Minute Taker:</th>
    <td> Yan Olerinskiy</td>
</tr>
<tr>
    <th style="padding: 15px"> Attendees:</th>
    <td>Adrian Slics, Federico Lentini, Manu Looij, Teodor Damyanov, Yan Olerinskiy</td>
</tr>
</table>
</div>

---

## Agenda Items:

- Opening by chair and check in: How is everyone doing? (1 min)
- Reviewing the agenda and approval of the agenda: Does anyone have any additions? (1 min)
- Recap of previous week & discuss per person what progress was made (5 min)
- Demo Presentation to TA (3 min)
- Feedback session by TA (3 min)
- Talking Points: (25 min)
    - Discuss the state of the project(8 min)
        - Are all the basic requirments finished, if not how to deal with that?(3 min)
        - Which of the add-ons assingned last week are done, and which still need more work?(5 min)
    - Discuss the remaining add-ons(17 min)
        - Which of the add-ons have not yet been implamented? (3 min)
        - What needs to be done?(10 min)
        - Are there any problems with the tasks that are to be implemented, we should to look out for?(4 min)
- Summarize action points: Who, what, when? (2 min)
- Feedback round: What went well and what can be improved next time? (2 min)
- Question round: Does anyone have anything to add before the meeting closes? (2 min)
- Closure (1 min)

# Questions for the TA

- Do you have any tips for the project pitch?

## Minute Taker Notes

# Talking point 1 - The state of the project

- With the published implemented features feedback we should do the following:
    - Fix long polling and web sockets, currently information isn't propagated to other clients
    - Add server host address configuration
    - Fix participant deleting
    - Fix expense editing/deleting
    - Implement the debt statistics page
    - Finish language switching
    - Finalize event JSON uploading/downloading

# Talking point 2 - Remaining additional features

- With the project deadline approaching, the following points are important:
    - We should try and see if we can implement the Statistics feature almost completely this week, otherwise we
      should leave it out of the project
    - We should aim to finish open debts and email notifications completely
    - It is better to perfect extensions that are already done rather than try and
      implement all extensions
    - Open debts should be deleted whenever they are settled, and other
      clients should also be updated

# Work distribution

Note: we should aim to do most of the work this week so that next week we can
polish everything, add tests and make sure that everything works as intended

- Federico: fixing basic requirements
- Teodor: testing serverUtils
- Adrians: try and implement statistics and polish websockets/long polling with Yan
- Manu: add a debt statistics page, finish language feature
- Yan: fix open debts, implement web sockets/long polling with Adrians

# Tips from TA

- If we want more feedback on our project presentation we should add this
  block to the agenda next week