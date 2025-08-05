# Script para descargar bibliotecas MP3 automáticamente
# ====================================================

Write-Host "=== DESCARGADOR DE BIBLIOTECAS MP3 ===" -ForegroundColor Green
Write-Host ""

$libDir = "lib-mp3"
if (!(Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir
    Write-Host "✅ Directorio $libDir creado" -ForegroundColor Green
}

Write-Host "Descargando bibliotecas MP3..." -ForegroundColor Yellow

# URLs de las bibliotecas (estas son ejemplos - necesitarás las URLs reales)
$libraries = @{
    "mp3spi1.9.5.jar" = "https://repo1.maven.org/maven2/com/googlecode/soundlibs/mp3spi/1.9.5.4/mp3spi-1.9.5.4.jar"
    "jl1.0.1.jar" = "https://repo1.maven.org/maven2/javazoom/jlayer/1.0.1/jlayer-1.0.1.jar"
    "tritonus_share.jar" = "https://repo1.maven.org/maven2/com/googlecode/soundlibs/tritonus-share/0.3.7.4/tritonus-share-0.3.7.4.jar"
}

foreach ($lib in $libraries.GetEnumerator()) {
    $fileName = $lib.Key
    $url = $lib.Value
    $destination = Join-Path $libDir $fileName
    
    try {
        Write-Host "📥 Descargando $fileName..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $url -OutFile $destination -UseBasicParsing
        Write-Host "✅ $fileName descargado" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Error descargando $fileName" -ForegroundColor Red
        Write-Host "   URL: $url" -ForegroundColor Gray
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
        Write-Host "   💡 Descarga manual requerida" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "=== VERIFICACIÓN ===" -ForegroundColor Green
Get-ChildItem $libDir -Filter "*.jar" | ForEach-Object {
    $size = [math]::Round($_.Length / 1KB, 2)
    Write-Host "✅ $($_.Name) ($size KB)" -ForegroundColor Green
}

if ((Get-ChildItem $libDir -Filter "*.jar").Count -eq 3) {
    Write-Host ""
    Write-Host "🎉 ¡Todas las bibliotecas descargadas correctamente!" -ForegroundColor Green
    Write-Host "   Ahora puedes compilar con soporte MP3 usando compilar.bat" -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "⚠️  Algunas bibliotecas no se descargaron" -ForegroundColor Yellow
    Write-Host "   Revisa lib-mp3/INSTRUCCIONES_DESCARGA.txt para descarga manual" -ForegroundColor Gray
}

Write-Host ""
Read-Host "Presiona Enter para continuar"
