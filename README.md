simple-escp
===========

simple-escp is a Java library for text mode printing and ESC/P. This library is designed to make it easy to perform dot matrix printing tasks in Java.  Currently it supports only ESC/P 9-pin commands.

To use simple-escp, first define a JSON template.  For example, the following _template.json_ is a report template:

```
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

```
Map<String, String> data = new Hashmap<>();
data.put("id", "007");
data.put("nickname", "Jocki Hendry");
```

simple-escp has a Swing preview panel that can be used to display and print a filled template.  For example, in a `JFrame`, the following panel will display the result of filling our template with its data:

```
File file = Paths.get("/template.json");
Template template = new Template(file);
PrintPreviewPane printPreview = new PrintPreviewPane(template, data);
```

The following code will print the filled template directly to default printer:

```
simpleEscp.print(template, data);
```
