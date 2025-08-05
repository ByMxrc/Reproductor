/*
 * Demo del Explorador Nativo de Windows
 */
package reproductor1;

import javax.swing.*;
import java.io.File;

public class DemoExploradorWindows {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== DEMO DEL EXPLORADOR NATIVO DE WINDOWS ===");
            System.out.println("Características:");
            System.out.println("• Utiliza el explorador nativo de Windows (FileDialog)");
            System.out.println("• Interfaz familiar para usuarios de Windows");
            System.out.println("• Integración completa con el sistema operativo");
            System.out.println("• Filtros automáticos para archivos de audio");
            System.out.println("• Directorio inicial en carpeta Música");
            System.out.println("• Fallback a JFileChooser si hay problemas");
            System.out.println();
            System.out.println("Abriendo explorador nativo de Windows...");
            
            File archivo = NodoLista.mostrarExploradorWindows();
            
            if (archivo != null) {
                System.out.println("✓ Archivo seleccionado: " + archivo.getName());
                System.out.println("✓ Ruta completa: " + archivo.getAbsolutePath());
                System.out.println("✓ Tamaño: " + formatFileSize(archivo.length()));
                
                // Verificar si es un archivo de audio válido
                boolean isValidAudio = NodoLista.isValidAudioFormat(archivo.getName());
                
                if (isValidAudio) {
                    System.out.println("✓ Formato de audio válido");
                    System.out.println("✓ Formatos soportados: " + NodoLista.getSupportedFormatsString());
                } else {
                    System.out.println("⚠ Advertencia: El archivo no tiene una extensión de audio reconocida");
                    System.out.println("ℹ Formatos soportados: " + NodoLista.getSupportedFormatsString());
                }
                
            } else {
                System.out.println("✗ No se seleccionó ningún archivo");
            }
            
            System.out.println("\nDemo completada. Cerrando...");
            System.exit(0);
        });
    }
    
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
