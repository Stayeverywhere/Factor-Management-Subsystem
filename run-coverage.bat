@echo off
set PROJECT_DIR=%~dp0
set MAVEN_OPTS=-javaagent:%PROJECT_DIR%target\jacoco-agent.jar=destfile=%PROJECT_DIR%target\jacoco.exec
call mvn clean test jacoco:report
echo.
echo Coverage report: %PROJECT_DIR%target\site\jacoco\index.html
pause
