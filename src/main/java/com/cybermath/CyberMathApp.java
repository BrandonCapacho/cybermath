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
import javafx.scene.paint.Color; // <--- FALTABA ESTO
import javafx.scene.shape.Line;  // <--- FALTABA ESTO
import javafx.stage.Stage;
import javafx.util.Duration;

public class CyberMathApp extends Application {

    private Stage ventana;
    private Scene escenaPrincipal;
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

    // Variable global para controlar el fondo rojo
    private VBox rootJuego;

    private Timeline timerAnimacion;
    private int tiempoRestante;
    private final int TIEMPO_MAX = 30;

    @Override
    public void start(Stage stage) {
        this.ventana = stage;

        VBox rootInicial = new VBox();
        rootInicial.setStyle("-fx-background-color: black;");

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

    private void mostrarIntroBoot() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label lblBoot = new Label("INICIALIZANDO KERNEL...");
        lblBoot.setStyle("-fx-text-fill: #00ff41; -fx-font-family: 'Consolas'; -fx-font-size: 18px;");

        root.getChildren().add(lblBoot);
        escenaPrincipal.setRoot(root);

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
        escenaPrincipal.setRoot(root);

        new Thread(() -> {
            String historia = "AÑO 2088. LA COLMENA HA TOMADO EL CONTROL.\n\nTU MISIÓN: ROMPER EL FIREWALL USANDO MATEMÁTICAS.\nSI TU INTEGRIDAD LLEGA A 0%, SERÁS ELIMINADO.\n\nBUENA SUERTE.";
            for (char c : historia.toCharArray()) {
                try { Thread.sleep(30); } catch(Exception ex){}
                Platform.runLater(() -> txtHistoria.appendText(String.valueOf(c)));
            }
        }).start();
    }

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
            btn.setOnAction(e -> {
                jugador = new Usuario("OPERADOR_0" + slot);
                mostrarMapaNiveles(); // <--- AHORA VA AL ARBOL DE NIVELES
            });
            panelSlots.getChildren().add(btn);
        }

        root.getChildren().addAll(titulo, panelSlots);
        escenaPrincipal.setRoot(root);
    }

    // --- NUEVO: MAPA DE ÁRBOL CON CONEXIONES ---
    private void mostrarMapaNiveles() {
        AnchorPane mapaArbol = new AnchorPane();
        mapaArbol.getStyleClass().add("root");
        mapaArbol.setPrefSize(1200, 800); // Tamaño grande para scroll

        Label titulo = new Label("DIRECTORIO_RAÍZ: /SISTEMA/NODOS");
        titulo.setLayoutX(50); titulo.setLayoutY(30);
        titulo.setId("lblReto");
        mapaArbol.getChildren().add(titulo);

        // Generar 5 ramas con 10 niveles cada una
        for (int rama = 0; rama < 5; rama++) {
            for (int nivelEnRama = 1; nivelEnRama <= 10; nivelEnRama++) {
                int nivelId = (rama * 10) + nivelEnRama;

                Button btnNodo = new Button("N_" + nivelId);
                btnNodo.setPrefSize(60, 40);

                // Coordenadas para dibujar el árbol
                double x = 100 + (rama * 180);
                double y = 80 + (nivelEnRama * 60);

                btnNodo.setLayoutX(x);
                btnNodo.setLayoutY(y);

                configurarEstiloNodo(btnNodo, nivelId);
                mapaArbol.getChildren().add(btnNodo);

                // Dibujar línea "cable" al nodo anterior
                if (nivelEnRama > 1) {
                    Line conector = new Line(x + 30, y, x + 30, y - 20); // Conecta arriba
                    conector.setStroke(Color.web("#00ff41"));
                    conector.setOpacity(0.4);
                    conector.setStrokeWidth(2);
                    mapaArbol.getChildren().add(0, conector); // Agregar al fondo
                }
            }
        }

        Button btnVolver = new Button("LOGOUT");
        btnVolver.getStyleClass().add("button-hack");
        btnVolver.setLayoutX(50); btnVolver.setLayoutY(80);
        btnVolver.setOnAction(e -> mostrarSelectorSlots());
        mapaArbol.getChildren().add(btnVolver);

        ScrollPane scroll = new ScrollPane(mapaArbol);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: black; -fx-border-color: #00ff41;");

        escenaPrincipal.setRoot(scroll);
    }

    private void configurarEstiloNodo(Button btn, int id) {
        if (id <= jugador.getNivelMaximo()) {
            btn.getStyleClass().add("button-hack");
            btn.setOnAction(e -> mostrarBriefingMision(id));
        } else {
            btn.setStyle("-fx-opacity: 0.2; -fx-background-color: #111; -fx-border-color: gray; -fx-text-fill: gray;");
            btn.setDisable(true);
        }
    }

    // --- NUEVO: PANTALLA DE BRIEFING ---
    private void mostrarBriefingMision(int nivelId) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        String[] info = logica.getInfoMision(nivelId);

        Label lblTitulo = new Label(">>> " + info[0]);
        lblTitulo.setId("lblReto");

        TextArea txtDetalles = new TextArea();
        // Estilo manual para asegurar visibilidad
        txtDetalles.setStyle("-fx-control-inner-background: #050505; -fx-text-fill: #00ff41; -fx-font-family: 'Consolas'; -fx-font-size: 14px;");
        txtDetalles.setPrefHeight(150);
        txtDetalles.setMaxWidth(600);
        txtDetalles.setWrapText(true);
        txtDetalles.setEditable(false);

        Button btnConfirmar = new Button("ACEPTAR MISIÓN");
        btnConfirmar.getStyleClass().add("button-hack");
        btnConfirmar.setOnAction(e -> iniciarJuego(nivelId));

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.getStyleClass().add("button-hack");
        btnCancelar.setOnAction(e -> mostrarMapaNiveles());

        root.getChildren().addAll(lblTitulo, txtDetalles, btnConfirmar, btnCancelar);
        escenaPrincipal.setRoot(root);

        // Efecto escritura
        new Thread(() -> {
            txtDetalles.clear();
            for (char c : info[1].toCharArray()) {
                try { Thread.sleep(20); } catch(Exception ex){}
                Platform.runLater(() -> txtDetalles.appendText(String.valueOf(c)));
            }
        }).start();
    }

    private void iniciarJuego(int nivel) {
        // Inicializamos la variable global rootJuego
        this.rootJuego = new VBox(20);
        rootJuego.getStyleClass().add("root");
        rootJuego.setAlignment(Pos.TOP_CENTER);
        rootJuego.setPadding(new Insets(20));

        HBox header = new HBox(20);
        header.getStyleClass().add("cyber-panel");
        header.setAlignment(Pos.CENTER);

        lblHeader.setStyle("-fx-font-size: 14px;");
        lblTimer.setStyle("-fx-text-fill: #ff3333; -fx-font-weight: bold; -fx-font-size: 16px;");
        barraTiempo.setPrefWidth(300);
        barraTiempo.setStyle("-fx-accent: #ff3333;");

        header.getChildren().addAll(lblHeader, new Label("| TIEMPO:"), barraTiempo, lblTimer);
        actualizarHeader();

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
        txtLog.setText("[SYSTEM]: CONEXIÓN AL NODO " + nivel + " ESTABLECIDA.\n");

        terminal.getChildren().addAll(lblTitle, lblReto, txtLog);

        VBox inputArea = new VBox(15);
        inputArea.getStyleClass().add("cyber-panel");
        inputArea.setAlignment(Pos.CENTER);

        txtRespuesta.setPromptText("INGRESE CÓDIGO...");
        txtRespuesta.setMaxWidth(400);

        btnHack.getStyleClass().add("button-hack");
        btnHack.setPrefWidth(400);
        btnHack.setDefaultButton(true);
        btnHack.setOnAction(e -> verificarRespuesta(nivel));

        inputArea.getChildren().addAll(txtRespuesta, btnHack);

        rootJuego.getChildren().addAll(header, terminal, inputArea);
        escenaPrincipal.setRoot(rootJuego);

        nuevoReto(nivel);
    }

    private void nuevoReto(int nivel) {
        if (jugador.getIntegridad() <= 0) return;
        lblReto.setText(logica.generarReto(nivel));
        txtRespuesta.clear();
        txtRespuesta.setDisable(false);
        txtRespuesta.requestFocus();
        iniciarTemporizador(); // Ya no necesita argumentos porque usa la global
    }

    private void iniciarTemporizador() {
        if (timerAnimacion != null) timerAnimacion.stop();
        tiempoRestante = TIEMPO_MAX;
        barraTiempo.setProgress(1.0);

        // Limpiar estilo de alarma si existía
        if (rootJuego != null) {
            rootJuego.getStyleClass().remove("alarma-roja");
            if (!rootJuego.getStyleClass().contains("root")) {
                rootJuego.getStyleClass().add("root");
            }
        }

        timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            lblTimer.setText(tiempoRestante + "s");

            double progreso = (double)tiempoRestante / TIEMPO_MAX;
            barraTiempo.setProgress(progreso);

            // --- LÓGICA DE ALARMA ROJA ---
            if (progreso < 0.3) {
                barraTiempo.setStyle("-fx-accent: red;");
                if (rootJuego != null && !rootJuego.getStyleClass().contains("alarma-roja")) {
                    rootJuego.getStyleClass().add("alarma-roja");
                }
            } else {
                barraTiempo.setStyle("-fx-accent: #00ff41;");
            }

            if (tiempoRestante <= 0) procesarFallo("TIMEOUT");
        }));
        timerAnimacion.setCycleCount(Timeline.INDEFINITE);
        timerAnimacion.play();
    }

    private void verificarRespuesta(int nivel) {
        if (txtRespuesta.getText().trim().isEmpty()) return;
        try {
            double res = Double.parseDouble(txtRespuesta.getText());
            if (logica.verificar(res)) {
                timerAnimacion.stop();
                jugador.sumarCriptos(50);
                jugador.completarNivel(nivel);
                txtLog.appendText("\n> [SUCCESS] NODO " + nivel + " HACKEADO.");
                // audio.playSuccess(); // Descomenta si usas audio
                actualizarHeader();

                // Pequeña pausa antes de volver al mapa
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch(Exception ex){}
                    Platform.runLater(this::mostrarMapaNiveles);
                }).start();

            } else {
                procesarFallo("HASH INCORRECTO");
            }
        } catch (Exception e) {
            txtLog.appendText("\n> [!] ERROR SINTAXIS.");
        }
    }

    private void procesarFallo(String motivo) {
        if (jugador.getIntegridad() <= 0) return;
        jugador.recibirDaño();
        // audio.playError(); // Descomenta si usas audio
        txtLog.appendText("\n> [DANGER] " + motivo + ". DAÑO CRÍTICO.");
        actualizarHeader();
        if (jugador.getIntegridad() <= 0) {
            timerAnimacion.stop();
            lblReto.setText("SYSTEM FAILURE");
            lblReto.setStyle("-fx-text-fill: red;");
            txtRespuesta.setDisable(true);
            btnHack.setDisable(true);
            txtLog.appendText("\n\n> SEÑAL PERDIDA. OPERADOR ELIMINADO.");
            if (rootJuego != null) rootJuego.getStyleClass().add("alarma-roja");
        } else {
            iniciarTemporizador();
        }
    }

    private void actualizarHeader() {
        int hp = Math.max(0, jugador.getIntegridad());
        lblHeader.setText("OP: " + jugador.getNombre() + " | HP: " + hp + "% | BTC: " + jugador.getCriptos());
    }

    public static void main(String[] args) { launch(args); }
}