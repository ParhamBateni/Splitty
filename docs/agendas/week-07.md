<div style="width: fit-content; margin: 0px auto;text-align: center">
<h1> Week 6 Agenda </h1>
<table>
<tr> 
    <th style="padding: 15px"> Date:</th>
    <td> 26/03/2024</td>
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
    <td> Yan Olerinskiy </td>
</tr>
<tr>
    <th style="padding: 15px"> Minute Taker:</th>
    <td> Teodor Damyanov </td>
</tr>
<tr>
    <th style="padding: 15px"> Attendees:</th>
    <td>Adrian Slics, Federico Lentini, Manu Looij, Parham Bateni, Teodor Damyanov, Yan Olerinskiy</td>
</tr>
</table>
</div>

---

## Agenda Items:

- Opening by chair and check in: How is everyone doing? (1 min)
- Reviewing the agenda and approval of the agenda: Does anyone have any additions? (1 min)
- Recap of previous week & discuss per person what progress was made (4 min)
- Demo Presentation to TA (3 min)
- Feedback session by TA (3 min)
- Talking Points: (27 min) 
    - Review basic requirements (12 min)
        - Discuss each point: is it done? (7 min)
        - Are there any final touches we would like to make? (5 min)
    - Review add-ons and discuss next steps (15 min)
        - What is done and what has to be implemented? (9 min)
        - Which tasks are closest to being done? (3 min)
        - Are there any tricky details that we have to keep in mind? (3 min)
- Summarize action points: Who, what, when? (2 min)
- Feedback round: What went well and what can be improved next time? (1 min)
- Question round: Does anyone have anything to add before the meeting closes? (2 min)
- Closure (1 min)

# Questions for the TA

- Any tips for us to finish the course as smoothly as possible?
- How will week 9 look?

## Minute Taker Notes

Some additions from the TA:

- Do not make nested branches if only a single person works on the issue. Do it only if a couple of people work on closely correlated issues.
- Make sure main branch can be ran as well as passing the pipeline.
### Talking point 1) - Remaining basic requirments

- Some basic requirments still require polishing or bug fixing. They problems are:
- Having the client take the server url from the config file
- Fixing the expense information in the database
- Disabling admin buttons for showing event overview, deleting, and saving events when no event is selected
- Making other actions be recorded as last actions to an event
- Having the return button for event overview return to admin if a user reached it through the admin functionality
- Making the admin password appear in a more convinient locaiton in the server log
- General refractoring the admin UI
- Have conformation pop-ups for all admin interactions
- Completing the implementation for saving and loading files
- Creating a JSON file and for translation and implament the langauge changing capabilities
- Topics for further discussion:
- What does connecting do diffrent servers without recompiling mean>
- How to set up the depts overview page
### Talking point 2) - Additional features
- Extensions to be worked on this week:
- Creating a template JSON file allowing for custom language translation
- Implement email invites to join events
- Implement an automatic email being sent to uses added to an event
- Change the check for valid emails so that not just Gmail is accepted
- Topics for further discussion connected to the additional features:
- How to handle foreign currency change rates.
- What is to be done from the extensions in the limited time.

## Work distribution by backlog requirments
- Language switching: Many and Adrians
- Open debts: Yan
- Detailed expenses: Parham
- Finishing the basic requirments: Teodor and Federico

## Feeback from team
- Everybody has to stick to deadlines
## Question round
- The project should be done until Friday week 9
- To get good results for implementation go over the rubrics and check what is to be graded
- For the project pitch, make slides