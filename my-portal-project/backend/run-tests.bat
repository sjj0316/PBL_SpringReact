@echo off
echo Running tests...
call mvnw.cmd clean test-compile
call mvnw.cmd test -Dtest=SanityTest
echo Tests completed.
pause 