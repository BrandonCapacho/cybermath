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
    private GestorSonido audio = new GestorSonido();
    private TiendaHardware tienda = new TiendaHardware();

    // UI Global
    private Label lblHeader = new Label();
    private Label lblReto = new Label();
    private Label lblTimer = new Label();
    private ProgressBar barraTiempo = new ProgressBar(1.0);
    private TextArea txtLog = new TextArea();
    private TextField txtRespuesta = new TextField();
    private Button btnHack = new Button("EJECUTAR CÓDIGO [ENTER]");

    private VBox rootJuego;
    private Timeline timerAnimacion;
    private int tiempoRestante;
    private final int TIEMPO_MAX = 30;
    private int preguntasPendientes;
    private int totalPreguntasActual;
    private int nivelActualJugando;
    private boolean alarmaSonando = false;

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

        mostrarIntroHistoria(); // <-- RESTAURADA LA HISTORIA INICIAL
    }

    private void cargarCSS() {
        try {
            escenaPrincipal.getStylesheets().clear();
            escenaPrincipal.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        } catch (Exception e) {}
    }

    // --- EFECTO MÁQUINA DE ESCRIBIR (RESTAURADO) ---
    private void escribirTextoAnimado(TextArea area, String texto, Runnable alTerminar) {
        area.clear();
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

    // --- PANTALLA DE HISTORIA INICIAL ---
    private void mostrarIntroHistoria() {
        audio.playIntro(); // SONIDO BOOT
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(50));

        Label lblTitulo = new Label("INICIALIZANDO KERNEL...");
        lblTitulo.setId("lblReto");

        TextArea txtHistoria = new TextArea();
        txtHistoria.setWrapText(true);
        txtHistoria.setEditable(false);
        txtHistoria.getStyleClass().add("text-area");
        txtHistoria.setStyle("-fx-font-size: 18px; -fx-text-fill: #00ff41; -fx-control-inner-background: black;");
        txtHistoria.setPrefHeight(300);
        txtHistoria.setMaxWidth(800);

        Button btnContinuar = new Button("CONECTAR AL SISTEMA >>");
        btnContinuar.getStyleClass().add("button-hack");
        btnContinuar.setVisible(false);
        btnContinuar.setOnAction(e -> mostrarSelectorSlots());

        root.getChildren().addAll(lblTitulo, txtHistoria, btnContinuar);
        escenaPrincipal.setRoot(root);

        String historia = "AÑO 2088. LA RED GLOBAL 'THE HIVE' HA COLAPSADO.\n\n" +
                "Corporaciones rivales han llenado el ciberespacio de malware, ransomware y trampas lógicas.\n\n" +
                "Eres un Arquitecto de Sistemas Renegado. Tu misión es restaurar los nodos corruptos usando algoritmos matemáticos y lógica de programación.\n\n" +
                "ADVERTENCIA: Si la integridad de tu conexión llega a 0%, tu perfil será purgado del servidor.\n\n" +
                "Estableciendo conexión segura...";

        escribirTextoAnimado(txtHistoria, historia, () -> btnContinuar.setVisible(true));
    }

    // --- SELECTOR DE PERFILES ---
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
                audio.playInfiltrado();
                mostrarMapaArbol();
            });
            panelSlots.getChildren().add(btn);
        }
        root.getChildren().addAll(titulo, panelSlots);
        escenaPrincipal.setRoot(root);
    }

    // --- MAPA DE ÁRBOL Y TIENDA ---
    private void mostrarMapaArbol() {
        Pane mapaCanvas = new Pane();
        mapaCanvas.getStyleClass().add("root");

        Label titulo = new Label("SISTEMA CENTRAL - SELECCIONE NODO");
        titulo.setLayoutX(20); titulo.setLayoutY(20);
        titulo.setId("lblReto");
        mapaCanvas.getChildren().add(titulo);

        Label lblEstado = new Label("OP: " + jugador.getNombre() + " | HP: " + Math.max(0, jugador.getIntegridad()) + "% | BTC: " + jugador.getCriptos());
        lblEstado.setStyle("-fx-text-fill: #00ff41; -fx-font-size: 16px; -fx-font-weight: bold;");
        lblEstado.setLayoutX(20); lblEstado.setLayoutY(70);
        mapaCanvas.getChildren().add(lblEstado);

        double centerX = 500; double centerY = 700;

        crearNodoEstilizado(mapaCanvas, centerX, centerY, 1, true);
        dibujarRama(mapaCanvas, centerX, centerY, 2, 11, -90);
        dibujarRama(mapaCanvas, centerX, centerY, 12, 21, -60);
        dibujarRama(mapaCanvas, centerX, centerY, 22, 31, -30);
        dibujarRama(mapaCanvas, centerX, centerY, 32, 41, -120);
        dibujarRama(mapaCanvas, centerX, centerY, 42, 50, -150);

        Button btnTienda = new Button(">> MERCADO NEGRO (TIENDA)");
        btnTienda.getStyleClass().add("button-hack");
        btnTienda.setLayoutX(700); btnTienda.setLayoutY(20);
        btnTienda.setOnAction(e -> mostrarTienda());
        mapaCanvas.getChildren().add(btnTienda);

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
        double currentX = startX; double currentY = startY;
        double distancia = 80;
        double rad = Math.toRadians(anguloGrados);
        Color neonColor = Color.web("#00ffff");

        for (int i = nivelInicio; i <= nivelFin; i++) {
            double zigZag = (i % 2 == 0) ? 15 : -15;
            double nextX = currentX + (Math.cos(rad) * distancia) + (Math.sin(rad) * zigZag * 0.3);
            double nextY = currentY + (Math.sin(rad) * distancia) + (Math.cos(rad) * zigZag * 0.3);

            Line cable = new Line(currentX, currentY, nextX, nextY);
            cable.setStroke(Color.web("#004444"));
            cable.setStrokeWidth(3);
            if (i <= jugador.getNivelMaximo() + 1) {
                cable.setStroke(neonColor);
                cable.setOpacity(0.8);
                cable.setEffect(new DropShadow(10, neonColor));
            }
            canvas.getChildren().add(0, cable);
            boolean desbloqueado = (i <= jugador.getNivelMaximo());
            crearNodoEstilizado(canvas, nextX, nextY, i, desbloqueado);
            currentX = nextX; currentY = nextY;
        }
    }

    private void crearNodoEstilizado(Pane canvas, double x, double y, int nivel, boolean desbloqueado) {
        StackPane nodoStack = new StackPane();
        nodoStack.setLayoutX(x - 25); nodoStack.setLayoutY(y - 25);

        Polygon baseShape = new Polygon();
        baseShape.getPoints().addAll(new Double[]{ 0.0, -25.0, 20.0, -10.0, 15.0, 15.0, 0.0, 25.0, -15.0, 15.0, -20.0, -10.0 });
        Color neonColor = Color.web("#00ffff");
        baseShape.setStroke(neonColor); baseShape.setStrokeWidth(2);

        if (desbloqueado) {
            baseShape.setFill(Color.web("#002233"));
            baseShape.setEffect(new DropShadow(15, neonColor));
            Label lblNum = new Label(String.valueOf(nivel));
            lblNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            nodoStack.setCursor(Cursor.HAND);
            nodoStack.setOnMouseClicked(e -> mostrarBriefingMision(nivel));
            nodoStack.setOnMouseEntered(e -> baseShape.setFill(neonColor.deriveColor(0, 1, 1, 0.3)));
            nodoStack.setOnMouseExited(e -> baseShape.setFill(Color.web("#002233")));
            nodoStack.getChildren().addAll(baseShape, lblNum);
        } else {
            baseShape.setFill(Color.BLACK); baseShape.setStroke(Color.GRAY); baseShape.setOpacity(0.4);
            nodoStack.getChildren().add(baseShape);
        }
        canvas.getChildren().add(nodoStack);
    }

    // --- INTERFAZ DE TIENDA ---
    private void mostrarTienda() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titulo = new Label(">>> MERCADO NEGRO (DEEP WEB) <<<");
        titulo.setId("lblReto");
        titulo.setStyle("-fx-text-fill: #ff00ff;");

        Label lblFondos = new Label("FONDOS DISPONIBLES: " + jugador.getCriptos() + " BTC\nINTEGRIDAD ACTUAL: " + Math.max(0, jugador.getIntegridad()) + "%");
        lblFondos.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        VBox listaItems = new VBox(15);
        listaItems.setAlignment(Pos.CENTER);

        for (int i = 0; i < tienda.getCatalogo().size(); i++) {
            ItemHardware item = tienda.getCatalogo().get(i);
            Button btnItem = new Button(item.getNombre() + " - COSTO: " + item.getPrecio() + " BTC");
            btnItem.getStyleClass().add("button-hack");
            btnItem.setPrefWidth(500);
            int index = i;
            btnItem.setOnAction(e -> {
                String resultado = tienda.comprar(index, jugador);
                if (resultado.contains("EXITOSA")) {
                    audio.playSuccess();
                } else {
                    audio.playError();
                }
                mostrarTienda();
            });
            listaItems.getChildren().add(btnItem);
        }

        Button btnVolver = new Button("VOLVER AL MAPA");
        btnVolver.getStyleClass().add("button-hack");
        btnVolver.setOnAction(e -> mostrarMapaArbol());

        root.getChildren().addAll(titulo, lblFondos, listaItems, btnVolver);
        escenaPrincipal.setRoot(root);
    }

    // --- BRIEFING ANIMADO (RESTAURADO) ---
    private void mostrarBriefingMision(int nivel) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(40));

        Label lblTitulo = new Label("INFORME DE MISIÓN: " + logica.getNombreRama(nivel));
        lblTitulo.setId("lblReto");

        TextArea txtDetalles = new TextArea();
        txtDetalles.setWrapText(true); txtDetalles.setEditable(false);
        txtDetalles.getStyleClass().add("text-area");
        txtDetalles.setStyle("-fx-font-size: 16px; -fx-text-fill: #00ff41; -fx-control-inner-background: black;");
        txtDetalles.setPrefHeight(250); txtDetalles.setMaxWidth(700);

        Button btnIniciar = new Button("EJECUTAR HACKEO");
        btnIniciar.getStyleClass().add("button-hack");
        btnIniciar.setVisible(false); // Oculto hasta que termine la escritura
        btnIniciar.setOnAction(e -> iniciarJuego(nivel));

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.getStyleClass().add("button-hack");
        btnCancelar.setOnAction(e -> mostrarMapaArbol());

        HBox botones = new HBox(20, btnCancelar, btnIniciar);
        botones.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblTitulo, txtDetalles, botones);
        escenaPrincipal.setRoot(root);

        String descripcion = logica.getDescripcionMision(nivel) + "\n\nSECUENCIAS A RESOLVER: " + logica.getPreguntasPorNivel(nivel);
        escribirTextoAnimado(txtDetalles, descripcion, () -> btnIniciar.setVisible(true));
    }

    // --- JUEGO PRINCIPAL ---
    private void iniciarJuego(int nivel) {
        this.nivelActualJugando = nivel;
        this.totalPreguntasActual = logica.getPreguntasPorNivel(nivel);
        this.preguntasPendientes = totalPreguntasActual;
        this.alarmaSonando = false;

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
        txtLog.setEditable(false); txtLog.setPrefHeight(150);
        txtLog.setText("[SYSTEM]: CONECTADO AL NODO " + nivel + "...\n");
        terminal.getChildren().addAll(lblTitle, lblReto, txtLog);

        VBox inputArea = new VBox(15);
        inputArea.getStyleClass().add("cyber-panel");
        inputArea.setAlignment(Pos.CENTER);
        txtRespuesta.setPromptText("CODE...");
        btnHack.getStyleClass().add("button-hack");
        btnHack.setPrefWidth(400); btnHack.setDefaultButton(true);
        btnHack.setOnAction(e -> verificarRespuesta());
        inputArea.getChildren().addAll(txtRespuesta, btnHack);

        rootJuego.getChildren().addAll(header, terminal, inputArea);
        escenaPrincipal.setRoot(rootJuego);

        nuevoReto();
    }

    private void nuevoReto() {
        if (jugador.getIntegridad() <= 0) return;
        int preguntaActual = (totalPreguntasActual - preguntasPendientes) + 1;
        lblReto.setText(logica.generarReto(nivelActualJugando, preguntaActual, totalPreguntasActual));
        txtRespuesta.clear(); txtRespuesta.setDisable(false); txtRespuesta.requestFocus();
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (timerAnimacion != null) timerAnimacion.stop();
        tiempoRestante = TIEMPO_MAX;
        barraTiempo.setProgress(1.0);
        rootJuego.getStyleClass().remove("alarma-roja");
        alarmaSonando = false;

        timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            lblTimer.setText(tiempoRestante + "s");
            double progreso = (double)tiempoRestante / TIEMPO_MAX;
            barraTiempo.setProgress(progreso);

            if (progreso < 0.3) {
                if (!rootJuego.getStyleClass().contains("alarma-roja")) {
                    rootJuego.getStyleClass().add("alarma-roja");
                }
                if (!alarmaSonando) {
                    audio.playAmenaza();
                    alarmaSonando = true;
                }
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
                audio.playSuccess();
                preguntasPendientes--;
                txtLog.appendText("\n> [OK] SECUENCIA VALIDADA.");

                if (preguntasPendientes == 0) {
                    timerAnimacion.stop();
                    jugador.sumarCriptos(100);
                    jugador.completarNivel(nivelActualJugando);
                    txtLog.appendText("\n> [SUCCESS] NODO CAPTURADO. +100 BTC.");
                    actualizarHeader();
                    new Thread(() -> {
                        try { Thread.sleep(1500); Platform.runLater(this::mostrarMapaArbol); } catch(Exception ex){}
                    }).start();
                } else {
                    nuevoReto();
                }
            } else {
                procesarFallo("HASH INVÁLIDO");
            }
        } catch (Exception e) { txtLog.appendText("\n> [ERROR] SINTAXIS."); audio.playError(); }
    }

    // --- MANEJO DEL GAME OVER (RESTAURADO) ---
    private void procesarFallo(String motivo) {
        audio.playError();
        jugador.recibirDaño();
        txtLog.appendText("\n> [FAIL] " + motivo + ". DAÑO CRÍTICO.");
        actualizarHeader();

        if (jugador.getIntegridad() <= 0) {
            timerAnimacion.stop();
            lblReto.setText("SYSTEM FAILURE");
            txtRespuesta.setDisable(true); btnHack.setDisable(true);
            txtLog.appendText("\n\n> INTEGRIDAD AL 0%. SISTEMA DESTRUIDO.\n> REINICIANDO CONEXIÓN AL NÚCLEO...");

            if (!rootJuego.getStyleClass().contains("alarma-roja")) {
                rootJuego.getStyleClass().add("alarma-roja");
            }

            // Redirige al menú principal después de 3.5 segundos
            new Thread(() -> {
                try { Thread.sleep(3500); } catch(Exception ex){}
                Platform.runLater(this::mostrarSelectorSlots);
            }).start();

        } else {
            iniciarTemporizador();
        }
    }

    private void actualizarHeader() {
        lblHeader.setText("OP: " + jugador.getNombre() + " | HP: " + Math.max(0, jugador.getIntegridad()) + "% | BTC: " + jugador.getCriptos());
    }

    public static void main(String[] args) { launch(args); }
}