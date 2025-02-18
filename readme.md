# Jar Updater
A simple command line utility to update a jar file from a url and run it.

# Usage
```shell
java -jar jar-updater.jar <file path> <url>
```
This will delete the file at `<file path>` and download the file from `<url>` to the parent folder of `<file path>`. Afterwards, it wll run the file using the same java version used to run the command.