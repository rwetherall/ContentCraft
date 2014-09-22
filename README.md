ContentCraft
============

##Introduction

ContentCraft is a Bukkit style plugin for Mimecraft that connects, via CMIS, to an Alfresco repository.

##Prerequisites

* Alfresco One (recommend 5.0.a http://sourceforge.net/projects/alfresco/)
* Minecraft 1.8 client (https://minecraft.net/download)

##Development Setup

* Ensure your Alfresco One repository is running and accessable
* Pull repository
* Edit src/main/resources/cmis.json with the connection details of your Alfresco One repository
* Run "ant deploy" to build Bukkit plugin and deploy to Spigot server
* Run "ant start-server" to start Spigot Minecraft server with ContentCraft plugin installed
* Run Minecraft client and connect to running server
* Execute command "/cmis" to verify that the Minecraft server can successfully connect to the Alfresco One repository

