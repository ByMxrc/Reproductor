/*
 * Reproductor de Música - Clase de utilidades para manejo de listas
 * Versión mejorada con tipos genéricos y manejo de errores
 * @author Robert Moreira 
 */
package reproductor1;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.io.File;
import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Clase de utilidades para manejo de listas de reproducción
 * @author Robert Moreira
 */
public class NodoLista {
    /**
     * Crea una nueva lista de reproducción o limpia la existente
     */
    public static LinkedList<URL> crearLista(LinkedList<URL> laLista) {
        if(laLista == null) {    
            JOptionPane.showMessageDialog(null, "Acaba de crearse la lista");
            return new LinkedList<URL>();
        }
        
        int respuesta = JOptionPane.showConfirmDialog(null, "La lista tiene elementos ¿Quiere borrarlos?");
        if(respuesta == 0) { // Usuario presiona "si"
            return new LinkedList<URL>();
        }
        return laLista;
    }
    
    /**
     * Abre el explorador nativo de Windows para seleccionar archivos de audio
     */
    public static File mostrarExploradorWindows() {
        try {
            // Usar el explorador nativo de Windows con filtros de audio
            java.awt.FileDialog fileDialog = new java.awt.FileDialog((java.awt.Frame) null, "Seleccionar archivo de audio", java.awt.FileDialog.LOAD);
            
            // Intentar abrir en la carpeta de música por defecto
            File musicFolder = new File(System.getProperty("user.home"), "Music");
            if (musicFolder.exists()) {
                fileDialog.setDirectory(musicFolder.getAbsolutePath());
            } else {
                fileDialog.setDirectory(System.getProperty("user.home"));
            }
            
            // Configurar filtros de archivo para audio
            fileDialog.setFilenameFilter(new java.io.FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return isValidAudioFormat(name);
                }
            });
            
            // Mostrar el diálogo
            fileDialog.setVisible(true);
            
            // Obtener el archivo seleccionado
            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            
            if (fileName != null && directory != null) {
                return new File(directory, fileName);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al abrir explorador de Windows: " + e.getMessage());
            
            // Fallback al JFileChooser tradicional si falla
            return mostrarExploradorTradicional();
        }
        
