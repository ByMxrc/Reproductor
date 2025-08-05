/*
 * Reproductor de Música - Estructuras de Datos
 * Versión mejorada con interfaz moderna
 * @author Robert Moreira
 */
package reproductor1;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

// Comentado temporalmente para resolver problemas de compilación
// import javax.media.Player;



/**
 * Reproductor de Música Moderno
 * Interfaz completamente rediseñada con componentes modernos
 */
public class VentanaPrincipal extends JFrame {
    
    // Variables principales
    private LinkedList<URL> miLista = null;
    private int currentTrack = -1;
    
    // Componentes de la interfaz moderna
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel playlistPanel;
    private JPanel controlPanel;
    private JPanel buttonPanel;
    
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel trackInfoLabel;
    
    private JList<String> playlistJList;
    private DefaultListModel<String> playlistModel;
    private JScrollPane playlistScrollPane;
    
    // Botones principales de control
    private JButton playPauseButton; // Botón unificado de reproducir/pausar
    private JButton stopButton;
    private JButton previousButton;
    private JButton nextButton;
    private JButton repeatButton;
    private JButton shuffleButton;
    
    // Estado del reproductor
    private boolean isPlaying = false;
    private int repeatMode = 0; // 0: sin repetir, 1: repetir lista, 2: repetir canción
    private boolean shuffleMode = false;
    private java.util.Set<Integer> playedTracks = new java.util.HashSet<>(); // Canciones ya reproducidas en el ciclo actual
    private java.util.List<Integer> availableTracks = new java.util.ArrayList<>(); // Canciones disponibles para reproducir
    
    // Botones de gestión de playlist
    private JButton addTrackButton;
    private JButton removeButton;
    private JButton clearPlaylistButton;
    
    // Botones de reordenamiento
    private JButton moveUpButton;
    private JButton moveDownButton;
    
    // Componentes de línea de tiempo y volumen
    private JSlider timelineSlider;
    private JSlider volumeSlider;
    private JLabel currentTimeLabel;
    private JLabel totalTimeLabel;
    private JLabel volumeLabel;
    private JPanel timelinePanel;
    private JPanel volumePanel;
    
    // Variables de tiempo y volumen
    private int currentTime = 0;
    private int totalTime = 0;
    private int currentVolume = 75; // Volumen inicial 75%
    private javax.swing.Timer timelineTimer;
    private boolean userInteracting = false; // Variable para saber si el usuario está interactuando
    private Thread trackEndDetectorThread = null; // Thread para detectar fin de canción
    
    // Colores del tema moderno
    private final Color PRIMARY_COLOR = new Color(25, 25, 25);
    private final Color SECONDARY_COLOR = new Color(40, 40, 40);
    private final Color ACCENT_COLOR = new Color(29, 185, 84);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color TEXT_SECONDARY = new Color(179, 179, 179);
    
    public VentanaPrincipal() {
        // Crear automáticamente la lista de reproducción al inicializar
        miLista = new LinkedList<>();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureWindow();
        
        // Actualizar la interfaz con la lista inicial
        updateStatus("Lista de reproducción creada - Listo para agregar música");
        updateTrackInfo();
    }
    
