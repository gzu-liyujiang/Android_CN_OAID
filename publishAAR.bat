@echo publish aar to local repository
cd /d ./
gradlew.bat clean cleanBuildCache publishReleasePublicationToLocalRepository --info --warning-mode all
pause

