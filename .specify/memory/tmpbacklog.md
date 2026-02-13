# Big Next

* Cytoscape Upgrade/Replacement (spec & document existing features and settings)
* Logic DSL, Text Editor, WYSIWIG Graph Rules
* Website, Tutorials, Examples, Documentation

# Perso
Why bin folder? (remove from .gitIgnore)
Transform screenshot taking into command, destination folder "build/gui-screenshots", then remove, as well from .gitignore 

# Claude
- remove brownfiled files and merge in .specify/memory constitution, etc.
- update SpecKit (re-run init?)
- simplify skills
- remove unused repomix MCP
- facilitate auto-running commands
    * Learn how to run lotrec (for future tests)
- share project settings via Git

# ALL

Finalize/complete:

  ├──────┼──────────────────────────────────────────────────────────┤
  │ T067 │ Visual comparison of 42 states — needs both GUIs running │
  ├──────┼──────────────────────────────────────────────────────────┤
  │ T075 │ Remove Swing GUI — blocked by T066-T074                  │
  └──────┴──────────────────────────────────────────────────────────┘

Update CLAUDE, Constitution, roadmap... after this GUI Maigration

# Next
Put Add/Edit/Delete buttons to the left to gain more space?
Simplify list of Actions and Conditions (non-collapsable list, large space by default)
Verify all menu items are functional

Create in .specify/memoryReference V3 JavaFX screenshots for the GUI right after Migration

# Current

 Make sure tableaux are built and displayable (make sure Cytoscape is displayed correctly)

# DONE

Follow up 003
Solve UI Mismatch/Gaps in report `semantic-analysis-report.md`.  
After running "/visual-check" you generated Swing and FX screenshots, compared them and generated the following report specs\001-javafx-gui-migration\002-semantic-analysis-report.md
Plan the following modificaions of the FX GUI as follows:
- Under "Formula Code" the infix format of the selected formula must be displayed 
- Connectors Tab: auto-select first connector and populate detail fields, use inline labels, add back the "Edit" button between "Add" and "Delete", add a "Comment" field (one line by default but expandable)
- Rules tab: auto-select first rule, display the actions and conditions instead of their objects IDs(currently we see object IDs), add back the "Edit" button between "Add" and "Delete", add a "Comment" field (one line by default but expandable), add "Add/Edit/Delete" buttons to both Actions and Condtions
- Strategy tab: auto-select and display main strategy, allow to select main strategy, add a "Comment" field (one line by default but expandable)
- Formula tab: populate the list with formulas DisplayName (pretty print infix format from 
  TestingFormula.getDisplayName()), auto-select first formula, display the formula's Code in an editable and expandable text area, add a "Comment" field (one line by default but expandable)                                                                                               
If this list seems too long to track in a single planning and implementation run, let me know to chunk it in small peices. If you find it better to make all needed changes in FX GUI code then test once for all, then proceed.

Let me know what you think, ask me questions if you need, especially on the right choice for the comment field (expandable / foldable, or text area with a scroll and a handle to enlarge in case it is needed). 

===============

Follow up 002
"Step 0.5: Visual Validation Infrastructure (before migration begins)"
from
`.specify\memory\GUI-V2\javafx-migration-speckit-prompt.md`  
and make sure that all the components are built, and that we have a "Claude Code visual-check skill", otherwise, let us plan it, and ask me questions if needed.
This shall probably enable completing task "T067 is a batch validation run" from `specs/001-javafx-gui-migration/tasks.md`. Please confirm first and explain to me before planning.

plan it, and ask me questions if needed. Make sure the gui-screenshots, whether Swing or JavaFX, are stored in a subfolder of build. Do not use specs\001-javafx-gui-migration\screenshots anymore for this purpose. Do not alter the manually taken screenshots that are stored in .specify\memory\GUI-V2. You can use them as an initial baseline. However, using the programmatic Swing baseline screeshots, you can also compare the panels one by one, since the Swing GUI wast not altered. 

~~Verify tasks were fully done~~