# 1.2
- Support for Fabric
- Added "start-kick-threshold" option to config.json.

`%server-slots%-3` means that when the server has 3 free slots it starts to kick people

*Set to -1 to disable*

# 1.1
- Reworked the last_played.json file to be more efficient
- Fixed using Util.getMillis() instead of System.currentTimeMillis()
- Now these events reset the afk timer:
  - Joining the server
  - Moving
  - Moving the camera
  - Breaking and placing blocks
  - Using items

# 1.0
- config.json with "idle-timeout" option, in seconds
- name grays out when afk
- `/afk` command to toggle afk status
- joining, moving or moving the camera are the only actions that reset the afk timer
- permission `idler.afk` to not be kicked for being afk