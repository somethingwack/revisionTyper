@ECHO Off

set BASEDIR=%~dp0

javac -d "%BASEDIR%out" "%BASEDIR%revisionMain.java"
javac -d "%BASEDIR%out" "%BASEDIR%revisionTyper.java"
javac -d "%BASEDIR%out" "%BASEDIR%revisionGUI.java"

cls
cd /d "%BASEDIR%out"

java revisionMain

endlocal