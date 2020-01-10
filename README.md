# Home Finance #
*A simple, open source, home finance tracking app*

## Note ##
This software is in development.  It may be useful to look at for seeing how to use JavaFX with FXML files, or see a simple DAO layer, or see how to use [SQLite](https://www.sqlite.org/ "SQLite").

## Setup ##
* Requires Java 8 or higher
* Requires Maven 3

[Note: the POM is setup for Java 11, but it can be easily downgraded.]

"cd" to the directory you installed into and issue the command:

    mvn install

Alternatively, you can import the source into an IDE, make the project a Maven project, then build.

## Executing ##
"cd" into the directory you installed into and issue the command:

	mvn javafx:run
	
If you execute this project from an IDE and the version is 11 or higher, be sure to add these VM argument to your project or run configuration:

	--module-path /path/to/javafx-sdk-11.x.x/lib --add-modules javafx.controls,javafx.fxml