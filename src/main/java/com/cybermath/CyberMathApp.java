package com.cybermath;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CyberMathApp extends Application {

    private Stage ventana;
    private Scene escenaPrincipal; // <--- MANTENDREMOS UNA SOLA ESCENA
    private LogicaJuego logica = new LogicaJuego();
    private Usuario jugador;
    // private GestorSonido audio = new GestorSonido();

    // UI Global
    private Label lblHeader = new Label();
    private Label lblReto = new Label();
    private Label lblTimer = new Label();
    private ProgressBar barraTiempo = new ProgressBar(1.0);
    private TextArea txtLog = new TextArea();
    private TextField txtRespuesta = new TextField();
    private Button btnHack = new Button("EJECUTAR INYECCIÓN [ENTER]");

    private Timeline timerAnimacion;
    private int tiempoRestante;
    private final int TIEMPO_MAX = 30;

    @Override
    public void start(Stage stage) {
        this.ventana = stage;

        // Configuración inicial de la ventana
        VBox rootInicial = new VBox();
        rootInicial.setStyle("-fx-background-color: black;"); // Fondo de seguridad

        // CREAMOS LA ESCENA UNA SOLA VEZ Y CARGAMOS EL CSS
        this.escenaPrincipal = new Scene(rootInicial, 900, 750);
        cargarCSS();

        ventana.setScene(escenaPrincipal);
        ventana.setTitle("CYBERMATH PROTOCOL v5.0 (STABLE)");
        ventana.show();

        mostrarIntroBoot();
    }

    private void cargarCSS() {
        try {
            escenaPrincipal.getStylesheets().clear();
            escenaPrincipal.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        } catch (Exception e) {
            System.out.println(">> ERROR CRÍTICO: CSS NO ENCONTRADO.");
        }
    }

    // --- FASE 1: BOOT ---
    private void mostrarIntroBoot() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); // Forzamos el estilo oscuro

        Label lblBoot = new Label("INICIALIZANDO KERNEL...");
        lblBoot.setStyle("-fx-text-fill: #00ff41; -fx-font-family: 'Consolas'; -fx-font-size: 18px;");

        root.getChildren().add(lblBoot);
        escenaPrincipal.setRoot(root); // CAMBIAMOS EL CONTENIDO, NO LA ESCENA

        new Thread(() -> {
            try {
                String[] bootLines = {
                        "> CHECKING RAM... OK", "> LOADING DRIVERS... OK",
                        "> BYPASSING SECURITY...", "> SYSTEM READY."
                };
                for (String line : bootLines) {
                    Thread.sleep(600);
                    Platform.runLater(() -> lblBoot.setText(lblBoot.getText() + "\n" + line));
                }
                Thread.sleep(800);
                Platform.runLater(this::mostrarNarrativa);
            } catch (Exception e) {}
        }).start();
    }

    // --- FASE 2: HISTORIA ---
    private void mostrarNarrativa() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        VBox panelTexto = new VBox();
        panelTexto.getStyleClass().add("cyber-panel");
        panelTexto.setMaxWidth(700);

        TextArea txtHistoria = new TextArea();
        txtHistoria.setWrapText(true);
        txtHistoria.setEditable(false);
        txtHistoria.setPrefHeight(250);
        txtHistoria.setStyle("-fx-control-inner-background: transparent; -fx-background-color: transparent; -fx-text-fill: #00ff41; -fx-font-size: 16px;");

        panelTexto.getChildren().add(txtHistoria);

        Button btnSkip = new Button(">>> INICIAR SISTEMA");
        btnSkip.getStyleClass().add("button-hack");
        btnSkip.setOnAction(e -> mostrarSelectorSlots());

        root.getChildren().addAll(panelTexto, btnSkip);
        escenaPrincipal.setRoot(root); // Mantiene el CSS

        // Escribir texto
        new Thread(() -> {
            String historia = "AÑO 2088. LA COLMENA HA TOMADO EL CONTROL.\n\nTU MISIÓN: ROMPER EL FIREWALL USANDO MATEMÁTICAS.\nSI TU INTEGRIDAD LLEGA A 0%, SERÁS ELIMINADO.\n\nBUENA SUERTE.";
            for (char c : historia.toCharArray()) {
                try { Thread.sleep(30); } catch(Exception ex){}
                Platform.runLater(() -> txtHistoria.appendText(String.valueOf(c)));
            }
        }).start();
    }

    // --- FASE 3: SLOTS ---
    private void mostrarSelectorSlots() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titulo = new Label("SELECCIONE NODO DE MEMORIA");
        titulo.setId("lblReto");

        HBox panelSlots = new HBox(20);
        panelSlots.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 3; i++) {
            Button btn = new Button("SLOT " + i + "\n[VACÍO]");
            btn.getStyleClass().add("button-hack");
            btn.setPrefSize(180, 120);
            int slot = i;
            btn.setOnAction(e -> iniciarJuego("OPERADOR_0" + slot));
            panelSlots.getChildren().add(btn);
        }

        root.getChildren().addAll(titulo, panelSlots);
        escenaPrincipal.setRoot(root); // Mantiene el CSS
    }

    // --- FASE 4: JUEGO ---
    private void iniciarJuego(String nombre) {
        jugador = new Usuario(nombre);

        VBox root = new VBox(20);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        // 1. HEADER
        HBox header = new HBox(20);
        header.getStyleClass().add("cyber-panel");
        header.setAlignment(Pos.CENTER);

        lblHeader.setStyle("-fx-font-size: 14px;");
        lblTimer.setStyle("-fx-text-fill: #ff3333; -fx-font-weight: bold; -fx-font-size: 16px;");
        barraTiempo.setPrefWidth(300);
        barraTiempo.setStyle("-fx-accent: #ff3333;");

        header.getChildren().addAll(lblHeader, new Label("| TIEMPO:"), barraTiempo, lblTimer);
        actualizarHeader();

        // 2. TERMINAL
        VBox terminal = new VBox(10);
        terminal.getStyleClass().add("cyber-panel");
        terminal.setAlignment(Pos.CENTER);

        Label lblTitle = new Label(">>> TERMINAL_OUTPUT");
        lblTitle.getStyleClass().add("panel-title");

        lblReto.setId("lblReto");
        FadeTransition blink = new FadeTransition(Duration.seconds(0.8), lblReto);
        blink.setFromValue(1.0); blink.setToValue(0.4);
        blink.setCycleCount(FadeTransition.INDEFINITE); blink.setAutoReverse(true);
        blink.play();

        txtLog.setEditable(false);
        txtLog.setPrefHeight(150);
        txtLog.setText("[SYSTEM]: CONEXIÓN ESTABLECIDA.\n");

        terminal.getChildren().addAll(lblTitle, lblReto, txtLog);

        // 3. INPUT
        VBox inputArea = new VBox(15);
        inputArea.getStyleClass().add("cyber-panel");
        inputArea.setAlignment(Pos.CENTER);

        txtRespuesta.setPromptText("INGRESE CÓDIGO...");
        txtRespuesta.setMaxWidth(400);

        btnHack.getStyleClass().add("button-hack");
        btnHack.setPrefWidth(400);
        btnHack.setDefaultButton(true);
        btnHack.setOnAction(e -> verificarRespuesta());

        inputArea.getChildren().addAll(txtRespuesta, btnHack);

        root.getChildren().addAll(header, terminal, inputArea);
        escenaPrincipal.setRoot(root); // Mantiene el CSS

        nuevoReto();
    }

    private void nuevoReto() {
        if (jugador.getIntegridad() <= 0) return; // Si está muerto, no hay nuevo reto
        lblReto.setText(logica.generarReto());
        txtRespuesta.clear();
        txtRespuesta.setDisable(false);
        txtRespuesta.requestFocus();
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (timerAnimacion != null) timerAnimacion.stop();
        tiempoRestante = TIEMPO_MAX;
        barraTiempo.setProgress(1.0);

        timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            lblTimer.setText(tiempoRestante + "s");
            barraTiempo.setProgress((double)tiempoRestante / TIEMPO_MAX);

            if (tiempoRestante <= 0) {
                procesarFallo("TIMEOUT");
            }
        }));
        timerAnimacion.setCycleCount(Timeline.INDEFINITE);
        timerAnimacion.play();
    }

    private void verificarRespuesta() {
        if (txtRespuesta.getText().trim().isEmpty()) return;

        try {
            double res = Double.parseDouble(txtRespuesta.getText());
            if (logica.verificar(res)) {
                jugador.sumarCriptos(50);
                txtLog.appendText("\n> [SUCCESS] CÓDIGO VÁLIDO.");
                // audio.playSuccess();
                actualizarHeader();
                nuevoReto();
            } else {
                procesarFallo("HASH INCORRECTO");
            }
        } catch (Exception e) {
            txtLog.appendText("\n> [!] ERROR SINTAXIS.");
        }
    }

    private void procesarFallo(String motivo) {
        if (jugador.getIntegridad() <= 0) return; // Si ya murió, ignorar

        jugador.recibirDaño();
        // audio.playError();
        txtLog.appendText("\n> [DANGER] " + motivo + ". DAÑO CRÍTICO.");

        // --- AQUÍ ARREGLAMOS LA VIDA NEGATIVA ---
        if (jugador.getIntegridad() < 0) {
            // Esto requiere un setter en Usuario.java o controlar la resta ahí
            // Por ahora asumimos que Usuario maneja ints simples
        }

        actualizarHeader();

        if (jugador.getIntegridad() <= 0) {
            // GAME OVER REAL
            timerAnimacion.stop(); // DETENER TIEMPO
            lblReto.setText("SYSTEM FAILURE");
            lblReto.setStyle("-fx-text-fill: red;");
            txtRespuesta.setDisable(true);
            btnHack.setDisable(true);
            txtLog.appendText("\n\n> SEÑAL PERDIDA. OPERADOR ELIMINADO.");
        } else {
            // Si sigue vivo, reinicia tiempo
            iniciarTemporizador();
        }
    }

    private void actualizarHeader() {
        // Evitamos mostrar negativos en la interfaz
        int hp = Math.max(0, jugador.getIntegridad());
        lblHeader.setText("OP: " + jugador.getNombre() + " | HP: " + hp + "% | BTC: " + jugador.getCriptos());
    }

    public static void main(String[] args) { launch(args); }
}