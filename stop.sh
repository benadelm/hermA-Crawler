#!/usr/bin/bash

# This Source Code Form is subject to the terms of the hermACrawler
# Licence. If a copy of the licence was not distributed with this
# file, You have received this Source Code Form in a manner that does
# not comply with the terms of the licence.

# if an argument is given, change directory to there
if [ $# -gt 0 ]
then
	cd "$1"
fi

touch stop