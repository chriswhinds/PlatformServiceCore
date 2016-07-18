#!/usr/bin/env bash
if [ "$1" -eq "debug" ]; then
	svcctrl.sh ConfigurationService 01 debug
else
	svcctrl.sh ConfigurationService 01
fi

