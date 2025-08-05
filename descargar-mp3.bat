@echo off
REM Descargador simple de bibliotecas MP3
echo === DESCARGANDO BIBLIOTECAS MP3 ===

if not exist "lib-mp3" mkdir lib-mp3

echo.
echo 📥 Descargando bibliotecas desde Maven Central...

REM Descargar jlayer (JavaLayer)
echo Descargando jlayer-1.0.1.jar...
curl -L -o "lib-mp3\jlayer-1.0.1.jar" "https://repo1.maven.org/maven2/javazoom/jlayer/1.0.1/jlayer-1.0.1.jar"

REM Descargar mp3spi
echo Descargando mp3spi-1.9.5.4.jar...
curl -L -o "lib-mp3\mp3spi-1.9.5.4.jar" "https://repo1.maven.org/maven2/com/googlecode/soundlibs/mp3spi/1.9.5.4/mp3spi-1.9.5.4.jar"

REM Descargar tritonus-share
echo Descargando tritonus-share-0.3.7.4.jar...
curl -L -o "lib-mp3\tritonus-share-0.3.7.4.jar" "https://repo1.maven.org/maven2/com/googlecode/soundlibs/tritonus-share/0.3.7.4/tritonus-share-0.3.7.4.jar"

echo.
echo === VERIFICACION ===
dir lib-mp3\*.jar

echo.
if exist "lib-mp3\jlayer-1.0.1.jar" if exist "lib-mp3\mp3spi-1.9.5.4.jar" if exist "lib-mp3\tritonus-share-0.3.7.4.jar" (
    echo ✅ Todas las bibliotecas descargadas correctamente
    echo 🎵 Ahora puedes reproducir archivos MP3
    echo.
    echo Compila con: compilar.bat
    echo O manualmente: javac -cp "JMF-2.1.1e/lib/*;lib-mp3/*" -d build/classes src/reproductor1/*.java
) else (
    echo ⚠️  Algunas bibliotecas no se descargaron
    echo Verifica tu conexión a internet
    echo O descarga manualmente desde las URLs en INSTRUCCIONES_DESCARGA.txt
)

pause
