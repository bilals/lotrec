# Overview
LoTREC is an automated theorem prover for modal and description logic. It allows students and researchers in logic to define well-known and new exotic logics with Kripke's semantics, and to check the properties of their formulas, i.e. their satisfiability or validity, as well as performing model checking.

The syntax of a logic is defined by providing a set of connectors names and syntactical rules, and the sematincs of the logic is defined by providing a set of graph rewriting rules. A formula can be then automatically tested with LoTREC, which applies the graph rewriting rules in order to decompose and analyze the formula.

# Installation
[**Download the latest release here**](https://github.com/bilals/lotrec/releases/latest), extract the ZIP and execute `bin/LoTREC.bat` on Windows or `bin/LoTREC` on Linux.

All you need in order to run LoTREC is the Java Runtime Environement (JRE) 8 (i.e. 1.8) or later. The latest versions, e.g., Java 21, works well and it can be downloaded from the Eclipse Adoptium site https://adoptium.net/ or alternatively OpenJDK, or Oracle's Java website.

## For Geeks
Alternatively, fork then checkout or download LoTREC's code base from GitHub,open in VS Code, then build and run it with Gradle.

## Allocate More Memory
It is imporatnat to increase the amount of allocated RAM if you are debugging or working extensively with LoTREC, specially if you are displaying many graphs during your session. Otherwise, LoTREC would run out of memory and should be consequently restarted.

Edit the file "bin/LoTREC.bat", and look for the following line:
``` 
set DEFAULT_JVM_OPTS=
``` 
You can specify the allocated memory as follows:
``` 
set DEFAULT_JVM_OPTS=-Xmx512M
``` 
The command option -Xmx512M allocates 512MB RAM to LoTREC. It can be further increased, e.g., -Xmx2048M for 2GB RAM.

# Contact
Please feel free to contact us here on GitHub, to fork the project, contribute to it and send us pull requests to integrate your contributions, or report any problem.

For any further information, please visit our web site:
https://github.com/bilals/lotrec

Legacy site on the IRIT Research Lab
https://www.irit.fr/Lotrec/
