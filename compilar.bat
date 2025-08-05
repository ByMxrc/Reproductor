@echo off
REM Script de compilación con soporte opcional para MP3
REM ===================================================

echo === COMPILANDO REPRODUCTOR DE MUSICA ===

REM Verificar si existen las bibliotecas MP3
if exist "lib-mp3\*.jar" (
    echo ✅ Bibliotecas MP3 encontradas - Compilando con soporte completo
    set "MP3_LIBS=lib-mp3\*;"
    set "COMPILE_MESSAGE=CON SOPORTE MP3"
) else (
    echo ⚠️  Bibliotecas MP3 no encontradas - Compilando sin soporte MP3
    set "MP3_LIBS="
    set "COMPILE_MESSAGE=SIN SOPORTE MP3"
)

echo.
echo Compilando %COMPILE_MESSAGE%...

REM Compilar el proyecto
javac -cp "JMF-2.1.1e/lib/*;%MP3_LIBS%" -d build/classes src/reproductor1/*.java

if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilación exitosa
    echo.
    echo === COMO EJECUTAR ===
    echo Reproductor principal:
    echo java -cp "build/classes;JMF-2.1.1e/lib/*;%MP3_LIBS%" reproductor1.VentanaPrincipal
    echo.
    echo Verificar formatos:
    echo java -cp "build/classes;JMF-2.1.1e/lib/*;%MP3_LIBS%" reproductor1.DemoVerificacionFormatos
    echo.
    if defined MP3_LIBS (
        echo 🎵 MP3 debería funcionar correctamente
    ) else (
        echo 💡 Para soporte MP3, descarga las bibliotecas en lib-mp3/
    )
) else (
    echo ❌ Error en la compilación
    echo Verifica que todas las dependencias estén disponibles
)

pause
