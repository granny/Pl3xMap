## jmp has forked the plugin and the fabric client (renamed to squaremap) and is offering support on the M.O.S.S. discord at https://discord.gg/PHpuzZS

## NeumimTo has forked the plugin and is offering support on the G.E.A.R.S discord at https://discord.gg/y66aV9VC6s

<div align="center">
<img src="https://raw.githubusercontent.com/pl3xgaming/Pl3xMap/master/plugin/src/main/resources/web/images/og.png" alt="Pl3xMap">

# Pl3xMap

Pl3xMap is a minimalistic and lightweight live world map viewer for Paper servers.

</div>

## What is Pl3xMap

If, like me, you have no real need for 3D views, the novelty of Dynmap and Bluemap have worn off, and you're ready for something actually usable for navigation without all the heavy bulk or slow renders then this is the plugin for you.

## Features

* Ultra fast render times. Get your map viewable today, not next week!
* Simple vanilla-like top down 2D view, designed for navigation.
* Player markers showing yaw rotation, health, and armor
* [Easy configuration](https://github.com/pl3xgaming/Pl3xMap/wiki/Default-config.yml). Even a caveman can do it.
* Up to date Leaflet front-end.
* [Addons and integrations](ADDONS_INTEGRATIONS.md) for many popular plugins.

## Downloads
Downloads can be obtained from the [releases](https://github.com/pl3xgaming/Pl3xMap/releases) section.

## Servers Using Pl3xMap

[Click here](SERVERS.md) to view a list of servers using Pl3xMap.

## License
[![MIT License](https://img.shields.io/github/license/pl3xgaming/Pl3xMap?&logo=github)](License)

This project is licensed under the [MIT license](https://github.com/pl3xgaming/Pl3xMap/blob/master/LICENSE)

Leaflet (the web ui frontend) is licensed under [2-clause BSD License](https://github.com/Leaflet/Leaflet/blob/master/LICENSE)

## bStats

[![bStats Graph Data](https://bstats.org/signatures/bukkit/Pl3xMap.svg)](https://bstats.org/plugin/bukkit/Pl3xMap/10133)

## Building from source

To compile Pl3xMap, you need to have the Paper server implementation installed to your local maven repository.

Download Paper from [papermc.io/downloads](https://papermc.io/downloads) then run this Java command (using the JAR you downloaded)

```
java -jar -Dpaperclip.install=true paper-1.17-75.jar
```

Once that is complete you can compile Pl3xMap with this command

```
./gradlew build
```
