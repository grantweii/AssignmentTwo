# AssignmentTwo

## Extensions implemented ##

* Sun that changes position and colour over time
* Night mode with torch
* Portals you can walk through

### Moving Sun ###

* Press Q to toggle between the base version of sunlight in which the sun's direction is specified by the vector in the scene language and the extension in which the sun moves and changes colour
* The sunlight starts from below the horizon where only ambient light is on, and finishes below the horizon on the opposite side of the terrain
* The sunlight starts from a blue colour and slowly turns to a red
* I have made the colours a bit dramatic so the effect is easier to see
* Once this cycle is over it restarts from the beginning

### Night mode ###

* Press W to change between day and night
* Press E to turn the torch on and off

### Portals ###

* Press Y to turn the portals on and off
* They spawn in hardcoded coordinates

### Other Controls ###

* Press T to toggle between first and third person view

## Problems ##

When running on a Mac, there is a glitch with the shading of the enemies, on PC it acts as intended
