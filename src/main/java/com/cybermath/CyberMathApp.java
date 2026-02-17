package com.cybermath;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CyberMathApp extends Application {

    private Stage ventana;
    private Scene escenaPrincipal;
    private LogicaJuego logica = new LogicaJuego();
    private Usuario jugador;

    // UI Global
    private Label lblHeader = new Label();
    private Label lblReto = new Label();
    private Label lblTimer = new Label();
    private ProgressBar barraTiempo = new ProgressBar(1.0);
    private TextArea txtLog = new TextArea();
    private TextField txtRespuesta = new TextField();
    private Button btnHack = new Button("EJECUTAR CÓDIGO [ENTER]");

    // Estado
    private VBox rootJuego;
    private Timeline timerAnimacion;
    private int tiempoRestante;
    private final int TIEMPO_MAX = 30;
    private int preguntasPendientes;
    private int totalPreguntasActual;
    private int nivelActualJugando;

    @Override
    public void start(Stage stage) {
        this.ventana = stage;
        VBox rootInicial = new VBox();
        rootInicial.setStyle("-fx-background-color: black;");
        this.escenaPrincipal = new Scene(rootInicial, 1000, 800);
        cargarCSS();
        ventana.setScene(escenaPrincipal);
        ventana.setTitle("CYBERMATH: PROTOCOL ZERO");
        ventana.show();

        mostrarIntroHistoria();
    }

    private void cargarCSS() {
        try {
            escenaPrincipal.getStylesheets().clear();
            escenaPrincipal.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        } catch (Exception e) {}
    }

    // --- 1. PANTALLA DE HISTORIA (INTRO) ---
    private void mostrarIntroHistoria() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(50));

        Label lblTitulo = new Label("AÑO 2088...");
        lblTitulo.setId("lblReto");

        TextArea txtHistoria = new TextArea();
        txtHistoria.setWrapText(true);
        txtHistoria.setEditable(false);
        txtHistoria.getStyleClass().add("text-area");
        txtHistoria.setStyle("-fx-font-size: 18px; -fx-text-fill: #00ff41; -fx-control-inner-background: black;");
        txtHistoria.setPrefHeight(300);
        txtHistoria.setMaxWidth(800);

        Button btnContinuar = new Button("INICIAR SISTEMA >>");
        btnContinuar.getStyleClass().add("button-hack");
        btnContinuar.setVisible(false);
        btnContinuar.setOnAction(e -> mostrarSelectorSlots());

        root.getChildren().addAll(lblTitulo, txtHistoria, btnContinuar);
        escenaPrincipal.setRoot(root);

        String historia = "La red global 'The Hive' ha colapsado.\n\n" +
                "Corporaciones rivales han llenado el ciberespacio de malware, ransomware y trampas lógicas.\n\n" +
                "Eres un Arquitecto de Sistemas Renegado. Tu misión es restaurar los nodos corruptos usando algoritmos matemáticos olvidados.\n\n" +
                "El destino de la información libre depende de tu velocidad mental.\n\n" +
                "Conectando...";

        escribirTextoAnimado(txtHistoria, historia, () -> btnContinuar.setVisible(true));
    }

    private void escribirTextoAnimado(TextArea area, String texto, Runnable alTerminar) {
        new Thread(() -> {
            for (char c : texto.toCharArray()) {
                try {
                    Thread.sleep(30);
                    Platform.runLater(() -> area.appendText(String.valueOf(c)));
                } catch (Exception e) {}
            }
            if (alTerminar != null) {
                Platform.runLater(alTerminar);
            }
        }).start();
    }

    // --- 2. SELECTOR DE SLOTS ---
    private void mostrarSelectorSlots() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        Label titulo = new Label("SELECCIONE PERFIL DE OPERADOR");
        titulo.setId("lblReto");
        HBox panelSlots = new HBox(20);
        panelSlots.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 3; i++) {
            Button btn = new Button("MEMORIA " + i);
            btn.getStyleClass().add("button-hack");
            btn.setPrefSize(150, 100);
            int slot = i;
            btn.setOnAction(e -> {
                jugador = new Usuario("HACKER_0" + slot);
                mostrarMapaArbol();
            });
            panelSlots.getChildren().add(btn);
        }
        root.getChildren().addAll(titulo, panelSlots);
        escenaPrincipal.setRoot(root);
    }

    // --- 3. MAPA DE ÁRBOL ESTILIZADO (NUEVO) ---
    private void mostrarMapaArbol() {
        Pane mapaCanvas = new Pane();
        mapaCanvas.getStyleClass().add("root");

        Label titulo = new Label("ARQUITECTURA DE RED - NÚCLEO CENTRAL");
        titulo.setLayoutX(20); titulo.setLayoutY(20);
        titulo.setId("lblReto");
        mapaCanvas.getChildren().add(titulo);

        // INICIO DESDE ABAJO (Árbol)
        double centerX = 500;
        double centerY = 700;

        // NODO CENTRAL (Nivel 1)
        crearNodoEstilizado(mapaCanvas, centerX, centerY, 1, true);

        // RAMAS CRECIENDO HACIA ARRIBA
        // Rama 1: Phishing (Centro Arriba)
        dibujarRama(mapaCanvas, centerX, centerY, 2, 11, -90);
        // Rama 2: Firewall (Diagonal Arriba-Derecha)
        dibujarRama(mapaCanvas, centerX, centerY, 12, 21, -60);
        // Rama 3: Cripto (Más a la Derecha)
        dibujarRama(mapaCanvas, centerX, centerY, 22, 31, -30);
        // Rama 4: SQL (Diagonal Arriba-Izquierda)
        dibujarRama(mapaCanvas, centerX, centerY, 32, 41, -120);
        // Rama 5: DDoS (Más a la Izquierda)
        dibujarRama(mapaCanvas, centerX, centerY, 42, 50, -150);

        Button btnVolver = new Button("CERRAR SESIÓN");
        btnVolver.getStyleClass().add("button-hack");
        btnVolver.setLayoutX(20); btnVolver.setLayoutY(720);
        btnVolver.setOnAction(e -> mostrarSelectorSlots());
        mapaCanvas.getChildren().add(btnVolver);

        ScrollPane scroll = new ScrollPane(mapaCanvas);
        scroll.setFitToWidth(true); scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: black;");
        escenaPrincipal.setRoot(scroll);
    }

    private void dibujarRama(Pane canvas, double startX, double startY, int nivelInicio, int nivelFin, double anguloGrados) {
        double currentX = startX;
        double currentY = startY;
        double distancia = 80; // Un poco más de separación
        double rad = Math.toRadians(anguloGrados);

        // Color Neón Cian
        Color neonColor = Color.web("#00ffff");

        for (int i = nivelInicio; i <= nivelFin; i++) {
            double zigZag = (i % 2 == 0) ? 15 : -15;
            double nextX = currentX + (Math.cos(rad) * distancia) + (Math.sin(rad) * zigZag * 0.3);
            double nextY = currentY + (Math.sin(rad) * distancia) + (Math.cos(rad) * zigZag * 0.3);

            Line cable = new Line(currentX, currentY, nextX, nextY);
            cable.setStroke(Color.web("#004444")); // Base oscura
            cable.setStrokeWidth(3);

            if (i <= jugador.getNivelMaximo() + 1) {
                cable.setStroke(neonColor); // Brillo si está accesible
                cable.setOpacity(0.8);
                cable.setEffect(new DropShadow(10, neonColor)); // Efecto de luz
            }
            canvas.getChildren().add(0, cable);

            boolean desbloqueado = (i <= jugador.getNivelMaximo());
            crearNodoEstilizado(canvas, nextX, nextY, i, desbloqueado);

            currentX = nextX;
            currentY = nextY;
        }
    }

    // --- NUEVO MÉTODO PARA NODO CON FORMA DE ESCUDO ---
    private void crearNodoEstilizado(Pane canvas, double x, double y, int nivel, boolean desbloqueado) {
        StackPane nodoStack = new StackPane();
        nodoStack.setLayoutX(x - 25); // Centrar el StackPane
        nodoStack.setLayoutY(y - 25);

        // Forma Geométrica Compleja (Escudo)
        Polygon baseShape = new Polygon();
        baseShape.getPoints().addAll(new Double[]{
                0.0, -25.0,  // Punta Superior
                20.0, -10.0, // Hombro Derecho
                15.0, 15.0,  // Base Derecha
                0.0, 25.0,   // Punta Inferior
                -15.0, 15.0, // Base Izquierda
                -20.0, -10.0 // Hombro Izquierdo
        });

        Color neonColor = Color.web("#00ffff"); // Cian brillante
        baseShape.setStroke(neonColor);
        baseShape.setStrokeWidth(2);

        if (desbloqueado) {
            baseShape.setFill(Color.web("#002233")); // Relleno oscuro azulado
            baseShape.setEffect(new DropShadow(15, neonColor)); // Resplandor intenso

            Label lblNum = new Label(String.valueOf(nivel));
            lblNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            // Interactividad
            nodoStack.setCursor(Cursor.HAND);
            nodoStack.setOnMouseClicked(e -> mostrarBriefingMision(nivel));
            nodoStack.setOnMouseEntered(e -> baseShape.setFill(neonColor.deriveColor(0, 1, 1, 0.3)));
            nodoStack.setOnMouseExited(e -> baseShape.setFill(Color.web("#002233")));

            nodoStack.getChildren().addAll(baseShape, lblNum);
        } else {
            baseShape.setFill(Color.BLACK);
            baseShape.setStroke(Color.GRAY);
            baseShape.setOpacity(0.4);
            nodoStack.getChildren().add(baseShape);
        }

        canvas.getChildren().add(nodoStack);
    }

    // --- 4. BRIEFING ANIMADO ---
    private void mostrarBriefingMision(int nivel) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(40));

        Label lblTitulo = new Label("INFORME DE MISIÓN: " + logica.getNombreRama(nivel));
        lblTitulo.setId("lblReto");

        TextArea txtDetalles = new TextArea();
        txtDetalles.setWrapText(true);
        txtDetalles.setEditable(false);
        txtDetalles.getStyleClass().add("text-area");
        txtDetalles.setStyle("-fx-font-size: 16px; -fx-text-fill: #00ff41; -fx-control-inner-background: black;");
        txtDetalles.setPrefHeight(250);
        txtDetalles.setMaxWidth(700);

        Button btnIniciar = new Button("EJECUTAR HACKEO");
        btnIniciar.getStyleClass().add("button-hack");
        btnIniciar.setVisible(false);
        btnIniciar.setOnAction(e -> iniciarJuego(nivel));

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.getStyleClass().add("button-hack");
        btnCancelar.setOnAction(e -> mostrarMapaArbol());

        HBox botones = new HBox(20, btnCancelar, btnIniciar);
        botones.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblTitulo, txtDetalles, botones);
        escenaPrincipal.setRoot(root);

        String descripcion = logica.getDescripcionMision(nivel) +
                "\n\nSECUENCIAS A RESOLVER: " + logica.getPreguntasPorNivel(nivel);

        escribirTextoAnimado(txtDetalles, descripcion, () -> btnIniciar.setVisible(true));
    }

    // --- 5. JUEGO PRINCIPAL ---
    private void iniciarJuego(int nivel) {
        this.nivelActualJugando = nivel;
        this.totalPreguntasActual = logica.getPreguntasPorNivel(nivel);
        this.preguntasPendientes = totalPreguntasActual;

        rootJuego = new VBox(20);
        rootJuego.getStyleClass().add("root");
        rootJuego.setAlignment(Pos.TOP_CENTER);
        rootJuego.setPadding(new Insets(20));

        HBox header = new HBox(20);
        header.getStyleClass().add("cyber-panel");
        header.setAlignment(Pos.CENTER);
        lblHeader.setStyle("-fx-font-size: 14px;");
        lblTimer.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        barraTiempo.setPrefWidth(300);
        header.getChildren().addAll(lblHeader, new Label("| T:"), barraTiempo, lblTimer);
        actualizarHeader();

        VBox terminal = new VBox(10);
        terminal.getStyleClass().add("cyber-panel");
        terminal.setAlignment(Pos.CENTER);
        Label lblTitle = new Label(">>> CONSOLA DE COMANDOS");
        lblTitle.getStyleClass().add("panel-title");
        lblReto.setId("lblReto");
        txtLog.setEditable(false);
        txtLog.setPrefHeight(150);
        txtLog.setText("[SYSTEM]: CONECTADO AL NODO " + nivel + "...\n");
        terminal.getChildren().addAll(lblTitle, lblReto, txtLog);

        VBox inputArea = new VBox(15);
        inputArea.getStyleClass().add("cyber-panel");
        inputArea.setAlignment(Pos.CENTER);
        txtRespuesta.setPromptText("CODE...");
        btnHack.getStyleClass().add("button-hack");
        btnHack.setPrefWidth(400);
        btnHack.setDefaultButton(true);
        btnHack.setOnAction(e -> verificarRespuesta());
        inputArea.getChildren().addAll(txtRespuesta, btnHack);

        rootJuego.getChildren().addAll(header, terminal, inputArea);
        escenaPrincipal.setRoot(rootJuego);

        nuevoReto();
    }

    private void nuevoReto() {
        if (jugador.getIntegridad() <= 0) return;
        int preguntaActual = (totalPreguntasActual - preguntasPendientes) + 1;
        String retoTexto = logica.generarReto(nivelActualJugando, preguntaActual, totalPreguntasActual);

        lblReto.setText(retoTexto);
        txtRespuesta.clear();
        txtRespuesta.setDisable(false);
        txtRespuesta.requestFocus();
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (timerAnimacion != null) timerAnimacion.stop();
        tiempoRestante = TIEMPO_MAX;
        barraTiempo.setProgress(1.0);
        rootJuego.getStyleClass().remove("alarma-roja");

        timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            lblTimer.setText(tiempoRestante + "s");
            double progreso = (double)tiempoRestante / TIEMPO_MAX;
            barraTiempo.setProgress(progreso);

            if (progreso < 0.3 && !rootJuego.getStyleClass().contains("alarma-roja")) {
                rootJuego.getStyleClass().add("alarma-roja");
            }
            if (tiempoRestante <= 0) procesarFallo("TIMEOUT");
        }));
        timerAnimacion.setCycleCount(Timeline.INDEFINITE);
        timerAnimacion.play();
    }

    private void verificarRespuesta() {
        if (txtRespuesta.getText().trim().isEmpty()) return;
        try {
            double res = Double.parseDouble(txtRespuesta.getText());
            if (logica.verificar(res)) {
                preguntasPendientes--;
                txtLog.appendText("\n> [OK] SECUENCIA VALIDADA.");

                if (preguntasPendientes == 0) {
                    timerAnimacion.stop();
                    jugador.sumarCriptos(100);
                    jugador.completarNivel(nivelActualJugando);
                    txtLog.appendText("\n> [SUCCESS] NODO CAPTURADO.");

                    new Thread(() -> {
                        try { Thread.sleep(1500); Platform.runLater(this::mostrarMapaArbol); } catch(Exception ex){}
                    }).start();
                } else {
                    nuevoReto();
                }
            } else {
                procesarFallo("HASH INVÁLIDO");
            }
        } catch (Exception e) { txtLog.appendText("\n> [ERROR] SINTAXIS."); }
    }

    private void procesarFallo(String motivo) {
        jugador.recibirDaño();
        txtLog.appendText("\n> [FAIL] " + motivo + ". DAÑO: -34%");
        actualizarHeader();
        if (jugador.getIntegridad() <= 0) {
            timerAnimacion.stop();
            lblReto.setText("SYSTEM FAILURE");
            txtRespuesta.setDisable(true);
            btnHack.setDisable(true);
        } else {
            iniciarTemporizador();
        }
    }

    private void actualizarHeader() {
        lblHeader.setText("OP: " + jugador.getNombre() + " | HP: " + jugador.getIntegridad() + "% | BTC: " + jugador.getCriptos());
    }

    public static void main(String[] args) { launch(args); }
}