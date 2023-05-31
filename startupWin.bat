@echo OFF
::echo ON

:check_java
where java >nul 2>&1
IF %ERRORLEVEL% equ 1 (
    ECHO Java is not installed. Please install Java 11 and try again.
    pause
    EXIT /B 1
) ELSE (
    ECHO Java installation found.
)
GOTO :check_maven

:check_maven
where mvn >nul 2>&1
IF %ERRORLEVEL% equ 1 (
    ECHO Maven is not installed. Please install and try again.
    pause
    EXIT /B 1
) ELSE (
    ECHO Maven installation found.
)
CALL :build_cps
::ECHO ERRORLEVEL %ERRORLEVEL%
EXIT /B 0

:build_cps
IF NOT EXIST target (
    ECHO Building Channel Processing System! Please wait...
    mvn clean install >nul 2>&1
) ELSE (
    SET /p yORn=Target dir is already available. Do you want to rebuild?(y or n)
    ::choice /c yn /m "Target dir is already available. Do you want to rebuild?(y or n)"
    ::ECHO Input is: %yORn%
    ::IF /i %yORn% equ y (
    IF "%yORn%" equ "y" (
        ECHO Building Channel Processing System! Please wait...
        mvn clean install >nul 2>&1
        IF %ERRORLEVEL% equ 0 (
            GOTO :startingCPS
        ) ELSE (
            ECHO Build Failed! Please check and run startup script again.
            EXIT /B 1
        )
    ) ELSE IF "%yORn%" equ "n" (
        GOTO :startingCPS
    ) ELSE (
        ECHO Please provide valid input. Try running script again.
        CALL :build_cps
    )
)

:startingCPS
cd target
IF EXIST channel-processing-system-1.0-SNAPSHOT.jar (
    ECHO Starting Channel Processing System, please wait!
    java -jar channel-processing-system-1.0-SNAPSHOT.jar
    IF %ERRORLEVEL% equ 1 (
        ECHO Failed to run the application.
        EXIT /B 1
    )
) ELSE (
    ECHO Channel Processing Snapshot is not available. Please rebuild.
    EXIT /B 1
)
EXIT /B 0