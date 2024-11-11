<div style="width: fit-content; margin: 0px auto;text-align: center">
<h1> Week 4 Agenda </h1>
<table>
<tr> 
    <th style="padding: 15px"> Date:</th>
    <td> 05/03/2024</td>
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
    <td> Adrians Slics</td>
</tr>
<tr>
    <th style="padding: 15px"> Minute Taker:</th>
    <td>Manu Looij</td>
</tr>
<tr>
    <th style="padding: 15px"> Attendees:</th>
    <td>?</td>

[//]: # (    <td>Adrian Slics, Federico Lentini, Manu Looij, Parham Bateni, Teodor Damyanov, Yan Olerinskiy</td>)
</tr>
</table>
</div>

---

## Agenda Items:

- Opening by chair and check in: How is everyone doing (1 min)
- Reviewing the agenda and approval of the agenda: Does anyone have any additions? (1 min)
- Feedback session by TA (3 min)
- Demo Presentation to TA & recap of previous week (3 min)
- Talking Points: (30 min)
    - Discussing advances on finishing Basic Requirements (15 min)
        - Understand what problems have been solved and have arisen. (5 min)
        - Discussing policies of functionallity about debt. (5 min)
        - Discussing basic Admin features in addition to the application. (5 min)
    - Discussing the Project timeline (i.e. 'what's next?') (10 min)
    - Coding tasks assignment (5 min)

- Summarize action points: Who , what , when? (2 min)
- Feedback round: What went well and what can be improved next time? (2 min)
- Question round: Does anyone have anything to add before the meeting closes? (2 min)
- Closure (1 min)

## Questions for the TA

- Should policy issues be decided by us, developers (for isntance, when splitting the bill, who keeps or pays for the
  remainder)?

## Action points for next week (Scrum board)

### task 1: Body checker

Add the feedback required, which is availabe in Brightspace.

### task 2: Frontend testing

By methods, like impendency inhection, on the frontend side test to archieve the set out line coverage.

### task 3: Implementing Additional Basic Requirements features

As decided in the meeting, assign tasks and issues to implement remaining requirements.

## Minute Taker Notes

Some additions from the TA:

- The time on GitLab should be kept using actual time, not in weights.
- The Buddycheck is this week!
- Whatever is not stated in the backlog is up for interpretation. It's not a requirement.
- Inappropriate behavior within the team should be shared with the TA. This can be done privately on Mattermost.

### Talking point 1) - Discussing advances in finishing Basic Requirements

- All but a few tasks from week 3 were completed. These need to be completed before next Friday.

### Talking point 2) - Discussing policies of functionality about debt.

- We have decided to not split cents when splitting the depts.
- When the cents cannot be split equally, the cents will be split randomly.
- In the future, we could also recalculate this when calculating the settlement amounts.

### Talking point 3) - Discussing basic Admin features in addition to the application.

- All issues regarding the admin user stories have been added to GitLab.
- What an admin can do is up to us, as long as it follows the user stories.
- Our TA will check what is meant by "console output" in the user stories, regarding logging in as an admin.
- We will implement separate API endpoints for the Admin (e.g. /admin/...).

### Talking point 4) - Discussing the Project timeline

- We will aim to finish implementing most tasks from week 4 before next Friday.
- After Friday, work will start on the week 4/5 tasks.
- All issues need to have an estimated time of completion, labels and need to be assigned to someone.
- All merge requests need to have multiple comments discussing the changes. The bigger the change, the more discussion.
- We have to discuss the accessibility rubric when it is published.
- Week 10 will be dedicated to polishing the project. This could include removing errors and adding additional user
  feedback (e.g. "Are you sure?" pop-up).
    - An extra milestone needs to be added for this so issues can be created.
- One possibility is that we use an API to check if an IBAN is valid. We have not decided if we will implement
  this.

### Dependency injection tangential discussion

- Extra issues need to be created for additional testing on top of the basic testing done when implementing the
  requirements.
- This also means every merge request from now on should have at least minimal testing.
- We need to discuss the testing rubric to see what does and does not need to be tested.

### Division of tasks

Just after the meeting we sat together and assigned the issues that were open in GitLab. This week we will work on these
tasks following the timeline discussed in the meeting.