    private void initializeComponents() {
        // Configurar modelo de lista
        playlistModel = new DefaultListModel<>();
        playlistJList = new JList<>(playlistModel);
        
        // Configurar paneles principales
        mainPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel = new JPanel(new BorderLayout());
        playlistPanel = new JPanel(new BorderLayout());
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10)); // Cambiado a 2 filas, 3 columnas
        
        // Configurar labels
        titleLabel = new JLabel("Reproductor de Música Moderno", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        statusLabel = new JLabel("Listo para reproducir", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(TEXT_SECONDARY);
        
        trackInfoLabel = new JLabel("No hay pista seleccionada", JLabel.CENTER);
        trackInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        trackInfoLabel.setForeground(TEXT_SECONDARY);
        
        // Configurar lista de reproducción
        setupPlaylist();
        
        // Configurar botones de control
        setupControlButtons();
        
        // Configurar botones de gestión
        setupManagementButtons();
        
        // Configurar línea de tiempo y volumen
        setupTimelineAndVolume();
    }
    
    private void setupPlaylist() {
        playlistJList.setBackground(SECONDARY_COLOR);
        playlistJList.setForeground(TEXT_COLOR);
        playlistJList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        playlistJList.setSelectionBackground(ACCENT_COLOR);
        playlistJList.setSelectionForeground(Color.WHITE);
        playlistJList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Habilitar drag & drop para reordenar
        playlistJList.setDragEnabled(true);
        playlistJList.setDropMode(DropMode.INSERT);
        playlistJList.setTransferHandler(new PlaylistTransferHandler());
        
        playlistScrollPane = new JScrollPane(playlistJList);
        playlistScrollPane.setBackground(SECONDARY_COLOR);
        playlistScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2), 
            "Lista de Reproducción - Arrastra para reordenar", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 14), 
            TEXT_COLOR
        ));
        playlistScrollPane.setPreferredSize(new Dimension(400, 300));
    }
    
    private void setupControlButtons() {
        // Botón unificado Play/Pause
        playPauseButton = createStyledButton("Reproducir", ACCENT_COLOR);
        playPauseButton.setToolTipText("Reproducir pista seleccionada");
        
        // Botón Stop
        stopButton = createStyledButton("Detener", new Color(220, 53, 69));
        stopButton.setToolTipText("Detener reproducción");
        stopButton.setEnabled(false);
        
        // Botón Anterior
        previousButton = createStyledButton("<<", new Color(108, 117, 125));
        previousButton.setToolTipText("Pista anterior");
        
        // Botón Siguiente
        nextButton = createStyledButton(">>", new Color(108, 117, 125));
        nextButton.setToolTipText("Pista siguiente");
        
        // Botón Repetir
        repeatButton = createStyledButton("REP", new Color(255, 152, 0));
        updateRepeatButton();
        
        // Botón Shuffle
        shuffleButton = createStyledButton("ALT", new Color(123, 104, 238));
        updateShuffleButton();
    }
    
    private void setupManagementButtons() {
        addTrackButton = createStyledButton("Agregar Música", new Color(32, 201, 151));
        addTrackButton.setToolTipText("Agregar nueva canción a la lista");
        
        removeButton = createStyledButton("Eliminar Pista", new Color(220, 53, 69));
        removeButton.setToolTipText("Eliminar la canción seleccionada");
        
        clearPlaylistButton = createStyledButton("Limpiar Lista", new Color(220, 53, 69));
        clearPlaylistButton.setToolTipText("Eliminar todas las canciones de la lista");
        
        // Botones de reordenamiento
        moveUpButton = createStyledButton("Subir", new Color(75, 85, 99));
        moveUpButton.setToolTipText("Mover la canción seleccionada hacia arriba");
        
        moveDownButton = createStyledButton("Bajar", new Color(75, 85, 99));
        moveDownButton.setToolTipText("Mover la canción seleccionada hacia abajo");
    }
    
    private void setupTimelineAndVolume() {
        // Configurar línea de tiempo
        timelineSlider = new JSlider(0, 100, 0);
        timelineSlider.setBackground(SECONDARY_COLOR);
        timelineSlider.setForeground(ACCENT_COLOR);
        timelineSlider.setEnabled(false); // Habilitado solo durante reproducción
        
        // Labels de tiempo
        currentTimeLabel = new JLabel("0:00");
        currentTimeLabel.setForeground(TEXT_COLOR);
        currentTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        totalTimeLabel = new JLabel("0:00");
        totalTimeLabel.setForeground(TEXT_COLOR);
        totalTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Panel de línea de tiempo
        timelinePanel = new JPanel(new BorderLayout(5, 0));
        timelinePanel.setBackground(SECONDARY_COLOR);
        timelinePanel.add(currentTimeLabel, BorderLayout.WEST);
        timelinePanel.add(timelineSlider, BorderLayout.CENTER);
        timelinePanel.add(totalTimeLabel, BorderLayout.EAST);
        
        // Configurar control de volumen
        volumeSlider = new JSlider(0, 100, currentVolume);
        volumeSlider.setBackground(SECONDARY_COLOR);
        volumeSlider.setForeground(ACCENT_COLOR);
        volumeSlider.setPreferredSize(new Dimension(100, 30));
        
        volumeLabel = new JLabel("Vol: " + currentVolume + "%");
        volumeLabel.setForeground(TEXT_COLOR);
        volumeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        volumeLabel.setPreferredSize(new Dimension(70, 30));
        
        // Panel de volumen
        volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        volumePanel.setBackground(SECONDARY_COLOR);
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        
        // Configurar timer para actualizar línea de tiempo
        timelineTimer = new javax.swing.Timer(500, e -> updateTimeline()); // Reducir a 500ms para más suavidad
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        // Panel de encabezado
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Panel de lista de reproducción
        playlistPanel.setBackground(PRIMARY_COLOR);
        playlistPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        playlistPanel.add(playlistScrollPane, BorderLayout.CENTER);
        playlistPanel.add(trackInfoLabel, BorderLayout.SOUTH);
        
        // Panel de controles de reproducción
        controlPanel.setBackground(SECONDARY_COLOR);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            "Controles de Reproducción",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
        
        // Crear sub-panel para botones de control
        JPanel buttonsControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsControlPanel.setBackground(SECONDARY_COLOR);
        buttonsControlPanel.add(previousButton);
        buttonsControlPanel.add(playPauseButton);
        buttonsControlPanel.add(stopButton);
        buttonsControlPanel.add(nextButton);
        buttonsControlPanel.add(repeatButton);
        buttonsControlPanel.add(shuffleButton);
        
        // Crear sub-panel para línea de tiempo y volumen
        JPanel timelineVolumeControlPanel = new JPanel(new BorderLayout(10, 5));
        timelineVolumeControlPanel.setBackground(SECONDARY_COLOR);
        timelineVolumeControlPanel.add(timelinePanel, BorderLayout.CENTER);
        timelineVolumeControlPanel.add(volumePanel, BorderLayout.EAST);
        
        // Agregar todo al panel de controles principal
        controlPanel.setLayout(new BorderLayout(0, 10));
        controlPanel.add(buttonsControlPanel, BorderLayout.NORTH);
        controlPanel.add(timelineVolumeControlPanel, BorderLayout.SOUTH);
        
        // Panel de botones de gestión
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            "Gestión de Playlist",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
        buttonPanel.add(addTrackButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearPlaylistButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        // Agregar un panel vacío para mantener la simetría del grid 2x3
        buttonPanel.add(new JLabel());
        
        // Panel principal
        mainPanel.setBackground(PRIMARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(playlistPanel, BorderLayout.CENTER);
        
        // Panel inferior con controles
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        bottomPanel.setBackground(PRIMARY_COLOR);
        bottomPanel.add(controlPanel);
        bottomPanel.add(buttonPanel);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Eventos de control de reproducción
        playPauseButton.addActionListener(e -> togglePlayPause());
        stopButton.addActionListener(e -> stopPlayback());
        previousButton.addActionListener(e -> playPreviousTrack());
        nextButton.addActionListener(e -> playNextTrack());
        repeatButton.addActionListener(e -> toggleRepeatMode());
        shuffleButton.addActionListener(e -> toggleShuffleMode());
        
        // Eventos de gestión de playlist
        addTrackButton.addActionListener(e -> addTrack());
        removeButton.addActionListener(e -> removeSelectedTrack());
        clearPlaylistButton.addActionListener(e -> clearPlaylist());
        
        // Eventos de reordenamiento
        moveUpButton.addActionListener(e -> moveTrackUp());
        moveDownButton.addActionListener(e -> moveTrackDown());
        
        // Eventos de línea de tiempo y volumen
        setupTimelineAndVolumeListeners();
        
        // Evento de selección de lista
        playlistJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateTrackInfo();
            }
        });
        
        // Doble clic para reproducir
        playlistJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playSelectedTrack();
                }
            }
        });
    }
    
    private void configureWindow() {
        setTitle("Reproductor de Música - Estructuras de Datos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setResizable(true);
        setMinimumSize(new Dimension(800, 600));
        
        // Centrar ventana
        setLocationRelativeTo(null);
        
        // Icono de la aplicación (si existe)
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/reproductor1/Nt6v.gif")));
        } catch (Exception e) {
            // Usar icono por defecto si no se encuentra
        }
        
        pack();
    }
    
    private void setupTimelineAndVolumeListeners() {
        // Listener para el slider de línea de tiempo con detección de interacción
        timelineSlider.addChangeListener(e -> {
            if (timelineSlider.getValueIsAdjusting()) {
                userInteracting = true; // Usuario está arrastrando
            } else if (userInteracting && timelineSlider.isEnabled()) {
                // Usuario terminó de arrastrar, aplicar cambio
                userInteracting = false;
                int newPosition = timelineSlider.getValue();
                seekToPosition(newPosition);
            }
        });
        
        // Detectar cuando el usuario empieza a interactuar
        timelineSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                userInteracting = true;
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Dar un pequeño delay antes de reanudar actualizaciones automáticas
                javax.swing.Timer delayTimer = new javax.swing.Timer(100, event -> {
                    userInteracting = false;
                    ((javax.swing.Timer) event.getSource()).stop();
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });
        
        // Listener para el slider de volumen
        volumeSlider.addChangeListener(e -> {
            currentVolume = volumeSlider.getValue();
            volumeLabel.setText("Vol: " + currentVolume + "%");
            NodoLista.setVolume(currentVolume / 100.0f); // Convertir a 0.0-1.0
        });
    }
    
    private void updateTimeline() {
        // Solo actualizar si realmente está reproduciéndose y no hay interacción del usuario
        if (NodoLista.estaReproduciendo() && !userInteracting && !timelineSlider.getValueIsAdjusting()) {
            try {
                // Obtener tiempo actual y total de la canción
                long currentTimeMs = NodoLista.getCurrentTime();
                long totalTimeMs = NodoLista.getTotalTime();
                
                if (totalTimeMs > 0) {
                    int newCurrentTime = (int) (currentTimeMs / 1000);
                    int newTotalTime = (int) (totalTimeMs / 1000);
                    
                    // Solo actualizar si el tiempo ha cambiado significativamente
                    if (Math.abs(newCurrentTime - currentTime) >= 1) {
                        currentTime = newCurrentTime;
                        totalTime = newTotalTime;
                        
                        // Actualizar slider de forma thread-safe
                        SwingUtilities.invokeLater(() -> {
                            if (!userInteracting && !timelineSlider.getValueIsAdjusting()) {
                                int percentage = (int) ((currentTimeMs * 100) / totalTimeMs);
                                timelineSlider.setValue(percentage);
                            }
                        });
                        
                        // Actualizar labels de tiempo
                        SwingUtilities.invokeLater(() -> {
                            currentTimeLabel.setText(formatTime(currentTime));
                            totalTimeLabel.setText(formatTime(totalTime));
                        });
                    }
                }
            } catch (Exception e) {
                // Evitar que errores en la actualización afecten la reproducción
                System.err.println("Error actualizando timeline: " + e.getMessage());
            }
        }
    }
    
    private void seekToPosition(int percentage) {
        if (totalTime > 0) {
            long newTimeMs = (long) ((percentage / 100.0) * totalTime * 1000);
            NodoLista.seekTo(newTimeMs);
        }
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
    
    // =============== MÉTODOS DE CONTROL DE REPRODUCCIÓN ===============
    
    /**
     * Método unificado que alterna entre reproducir y pausar
     */
    private void togglePlayPause() {
        if (isPlaying) {
            pausePlayback();
        } else {
            if (currentTrack >= 0) {
                resumePlayback();
            } else {
                playSelectedTrack();
            }
        }
    }
    
    private void playSelectedTrack() {
        if (miLista == null || miLista.isEmpty()) {
            showMessage("No hay pistas en la lista de reproducción", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedIndex = playlistJList.getSelectedIndex();
        if (selectedIndex == -1) {
            selectedIndex = 0; // Reproducir la primera si no hay selección
            playlistJList.setSelectedIndex(selectedIndex);
        }
        
        currentTrack = selectedIndex;
        
        try {
            // Interrumpir detector anterior si existe antes de iniciar nueva reproducción
            if (trackEndDetectorThread != null && trackEndDetectorThread.isAlive()) {
                trackEndDetectorThread.interrupt();
            }
            
            // Detener cualquier audio anterior
            NodoLista.detenerAudio();
            
            // Pequeña pausa para asegurar que el audio anterior se detuvo
            Thread.sleep(100);
            
            // Usar la nueva funcionalidad de audio
            URL selectedTrack = miLista.get(currentTrack);
            boolean success = NodoLista.reproducirAudio(selectedTrack);
            
            if (success) {
                isPlaying = true;
                updatePlayPauseButton();
                updateStatus("Reproduciendo...");
                updateTrackInfo();
                stopButton.setEnabled(true);
                
                // Habilitar línea de tiempo y iniciar timer
                timelineSlider.setEnabled(true);
                if (timelineTimer != null) {
                    timelineTimer.start();
                }
                
                // Crear un hilo para detectar cuando termine la canción
                createTrackEndDetector();
            } else {
                showMessage("Error al reproducir la pista", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessage("Error al reproducir la pista: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crea un detector para saber cuándo termina la canción actual
     */
    private void createTrackEndDetector() {
        // Interrumpir detector anterior si existe
        if (trackEndDetectorThread != null && trackEndDetectorThread.isAlive()) {
            trackEndDetectorThread.interrupt();
        }
        
        trackEndDetectorThread = new Thread(() -> {
            try {
                // Esperar mientras la canción esté reproduciéndose
                while (isPlaying && !Thread.currentThread().isInterrupted()) {
                    boolean audioPlaying = NodoLista.estaReproduciendo();
                    
                    // Solo verificar si realmente terminó si hemos estado reproduciéndose
                    if (!audioPlaying) {
                        // Verificar múltiples veces para asegurar que realmente terminó
                        Thread.sleep(1000);
                        
                        // Doble verificación
                        if (!NodoLista.estaReproduciendo() && isPlaying && !Thread.currentThread().isInterrupted()) {
                            // Verificar que no sea una pausa temporal
                            long currentTime = NodoLista.getCurrentTime();
                            long totalTime = NodoLista.getTotalTime();
                            
                            // Solo considerar como "terminado" si estamos cerca del final
                            if (totalTime > 0 && currentTime >= (totalTime - 2000)) { // 2 segundos de margen
                                SwingUtilities.invokeLater(() -> handleTrackEnd());
                                break;
                            }
                        }
                    }
                    
                    Thread.sleep(1000); // Verificar cada segundo
                }
            } catch (InterruptedException e) {
                // Thread interrumpido, salir silenciosamente
            }
        });
        trackEndDetectorThread.start();
    }
    
    /**
     * Maneja lo que sucede cuando una canción termina de reproducirse
     */
    private void handleTrackEnd() {
        // Verificar una vez más que realmente terminó
        if (!NodoLista.estaReproduciendo() && isPlaying) {
            if (repeatMode == 2) { // Repetir canción
                // Pequeña pausa antes de repetir para evitar problemas
                javax.swing.Timer delayTimer = new javax.swing.Timer(100, e -> {
                    playSelectedTrack(); // Reproducir la misma canción otra vez
                    ((javax.swing.Timer) e.getSource()).stop();
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            } else if (repeatMode == 1) { // Repetir lista
                // Para repetir lista, siempre avanzar (o volver al inicio si es la última)
                if (miLista != null && !miLista.isEmpty()) {
                    javax.swing.Timer delayTimer = new javax.swing.Timer(100, e -> {
                        if (miLista.size() == 1) {
                            // Si solo hay una canción, repetirla directamente
                            playSelectedTrack();
                        } else {
                            // Si hay múltiples canciones, avanzar normalmente
                            playNextTrack();
                        }
                        ((javax.swing.Timer) e.getSource()).stop();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }
            } else {
                // Modo normal: avanzar a la siguiente canción (si existe)
                javax.swing.Timer delayTimer = new javax.swing.Timer(100, e -> {
                    playNextTrack();
                    ((javax.swing.Timer) e.getSource()).stop();
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        }
    }
    
    private void pausePlayback() {
        if (NodoLista.estaReproduciendo()) {
            NodoLista.pausarAudio();
            isPlaying = false;
            updatePlayPauseButton();
            updateStatus("Pausado");
            
            // Interrumpir detector de fin de canción
            if (trackEndDetectorThread != null && trackEndDetectorThread.isAlive()) {
                trackEndDetectorThread.interrupt();
            }
            
            // Pausar timer de línea de tiempo
            if (timelineTimer != null) {
                timelineTimer.stop();
            }
        }
    }
    
    /**
     * Reanuda la reproducción pausada
     */
    private void resumePlayback() {
        if (currentTrack >= 0 && miLista != null && currentTrack < miLista.size()) {
            NodoLista.reanudarAudio();
            isPlaying = true;
            updatePlayPauseButton();
            updateStatus("Reproduciendo...");
            
            // Crear un nuevo detector para cuando termine la canción
            createTrackEndDetector();
            
            // Reanudar timer de línea de tiempo
            if (timelineTimer != null) {
                timelineTimer.start();
            }
        }
    }
    
    /**
     * Actualiza el texto y apariencia del botón Play/Pause según el estado
     */
    private void updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setText("Pausar");
            playPauseButton.setToolTipText("Pausar reproducción");
            playPauseButton.setBackground(new Color(255, 193, 7)); // Amarillo para pausar
        } else {
            playPauseButton.setText("Reproducir");
            playPauseButton.setToolTipText("Reproducir pista seleccionada");
            playPauseButton.setBackground(ACCENT_COLOR); // Verde para reproducir
        }
    }
    
    private void stopPlayback() {
        NodoLista.detenerAudio();
        currentTrack = -1;
        isPlaying = false;
        updatePlayPauseButton();
        updateStatus("Detenido");
        stopButton.setEnabled(false);
        playlistJList.clearSelection();
        updateTrackInfo();
        
        // Interrumpir detector de fin de canción
        if (trackEndDetectorThread != null && trackEndDetectorThread.isAlive()) {
            trackEndDetectorThread.interrupt();
        }
        
        // Detener timer y resetear línea de tiempo
        if (timelineTimer != null) {
            timelineTimer.stop();
        }
        timelineSlider.setValue(0);
        timelineSlider.setEnabled(false);
        currentTimeLabel.setText("0:00");
        totalTimeLabel.setText("0:00");
    }
    
    private void playPreviousTrack() {
        if (miLista == null || miLista.isEmpty()) return;
        
        if (currentTrack > 0) {
            currentTrack--;
        } else {
            currentTrack = miLista.size() - 1; // Ir al último
        }
        
        playlistJList.setSelectedIndex(currentTrack);
        playSelectedTrack();
    }
    
    private void playNextTrack() {
        if (miLista == null || miLista.isEmpty()) return;
        
        // Si está en modo repetir canción, cambiar a repetir lista al presionar siguiente
        if (repeatMode == 2) {
            repeatMode = 1; // Cambiar a repetir lista
            updateRepeatButton();
            updateStatus("Modo: Repetir lista (cambio automático)");
        }
        
        if (shuffleMode) {
            playNextShuffleTrack();
        } else {
            if (currentTrack < miLista.size() - 1) {
                currentTrack++;
            } else {
                // Al llegar al final de la lista
                if (repeatMode == 1) { // Repetir lista
                    currentTrack = 0;
                } else if (repeatMode == 0) { // Sin repetir - volver al inicio pero continuar
                    currentTrack = 0;
                } else {
                    return; // No hacer nada en otros casos
                }
            }
            
            playlistJList.setSelectedIndex(currentTrack);
            playSelectedTrack();
        }
    }
    
    /**
     * Alterna entre los modos de repetición: Sin repetir -> Repetir lista -> Repetir canción
     */
    private void toggleRepeatMode() {
        repeatMode = (repeatMode + 1) % 3;
        updateRepeatButton();
        
        String mode = "";
        switch (repeatMode) {
            case 0: mode = "Sin repetición"; break;
            case 1: mode = "Repetir lista"; break;
            case 2: mode = "Repetir canción"; break;
        }
        updateStatus("Modo: " + mode);
    }
    
    /**
     * Alterna el modo de reproducción aleatoria
     */
    private void toggleShuffleMode() {
        shuffleMode = !shuffleMode;
        updateShuffleButton();
        
        if (shuffleMode) {
            createShuffleOrder();
            updateStatus("🔀 Modo aleatorio inteligente activado (sin repeticiones)");
        } else {
            // Limpiar el sistema de aleatorio
            playedTracks.clear();
            availableTracks.clear();
            updateStatus("Modo aleatorio desactivado");
        }
    }
    
    /**
     * Actualiza la apariencia del botón de repetición según el modo actual
     */
    private void updateRepeatButton() {
        switch (repeatMode) {
            case 0: // Sin repetir
                repeatButton.setText("REP");
                repeatButton.setBackground(new Color(108, 117, 125));
                repeatButton.setToolTipText("Sin repetición - Click para repetir lista");
                break;
            case 1: // Repetir lista
                repeatButton.setText("REP");
                repeatButton.setBackground(new Color(255, 152, 0));
                repeatButton.setToolTipText("Repetir lista - Click para repetir canción");
                break;
            case 2: // Repetir canción
                repeatButton.setText("REP1");
                repeatButton.setBackground(new Color(255, 152, 0));
                repeatButton.setToolTipText("Repetir canción - Click para desactivar");
                break;
        }
    }
    
    /**
     * Actualiza la apariencia del botón shuffle según el modo actual
     */
    private void updateShuffleButton() {
        if (shuffleMode) {
            shuffleButton.setBackground(new Color(123, 104, 238));
            shuffleButton.setToolTipText("Modo aleatorio activado - Click para desactivar");
        } else {
            shuffleButton.setBackground(new Color(108, 117, 125));
            shuffleButton.setToolTipText("Modo aleatorio desactivado - Click para activar");
        }
    }
    
    /**
     * Inicializa el sistema de aleatorio sin repeticiones hasta completar un ciclo
     */
    private void createShuffleOrder() {
        if (miLista == null || miLista.isEmpty()) return;
        
        // Reiniciar el sistema de aleatorio inteligente
        playedTracks.clear();
        availableTracks.clear();
        
        // Agregar todas las canciones como disponibles
        for (int i = 0; i < miLista.size(); i++) {
            availableTracks.add(i);
        }
        
        // Mezclar la lista de canciones disponibles
        Collections.shuffle(availableTracks);
    }
    
    /**
     * Reproduce la siguiente pista en modo aleatorio sin repeticiones hasta completar ciclo
     */
    private void playNextShuffleTrack() {
        if (miLista == null || miLista.isEmpty()) return;
        
        // Inicializar si es necesario
        if (availableTracks.isEmpty() && playedTracks.isEmpty()) {
            createShuffleOrder();
            if (availableTracks.isEmpty()) return;
        }
        
        // Marcar la canción actual como reproducida
        if (currentTrack >= 0 && currentTrack < miLista.size()) {
            playedTracks.add(currentTrack);
            availableTracks.remove(Integer.valueOf(currentTrack));
        }
        
        // Si ya no quedan canciones disponibles, reiniciar el ciclo
        if (availableTracks.isEmpty()) {
            if (repeatMode == 1) { // Repetir lista
                // Reiniciar ciclo completo
                playedTracks.clear();
                for (int i = 0; i < miLista.size(); i++) {
                    availableTracks.add(i);
                }
                Collections.shuffle(availableTracks);
                updateStatus("🔀 Nuevo ciclo aleatorio iniciado");
            } else if (repeatMode == 0) { // Sin repetir - nuevo ciclo automático
                // Crear nuevo ciclo automáticamente
                playedTracks.clear();
                for (int i = 0; i < miLista.size(); i++) {
                    availableTracks.add(i);
                }
                Collections.shuffle(availableTracks);
                updateStatus("🔀 Ciclo aleatorio completado - Nuevo ciclo iniciado");
            } else {
                return; // No hacer nada en otros casos
            }
        }
        
        // Seleccionar la siguiente canción aleatoria disponible
        if (!availableTracks.isEmpty()) {
            currentTrack = availableTracks.get(0);
            playlistJList.setSelectedIndex(currentTrack);
            playSelectedTrack();
            
            // Mostrar progreso del ciclo aleatorio
            int remaining = availableTracks.size() - 1; // -1 porque acabamos de tomar una
            int total = miLista.size();
            updateStatus(String.format("🎵 Aleatorio: %d de %d reproducidas", total - remaining, total));
        }
    }
    
    // =============== MÉTODOS DE GESTIÓN DE PLAYLIST ===============
    
    /**
     * Método simplificado para agregar una pista a la lista
     */
    private void addTrack() {
        URL trackUrl = selectAudioFile();
        if (trackUrl != null) {
            miLista.addLast(trackUrl); // Agregar al final por defecto
            updatePlaylistDisplay();
            updateStatus("Pista agregada a la lista");
        }
    }
    
    private void removeSelectedTrack() {
        if (miLista == null || miLista.isEmpty()) {
            showMessage("No hay pistas para eliminar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int selectedIndex = playlistJList.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Seleccione una pista para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar la pista seleccionada?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (respuesta == JOptionPane.YES_OPTION) {
            miLista.remove(selectedIndex);
            
            // Ajustar currentTrack si es necesario
            if (currentTrack == selectedIndex) {
                stopPlayback();
            } else if (currentTrack > selectedIndex) {
                currentTrack--;
            }
            
            updatePlaylistDisplay();
            updateStatus("Pista eliminada");
        }
    }
    
    private void clearPlaylist() {
        if (miLista == null || miLista.isEmpty()) {
            showMessage("La lista ya está vacía", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar todas las pistas de la lista?",
            "Confirmar limpieza",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (respuesta == JOptionPane.YES_OPTION) {
            stopPlayback();
            miLista.clear();
            playlistModel.clear();
            updateStatus("Lista limpiada");
        }
    }
    
    // =============== MÉTODOS AUXILIARES ===============
    
    private URL selectAudioFile() {
        File selectedFile = NodoLista.mostrarExploradorWindows();
        if (selectedFile != null) {
            try {
                String formattedPath = NodoLista.cambiarRutaFormatoJMF(selectedFile.getAbsolutePath());
                return new URL(formattedPath);
            } catch (Exception e) {
                showMessage("Error al cargar el archivo: " + e.getMessage(), 
                           "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    private void updatePlaylistDisplay() {
        playlistModel.clear();
        if (miLista != null) {
            for (int i = 0; i < miLista.size(); i++) {
                String fileName = getFileNameFromURL(miLista.get(i));
                playlistModel.addElement((i + 1) + ". " + fileName);
            }
            
            // Regenerar orden shuffle si está activado
            if (shuffleMode) {
                createShuffleOrder();
            }
        }
        updateTrackInfo();
    }
    
    private String getFileNameFromURL(URL url) {
        String path = url.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
    
    private void updateTrackInfo() {
        if (miLista == null || miLista.isEmpty()) {
            trackInfoLabel.setText("No hay pistas en la lista");
        } else if (currentTrack >= 0 && currentTrack < miLista.size()) {
            String fileName = getFileNameFromURL(miLista.get(currentTrack));
            trackInfoLabel.setText("Reproduciendo: " + fileName + " (" + (currentTrack + 1) + "/" + miLista.size() + ")");
        } else {
            trackInfoLabel.setText("Total: " + miLista.size() + " pistas");
        }
    }
    
    private void updateStatus(String status) {
        statusLabel.setText(status);
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    // =============== MÉTODOS DE REORDENAMIENTO ===============
    
    /**
     * Mueve la pista seleccionada hacia arriba en la lista
     */
    private void moveTrackUp() {
        int selectedIndex = playlistJList.getSelectedIndex();
        if (selectedIndex <= 0 || miLista.isEmpty()) {
            return; // No se puede mover más arriba o no hay selección
        }
        
        // Intercambiar elementos en la lista
        URL track = miLista.remove(selectedIndex);
        miLista.add(selectedIndex - 1, track);
        
        // Actualizar currentTrack si es necesario
        if (currentTrack == selectedIndex) {
            currentTrack = selectedIndex - 1;
        } else if (currentTrack == selectedIndex - 1) {
            currentTrack = selectedIndex;
        }
        
        // Actualizar la interfaz
        updatePlaylistDisplay();
        playlistJList.setSelectedIndex(selectedIndex - 1);
        updateStatus("Pista movida hacia arriba");
    }
    
    /**
     * Mueve la pista seleccionada hacia abajo en la lista
     */
    private void moveTrackDown() {
        int selectedIndex = playlistJList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= miLista.size() - 1) {
            return; // No se puede mover más abajo o no hay selección
        }
        
        // Intercambiar elementos en la lista
        URL track = miLista.remove(selectedIndex);
        miLista.add(selectedIndex + 1, track);
        
        // Actualizar currentTrack si es necesario
        if (currentTrack == selectedIndex) {
            currentTrack = selectedIndex + 1;
        } else if (currentTrack == selectedIndex + 1) {
            currentTrack = selectedIndex;
        }
        
        // Actualizar la interfaz
        updatePlaylistDisplay();
        playlistJList.setSelectedIndex(selectedIndex + 1);
        updateStatus("Pista movida hacia abajo");
    }
    
    /**
     * Mueve una pista de una posición a otra (usado por drag & drop)
     */
    private void moveTrack(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= miLista.size() || 
            toIndex < 0 || toIndex >= miLista.size() || 
            fromIndex == toIndex) {
            return;
        }
        
        // Remover y agregar en la nueva posición
        URL track = miLista.remove(fromIndex);
        miLista.add(toIndex, track);
        
        // Actualizar currentTrack
        if (currentTrack == fromIndex) {
            currentTrack = toIndex;
        } else if (fromIndex < currentTrack && toIndex >= currentTrack) {
            currentTrack--;
        } else if (fromIndex > currentTrack && toIndex <= currentTrack) {
            currentTrack++;
        }
        
        // Actualizar la interfaz
        updatePlaylistDisplay();
        playlistJList.setSelectedIndex(toIndex);
    }

    /**
     * Método principal para ejecutar la aplicación
     */
    public static void main(String args[]) {
        // Configurar Look and Feel moderno
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Usar el look and feel por defecto si falla
        }

        // Crear y mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
    
    // =============== CLASE INTERNA PARA DRAG & DROP ===============
    
    /**
     * TransferHandler personalizado para permitir drag & drop en la playlist
     */
    private class PlaylistTransferHandler extends TransferHandler {
        private int[] indices = null;
        private int addIndex = -1;
        
        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            // Solo permitir operaciones de mover dentro de la misma lista
            return support.getComponent() == playlistJList && 
                   support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) c;
            indices = list.getSelectedIndices();
            String data = list.getSelectedValue();
            return new StringTransferable(data);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }
        
        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            int index = dl.getIndex();
            
            // Mover el elemento en la lista original
            if (indices != null && indices.length == 1) {
                int fromIndex = indices[0];
                int toIndex = index;
                
                // Ajustar índice si se está moviendo hacia abajo
                if (fromIndex < toIndex) {
                    toIndex--;
                }
                
                moveTrack(fromIndex, toIndex);
                addIndex = toIndex;
                return true;
            }
            
            return false;
        }
        
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == TransferHandler.MOVE && indices != null) {
                // Seleccionar el elemento movido
                if (addIndex >= 0) {
                    playlistJList.setSelectedIndex(addIndex);
                }
            }
            indices = null;
            addIndex = -1;
        }
        
        // Clase auxiliar para transferir strings
        private class StringTransferable implements Transferable {
            private String data;
            
            public StringTransferable(String data) {
                this.data = data;
            }
            
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { DataFlavor.stringFlavor };
            }
            
            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.stringFlavor.equals(flavor);
            }
            
            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (isDataFlavorSupported(flavor)) {
                    return data;
                }
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }
}