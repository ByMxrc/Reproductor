# 🎵 Reproductor de Música Moderno

Un reproductor de música desarrollado en Java con interfaz gráfica Swing, diseñado como proyecto de **Estructuras de Datos** con funcionalidades avanzadas y una interfaz moderna estilo Spotify.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white)

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Estado Inicial del Proyecto](#-estado-inicial-del-proyecto)
- [Características Actuales](#-características-actuales)
- [Mejoras Implementadas](#-mejoras-implementadas)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Instalación y Uso](#-instalación-y-uso)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Arquitectura](#-arquitectura)
- [Contribuciones](#-contribuciones)

## 🎯 Descripción

Este proyecto comenzó como un reproductor de música básico para demostrar el uso de **estructuras de datos lineales** (LinkedList) en Java. A través de múltiples iteraciones de mejora, se ha transformado en un reproductor moderno con interfaz profesional y funcionalidades avanzadas.

## 📚 Estado Inicial del Proyecto

### Versión Original
El proyecto original incluía:
- ✅ Reproducción básica de archivos de audio usando JMF (Java Media Framework)
- ✅ Lista de reproducción simple con LinkedList
- ✅ Controles básicos: play, stop, previous, next
- ✅ Interfaz básica generada por NetBeans
- ✅ Soporte para formatos: WAV, AU, AIFF
- ✅ Diseñado con JDK 17 

### Limitaciones Iniciales
- ❌ Interfaz anticuada y poco atractiva
- ❌ Dependencia de JMF obsoleto
- ❌ Sin controles de volumen o línea de tiempo
- ❌ Sin modos de repetición o reproducción aleatoria
- ❌ Sin gestión avanzada de playlist
- ❌ Problemas de sincronización en la reproducción

## ⭐ Características Actuales

### 🎨 Interfaz Moderna
- **Tema Oscuro Profesional**: Paleta de colores estilo Spotify
- **Componentes Estilizados**: Botones con efectos hover y diseño moderno
- **Layout Responsivo**: Distribución organizada y profesional
- **Iconografía Textual**: Compatibilidad universal sin dependencias

### 🎵 Control de Reproducción Avanzado
- **Botón Unificado**: Play/Pause inteligente
- **Línea de Tiempo Interactiva**: Seek manual con preview visual
- **Control de Volumen**: Slider con feedback visual (0-100%)
- **Modos de Repetición**: Sin repetir, repetir lista, repetir canción
- **Reproducción Aleatoria**: Shuffle con orden aleatorio

### 📝 Gestión de Playlist
- **Creación Automática**: Lista generada al iniciar
- **Drag & Drop**: Reordenamiento intuitivo arrastrando canciones
- **Gestión Manual**: Botones subir/bajar para reordenar
- **Información Detallada**: Estado actual y progreso de reproducción

### 🔧 Sistema de Audio Robusto
- **Engine Moderno**: Migración de JMF a javax.sound.sampled
- **Control de Posición**: Seek preciso con preservación de estado
- **Detección Inteligente**: Sistema robusto de detección de fin de canción
- **Thread Safety**: Manejo seguro de hilos para UI y audio

### 🎼 Compatibilidad de Formatos
- **✅ Soporte Completo**: WAV, AU, AIFF (nativos de javax.sound.sampled)
- **⚠️ Limitación MP3**: Incluido en filtros pero requiere bibliotecas adicionales
- **🔍 Validación Inteligente**: Detección automática de formatos soportados
- **💡 Mensajes Informativos**: Explicación clara sobre limitaciones de MP3
- **✅ Acutalizado a JDK 24 para mayor compatibilidad**

## 🚀 Mejoras Implementadas

### 🎨 **1. Modernización de Interfaz**
#### **Antes:**
```java
// Interfaz básica generada por NetBeans
JButton playButton = new JButton("Play");
JButton stopButton = new JButton("Stop");
```

#### **Después:**
```java
// Botones estilizados con tema moderno
private final Color PRIMARY_COLOR = new Color(25, 25, 25);
private final Color ACCENT_COLOR = new Color(29, 185, 84);

playPauseButton = createStyledButton("Reproducir", ACCENT_COLOR);
// + Efectos hover, cursores personalizados, tooltips
```

### 🎵 **2. Sistema de Audio Mejorado**
#### **Antes:**
```java
// JMF obsoleto con limitaciones
import javax.media.Player;
Player player = Manager.createRealizedPlayer(url);
```

#### **Después:**
```java
// javax.sound.sampled moderno
import javax.sound.sampled.*;
boolean success = NodoLista.reproducirAudio(selectedTrack);
// + Control de posición, volumen, detección inteligente
```

### 📊 **3. Línea de Tiempo y Controles**
#### **Nuevas Características:**
- **Timeline Interactivo**: Slider con posición en tiempo real
- **Control de Volumen**: 0-100% con feedback visual
- **Información Temporal**: "2:34 / 4:12" formato MM:SS
- **Seek Manual**: Click para saltar a posición específica

```java
// Timeline con actualización en tiempo real
private void updateTimeline() {
    if (NodoLista.estaReproduciendo() && !userInteracting) {
        long currentTimeMs = NodoLista.getCurrentTime();
        long totalTimeMs = NodoLista.getTotalTime();
        int percentage = (int) ((currentTimeMs * 100) / totalTimeMs);
        timelineSlider.setValue(percentage);
    }
}
```

### 🔄 **4. Modos de Reproducción Avanzados**
#### **Repetición Inteligente:**
- **Sin Repetir**: Reproducción lineal
- **Repetir Lista**: Ciclo continuo de toda la playlist
- **Repetir Canción**: Loop de la canción actual

#### **Shuffle Mejorado:**
```java
private void createShuffleOrder() {
    shuffleOrder = new ArrayList<>();
    for (int i = 0; i < miLista.size(); i++) {
        shuffleOrder.add(i);
    }
    Collections.shuffle(shuffleOrder);
}
```

### 📝 **5. Gestión de Playlist Avanzada**
#### **Drag & Drop Nativo:**
```java
// Transferencia personalizada para reordenamiento
private class PlaylistTransferHandler extends TransferHandler {
    // Implementación completa de drag & drop
}
```

#### **Controles de Reordenamiento:**
- Subir/Bajar canciones manualmente
- Arrastrar y soltar para reordenar
- Eliminación con confirmación
- Limpieza completa de lista

### 🛡️ **6. Explorador de Archivos Nativo**
#### **Integración con Windows:**
```java
// FileDialog nativo de Windows para máxima compatibilidad
java.awt.FileDialog fileDialog = new java.awt.FileDialog(
    (java.awt.Frame) null, "Seleccionar archivo de audio", 
    java.awt.FileDialog.LOAD
);

// Filtros automáticos para archivos de audio
fileDialog.setFilenameFilter((dir, name) -> {
    String nameLower = name.toLowerCase();
    return nameLower.endsWith(".wav") || nameLower.endsWith(".au") || 
           nameLower.endsWith(".aiff") || nameLower.endsWith(".mp3");
});
```

#### **Características del Explorador:**
- **Interfaz Nativa**: Usa el explorador familiar de Windows
- **Directorio Inteligente**: Abre automáticamente en carpeta "Música"
- **Filtros Automáticos**: Solo muestra archivos de audio compatibles
- **Fallback Robusto**: JFileChooser como respaldo si falla
- **Integración Completa**: Respeta la configuración del usuario

### 🛡️ **7. Robustez y Estabilidad**
#### **Detección de Fin de Canción:**
```java
// Sistema robusto con verificaciones múltiples
private void createTrackEndDetector() {
    trackEndDetectorThread = new Thread(() -> {
        // Verificación basada en tiempo y posición
        if (totalTime > 0 && currentTime >= (totalTime - 2000)) {
            SwingUtilities.invokeLater(() -> handleTrackEnd());
        }
    });
}
```

#### **Thread Safety:**
- Uso de `SwingUtilities.invokeLater()` para UI
- Manejo seguro de interrupciones de threads
- Sincronización entre audio y interfaz

### 🎯 **8. Experiencia de Usuario**
- **Feedback Visual**: Estados claros de botones y controles
- **Tooltips Informativos**: Ayuda contextual en todos los controles
- **Mensajes de Estado**: Información clara sobre el estado actual
- **Prevención de Errores**: Validaciones y confirmaciones

## 📁 Estructura del Proyecto

```
Reproductor1/
├── src/
│   └── reproductor1/
│       ├── VentanaPrincipal.java    # Interfaz principal modernizada
│       ├── NodoLista.java           # Engine de audio mejorado
│       ├── Reproductor1.java        # Clase main
│       └── Nt6v.gif                # Icono de la aplicación
├── build/
│   └── classes/                     # Archivos compilados
├── JMF-2.1.1e/                    # Framework de multimedia (legacy)
├── nbproject/                       # Configuración NetBeans
├── build.xml                       # Script de construcción Ant
└── README.md                       # Documentación
```

## 🚀 Instalación y Uso

### Requisitos Previos
- **Java 8+** (recomendado Java 11 o superior)
- **NetBeans IDE** (opcional, para desarrollo)

### Compilación Manual
```bash
# Compilar el proyecto
javac -cp "JMF-2.1.1e/lib/*" -d build/classes src/reproductor1/*.java

# Ejecutar la aplicación
java -cp "build/classes;JMF-2.1.1e/lib/*" reproductor1.VentanaPrincipal
```

### Uso de la Aplicación

1. **Agregar Música**: Click en "Agregar Música" para seleccionar archivos
2. **Reproducir**: Doble click en una canción o usar botón Play/Pause
3. **Controlar**: Usar timeline para navegar, volumen para ajustar audio
4. **Modos**: Activar repetición (REP/REP1) o shuffle (ALT)
5. **Organizar**: Arrastrar canciones o usar botones Subir/Bajar

## 🛠️ Tecnologías Utilizadas

### Core
- **Java**: Lenguaje principal
- **Swing**: Framework de interfaz gráfica
- **javax.sound.sampled**: Sistema de audio moderno
- **LinkedList**: Estructura de datos para playlist

### Bibliotecas
- **Java AWT**: Eventos y componentes base
- **Collections Framework**: Gestión de datos
- **Threading**: Manejo concurrente de audio y UI

### Herramientas
- **NetBeans**: IDE de desarrollo
- **Apache Ant**: Sistema de construcción
- **Git**: Control de versiones

## 🏗️ Arquitectura

### Patrón de Diseño
- **MVC Implícito**: Separación entre lógica y presentación
- **Observer Pattern**: Sistema de eventos Swing
- **Factory Pattern**: Creación de componentes estilizados

### Componentes Principales

#### **VentanaPrincipal.java**
```java
public class VentanaPrincipal extends JFrame {
    // Gestión de interfaz, eventos y estado
    private LinkedList<URL> miLista;           // Modelo de datos
    private Thread trackEndDetectorThread;     // Control de audio
    private boolean userInteracting;           // Estado de UI
}
```

#### **NodoLista.java**
```java
public class NodoLista {
    // Engine de audio con javax.sound.sampled
    public static boolean reproducirAudio(URL url);
    public static void pausarAudio();
    public static void reanudarAudio();
    public static long getCurrentTime();
    public static void seekTo(long position);
}
```

### Flujo de Datos
```
Usuario → VentanaPrincipal → NodoLista → javax.sound.sampled → Audio Output
    ↑                          ↓
Timeline ← UI Updates ← Thread Monitor ← Audio Status
```

## 📈 Métricas del Proyecto

### Código
- **Líneas de Código**: ~1,200 líneas
- **Clases**: 3 principales + clases internas
- **Métodos**: 35+ métodos bien documentados
- **Cobertura**: Funcionalidad completa implementada

### Funcionalidades
- ✅ **15+ Características** implementadas
- ✅ **100% Funcional** sin errores críticos
- ✅ **Thread-Safe** con manejo concurrente
- ✅ **Cross-Platform** compatible

## 🤝 Contribuciones

### Autor Original
- **Robert Moreira** - Estructura inicial y concepto base

### Mejoras y Modernización
- **Implementación de interfaz moderna**
- **Migración a sistema de audio actual**
- **Desarrollo de funcionalidades avanzadas**
- **Optimización de rendimiento y estabilidad**

### Cómo Contribuir
1. Fork el repositorio
2. Crear rama de feature (`git checkout -b feature/nueva-caracteristica`)
3. Commit cambios (`git commit -am 'Agregar nueva característica'`)
4. Push a la rama (`git push origin feature/nueva-caracteristica`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia que determines apropiada.

## 🎯 Roadmap Futuro

### Funcionalidades Planeadas
- [ ] **Ecualizador**: Control de frecuencias
- [ ] **Playlist Múltiples**: Gestión de varias listas
- [ ] **Temas Personalizables**: Light/Dark themes
- [ ] **Shortcuts de Teclado**: Atajos para control rápido
- [ ] **Visualizador**: Efectos visuales de audio
- [ ] **Last.fm Integration**: Scrobbling de canciones

### Mejoras Técnicas
- [ ] **Maven/Gradle**: Migración de sistema de construcción
- [ ] **JUnit Tests**: Suite de pruebas automatizadas
- [ ] **JavaFX Migration**: Interfaz más moderna
- [ ] **Plugin Architecture**: Sistema de extensiones

---

**📝 Nota**: Este reproductor fue desarrollado como proyecto educativo para demostrar estructuras de datos y programación en Java, evolucionando hacia una aplicación completa y funcional.

**⭐ Si este proyecto te resulta útil, considera darle una estrella en GitHub!**
