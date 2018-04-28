Notes RE:  Refactoring of the Continuous Mapper
--------------------------------------------------

For questions, email Ethan Cerami (cerami@cbio.mskcc.org)

Functional Changes:
-------------------

All functionality is the same except:

1.  Users can now add/delete new points.  This was broken before. [DONE]
2.  Data Validation Errors now result in an informative error message
    dialog box. [DONE]
3.  The UI Panel now contains an "Apply to Graph" button. [DONE]

Refactoring Overview:
---------------------
ContinuousMapping.java has now been replaced with nine new classes, all
of which now live in cytoscape.visual.mappings.continuous.

ContinuousMappingPoint:     Encapsulates a continuous mapping point.
ContinuousMappingReader:    Reads in VisualStyles Properties.
ContinuousMappingWriter:    Writes out VisualStyle Properties.
ContinuousUI:               UI for ContinuousMapping

AddPointListener:           Responds to user request to add a new point.
DeletePointListener:        Responds to user request to delete an existing point.
PointTextListener:          Responds to user change in any point value,
                            and validates the new point value.
ValueListener:              Responds to user request to modify a value,
                            e.g. Color, Line Type or Size.

JUnit Tests:
------------
ContinuousMappingTestSuite          Runs all ContinuousMapping Tests
TestContinuousColorRangeCalculator  Runs tests for a Continuous Color Calculator.
TestContinuousMappingReader         Tests the Reader class.
TestContinuousMappingWriter         Tests the Writer class.


Functional Test List:
----------------------
1.  Take an existing mapper, and add a new point.  Does the new point
    appear in the list?  Delete an existing point.  Is it removed from
    the list?  [Tested --> OK]

2.  Take an existing Continuous mapper, modify a point.  Restart Cytoscape,
    and verify that the mapper was saved correctly.  [Tested -->  OK]

3.  In the text box for an existing point, try typing some letters, e.g.
    "three".  You should get an error message dialog box. [Tested -->  OK]

4.  Create a new Continuous Mapper, add a bunch of points.  Restart Cytoscape,
    and verify that the mapper was saved correctly. [Tested --> OK]

5.  Clone an existing Continuous Mapper, and modify it.  Restart Cytoscape,
    and verify that the mapper was saved correctly. [Tested --> OK]

6.  Take an existing ContinuousMapper for Node Color, and apply it to
    a graph. [Tested --> OK]

7.  Take an existing ContinuousMapper for Node Border Color, and apply it
    to a graph. [Tested --> OK]

8.  Create a ContinuousMapper for Node Size, and apply it to graph.
    [Tested --> OK]

9.  Take an existing ContinuousMapper Node Border LineType, and apply it
    to a graph. [Tested --> DOES NOT WORK]