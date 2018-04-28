REM Simple Cytoscape batch script for windows/dos
REM (c) Trey Ideker June 21, 2002; Owen Ozier March 06, 2003
REM
REM Runs Cytoscape from its jar file with GO data loaded

java -Dswing.aatext=true -Xmx512M -jar cytoscape.jar cytoscape.CyMain -p plugins %*



pause