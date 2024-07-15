# Rosewood Plugin Template

This is a template repository for creating new Rosewood plugins without having to worry about writing all the managers and other boilerplate code. 

## Files to Modify
These are all the files that should be modified when creating a new plugin from this template.
> `settings.gradle` - Change `rootProject.name` to the name of your plugin.
> `gradle.properties` - Change `spigotVersion`, `gardenVersion` and `guiVersion` to the versions of the respective dependencies you want to use.
> `build.gradle` - Change `myplugin` to your plugin's name (in lowercase). You can also disable MySQL Support here.
> `src/main/resources/plugin.yml` - Change `name` to fit your plugin value.
> `src/main/resources/locale/en_US.yml` - The default locale file.
> `src/main/java/com/rosewood/myplugin/` - Repackage this directory to match your plugin's package name.
> `ExampleCommandWrapper.java` - Rename this file to match your plugin's primary command name, as well as the values inside.
> `_1_CreateInitialTables.java` - If you are using MySQL, Change the data migrations to match what you need.

### Support
If there's anything we left out, you have a question, you want to report a bug, or anything else, please [join our Discord server](https://discord.gg/MgUsTBK).  We offer any and all support in our server.
