# Big Next

* GraphViz Upgrade/Replacement (spec & document existing features and settings)
* Logic DSL, Text Editor, WYSIWIG Graph Rules
* Website, Tutorials, Examples, Documentation

# Perso
Why bin folder? (remove from .gitIgnore)
Transform screenshot taking into command, destination folder "build/gui-screenshots", then remove, as well from .gitignore 

# Claude
- facilitate auto-running commands
    * Learn how to run lotrec (for future tests)
- share project settings via Git

# ALL
Make sure tableaux are built and displayable (make sure GraphViz is displayed correctly)

Iterate on GUI Testing / enhancements...
- Make labels and textfields on the same row in Connector tab
- Make formula code appears in text area
- Verify all menu items


Reference screenshots for the GUI right after Migration

Finalize/complete:

  ├──────┼──────────────────────────────────────────────────────────┤
  │ T067 │ Visual comparison of 42 states — needs both GUIs running │
  ├──────┼──────────────────────────────────────────────────────────┤
  │ T075 │ Remove Swing GUI — blocked by T066-T074                  │
  └──────┴──────────────────────────────────────────────────────────┘

# Current

Solve UI Mismatch/Gaps in report `build/screenshots/semantic-analysis-report.md`.  



# DONE

Follow up on 
"Step 0.5: Visual Validation Infrastructure (before migration begins)"
from
`.specify\memory\GUI-V2\javafx-migration-speckit-prompt.md`  
and make sure that all the components are built, and that we have a "Claude Code visual-check skill", otherwise, let us plan it, and ask me questions if needed.
This shall probably enable completing task "T067 is a batch validation run" from `specs/001-javafx-gui-migration/tasks.md`. Please confirm first and explain to me before planning.

plan it, and ask me questions if needed. Make sure the gui-screenshots, whether Swing or JavaFX, are stored in a subfolder of build. Do not use specs\001-javafx-gui-migration\screenshots anymore for this purpose. Do not alter the manually taken screenshots that are stored in .specify\memory\GUI-V2. You can use them as an initial baseline. However, using the programmatic Swing baseline screeshots, you can also compare the panels one by one, since the Swing GUI wast not altered. 

~~Verify tasks were fully done~~