@echo OFF

ECHO Checking JAVA Installation...
CALL :check_java
ECHO Checking Maven Installation...
::CALL :check_maven
ECHO Checked Maven Installation.

REM Run the Maven app
if EXIST target (
    ECHO target folder is available!
    SET /p yORn=Do you want to rebuild? (y/n)
    ECHO Input is: %yORn%
    if /i "%yORn%"=="y" (
        ECHO Building Channel Processing System! Please wait...
        mvn clean install -q
        if ERRORLEVEL 1 (
            ECHO Build Failed! Please check and run startup script again.
            EXIT /B 1
        ) else (
            CALL :startingCPS
        )
    ) else if /i "%yORn%"=="n" (
        goto :startingCPS
    ) else (
        ECHO Please provide valid input.
        EXIT /B 0
    )
) else (
    ECHO Building Channel Processing System! Please wait.
    mvn clean install -q
    if %ERRORLEVEL%==0 (
        CALL :startingCPS
    ) else (
        ECHO Build Failed! Please check and run startup script again.
        EXIT /B 1
    )
)

:check_java
java -version >nul 2>&1
if %ERRORLEVEL%==1 (
    ECHO Java is not installed. Please install Java 11 and try again.
    EXIT /B 1
) else (
    ECHO Java installation found.
)
ECHO.
ECHO END of Java function.
EXIT /B 0

:check_maven
ECHO Bfore MVN command.
mvn -version >nul 2>&1
ECHO After MVN command.
if %ERRORLEVEL%==1 (
    ECHO Maven is not installed. Please install and try again.
    EXIT /B 1
) else (
    ECHO Maven installation found.
)
ECHO.
ECHO END of Maven function.
EXIT /B 0

:startingCPS
ECHO Begining of CPS function.
cd target
ECHO IN target
if EXIST channel-processing-system-1.0-SNAPSHOT.jar (
    ECHO Starting Channel Processing System, please wait!
    java -jar channel-processing-system-1.0-SNAPSHOT.jar
    if %ERRORLEVEL%==1 (
        ECHO Failed to run the application.
        EXIT /b 1
    )
) else (
    ECHO Channel Processing Snapshot is not available. Please rebuild.
    EXIT /b 1
)
ECHO.
EXIT /B 0