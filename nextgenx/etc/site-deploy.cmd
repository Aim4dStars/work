del /F /S /Q \\dwgps0026\site\nextgen\%1\*.*
xcopy /Y /F /E /I target\docs target\site\docs
xcopy /Y /F /E target\site\*.* \\dwgps0026\site\nextgen\%1\