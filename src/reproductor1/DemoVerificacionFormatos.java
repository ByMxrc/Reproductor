/*
 * Demo de Verificación de Formatos de Audio
 * Verifica qué formatos están realmente soportados en el sistema
 * @author GitHub Copilot
 */
package reproductor1;

import javax.swing.*;

public class DemoVerificacionFormatos {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== DEMO DE VERIFICACIÓN DE FORMATOS ===");
            System.out.println("Verificando soporte de formatos de audio en el sistema...\n");
            
            // Verificar formatos soportados
            NodoLista.verificarSoporteFormatos();
            
            System.out.println("\n=== RECOMENDACIONES ===");
            System.out.println("✅ FORMATOS TOTALMENTE COMPATIBLES:");
            System.out.println("   • WAV - Formato sin compresión, máxima compatibilidad");
            System.out.println("   • AU - Formato de Sun/Oracle, bien soportado");
            System.out.println("   • AIFF - Formato de Apple, bien soportado");
            
            System.out.println("\n⚠️  FORMATO CON LIMITACIONES:");
            System.out.println("   • MP3 - Requiere bibliotecas adicionales");
            System.out.println("     Soluciones:");
            System.out.println("     1. Usar archivos WAV para máxima compatibilidad");
            System.out.println("     2. Agregar biblioteca MP3SPI al proyecto");
            System.out.println("     3. Usar BasicPlayer library");
            System.out.println("     4. Convertir MP3 a WAV antes de reproducir");
            
            System.out.println("\n=== PRUEBA PRÁCTICA ===");
            System.out.println("Selecciona un archivo para probar su compatibilidad:");
            
            try {
                java.io.File archivo = NodoLista.mostrarExploradorWindows();
                if (archivo != null) {
                    System.out.println("\n📄 Archivo seleccionado: " + archivo.getName());
                    
                    boolean esValido = NodoLista.isValidAudioFormat(archivo.getName());
                    System.out.println("🔍 Extensión válida: " + (esValido ? "✅ SÍ" : "❌ NO"));
                    
                    if (esValido) {
                        System.out.println("🎵 Intentando reproducir...");
                        boolean exito = NodoLista.reproducirAudio(archivo.toURI().toURL());
                        if (exito) {
                            System.out.println("✅ Reproducción exitosa!");
                            // Detener después de 3 segundos
                            Thread.sleep(3000);
                            NodoLista.detenerAudio();
                            System.out.println("🛑 Reproducción detenida");
                        } else {
                            System.out.println("❌ Error en la reproducción");
                        }
                    }
                } else {
                    System.out.println("❌ No se seleccionó ningún archivo");
                }
            } catch (Exception e) {
                System.out.println("❌ Error durante la prueba: " + e.getMessage());
            }
            
            System.out.println("\n=== DEMO COMPLETADA ===");
            System.exit(0);
        });
    }
}
