@rem JAVA_HOME -> Gradle 7 require JDK 11+, Gradle 8 require JDK 17+
@rem See https://docs.gradle.org/current/userguide/compatibility.html
@echo build and proguard apk
cd /d ./
gradlew.bat clean assembleRelease --info --warning-mode all
pause

