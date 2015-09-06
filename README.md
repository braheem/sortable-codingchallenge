My attempt at the Sortable Coding Challenge, which can be viewed at the link below:
http://sortable.com/challenge/

You may substitute your own listings text file by placing it in src/main/resources directory and either specifying the name when invoking with sbt or modifying the pom.xml (if executing with maven).

**You can just clone this repo and run "go.sh"**
	-See the generated "output.txt" file

To execute with Maven:

1. mvn clean install

2. mvn exec:java


To execute with sbt:

1. sbt "run products.txt listings.txt"


