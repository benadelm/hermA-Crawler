REM This Source Code Form is subject to the terms of the hermACrawler
REM Licence. If a copy of the licence was not distributed with this
REM file, You have received this Source Code Form in a manner that does
REM not comply with the terms of the licence.

@ECHO OFF

IF NOT "%~1" == "" PUSHD "%~1"

TYPE NUL >> stop1
REN stop1 stop

IF NOT "%~1" == "" POPD

ECHO ON