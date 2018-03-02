ContentCraft
============

## Introduction

ContentCraft is a Bukkit style plugin for Mimecraft that connects, via CMIS and REST API, to an Alfresco repository.

## Prerequisites

* Alfresco 4.2+
* Minecraft client 1.12+

## Development Setup

* Ensure your Alfresco One repository is running and accessible (http://localhost:8080/alfresco)
* Pull repository

* Edit `src/main/resources/cmis.json` with the connection details of your Alfresco repository
* Edit `src/main/resources/rest.json` with the connection details of your Alfresco repository
* Run `mvn clean package" to build Bukkit plugin and deploy to Spigot server
* Run `mvn clean install -Pstart-server` to start Spigot Minecraft server with ContentCraft plugin installed

* Run Minecraft client and connect to running server (i.e. 127.0.0.1)
* Execute command `/cmis` to verify that the Minecraft server can successfully connect to the Alfresco CMIS
* Execute command `/api` to verify that the Minecraft server can successfully connect to the Alfresco REST API
* Execute command `/build site <siteName>` to build the Site and spawn the members as villagers

You can start a new world (without your previous Site buildings) by using Maven cleaning profile:

```
$ mvn clean -Pclean-server
```

## Minecraft available commands

```
/help ContentCraft
```