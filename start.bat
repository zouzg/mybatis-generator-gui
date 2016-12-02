@echo off
set JAVA_HOME=%IDEA_JDK_64%
set path=%JAVA_HOME%\bin;%path%
call mvn compile
call mvn exec:java -Dexec.mainClass="com.zzg.mybatis.generator.MainUI"
pause