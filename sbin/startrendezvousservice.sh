#!/usr/bin/env bash
if [ "$1" -eq "debug" ]; then
	svcctrl.sh RendevousService 01 debug
else
	svcctrl.sh RendevousService 01
fi
