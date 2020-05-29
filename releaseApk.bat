@echo build release apk
cd /d .
@REM gradlew.bat clean cleanBuildCache assembleRelease --info --warning-mode all
gradlew.bat clean cleanBuildCache resguardRelease --info --warning-mode all
pause