        return null;
    }
    
    /**
     * Explorador tradicional como fallback
     */
    private static File mostrarExploradorTradicional() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de audio");
        
        // Configurar directorio inicial
        File musicFolder = new File(System.getProperty("user.home"), "Music");
        if (musicFolder.exists()) {
            fileChooser.setCurrentDirectory(musicFolder);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        
        // Filtro para archivos de audio
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return isValidAudioFormat(f.getName());
            }
            
            @Override
            public String getDescription() {
                return "Archivos de Audio (" + getSupportedFormatsString() + ")";
            }
        });
        
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        
        return null;
    }
    
    /**
     * Inserta un elemento al inicio de la lista usando el explorador nativo de Windows
     */
    public static LinkedList<URL> insercionCabecera(LinkedList<URL> lista) {
        try {
            File selectedFile = mostrarExploradorWindows();
            if (selectedFile != null) {
                String rutaFormateada = cambiarRutaFormatoJMF(selectedFile.getAbsolutePath());
                lista.addFirst(new URL(rutaFormateada));
                JOptionPane.showMessageDialog(null, "Archivo agregado al inicio de la lista: " + selectedFile.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar archivo: " + e.getMessage());
        }
        
        return lista;
    }
    
    /**
     * Inserta un elemento al final de la lista usando el explorador nativo de Windows
     */
    public static LinkedList<URL> insercionCima(LinkedList<URL> lista) {
        try {
            File selectedFile = mostrarExploradorWindows();
            if (selectedFile != null) {
                String rutaFormateada = cambiarRutaFormatoJMF(selectedFile.getAbsolutePath());
                lista.addLast(new URL(rutaFormateada));
                JOptionPane.showMessageDialog(null, "Archivo agregado al final de la lista: " + selectedFile.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar archivo: " + e.getMessage());
        }
        
        return lista;
    }
    
    /**
     * Inserta un elemento en una posición específica de la lista usando el explorador nativo de Windows
     */
    public static LinkedList<URL> insercionPorPosicion(LinkedList<URL> lista) {
        try {
            String input = JOptionPane.showInputDialog(
                "Número de elementos en la lista: " + lista.size() + "\n" +
                "¿En qué posición desea introducir? (1-" + (lista.size() + 1) + ")"
            );
            
            if (input == null) return lista; // Usuario canceló
            
            Integer eleccion = Integer.parseInt(input.trim());
            
            if (eleccion < 1 || eleccion > lista.size() + 1) {
                JOptionPane.showMessageDialog(null, "Posición inválida");
                return lista;
            }
            
            File selectedFile = mostrarExploradorWindows();
            if (selectedFile != null) {
                String rutaFormateada = cambiarRutaFormatoJMF(selectedFile.getAbsolutePath());
                lista.add(eleccion - 1, new URL(rutaFormateada));
                JOptionPane.showMessageDialog(null, "Archivo agregado en posición " + eleccion + ": " + selectedFile.getName());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese un número válido");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar archivo: " + e.getMessage());
        }
        
        return lista;
    }
    
    /**
     * Convierte una ruta de archivo local al formato URL que requiere JMF
     * FORMATO DE ENTRADA: "C:\wav\eresparami.wav"
     * FORMATO DE SALIDA: "file:/C:/wav/eresparami.wav"
     */
    public static String cambiarRutaFormatoJMF(String ruta) {
        ruta = ruta.replace("\\", "/");
        ruta = "file:/" + ruta;
        return ruta;
    }
    
    /**
     * Reproduce una lista - método original actualizado con nueva funcionalidad
     */
    public static Object reproducirLista(JFrame ventana, Object reproductor, LinkedList<URL> lista) {
        if (lista == null) {
            JOptionPane.showMessageDialog(null,"Hey...se debe crear primero la lista..");
        } else {
            JOptionPane.showMessageDialog(null, "Número de elementos en la lista: " + lista.size());
            
            try {
                String input = JOptionPane.showInputDialog("¿Cuál desea reproducir? (1-" + lista.size() + ")");
                if (input == null) return reproductor; // Usuario canceló
                
                Integer eleccion = Integer.parseInt(input.trim());
                
                if (eleccion < 1 || eleccion > lista.size()) {
                    JOptionPane.showMessageDialog(null, "Número inválido");
                    return reproductor;
                }
                
                // Reproducir usando la nueva funcionalidad
                URL selectedTrack = lista.get(eleccion - 1);
                String fileName = getFileNameFromURL(selectedTrack);
                
                boolean success = reproducirAudio(selectedTrack);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Reproduciendo: " + fileName);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al reproducir: " + fileName);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor ingrese un número válido");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al reproducir archivo: " + e.getMessage());
            }
        }
        
        return reproductor;    
    }     
    
    /**
     * Llena una JList con los elementos de una LinkedList
     */
    public static void LlenarJlistConLista(JList<String> elementos, LinkedList<URL> lista) {
        if(lista != null) {   
            DefaultListModel<String> modelo = new DefaultListModel<>();
            elementos.setModel(modelo);
            modelo.removeAllElements();
            for(int i = 0; i < lista.size(); i++) {
                String fileName = getFileNameFromURL(lista.get(i));
                modelo.addElement((i + 1) + ". " + fileName);
            }
        }
    }
    
    /**
     * Verifica si una lista está vacía
     */
    public static void MirarSiListaVacia(LinkedList<URL> laLista) {
        if (laLista == null)
            JOptionPane.showMessageDialog(null, "Flaco. La lista no estaba creada");
        else if (laLista.isEmpty())
            JOptionPane.showMessageDialog(null, "Flaco. La lista está creada pero no se le han guardado elementos");
        else 
            JOptionPane.showMessageDialog(null, "Flaco. Esta lista tiene " + laLista.size() + " elementos");
    }
    
    // =============== MÉTODOS MODERNOS AGREGADOS ===============
    
    // Variables estáticas para el reproductor de audio
    private static Clip currentClip = null;
    private static boolean isPlaying = false;
    
    // Variables para control de audio mejorado
    private static long pausedPosition = 0;
    
    // Variables para soporte MP3 (cuando esté disponible)
    private static boolean mp3Available = false;
    
    // Verificar disponibilidad de MP3 al cargar la clase
    static {
        checkMP3Support();
    }
    
    /**
     * Verifica si hay soporte para MP3 disponible
     */
    private static void checkMP3Support() {
        try {
            // Intentar cargar las clases necesarias para MP3
            Class.forName("javazoom.jl.player.Player");
            Class.forName("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader");
            mp3Available = true;
            System.out.println("✅ Soporte MP3 detectado");
        } catch (ClassNotFoundException e) {
            mp3Available = false;
            System.out.println("⚠️ Soporte MP3 no disponible - faltan bibliotecas");
        }
    }
    
    /**
     * Verifica si MP3 está soportado en este sistema
     */
    public static boolean isMP3Supported() {
        return mp3Available;
    }
    
    // Formatos de audio soportados
    private static final String[] SUPPORTED_FORMATS = {".wav", ".au", ".aiff", ".mp3"};
    
    /**
     * Verifica si un archivo tiene un formato de audio soportado
     */
    public static boolean isValidAudioFormat(String fileName) {
        if (fileName == null) return false;
        String lowerName = fileName.toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (lowerName.endsWith(format)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la lista de formatos soportados como string
     */
    public static String getSupportedFormatsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SUPPORTED_FORMATS.length; i++) {
            sb.append("*").append(SUPPORTED_FORMATS[i].toUpperCase());
            if (i < SUPPORTED_FORMATS.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    /**
     * Verifica qué formatos de audio están realmente soportados por el sistema
     */
    public static void verificarSoporteFormatos() {
        System.out.println("=== VERIFICACIÓN DE SOPORTE DE FORMATOS ===");
        
        // Obtener todos los tipos de archivo soportados por AudioSystem
        javax.sound.sampled.AudioFileFormat.Type[] supportedTypes = 
            AudioSystem.getAudioFileTypes();
        
        System.out.println("Formatos soportados nativamente por javax.sound.sampled:");
        for (javax.sound.sampled.AudioFileFormat.Type type : supportedTypes) {
            System.out.println("  • " + type.toString());
        }
        
        System.out.println("\nFormatos configurados en el reproductor:");
        for (String format : SUPPORTED_FORMATS) {
            System.out.println("  • " + format.toUpperCase());
        }
        
        System.out.println("\n=== ESTADO DEL SOPORTE MP3 ===");
        if (mp3Available) {
            System.out.println("✅ SOPORTE MP3 ACTIVADO");
            System.out.println("   Las bibliotecas MP3 están disponibles");
            System.out.println("   Los archivos MP3 se pueden reproducir");
        } else {
            System.out.println("❌ SOPORTE MP3 NO DISPONIBLE");
            System.out.println("   Faltan bibliotecas necesarias:");
            System.out.println("   - mp3spi1.9.5.jar");
            System.out.println("   - jl1.0.1.jar");
            System.out.println("   - tritonus_share.jar");
            System.out.println("   Consulta: lib-mp3/INSTRUCCIONES_DESCARGA.txt");
        }
        
        System.out.println("\n💡 RECOMENDACIÓN:");
        if (mp3Available) {
            System.out.println("   Todos los formatos están disponibles para reproducción");
        } else {
            System.out.println("   Para MP3: Descargar bibliotecas o convertir a WAV");
            System.out.println("   WAV/AU/AIFF: Funcionan perfectamente sin bibliotecas adicionales");
        }
    }
    
    /**
     * Reproduce un archivo de audio usando javax.sound.sampled o MP3 player
     */
    public static boolean reproducirAudio(URL audioURL) {
        try {
            // Detener cualquier reproducción previa
            detenerAudio();
            
            // Convertir URL a formato de archivo local si es necesario
            File audioFile;
            if (audioURL.getProtocol().equals("file")) {
                audioFile = new File(audioURL.getPath());
            } else {
                audioFile = new File(audioURL.toString());
            }
            
            // Verificar que el archivo existe
            if (!audioFile.exists()) {
                JOptionPane.showMessageDialog(null, "Error: El archivo no existe: " + audioFile.getAbsolutePath());
                return false;
            }
            
            // Verificar formato de archivo soportado
            String fileName = audioFile.getName();
            if (!isValidAudioFormat(fileName)) {
                JOptionPane.showMessageDialog(null, 
                    "Formato no soportado. Solo se permiten archivos: " + getSupportedFormatsString() + "\n" +
                    "Archivo: " + fileName);
                return false;
            }
            
            // Reproducir MP3 si está disponible el soporte
            if (fileName.toLowerCase().endsWith(".mp3")) {
                return reproducirMP3(audioFile);
            }
            
            // Reproducir formatos nativos (WAV, AU, AIFF)
            return reproducirFormatoNativo(audioFile);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Reproduce archivos MP3 usando bibliotecas especializadas
     */
    private static boolean reproducirMP3(File audioFile) {
        if (!mp3Available) {
            JOptionPane.showMessageDialog(null, 
                "❌ SOPORTE MP3 NO DISPONIBLE\n\n" +
                "Para reproducir MP3 necesitas descargar las bibliotecas:\n" +
                "• mp3spi1.9.5.jar\n" +
                "• jl1.0.1.jar\n" +
                "• tritonus_share.jar\n\n" +
                "Consulta: lib-mp3/INSTRUCCIONES_DESCARGA.txt\n" +
                "Por ahora, convierte el archivo a WAV.\n\n" +
                "Archivo: " + audioFile.getName());
            return false;
        }
        
        try {
            // Crear un AudioInputStream con soporte MP3
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            
            // Obtener el formato de audio
            AudioFormat baseFormat = audioStream.getFormat();
            
            // Crear formato PCM decodificado
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
            );
            
            // Convertir a formato decodificado
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
            
            // Crear y reproducir con Clip
            currentClip = AudioSystem.getClip();
            currentClip.open(decodedStream);
            currentClip.start();
            isPlaying = true;
            pausedPosition = 0;
            
            return true;
            
        } catch (UnsupportedAudioFileException e) {
            JOptionPane.showMessageDialog(null, 
                "❌ ERROR DE DECODIFICACIÓN MP3\n\n" +
                "Las bibliotecas MP3 no están correctamente instaladas.\n" +
                "Verifica que estén en el classpath:\n" +
                "• mp3spi1.9.5.jar\n" +
                "• jl1.0.1.jar\n" +
                "• tritonus_share.jar\n\n" +
                "Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reproduciendo MP3: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reproduce formatos nativos (WAV, AU, AIFF)
     */
    private static boolean reproducirFormatoNativo(File audioFile) {
        try {
            // Cargar y reproducir el archivo con soporte nativo
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            currentClip.start();
            isPlaying = true;
            pausedPosition = 0;
            
            return true;
            
        } catch (UnsupportedAudioFileException e) {
            JOptionPane.showMessageDialog(null, "Formato de audio no soportado: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + e.getMessage());
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Línea de audio no disponible: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pausa la reproducción actual guardando la posición
     */
    public static void pausarAudio() {
        if (currentClip != null && isPlaying) {
            pausedPosition = currentClip.getMicrosecondPosition(); // Guardar posición actual
            currentClip.stop();
            isPlaying = false;
        }
    }
    
    /**
     * Reanuda la reproducción desde la posición guardada
     */
    public static void reanudarAudio() {
        if (currentClip != null && !isPlaying) {
            currentClip.setMicrosecondPosition(pausedPosition); // Restaurar posición exacta
            currentClip.start();
            isPlaying = true;
        }
    }
    
    /**
     * Detiene completamente la reproducción
     */
    public static void detenerAudio() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            isPlaying = false;
            pausedPosition = 0; // Resetear posición pausada
        }
    }
    
    /**
     * Verifica si hay audio reproduciéndose
     */
    public static boolean estaReproduciendo() {
        if (currentClip != null && isPlaying) {
            try {
                return currentClip.isRunning() && currentClip.isOpen();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    /**
     * Establece el volumen de reproducción (0.0 a 1.0)
     */
    public static void setVolume(float volume) {
        if (currentClip != null) {
            try {
                FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                // Si no se puede controlar el volumen, no hacer nada
                System.out.println("No se puede controlar el volumen: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene el tiempo actual de reproducción en milisegundos
     */
    public static long getCurrentTime() {
        if (currentClip != null && currentClip.isOpen()) {
            try {
                return currentClip.getMicrosecondPosition() / 1000;
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Obtiene la duración total del audio en milisegundos
     */
    public static long getTotalTime() {
        if (currentClip != null && currentClip.isOpen()) {
            try {
                return currentClip.getMicrosecondLength() / 1000;
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Salta a una posición específica en milisegundos
     */
    public static void seekTo(long timeMs) {
        if (currentClip != null) {
            long timeMicros = timeMs * 1000;
            if (timeMicros >= 0 && timeMicros <= currentClip.getMicrosecondLength()) {
                boolean wasPlaying = isPlaying;
                if (wasPlaying) {
                    currentClip.stop();
                }
                currentClip.setMicrosecondPosition(timeMicros);
                pausedPosition = timeMicros; // Actualizar posición pausada
                if (wasPlaying) {
                    currentClip.start();
                }
            }
        }
    }
    
    /**
     * Convierte una ruta de archivo a URL en formato JMF
     */
    public static URL convertToURL(String filePath) throws Exception {
        String formattedPath = cambiarRutaFormatoJMF(filePath);
        return new URL(formattedPath);
    }
    
    /**
     * Reproduce una pista específica de la lista por índice
     */
    public static Object reproducirPista(JFrame ventana, Object reproductor, LinkedList<URL> lista, int index) throws Exception {
        if (lista == null || lista.isEmpty()) {
            throw new Exception("Lista vacía o no inicializada");
        }
        
        if (index < 0 || index >= lista.size()) {
            throw new Exception("Índice fuera de rango: " + index);
        }
        
        // Reproducir usando la nueva funcionalidad de audio
        URL selectedTrack = lista.get(index);
        String fileName = getFileNameFromURL(selectedTrack);
        
        boolean success = reproducirAudio(selectedTrack);
        if (success) {
            JOptionPane.showMessageDialog(ventana, "Reproduciendo: " + fileName);
        } else {
            JOptionPane.showMessageDialog(ventana, "Error al reproducir: " + fileName);
        }
        
        return reproductor;
    }
    
    /**
     * Actualiza la visualización de una JList con nombres de archivo más legibles
     */
    public static void updatePlaylistDisplay(JList<String> listComponent, LinkedList<URL> playlist) {
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (playlist != null) {
            for (int i = 0; i < playlist.size(); i++) {
                String fileName = getFileNameFromURL(playlist.get(i));
                model.addElement((i + 1) + ". " + fileName);
            }
        }
        
        listComponent.setModel(model);
    }
    
    /**
     * Extrae el nombre del archivo de una URL
     */
    private static String getFileNameFromURL(URL url) {
        String path = url.getPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        
        // Decodificar caracteres especiales si es necesario
        try {
            fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
        } catch (Exception e) {
            // Si falla la decodificación, usar el nombre original
        }
        
        return fileName;
    }
}