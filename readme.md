# processing-scala-sbt

This is a template project for developing [Processing](http://processing.org) programs in Scala. Includes:

* Processing core library and source
* SBT project and IDEA project template

Rename `.ideatemplate` to `.idea` and open the project with IntelliJ, or simply open the `build.sbt` in the IntelliJ 
window and go through the dialogs.

If you don't use the default template, make sure to add all the jars in `lib/` as modules.

### JDK issues
* Processing more or less requires Java 8, but later sbt versions default to 10
* Configuring the IntelliJ project to use 1.8 is easier than changing the `JAVA_HOME` just for this project
