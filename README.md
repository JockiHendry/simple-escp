simple-escp
===========

Getting Started
---------------

To use `simple-escp`, download zip distribution from https://github.com/JockiHendry/simple-escp/releases.  Inside the archive are JAR files that need to be included in project that uses `simple-escp`.

`simple-escp` is also hosted in bintray.  In a project that uses Gradle, add the following configuration to *build.gradle* to use `simple-escp`:

```groovy
repositories {
    maven {
        url "http://dl.bintray.com/jockihendry/maven"
    }
}
dependencies {
    compile group: 'jockihendry', name: 'simple-escp', version: '0.1'
}
```

Introduction
------------

simple-escp is a Java library for text mode printing and ESC/P. This library is designed to make it easy to perform dot matrix printing tasks in Java.  Currently it supports only ESC/P 9-pin commands.

To use simple-escp, first define a JSON template.  For example, the following _template.json_ is a report template:

```javascript
{
  "pageFormat": {
    "characterPitch": 17,
    "leftMargin": 3,
    "pageWidth": 20,
    "pageLength": 15
  },
  "placeholder": [ "id", "name" ],
  "template": [
    "User Report",
    "===========",
    "ID    : ${id}",
    "Name  : ${name}"
  ]
}
```

Template contains placeholders that need to be filled by data.  simple-escp supports filling a template with `Map` or Java Bean.  For example, the following code will create the data:

```java
Map<String, String> data = new Hashmap<>();
data.put("id", "007");
data.put("nickname", "Jocki Hendry");
```

simple-escp has a Swing preview panel that can be used to display and print a filled template.  For example, in a `JFrame`, the following panel will display the result of filling our template with its data:

```java
File file = Paths.get("/template.json");
Template template = new Template(file);
PrintPreviewPane printPreview = new PrintPreviewPane(template, data);
```

The following code will print the filled template directly to default printer:

```java
simpleEscp.print(template, data);
```

Development
-----------

To generate project files for IntelliJ IDEA, open shell or command prompt, `cd` to this project directory and enter the following command:

```
gradlew idea
```

Double-click on _simple-escp.ipr_ to open the project in IntelliJ IDEA.  To be able to display Gradle taks in IntelliJ IDEA, select **File**, **Import Module** and  choose _build.gradle_ from this project.  Available Gradle tasks can be displayed by selecting **View**, **Tool Windows**, **Gradle**.  Click on the **Refresh All Gradle projects** button to synchronize with latest Gradle settings. 

Gradle tasks can also be executed directly in command prompt or shell.  For example, to execute **test** task, `cd` to this project directory and enter the following command:
 
```
gradlew test
```

To run unit test that will print to printer, use the following command:
 
```
gradlew testRequirePrinter 
```
 
To create binary distribution in zip file, use the following command:
 
```
gradlew distZip
```

The output zip file is located at _build/distributions_ directory.

To release this project to bintray, use the following command:

```
gradlew bintrayUpload
```