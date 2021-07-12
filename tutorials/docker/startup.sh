#!/bin/sh

Xvfb :0 -screen 0 800x600x24+32 &
DISPLAY=:0 gradle run
