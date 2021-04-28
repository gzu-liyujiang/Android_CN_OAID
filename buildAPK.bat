@rem build and proguard apk
cd /d ./
gradlew.bat clean resguardRelease --warning-mode all
@rem gradlew.bat clean assembleRelease --warning-mode all
pause

