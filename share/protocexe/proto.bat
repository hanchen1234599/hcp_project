@echo off

::协议文件路径, 最后不要跟“\”符号
set SOURCE_FOLDER=..\pbc
set CILENT_FOLDER=..\..\..\hcp_01client\Assets\StreamingAssets\pbc

::Java编译器路径
set JAVA_COMPILER_PATH=.\bin\protoc.exe
::Java文件生成路径, 最后不要跟“\”符号
set JAVA_TARGET_PATH=..\src\proto

::删除之前创建的文件
del %JAVA_TARGET_PATH%\*.* /f /s /q
::遍历所有文件
for /f "delims=" %%i in ('dir /b "%SOURCE_FOLDER%\*.proto"') do (    
    ::生成 Java 代码
    echo %JAVA_COMPILER_PATH% -I %SOURCE_FOLDER% --java_out=%JAVA_TARGET_PATH% %SOURCE_FOLDER%\%%i
    %JAVA_COMPILER_PATH% -I %SOURCE_FOLDER% --java_out=%JAVA_TARGET_PATH% %SOURCE_FOLDER%\%%i
)

del %CILENT_FOLDER%\*.* /f /s /q
xcopy %SOURCE_FOLDER% %CILENT_FOLDER% /s /e /y

echo 协议生成完毕。

pause