# MangoUHC

A Minecraft Plugin for managing UHC (Ultimate Hardcore) events with Java 21 and Minecraft 1.21+

## Compilation

This project uses Maven for building. To compile the plugin:

1. Make sure you have Java 21 installed and set up with `JAVA_HOME`
2. Run `mvn clean install` to build the plugin

## Plugin Commands

Once installed, use these in-game commands to manage UHC events:

-   `/uhc start` - Start a new UHC event
-   `/uhc stop` - End the current UHC event
-   `/uhc status` - Check the status of the current event
-   `/uhc help` - List all available commands

## Installation

1. Compile the plugin as described above
2. Copy the generated JAR file from the `target/` directory to your Minecraft server's `plugins/` directory
3. Start your server - the plugin will be automatically loaded

TO DO

-   Heal despues del timer pvp ✅
-   no pvp desde inicio comando /pvp [on|off] ✅
-   Vivos no puedan ver chat spectadores
-   Subir Apple rate drop ✅
-   Checar borde (tp extraño al centro) ✅
-   server 1.21.4
