package com.cybermath;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class CyberMathApp extends Application {

    private static final String C_VERDE    = "#00ff41";
    private static final String C_CYAN     = "#00ffff";
    private static final String C_MAGENTA  = "#ff00ff";
    private static final String C_ROJO     = "#ff003c";
    private static final String C_AMARILLO = "#ffff00";
    private static final String C_FONDO    = "#020a02";

    private static final Object[][] RAMAS = {
            {  2, 11, "PHISHING",     "#ff6600" },
            { 12, 21, "FIREWALL",     "#00ccff" },
            { 22, 31, "CRIPTOGRAFÍA", "#cc00ff" },
            { 32, 41, "SQL INJECT",   "#ffcc00" },
            { 42, 50, "DDoS OMEGA",   "#ff0044" },
    };

    private Stage  ventana;
    private Scene  escenaPrincipal;

    private final LogicaJuego    logica = new LogicaJuego();
    private final GestorSonido   audio  = new GestorSonido();
    private final TiendaHardware tienda = new TiendaHardware();
    private final Configuracion  config = new Configuracion();

    private Usuario jugador;
    private int     slotActual;

    private Label       lblHeader;
    private Label       lblTimer;
    private ProgressBar barraTiempo;
    private TextArea    txtLog;
    private TextField   txtRespuesta;
    private Button      btnHack;
    private VBox        panelReto;
    private VBox        rootJuego;

    private Timeline animacionMatrix;
    private Timeline timerAnimacion;
    private int      tiempoRestante;
    private int      TIEMPO_MAX          = 30;
    private int      preguntasPendientes;
    private int      totalPreguntasActual;
    private int      nivelActualJugando;
    private boolean  alarmaSonando       = false;

    private ProgressBar barraEnergia;
    private Label       lblEnergia;
    private Button      btnOverclock;
    private Button      btnBruteForce;
    private Timeline    mineriaTimeline;
    private Timeline    animacionBypass;
    private double      posicionBypass  = 0.0;
    private boolean     moviendoDerecha = true;

    // Variables para el sistema de decisiones
    private int    bonusDecisionBTC      = 0;
    private int    bonusDecisionSegundos = 0;
    private String dialogoDecision       = "";

    @Override
    public void start(Stage stage) {
        this.ventana = stage;
        GestorDB.inicializar();
        VBox tmp = new VBox();
        tmp.setStyle("-fx-background-color:" + C_FONDO + ";");
        this.escenaPrincipal = new Scene(tmp, 1200, 820);
        cargarCSS();
        ventana.setScene(escenaPrincipal);
        ventana.setTitle("CYBERMATH: PROTOCOL ZERO");
        ventana.setMinWidth(900);
        ventana.setMinHeight(680);
        ventana.show();
        mostrarMenuPrincipal();
    }

    @Override
    public void stop() {
        if (mineriaTimeline  != null) mineriaTimeline.stop();
        if (timerAnimacion   != null) timerAnimacion.stop();
        if (animacionMatrix  != null) animacionMatrix.stop();
        if (animacionBypass  != null) animacionBypass.stop();
        GestorDB.cerrar();
    }

    // =========================================================================
    // HELPERS GENERALES
    // =========================================================================

    private void aplicarEfectoTema(Parent root) {
        if (jugador != null) {
            ColorAdjust ca = new ColorAdjust();
            switch (jugador.getTemaUI()) {
                case "AMBAR": ca.setHue(-0.6); break;
                case "AZUL":  ca.setHue(0.8);  break;
                default:      ca.setHue(0);    break;
            }
            root.setEffect(ca);
        }
    }

    private void cargarCSS() {
        try {
            escenaPrincipal.getStylesheets().clear();
            escenaPrincipal.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        } catch (Exception e) { System.err.println("[CSS] " + e.getMessage()); }
    }

    private void escribirTextoAnimado(TextArea area, String texto, Runnable fin) {
        area.clear();
        Thread h = new Thread(() -> {
            for (char c : texto.toCharArray()) {
                try {
                    Thread.sleep(config.isTextoRapido() ? 8 : 25);
                    Platform.runLater(() -> area.appendText(String.valueOf(c)));
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
            if (fin != null) Platform.runLater(fin);
        });
        h.setDaemon(true); h.start();
    }

    private void pausar(int ms, Runnable fin) {
        PauseTransition p = new PauseTransition(Duration.millis(ms));
        p.setOnFinished(e -> fin.run()); p.play();
    }

    private Button btn(String t) {
        Button b = new Button(t); b.getStyleClass().add("button-hack"); return b;
    }

    private Button btnAncho(String t, double w) {
        Button b = btn(t); b.setPrefWidth(w); return b;
    }

    /** Botón compacto para la topBar del mapa */
    private Button btnMini(String texto, String color) {
        Button b = new Button(texto);
        String base =
                "-fx-background-color:rgba(0,18,0,0.85);" +
                        "-fx-text-fill:" + color + ";" +
                        "-fx-border-color:" + color + ";" +
                        "-fx-border-width:1px;-fx-border-radius:3;-fx-background-radius:3;" +
                        "-fx-font-family:'Consolas';-fx-font-size:11px;-fx-padding:5 10;-fx-cursor:hand;";
        String hover =
                "-fx-background-color:rgba(0,40,0,0.95);" +
                        "-fx-text-fill:" + color + ";" +
                        "-fx-border-color:" + color + ";" +
                        "-fx-border-width:1.5px;-fx-border-radius:3;-fx-background-radius:3;" +
                        "-fx-font-family:'Consolas';-fx-font-size:11px;-fx-padding:5 10;-fx-cursor:hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        return b;
    }

    private void fadeIn(javafx.scene.Node n, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), n);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private Label estilo(String t, int size, String color) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:" + size +
                "px;-fx-font-family:'Consolas';-fx-font-weight:bold;");
        return l;
    }

    private String estadoJugador() {
        return "OP: " + jugador.getNombre() +
                "   HP: " + Math.max(0, jugador.getIntegridad()) + "%" +
                "   BTC: " + jugador.getCriptos() +
                "   NODOS: " + jugador.getNivelesSuperados() + "/50";
    }

    // =========================================================================
    // FONDO MATRIX — helper centralizado
    // FIX: un solo punto de creación evita el race-condition de bind+runLater
    // =========================================================================

    private Canvas crearCanvasMatrix() {
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(escenaPrincipal.widthProperty());
        canvas.heightProperty().bind(escenaPrincipal.heightProperty());
        // Lanzar la animación una sola vez cuando el canvas tiene tamaño real
        canvas.widthProperty().addListener((obs, ov, nv) -> {
            if (nv.doubleValue() > 0 && canvas.getHeight() > 0)
                animarFondoMatrix(canvas);
        });
        canvas.heightProperty().addListener((obs, ov, nv) -> {
            if (nv.doubleValue() > 0 && canvas.getWidth() > 0)
                animarFondoMatrix(canvas);
        });
        // Fallback por si ya tiene tamaño
        Platform.runLater(() -> {
            if (canvas.getWidth() > 0 && canvas.getHeight() > 0)
                animarFondoMatrix(canvas);
        });
        return canvas;
    }

    private void animarFondoMatrix(Canvas canvas) {
        if (animacionMatrix != null) animacionMatrix.stop();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Random rand = new Random();
        String chars = "01アイウカ∫∑∂≠≈ABCDEF#@%";
        animacionMatrix = new Timeline(new KeyFrame(Duration.millis(55), e -> {
            double w = canvas.getWidth(), h = canvas.getHeight();
            if (w <= 0 || h <= 0) return;
            gc.setFill(Color.rgb(2, 10, 2, 0.14));
            gc.fillRect(0, 0, w, h);
            gc.setFont(Font.font("Consolas", 13));
            int cols = Math.max(1, (int)(w / 16));
            for (int i = 0; i < cols; i++) {
                char c = chars.charAt(rand.nextInt(chars.length()));
                gc.setFill(Color.rgb(0, 255, 65, 0.07 + rand.nextDouble() * 0.28));
                gc.fillText(String.valueOf(c), i * 16, rand.nextDouble() * h);
            }
        }));
        animacionMatrix.setCycleCount(Timeline.INDEFINITE);
        animacionMatrix.play();
    }

    private void detenerAnimacionMatrix() {
        if (animacionMatrix != null) animacionMatrix.stop();
        animacionMatrix = null; // reset para que crearCanvasMatrix() lo reinicie bien
    }

    // =========================================================================
    // MENÚ PRINCIPAL
    // =========================================================================

    private void mostrarMenuPrincipal() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:" + C_FONDO + ";");

        Canvas fondo = crearCanvasMatrix();

        VBox panel = new VBox(18); panel.setAlignment(Pos.CENTER);

        Label lblT = new Label("CYBERMATH"); lblT.setStyle("-fx-font-size:54px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_VERDE + ";");
        lblT.setEffect(new DropShadow(28, Color.web(C_VERDE)));
        Label lblS = new Label("PROTOCOL  ZERO"); lblS.setStyle("-fx-font-size:18px;-fx-letter-spacing:6;-fx-font-family:'Consolas';-fx-text-fill:" + C_CYAN + ";");
        Label lblV = new Label("v2.0 — AÑO 2088"); lblV.setStyle("-fx-font-size:12px;-fx-font-family:'Consolas';-fx-text-fill:#3a5a3a;");

        Line sep = new Line(0, 0, 340, 0);
        sep.setStroke(Color.web(C_VERDE)); sep.setOpacity(0.35);

        Button btnJugar  = btnAncho("[ NUEVA MISIÓN ]",        300);
        Button btnCargar = btnAncho("[ CARGAR PERFIL ]",       300);
        Button btnConf   = btnAncho("[ CONFIGURACIÓN ]",       300);
        Button btnCred   = btnAncho("[ CRÉDITOS ]",            300);
        Button btnSalir  = btnAncho("[ TERMINAR CONEXIÓN ]",   300);

        btnJugar.setOnAction(e  -> { audio.playInfiltrado(); detenerAnimacionMatrix(); mostrarIntroHistoria(); });
        btnCargar.setOnAction(e -> { audio.playInfiltrado(); detenerAnimacionMatrix(); mostrarSelectorSlots(); });
        btnConf.setOnAction(e   -> { detenerAnimacionMatrix(); mostrarConfiguracion(); });
        btnCred.setOnAction(e   -> { detenerAnimacionMatrix(); mostrarCreditos(); });
        btnSalir.setOnAction(e  -> ventana.close());

        Label lblSt = new Label("UNIVERSIDAD DE SANTANDER — UDES · Ingeniería de Sistemas");
        lblSt.setStyle("-fx-text-fill:#2a3a2a;-fx-font-size:10px;-fx-font-family:'Consolas';");

        panel.getChildren().addAll(lblT, lblS, lblV, sep, btnJugar, btnCargar, btnConf, btnCred, btnSalir, lblSt);
        root.getChildren().addAll(fondo, panel);

        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root);
        fadeIn(panel, 500);
    }

    // =========================================================================
    // CONFIGURACIÓN Y CRÉDITOS
    // =========================================================================

    private void mostrarConfiguracion() {
        VBox root = new VBox(22); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label("[ CONFIGURACIÓN DEL SISTEMA ]"); titulo.setId("lblReto");

        VBox panel = new VBox(14); panel.setAlignment(Pos.CENTER_LEFT);
        panel.setMaxWidth(500); panel.getStyleClass().add("cyber-panel");

        CheckBox chkR = new CheckBox("Texto rápido (sin animación)");
        chkR.setSelected(config.isTextoRapido());
        chkR.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:13px;");
        chkR.setOnAction(e -> config.setTextoRapido(chkR.isSelected()));

        ToggleGroup tg = new ToggleGroup();
        HBox boxDif = new HBox(16);
        for (String d : new String[]{"NORMAL", "DIFÍCIL", "OMEGA"}) {
            RadioButton rb = new RadioButton(d); rb.setToggleGroup(tg);
            rb.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:13px;");
            if (d.equals(config.getDificultad())) rb.setSelected(true);
            rb.setOnAction(e -> config.setDificultad(d));
            boxDif.getChildren().add(rb);
        }

        Label lblT = new Label("► TIEMPO POR PREGUNTA: " + TIEMPO_MAX + "s");
        lblT.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;");
        Slider sl = new Slider(15, 60, TIEMPO_MAX);
        sl.setMajorTickUnit(15); sl.setShowTickLabels(true); sl.setShowTickMarks(true); sl.setSnapToTicks(true);
        sl.valueProperty().addListener((o, ov, nv) -> {
            TIEMPO_MAX = nv.intValue(); config.setTiempoMaximo(TIEMPO_MAX);
            lblT.setText("► TIEMPO POR PREGUNTA: " + TIEMPO_MAX + "s");
        });

        Label lblVol = new Label("► VOLUMEN GENERAL: " + (int)(config.getVolumen() * 100) + "%");
        lblVol.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;");
        Slider slVol = new Slider(0, 100, config.getVolumen() * 100);
        slVol.valueProperty().addListener((o, ov, nv) -> {
            double v = nv.doubleValue() / 100.0;
            config.setVolumen(v); audio.setVolumen(v);
            lblVol.setText("► VOLUMEN GENERAL: " + nv.intValue() + "%");
        });

        Label lA = new Label("► AUDIO");    lA.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;-fx-font-weight:bold;");
        Label lT = new Label("► SISTEMA");  lT.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;-fx-font-weight:bold;");
        Label lD = new Label("► DIFICULTAD"); lD.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;-fx-font-weight:bold;");

        panel.getChildren().addAll(lA, lblVol, slVol, new Separator(), lT, chkR, new Separator(), lblT, sl, new Separator(), lD, boxDif);

        Button btnV = btnAncho("[ GUARDAR Y VOLVER ]", 300);
        btnV.setOnAction(e -> mostrarMenuPrincipal());

        root.getChildren().addAll(titulo, panel, btnV);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root); fadeIn(root, 300);
    }

    private void mostrarCreditos() {
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label("[ CRÉDITOS DEL SISTEMA ]"); titulo.setId("lblReto");

        VBox panel = new VBox(12); panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(600); panel.getStyleClass().add("cyber-panel");

        String[][] datos = {
                {"DESARROLLO",  "Brandon Capacho  &  Daniel Perlaza"},
                {"INSTITUCIÓN", "Universidad de Santander — UDES"},
                {"PROGRAMA",    "Ingeniería de Sistemas"},
                {"TECNOLOGÍA",  "Java 17 · JavaFX · H2 · Maven"},
                {"VERSIÓN",     "2.0 TACTICAL — Protocol Zero"},
        };
        for (String[] f : datos) {
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER);
            Label k = new Label(f[0] + " :"); k.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;-fx-min-width:140px;");
            Label v = new Label(f[1]); v.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:14px;-fx-font-weight:bold;");
            row.getChildren().addAll(k, v); panel.getChildren().add(row);
        }

        Button btnV = btnAncho("[ VOLVER AL MENÚ ]", 300);
        btnV.setOnAction(e -> mostrarMenuPrincipal());
        root.getChildren().addAll(titulo, panel, btnV);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root); fadeIn(root, 300);
    }

    // =========================================================================
    // INTRO E HISTORIA
    // =========================================================================

    private void mostrarIntroHistoria() {
        audio.playIntro();
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(50));

        Label lbl = new Label(":: INICIALIZANDO KERNEL ::"); lbl.setId("lblReto");

        TextArea txt = new TextArea();
        txt.setWrapText(true); txt.setEditable(false);
        txt.getStyleClass().add("text-area"); txt.setStyle("-fx-font-size:16px;");
        txt.setPrefHeight(310); txt.setMaxWidth(820);

        Button btnSkip = btn("[ SKIP ]");
        btnSkip.setStyle(btnSkip.getStyle() + "-fx-opacity:0.55;-fx-font-size:12px;");
        Button btnSig = btnAncho("[ CONECTAR AL SISTEMA >> ]", 370);
        btnSig.setVisible(false);
        btnSkip.setOnAction(e -> mostrarSelectorSlots());
        btnSig.setOnAction(e  -> mostrarSelectorSlots());

        HBox bots = new HBox(20, btnSkip, btnSig); bots.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lbl, txt, bots);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root);

        String historia = "AÑO 2088. LA RED GLOBAL 'THE HIVE' HA COLAPSADO.\n\n" +
                "Corporaciones rivales han llenado el ciberespacio de malware.\n" +
                "Eres un ARQUITECTO DE SISTEMAS RENEGADO. Tu arma: la lógica matemática.\n\n" +
                "Restaura los nodos, usa Habilidades tácticas y sobrevive a los Firewall físicos.\n" +
                "Estableciendo conexión cifrada... ████████ 100%\nACCESO CONCEDIDO.";
        escribirTextoAnimado(txt, historia, () -> { btnSkip.setVisible(false); btnSig.setVisible(true); });
    }

    // =========================================================================
    // SELECTOR DE SLOTS
    // =========================================================================

    private void mostrarSelectorSlots() {
        VBox root = new VBox(28); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label(":: SELECCIONE PERFIL DE OPERADOR ::"); titulo.setId("lblReto");

        HBox slots = new HBox(24); slots.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 3; i++) {
            final int slot = i;
            Usuario tmp = GestorDB.cargarUsuario(slot);
            if (tmp != null && tmp.getIntegridad() <= 0) { GestorDB.borrarUsuario(slot); tmp = null; }
            final Usuario guardado = tmp;
            VBox tarjeta = tarjetaSlot(slot, guardado);

            tarjeta.setOnMouseClicked(e -> {
                slotActual = slot;
                if (guardado != null) {
                    jugador = guardado;
                    audio.playInfiltrado();
                    mostrarMapaArbol();
                } else {
                    audio.playInfiltrado();
                    mostrarCreacionPerfil(slot);
                }
            });
            slots.getChildren().add(tarjeta);
        }

        Button btnV = btn("[ VOLVER AL MENÚ ]");
        btnV.setOnAction(e -> mostrarMenuPrincipal());
        root.getChildren().addAll(titulo, slots, btnV);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root); fadeIn(slots, 400);
    }

    // =========================================================================
    // CREACIÓN DE PERFIL
    // =========================================================================

    private void mostrarCreacionPerfil(int slot) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(40));

        Label titulo = new Label("◈  ESTABLECER NUEVO ENLACE  ◈");
        titulo.setStyle("-fx-font-size:26px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_CYAN + ";");
        titulo.setEffect(new DropShadow(15, Color.web(C_CYAN)));

        Label subtitulo = new Label("ASIGNACIÓN DE MEMORIA: SLOT 0" + slot);
        subtitulo.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:14px;-fx-font-family:'Consolas';");

        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(500);
        panel.setStyle("-fx-background-color:rgba(0,18,0,0.90);" +
                "-fx-border-color:" + C_VERDE + ";-fx-border-width:1.5px;" +
                "-fx-padding:30;-fx-background-radius:5;-fx-border-radius:5;");

        Label instruccion = new Label("INGRESE SU ALIAS DE OPERADOR:");
        instruccion.setStyle("-fx-text-fill:white;-fx-font-size:14px;-fx-font-family:'Consolas';");

        TextField txtAlias = new TextField();
        txtAlias.setPromptText("HACKER_0" + slot);
        txtAlias.setMaxWidth(350);
        txtAlias.setAlignment(Pos.CENTER);

        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);

        Button btnConectar = btnAncho("[ INICIALIZAR ]", 200);
        btnConectar.setDefaultButton(true);
        Button btnCancelar = btnAncho("[ CANCELAR ]", 200);

        botones.getChildren().addAll(btnCancelar, btnConectar);
        panel.getChildren().addAll(instruccion, txtAlias, botones);

        btnCancelar.setOnAction(e -> mostrarSelectorSlots());
        btnConectar.setOnAction(e -> {
            String alias = txtAlias.getText().trim().toUpperCase();
            jugador = new Usuario(alias.isEmpty() ? "HACKER_0" + slot : alias);
            if (jugador.getNombre().equals("ROOT") || jugador.getNombre().equals("ADMIN")) {
                jugador.activarGodMode();
                System.out.println("[!] ADVERTENCIA: Privilegios ROOT detectados. Red comprometida.");
            }
            GestorDB.guardarUsuario(jugador, slotActual);
            audio.playSuccess();
            mostrarMapaArbol();
        });

        root.getChildren().addAll(titulo, subtitulo, panel);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root);
        fadeIn(root, 300);
        Platform.runLater(txtAlias::requestFocus);
    }

    private VBox tarjetaSlot(int slot, Usuario u) {
        VBox c = new VBox(10); c.setAlignment(Pos.CENTER);
        c.setPrefSize(210, 185); c.setPadding(new Insets(18)); c.setCursor(Cursor.HAND);
        boolean vacio = (u == null);
        String borde = vacio ? "#224422" : C_VERDE;

        String estiloN = "-fx-background-color:rgba(0,14,0,0.88);-fx-border-color:" + borde +
                ";-fx-border-width:2px;-fx-border-radius:5;-fx-background-radius:5;";
        String estiloH = "-fx-background-color:rgba(0,35,0,0.92);-fx-border-color:" + C_CYAN +
                ";-fx-border-width:2px;-fx-border-radius:5;-fx-background-radius:5;";

        c.setStyle(estiloN); c.setEffect(new DropShadow(18, Color.web(borde)));
        c.setOnMouseEntered(e -> c.setStyle(estiloH)); c.setOnMouseExited(e -> c.setStyle(estiloN));

        Label lblSl = new Label("MEMORIA_0" + slot);
        lblSl.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:12px;-fx-font-weight:bold;");

        if (vacio) {
            c.getChildren().addAll(lblSl, estilo("[ VACÍA ]", 20, "#2a3a2a"), estilo("Nueva partida", 12, "#3a5a3a"));
        } else {
            ProgressBar pb = new ProgressBar(u.getIntegridad() / 100.0);
            pb.setPrefWidth(160); pb.setStyle("-fx-accent:" + (u.getIntegridad() > 50 ? C_VERDE : C_ROJO) + ";");
            int[] stats = GestorDB.obtenerEstadisticas(slot);
            String eficiencia = (stats[0] > 0) ? (stats[1] * 100 / stats[0]) + "%" : "0%";
            c.getChildren().addAll(lblSl, estilo(u.getNombre(), 16, "white"), pb,
                    estilo("HP: " + u.getIntegridad() + "%", 12, u.getIntegridad() > 50 ? C_VERDE : C_ROJO),
                    estilo("Nodos: " + u.getNivelesSuperados() + "/50", 12, C_CYAN),
                    estilo("Eficiencia: " + eficiencia, 12, C_MAGENTA),
                    estilo("BTC: " + u.getCriptos(), 12, C_AMARILLO)
            );
        }
        return c;
    }

    // =========================================================================
    // MAPA ÁRBOL — FIX FONDO MATRIX + TOPBAR COMPACTA + TEMA UI FUNCIONAL
    // =========================================================================

    private boolean isDesbloqueado(int nivel) {
        if (nivel == 1) return true;
        if (nivel == 2 || nivel == 12 || nivel == 22 || nivel == 32 || nivel == 42)
            return jugador.isNivelCompletado(1);
        return jugador.isNivelCompletado(nivel - 1);
    }

    private void mostrarMapaArbol() {
        // RAÍZ: StackPane con Canvas Matrix de fondo
        StackPane rootMapa = new StackPane();
        rootMapa.setStyle("-fx-background-color:" + C_FONDO + ";");
        Canvas fondoMapa = crearCanvasMatrix();

        // BorderPane TRANSPARENTE para ver el Matrix a través
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color:transparent;");

        // --- TOP BAR COMPACTA ---
        HBox topBar = new HBox(8);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPrefHeight(46);
        topBar.setPadding(new Insets(0, 12, 0, 12));
        topBar.setStyle(
                "-fx-background-color:rgba(0,12,0,0.92);" +
                        "-fx-border-color:" + C_VERDE + ";-fx-border-width:0 0 1px 0;"
        );

        // Logo con pulso animado
        Label lblPulso = new Label("◈");
        lblPulso.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:16px;");
        lblPulso.setEffect(new DropShadow(8, Color.web(C_VERDE)));
        FadeTransition pulso = new FadeTransition(Duration.millis(900), lblPulso);
        pulso.setFromValue(0.3); pulso.setToValue(1.0);
        pulso.setCycleCount(Animation.INDEFINITE); pulso.setAutoReverse(true); pulso.play();

        // Stats en una línea
        double hp = jugador.getIntegridad();
        String hpColor = hp > 60 ? C_VERDE : hp > 30 ? C_AMARILLO : C_ROJO;
        Label lblStats = new Label(
                " " + jugador.getNombre() +
                        "  HP:" + (int)hp + "%" +
                        "  BTC:" + jugador.getCriptos() +
                        "  " + jugador.getNivelesSuperados() + "/50"
        );
        lblStats.setStyle("-fx-text-fill:" + hpColor + ";-fx-font-size:11px;-fx-font-family:'Consolas';");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        // Música
        ComboBox<String> comboM = new ComboBox<>();
        comboM.setStyle("-fx-background-color:#001500;-fx-text-fill:" + C_VERDE +
                ";-fx-border-color:#224422;-fx-font-family:'Consolas';-fx-font-size:11px;");
        comboM.setPrefWidth(145);
        comboM.getItems().add("♪ SILENCIO");
        if (jugador.isPistaDesbloqueada(1)) comboM.getItems().add("♪ SYNTHWAVE");
        if (jugador.isPistaDesbloqueada(2)) comboM.getItems().add("♪ DARK ELECTRO");
        comboM.getSelectionModel().select(0);
        comboM.setOnAction(e -> {
            int sel = comboM.getSelectionModel().getSelectedIndex();
            if (sel == 0) audio.detenerBGM();
            else audio.reproducirBGM(sel);
        });

        // Tema UI — FIX: usa getTemaUI() no isPistaDesbloqueada()
        ComboBox<String> comboTema = new ComboBox<>();
        comboTema.setStyle("-fx-background-color:#001500;-fx-text-fill:" + C_VERDE +
                ";-fx-border-color:#224422;-fx-font-family:'Consolas';-fx-font-size:11px;");
        comboTema.setPrefWidth(115);
        comboTema.getItems().addAll("UI:NEON", "UI:ÁMBAR", "UI:CIAN");
        switch (jugador.getTemaUI()) {
            case "AMBAR": comboTema.getSelectionModel().select("UI:ÁMBAR"); break;
            case "AZUL":  comboTema.getSelectionModel().select("UI:CIAN");  break;
            default:      comboTema.getSelectionModel().select("UI:NEON");  break;
        }
        comboTema.setOnAction(e -> {
            String sel = comboTema.getValue();
            if (sel == null) return;
            if      (sel.contains("ÁMBAR")) jugador.setTemaUI("AMBAR");
            else if (sel.contains("CIAN"))  jugador.setTemaUI("AZUL");
            else                             jugador.setTemaUI("NEON");
            aplicarEfectoTema(rootMapa);
            GestorDB.guardarUsuario(jugador, slotActual);
        });

        // Separadores decorativos
        Label s1 = new Label("|"); s1.setStyle("-fx-text-fill:#224422;");
        Label s2 = new Label("|"); s2.setStyle("-fx-text-fill:#224422;");
        Label s3 = new Label("|"); s3.setStyle("-fx-text-fill:#224422;");

        // Botones de acción
        Button btnBoveda   = btnMini("▣ BÓVEDA",   C_CYAN);
        Button btnRegistro = btnMini("▤ REGISTRO", C_VERDE);
        Button btnTienda   = btnMini("◈ MERCADO",  C_AMARILLO);
        Button btnSalir    = btnMini("✕ SALIR",    "#885555");

        btnBoveda.setOnAction(e   -> mostrarBovedaDatos());
        btnRegistro.setOnAction(e -> mostrarEstadisticas());
        btnTienda.setOnAction(e   -> mostrarTienda());
        btnSalir.setOnAction(e    -> {
            if (mineriaTimeline != null) mineriaTimeline.stop();
            audio.detenerBGM();
            detenerAnimacionMatrix();
            mostrarSelectorSlots();
        });

        topBar.getChildren().addAll(lblPulso, lblStats, sp, comboM, s1, comboTema, s2);

        if (jugador.isNivelCompletado(50)) {
            Button btnDeepWeb = btnMini("☠ DEEP WEB", C_ROJO);
            btnDeepWeb.setOnAction(e -> iniciarDeepWeb(1));
            topBar.getChildren().add(btnDeepWeb);
        }

        topBar.getChildren().addAll(btnBoveda, btnRegistro, btnTienda, s3, btnSalir);
        layout.setTop(topBar);

        // --- LEYENDA INFERIOR ---
        HBox leyenda = new HBox(24); leyenda.setAlignment(Pos.CENTER);
        leyenda.setPadding(new Insets(7));
        leyenda.setStyle("-fx-background-color:rgba(0,8,0,0.90);-fx-border-color:#1a3a1a;-fx-border-width:1px 0 0 0;");
        for (Object[] rama : RAMAS) leyenda.getChildren().add(itemLeyenda((String)rama[3], (String)rama[2]));
        layout.setBottom(leyenda);

        // --- MAPA transparente para ver el fondo Matrix ---
        Pane mapaPane = new Pane();
        mapaPane.setStyle("-fx-background-color:transparent;");
        escenaPrincipal.widthProperty().addListener((o, ov, nv) -> dibujarMapaEn(mapaPane));
        Platform.runLater(() -> dibujarMapaEn(mapaPane));

        ScrollPane scroll = new ScrollPane(mapaPane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;");
        layout.setCenter(scroll);

        rootMapa.getChildren().addAll(fondoMapa, layout);
        aplicarEfectoTema(rootMapa);
        escenaPrincipal.setRoot(rootMapa);
        fadeIn(mapaPane, 400);
        iniciarMineriaPasiva();
    }

    // =========================================================================
    // BÓVEDA, ESTADÍSTICAS
    // =========================================================================

    private void mostrarBovedaDatos() {
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label("◈  BÓVEDA DE DATOS ENCRIPTADOS");
        titulo.setStyle("-fx-font-size:26px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_CYAN + ";");
        titulo.setEffect(new DropShadow(15, Color.web(C_CYAN)));

        VBox lista = new VBox(15); lista.setAlignment(Pos.CENTER);
        String[] loreTexts = {
                "REPORTE 2087: El núcleo de The Hive fue corrompido desde adentro. Sospechamos del Arquitecto Zero.",
                "CORREO INTERCEPTADO: Detengan a los renegados. Si restauran los nodos, perderemos el monopolio de los datos.",
                "LOG DE USUARIO: Mi integridad baja. Los ICE son muy fuertes. Dejo este paquete por si alguien me encuentra...",
                "DIRECTIVA OMEGA: El ataque DDoS es una distracción. Lo que realmente protegen es el NÚCLEO PRIMORDIAL.",
                "REGISTRO AUDIO: (Estática) ...no confíes en la tienda de hardware, nos rastrean a través de los mineros."
        };

        for (int i = 0; i < 5; i++) {
            VBox tarjeta = new VBox(8); tarjeta.setMaxWidth(650);
            tarjeta.setStyle("-fx-background-color:rgba(0,18,0,0.90);-fx-border-color:" + C_VERDE +
                    ";-fx-border-width:1.5px;-fx-padding:18;-fx-background-radius:5;-fx-border-radius:5;");
            if (jugador.isLoreDesbloqueado(i)) {
                Label lblTit = estilo("FRAGMENTO DE DATOS #" + (i+1), 15, C_AMARILLO);
                Label lblTex = estilo(loreTexts[i], 14, "#e0e0e0"); lblTex.setWrapText(true);
                tarjeta.getChildren().addAll(lblTit, lblTex);
            } else {
                Label lblB = estilo("▶ FRAGMENTO CORRUPTO — RECOLECTA MÁS DATOS EN LA RED", 14, "#5f8f5f");
                lblB.setStyle(lblB.getStyle() + "-fx-font-style:italic;");
                tarjeta.getChildren().add(lblB);
            }
            lista.getChildren().add(tarjeta);
        }

        Button btnV = btnAncho("[ VOLVER AL MAPA ]", 300); btnV.setOnAction(e -> mostrarMapaArbol());
        root.getChildren().addAll(titulo, lista, btnV);
        aplicarEfectoTema(root); escenaPrincipal.setRoot(root); fadeIn(root, 300);
    }

    private void mostrarEstadisticas() {
        VBox root = new VBox(25); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label("◈  REGISTRO DE OPERADOR");
        titulo.setStyle("-fx-font-size:26px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_CYAN + ";");
        titulo.setEffect(new DropShadow(15, Color.web(C_CYAN)));

        VBox panel = new VBox(15); panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("cyber-panel"); panel.setMaxWidth(500);

        int[] stats = GestorDB.obtenerEstadisticas(slotActual);
        double porc = stats[0] > 0 ? (double) stats[1] / stats[0] * 100 : 0;

        panel.getChildren().addAll(
                estilo("OPERADOR: " + jugador.getNombre(), 18, "white"), new Separator(),
                estilo("TOTAL DE HACKEOS INTENTADOS: " + stats[0], 15, C_CYAN),
                estilo("SECUENCIAS EXITOSAS: " + stats[1], 15, C_VERDE),
                estilo("FALLOS CRÍTICOS: " + stats[2], 15, C_ROJO), new Separator(),
                estilo("TASA DE EFICIENCIA: " + String.format("%.1f", porc) + "%", 18, C_MAGENTA)
        );

        Button btnV = btnAncho("[ VOLVER AL MAPA ]", 300); btnV.setOnAction(e -> mostrarMapaArbol());
        root.getChildren().addAll(titulo, panel, btnV);
        aplicarEfectoTema(root); escenaPrincipal.setRoot(root); fadeIn(root, 300);
    }

    // =========================================================================
    // DIBUJO DEL MAPA
    // =========================================================================

    private void dibujarMapaEn(Pane pane) {
        pane.getChildren().clear();
        final double MARGEN = 30, Y_RAIZ = 70, RAIZ_R = 32, NODO_R = 18, SEP_V = 62, CONECTOR_V = 55;
        double anchoTotal = Math.max(800, escenaPrincipal.getWidth() - MARGEN * 2);
        int    numRamas   = RAMAS.length;
        double colAncho   = anchoTotal / numRamas;
        double xRaiz = anchoTotal / 2.0, yRaiz = Y_RAIZ;

        dibujarNodoHex(pane, xRaiz, yRaiz, 1, RAIZ_R, true, C_VERDE, "NÚCLEO");
        double yPrimerNodo = yRaiz + RAIZ_R + CONECTOR_V;

        for (int r = 0; r < numRamas; r++) {
            int nivelIni = (int) RAMAS[r][0], nivelFin = (int) RAMAS[r][1];
            String colorRama = (String) RAMAS[r][3], etiqueta = (String) RAMAS[r][2];
            double colX = colAncho * r + colAncho / 2.0;

            boolean activo = isDesbloqueado(nivelIni);
            dibujarLinea(pane, xRaiz, yRaiz + RAIZ_R, colX, yPrimerNodo - NODO_R, activo ? colorRama : "#1c2a1c", activo);

            Label lblRama = new Label(etiqueta);
            lblRama.setStyle("-fx-text-fill:" + colorRama + ";-fx-font-size:10px;-fx-font-weight:bold;-fx-font-family:'Consolas';" +
                    "-fx-background-color:rgba(0,0,0,0.65);-fx-padding:2 6;-fx-background-radius:3;");
            lblRama.setLayoutX(colX - 36); lblRama.setLayoutY(yPrimerNodo + NODO_R + 3);
            pane.getChildren().add(lblRama);

            double yPrev = yPrimerNodo;
            for (int nivel = nivelIni; nivel <= nivelFin; nivel++) {
                double yNodo = yPrimerNodo + SEP_V * (nivel - nivelIni);
                if (nivel > nivelIni) {
                    boolean cab = isDesbloqueado(nivel);
                    dibujarLinea(pane, colX, yPrev + NODO_R, colX, yNodo - NODO_R, cab ? colorRama : "#1c2a1c", cab);
                }
                dibujarNodoHex(pane, colX, yNodo, nivel, NODO_R, isDesbloqueado(nivel), colorRama, null);
                yPrev = yNodo;
            }
        }

        Random rand = new Random();

        // TRAMPA HONEYPOT (15%)
        if (rand.nextInt(100) < 15) {
            double hx = anchoTotal * (0.2 + rand.nextDouble() * 0.6);
            double hy = yPrimerNodo + rand.nextDouble() * (SEP_V * 8);
            dibujarNodoHex(pane, hx, hy, 999, NODO_R + 5, true, C_AMARILLO, "HONEYPOT");
        }

        // OPERADORES ALIADOS EN PELIGRO (25%)
        if (rand.nextInt(100) < 25) {
            String[] aliados = {"JUAN ANGEL", "KALETH", "VALENTINA", "DOUGLAS"};
            String aliadoActivo = aliados[rand.nextInt(aliados.length)];
            double ax = anchoTotal * (0.1 + rand.nextDouble() * 0.8);
            double ay = yPrimerNodo + rand.nextDouble() * (SEP_V * 9);

            Polygon hexAliado = hexagono(NODO_R);
            hexAliado.setLayoutX(ax); hexAliado.setLayoutY(ay);
            hexAliado.setFill(Color.web("#002244"));
            hexAliado.setStroke(Color.web("#0088ff")); hexAliado.setStrokeWidth(2);

            Label lblSOS = new Label("SOS");
            lblSOS.setStyle("-fx-text-fill:#0088ff;-fx-font-size:10px;-fx-font-weight:bold;-fx-font-family:'Consolas';");

            StackPane stackAliado = new StackPane(hexAliado, lblSOS);
            stackAliado.setLayoutX(ax - NODO_R); stackAliado.setLayoutY(ay - NODO_R);
            stackAliado.setCursor(Cursor.HAND);

            Tooltip t = new Tooltip("SEÑAL DE AUXILIO: Operador " + aliadoActivo + " interceptado.\n► Clic para asistir");
            t.setStyle("-fx-background-color:#001100;-fx-text-fill:#0088ff;");
            Tooltip.install(stackAliado, t);
            stackAliado.setOnMouseClicked(e -> iniciarRescateAliado(aliadoActivo));

            FadeTransition ft = new FadeTransition(Duration.millis(800), stackAliado);
            ft.setFromValue(0.4); ft.setToValue(1.0); ft.setCycleCount(Animation.INDEFINITE); ft.setAutoReverse(true); ft.play();
            pane.getChildren().add(stackAliado);
        }
        pane.setPrefSize(anchoTotal, yPrimerNodo + SEP_V * 11 + 80);
    }

    // =========================================================================
    // MODOS DE EXPANSIÓN
    // =========================================================================

    private void iniciarDeepWeb(int oleada) {
        if (jugador.getIntegridad() <= 0) { mostrarMapaArbol(); return; }

        nivelActualJugando = 1000 + oleada;
        totalPreguntasActual = 1;
        preguntasPendientes  = 1;

        panelReto = new VBox(8); panelReto.setAlignment(Pos.CENTER);
        txtRespuesta = new TextField(); txtRespuesta.setAlignment(Pos.CENTER); txtRespuesta.setMaxWidth(400);
        btnHack = btnAncho("[ INYECTAR ]", 420);
        txtLog = new TextArea(); txtLog.setPrefHeight(100); txtLog.setEditable(false);

        String retoTexto = logica.generarRetoDeepWeb(oleada);
        panelReto.getChildren().add(construirVistaReto(retoTexto));

        lblHeader = new Label("DEEP WEB — OLEADA: " + oleada + "  |  RÉCORD: " + jugador.getDeepWebScore());
        lblHeader.setStyle("-fx-text-fill:" + C_ROJO + ";-fx-font-size:16px;-fx-font-weight:bold;-fx-font-family:'Consolas';");
        lblTimer = new Label();
        barraTiempo = new ProgressBar(1.0); barraTiempo.setPrefWidth(300); barraTiempo.setStyle("-fx-accent:" + C_ROJO + ";");

        HBox top = new HBox(20, lblHeader, barraTiempo, lblTimer);
        top.setAlignment(Pos.CENTER); top.getStyleClass().add("cyber-panel");
        VBox root = new VBox(20, top, panelReto, txtRespuesta, btnHack, txtLog);
        root.setAlignment(Pos.CENTER); root.getStyleClass().add("root"); root.setPadding(new Insets(30));

        btnHack.setOnAction(e -> {
            try {
                if (logica.verificar(Double.parseDouble(txtRespuesta.getText().trim()))) {
                    audio.playSuccess();
                    jugador.setDeepWebScore(oleada);
                    jugador.sumarCriptos(15);
                    GestorDB.guardarUsuario(jugador, slotActual);
                    if (timerAnimacion != null) timerAnimacion.stop();
                    iniciarDeepWeb(oleada + 1);
                } else {
                    audio.playError(); jugador.recibirDaño();
                    txtLog.appendText("[FATAL] ICE DETECTADO. -34% HP.\n");
                    if (jugador.getIntegridad() <= 0) procesarFallo("DEEP WEB PURGE");
                }
            } catch (Exception ex) {}
        });

        aplicarEfectoTema(root); escenaPrincipal.setRoot(root);
        TIEMPO_MAX = Math.max(5, 15 - (oleada / 3));
        iniciarTemporizador();
    }

    private void iniciarRescateAliado(String aliado) {
        if (timerAnimacion != null) timerAnimacion.stop();
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER); root.getStyleClass().add("root");

        Label lblTit = estilo("◈ CONEXIÓN INTERCEPTADA: " + aliado, 20, "#0088ff");
        Label lblDesc = estilo("El operador está bajo ataque de rastreo. Requiere desencriptación Hexadecimal manual inmediata.", 14, "white");

        int valorDecimal = new Random().nextInt(256);
        String valorHex = Integer.toHexString(valorDecimal).toUpperCase();
        if (valorHex.length() == 1) valorHex = "0" + valorHex;

        VBox reto = new VBox(10); reto.setAlignment(Pos.CENTER); reto.getStyleClass().add("cyber-panel");
        reto.getChildren().addAll(
                estilo("DECODIFICAR DIRECCIÓN MAC", 12, C_CYAN),
                estilo("[ " + valorHex + " ]", 36, C_AMARILLO),
                estilo("Traducción a Decimal (0 - 255):", 12, "white")
        );

        TextField txtHex = new TextField(); txtHex.setMaxWidth(200); txtHex.setAlignment(Pos.CENTER);
        Button btnHex = btnAncho("[ DECODIFICAR ]", 200);
        Label timerRescate = estilo("10s", 24, C_ROJO);

        root.getChildren().addAll(lblTit, lblDesc, reto, txtHex, btnHex, timerRescate);
        aplicarEfectoTema(root); escenaPrincipal.setRoot(root);

        final int[] t = {10};
        Timeline timeHex = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            t[0]--; timerRescate.setText(t[0] + "s");
            if (t[0] <= 0) { audio.playError(); pausar(1000, this::mostrarMapaArbol); }
        }));
        timeHex.setCycleCount(10); timeHex.play();

        final String hexFinal = valorHex;
        btnHex.setOnAction(e -> {
            try {
                if (Integer.parseInt(txtHex.getText().trim()) == valorDecimal) {
                    timeHex.stop(); audio.playSuccess();
                    jugador.sumarCriptos(250);
                    GestorDB.guardarUsuario(jugador, slotActual);
                    lblTit.setText("¡OPERADOR " + aliado + " SALVADO!");
                    pausar(2000, this::mostrarMapaArbol);
                } else { audio.playError(); }
            } catch (Exception ex) {}
        });
    }

    private void dibujarLinea(Pane pane, double x1, double y1, double x2, double y2, String color, boolean activo) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web(color)); l.setStrokeWidth(activo ? 2.2 : 1.2); l.setOpacity(activo ? 0.75 : 0.25);
        if (activo) l.setEffect(new DropShadow(5, Color.web(color)));
        pane.getChildren().add(0, l);
    }

    private void dibujarNodoHex(Pane pane, double cx, double cy, int nivel, double r, boolean desbloqueado, String colorStr, String etiqueta) {
        boolean hecho = jugador.isNivelCompletado(nivel);
        Color color = Color.web(colorStr);
        Polygon hex = hexagono(r); hex.setLayoutX(cx); hex.setLayoutY(cy);

        if (desbloqueado) {
            hex.setFill(hecho ? color.deriveColor(0, 1, 0.22, 0.92) : color.deriveColor(0, 0.18, 0.07, 0.88));
            hex.setStroke(color); hex.setStrokeWidth(r > 20 ? 3 : 2);
            hex.setEffect(new DropShadow(hecho ? 20 : 10, color));
        } else {
            hex.setFill(Color.web("#0a0a0a")); hex.setStroke(Color.web("#202020")); hex.setOpacity(0.3);
        }

        StackPane stack = new StackPane(hex);
        stack.setLayoutX(cx - r); stack.setLayoutY(cy - r); stack.setPrefSize(r * 2, r * 2);

        if (hecho) {
            Label check = new Label("✓");
            check.setStyle("-fx-text-fill:" + colorStr + ";-fx-font-size:" + (r > 25 ? 16 : 11) + "px;-fx-font-weight:bold;");
            stack.getChildren().add(check);
        } else if (etiqueta != null) {
            Label lbl = new Label(etiqueta);
            lbl.setStyle("-fx-text-fill:#ffffff;-fx-font-size:10px;-fx-font-weight:bold;-fx-font-family:'Consolas';");
            lbl.setTranslateY(1);
            stack.getChildren().add(lbl);
        }

        if (desbloqueado) {
            Tooltip tip = new Tooltip(logica.getNombreRama(nivel) + "\nNodo #" + nivel + (hecho ? "\n✓ COMPLETADO" : "\n► Clic para entrar"));
            tip.setStyle("-fx-background-color:#001100;-fx-text-fill:#00ff41;-fx-font-family:'Consolas';-fx-font-size:12px;");
            Tooltip.install(stack, tip);
            stack.setCursor(Cursor.HAND);
            final Color cFill = hecho ? color.deriveColor(0, 1, 0.22, 0.92) : color.deriveColor(0, 0.18, 0.07, 0.88);
            stack.setOnMouseEntered(ev -> hex.setFill(color.deriveColor(0, 1, 0.5, 0.9)));
            stack.setOnMouseExited(ev  -> hex.setFill(cFill));
            stack.setOnMouseClicked(ev -> mostrarBriefingMision(nivel));
        }
        pane.getChildren().add(stack);
    }

    private Polygon hexagono(double r) {
        Polygon h = new Polygon();
        for (int i = 0; i < 6; i++) {
            double ang = Math.toRadians(60 * i - 30);
            h.getPoints().addAll(Math.cos(ang) * r, Math.sin(ang) * r);
        }
        return h;
    }

    private HBox itemLeyenda(String color, String texto) {
        HBox item = new HBox(6); item.setAlignment(Pos.CENTER);
        Rectangle rect = new Rectangle(12, 12);
        rect.setFill(Color.web(color).deriveColor(0, 1, 0.2, 0.9));
        rect.setStroke(Color.web(color)); rect.setStrokeWidth(1.5);
        Label l = new Label(texto); l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:11px;-fx-font-family:'Consolas';");
        item.getChildren().addAll(rect, l); return item;
    }

    // =========================================================================
    // TIENDA
    // =========================================================================

    private void mostrarTienda() {
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label("◈  MERCADO NEGRO — DEEP WEB");
        titulo.setStyle("-fx-font-size:26px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_MAGENTA + ";");
        titulo.setEffect(new DropShadow(15, Color.web(C_MAGENTA)));

        HBox panelEst = new HBox(30); panelEst.setAlignment(Pos.CENTER); panelEst.getStyleClass().add("cyber-panel");
        double hp = jugador.getIntegridad();
        ProgressBar pbHP = new ProgressBar(hp / 100.0); pbHP.setPrefWidth(180);
        pbHP.setStyle("-fx-accent:" + (hp > 60 ? C_VERDE : hp > 30 ? C_AMARILLO : C_ROJO) + ";");
        panelEst.getChildren().addAll(estilo("BTC: " + jugador.getCriptos(), 18, C_AMARILLO), pbHP, estilo("HP: " + (int)hp + "%", 15, "white"));

        VBox lista = new VBox(12); lista.setAlignment(Pos.CENTER);
        for (int i = 0; i < tienda.getCatalogo().size(); i++) {
            ItemHardware it = tienda.getCatalogo().get(i);
            HBox fila = new HBox(20); fila.setAlignment(Pos.CENTER); fila.getStyleClass().add("cyber-panel"); fila.setMaxWidth(620);
            VBox info = new VBox(4);
            info.getChildren().addAll(estilo(it.getNombre(), 14, "white"), estilo("Costo: " + it.getPrecio() + " BTC", 13, C_AMARILLO));
            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
            int idx = i; boolean puede = jugador.getCriptos() >= it.getPrecio();
            Button bc = btn(puede ? "[ COMPRAR ]" : "[ SIN FONDOS ]"); bc.setDisable(!puede);
            bc.setOnAction(e -> {
                if (tienda.comprar(idx, jugador).contains("EXITOSA")) {
                    audio.playSuccess(); GestorDB.guardarUsuario(jugador, slotActual);
                } else audio.playError();
                mostrarTienda();
            });
            fila.getChildren().addAll(info, s, bc); lista.getChildren().add(fila);
        }

        Button btnV = btnAncho("[ VOLVER AL MAPA ]", 300); btnV.setOnAction(e -> mostrarMapaArbol());
        root.getChildren().addAll(titulo, panelEst, lista, btnV);
        aplicarEfectoTema(root); escenaPrincipal.setRoot(root); fadeIn(root, 300);
    }

    // =========================================================================
    // INFORME DE MISIÓN — FIX FONDO MATRIX + CENTRADO
    // =========================================================================

    private void mostrarBriefingMision(int nivel) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:" + C_FONDO + ";");
        Canvas fondo = crearCanvasMatrix();

        VBox panel = new VBox(16);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(740);
        panel.setPadding(new Insets(32, 40, 32, 40));
        panel.setStyle(
                "-fx-background-color:rgba(0,14,0,0.93);" +
                        "-fx-border-color:" + C_CYAN + ";-fx-border-width:2px;" +
                        "-fx-border-radius:6;-fx-background-radius:6;"
        );

        Label lTit = new Label("◈  INFORME DE MISIÓN");
        lTit.setStyle("-fx-font-size:22px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_CYAN + ";");
        lTit.setEffect(new DropShadow(14, Color.web(C_CYAN)));

        Label lRama = new Label(logica.getNombreRama(nivel) + "  —  NODO #" + nivel);
        lRama.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:14px;-fx-font-family:'Consolas';");

        Line sep = new Line(0, 0, 640, 0);
        sep.setStroke(Color.web(C_CYAN)); sep.setOpacity(0.3);

        TextArea txt = new TextArea();
        txt.setWrapText(true); txt.setEditable(false);
        txt.getStyleClass().add("text-area");
        txt.setStyle("-fx-font-size:14px;");
        txt.setPrefHeight(200); txt.setMaxWidth(680);

        int pregs = logica.getPreguntasPorNivel(nivel);
        HBox infoDif = new HBox(28); infoDif.setAlignment(Pos.CENTER);
        infoDif.getStyleClass().add("cyber-panel"); infoDif.setMaxWidth(520);
        infoDif.getChildren().addAll(
                estilo("SECUENCIAS: " + pregs, 13, C_CYAN),
                estilo("TIEMPO: " + (TIEMPO_MAX + jugador.getBonusTiempo()) + "s", 13, C_CYAN),
                estilo("PENALIZACIÓN: -34% HP", 13, C_ROJO)
        );

        Button btnI = btnAncho("[ EJECUTAR HACKEO ]", 300);
        btnI.setStyle(btnI.getStyle() + "-fx-font-size:15px;-fx-padding:11 0;");
        btnI.setVisible(false);
        btnI.setOnAction(e -> { detenerAnimacionMatrix(); mostrarDecisionNodo(nivel); });

        Button btnC = btn("[ CANCELAR ]");
        btnC.setOnAction(e -> { detenerAnimacionMatrix(); mostrarMapaArbol(); });

        HBox bots = new HBox(20, btnC, btnI); bots.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(lTit, lRama, sep, txt, infoDif, bots);

        VBox contenedor = new VBox();
        contenedor.setAlignment(Pos.CENTER);
        StackPane.setAlignment(contenedor, Pos.CENTER);
        contenedor.getChildren().add(panel);

        root.getChildren().addAll(fondo, contenedor);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root);
        fadeIn(panel, 300);
        escribirTextoAnimado(txt,
                logica.getDescripcionMision(nivel) + "\n\nDIFICULTAD: " + config.getDificultad(),
                () -> btnI.setVisible(true));
    }

    // =========================================================================
    // TERMINAL DE DECISIÓN — FIX FONDO MATRIX + CENTRADO + TEXTO DINÁMICO
    // =========================================================================

    private void mostrarDecisionNodo(int nivel) {

        final Object[][][] DECISIONES_POR_ZONA = {
                // ZONA 0 — NÚCLEO (nivel 1)
                {{
                        "El sistema central de NEXUS-7 tiene dos vectores de entrada.\nGhost te transmite: \"Elige bien. No hay vuelta atrás.\"",
                        "ACCESO DIRECTO", "TÚNEL CIFRADO",
                        ">> Ghost: \"Directo al núcleo. Sin anestesia. Espero que seas rápido.\"",
                        ">> Ghost: \"Bien. El túnel es más lento pero te da margen para pensar.\""
                }},
                // ZONA 1 — PHISHING (niveles 2–11)
                {{
                        "Interceptaste el correo del CEO de NEXUS-7.\nKaleth te dice: \"Tenemos dos maneras de explotar esto.\"",
                        "SUPLANTAR IDENTIDAD", "ANALIZAR METADATOS",
                        ">> Kaleth: \"Nos hacemos pasar por el jefe. Ellos nunca lo verán venir.\"",
                        ">> Kaleth: \"Los metadatos nunca mienten. Tómate tu tiempo.\""
                },{
                        "El servidor de correo tiene dos vulnerabilidades abiertas.\nValentina te advierte: \"Una es un cebo. La otra es real.\"",
                        "EXPLOIT AGRESIVO", "FOOTPRINTING PREVIO",
                        ">> Valentina: \"Sin miedo. A veces el ruido es la mejor cobertura.\"",
                        ">> Valentina: \"Reconocimiento primero. Así no caemos en la trampa.\""
                },{
                        "Detectaste un empleado de NEXUS usando la misma clave en 3 sistemas.\nDouglas dice: \"Podemos forzar la entrada o ingeniería social.\"",
                        "CREDENTIAL STUFFING", "PRETEXTING CORPORATIVO",
                        ">> Douglas: \"Simple. Reutilizaron credenciales. Su error, nuestra ganancia.\"",
                        ">> Douglas: \"Nos hacemos pasar por soporte técnico. Más elegante.\""
                }},
                // ZONA 2 — FIREWALL (niveles 12–21)
                {{
                        "El firewall perimetral de NEXUS tiene una regla mal configurada.\nGhost te dice: \"Puedo darte BTC extra o tiempo extra. Tú decides.\"",
                        "ATAQUE DE FRAGMENTACIÓN", "EVASIÓN POR TTL",
                        ">> Ghost: \"Fragmentamos los paquetes. El firewall no puede reconstruirlos a tiempo.\"",
                        ">> Ghost: \"Manipulamos el Time-To-Live. Pasamos desapercibidos.\""
                },{
                        "El IDS corporativo está monitoreando el tráfico entrante.\nKaleth susurra: \"Hay dos formas de cegarlo.\"",
                        "FLOOD DE FALSOS POSITIVOS", "TÚNEL DNS CIFRADO",
                        ">> Kaleth: \"Lo saturamos con ruido falso. Cuando se distrae, entramos.\"",
                        ">> Kaleth: \"Usamos DNS como canal encubierto. Lento pero silencioso.\""
                },{
                        "Encontraste una DMZ mal segmentada en la red de NEXUS.\nValentina dice: \"Podemos pivotar desde aquí hacia el interior.\"",
                        "PIVOTING AGRESIVO", "ENUMERACIÓN DE SERVICIOS",
                        ">> Valentina: \"Sin rodeos. Saltamos de la DMZ directo al servidor interno.\"",
                        ">> Valentina: \"Primero mapeamos qué puertos están abiertos. Más seguro.\""
                }},
                // ZONA 3 — CRIPTOGRAFÍA (niveles 22–31)
                {{
                        "NEXUS-7 usa un cifrado RSA con clave de 512 bits (obsoleto).\nDouglas dice: \"Es vulnerable. Hay dos ángulos de ataque.\"",
                        "FACTORIZACIÓN DE CLAVE", "ATAQUE CHOSEN-PLAINTEXT",
                        ">> Douglas: \"512 bits es papel mojado en 2087. Factorizamos el módulo.\"",
                        ">> Douglas: \"Mandamos texto controlado y analizamos el output cifrado.\""
                },{
                        "Interceptaste un handshake TLS entre dos servidores de NEXUS.\nGhost dice: \"La negociación de cifrado es débil.\"",
                        "DOWNGRADE ATTACK", "REPLAY ATTACK",
                        ">> Ghost: \"Los forzamos a negociar TLS 1.0. Allí tenemos ventaja.\"",
                        ">> Ghost: \"Capturamos el handshake y lo reproducimos. Clásico pero efectivo.\""
                },{
                        "El gestor de contraseñas de NEXUS usa MD5 para hashear.\nValentina dice: \"MD5 está muerto. Podemos crackearlo.\"",
                        "RAINBOW TABLE ATTACK", "DICCIONARIO PERSONALIZADO",
                        ">> Valentina: \"Tengo 40GB de tablas rainbow precalculadas. A buscar.\"",
                        ">> Valentina: \"Construimos un diccionario con el lenguaje corporativo de NEXUS.\""
                }},
                // ZONA 4 — SQL INJECTION (niveles 32–41)
                {{
                        "La base de datos de usuarios de NEXUS tiene inputs sin sanitizar.\nKaleth dice: \"El login es un colador. ¿Cómo entramos?\"",
                        "BLIND SQL INJECTION", "ERROR-BASED INJECTION",
                        ">> Kaleth: \"Inyección a ciegas. No vemos el output pero inferimos la estructura.\"",
                        ">> Kaleth: \"Forzamos errores deliberados. Los mensajes revelan el esquema.\""
                },{
                        "Encontraste un endpoint de búsqueda vulnerable en el panel admin.\nDouglas dice: \"Podemos exfiltrar datos de dos maneras.\"",
                        "UNION-BASED EXTRACTION", "TIME-BASED BLIND",
                        ">> Douglas: \"Usamos UNION SELECT para añadir filas a los resultados. Directo.\"",
                        ">> Douglas: \"Medimos los tiempos de respuesta. Cada milisegundo habla.\""
                },{
                        "El ORM de NEXUS construye queries dinámicas con inputs sin validar.\nValentina dice: \"Clásico punto de inyección de segundo orden.\"",
                        "SECOND-ORDER INJECTION", "STORED PROCEDURE ABUSE",
                        ">> Valentina: \"El dato malicioso se almacena primero, se ejecuta después. Sigiloso.\"",
                        ">> Valentina: \"Abusamos de los procedimientos almacenados. Acceso directo al motor.\""
                }},
                // ZONA 5 — DDoS OMEGA (niveles 42–50)
                {{
                        "El núcleo primordial de The Hive está bajo protección máxima.\nGhost dice en voz baja: \"Este es el final. No hay segunda oportunidad.\"",
                        "AMPLIFICACIÓN DNS", "SLOWLORIS PROTOCOL",
                        ">> Ghost: \"Amplificamos el tráfico x70 usando servidores DNS abiertos. Demoledor.\"",
                        ">> Ghost: \"Conexiones lentas e incompletas. El servidor colapsa esperando.\""
                },{
                        "Los nodos de defensa cuántica de NEXUS están activos al 100%.\nKaleth dice: \"Necesitamos saturarlos o sobrepasarlos.\"",
                        "BOTNET MASIVA", "ATAQUE PULSANTE",
                        ">> Kaleth: \"50.000 nodos zombie activados. Que intenten parar eso.\"",
                        ">> Kaleth: \"Ataques en ráfagas cortas e impredecibles. Difícil de mitigar.\""
                },{
                        "EREBUS, la IA de defensa de NEXUS, ha detectado tu presencia.\nDouglas dice: \"Tenemos 30 segundos antes de que cierre todos los puertos.\"",
                        "EXPLOIT DE DÍA CERO", "INGENIERÍA INVERSA DE EREBUS",
                        ">> Douglas: \"Vulnerabilidad que NEXUS no conoce. Si funciona, entramos limpio.\"",
                        ">> Douglas: \"Analizamos los patrones de EREBUS. Cada IA tiene un punto ciego.\""
                }}
        };

        // Selección de zona y decisión
        int zonaIndex;
        if      (nivel == 1)  zonaIndex = 0;
        else if (nivel <= 11) zonaIndex = 1;
        else if (nivel <= 21) zonaIndex = 2;
        else if (nivel <= 31) zonaIndex = 3;
        else if (nivel <= 41) zonaIndex = 4;
        else                  zonaIndex = 5;

        int decisionIndex;
        if      (nivel == 1)  decisionIndex = 0;
        else if (nivel <= 11) decisionIndex = (nivel - 2)  % DECISIONES_POR_ZONA[1].length;
        else if (nivel <= 21) decisionIndex = (nivel - 12) % DECISIONES_POR_ZONA[2].length;
        else if (nivel <= 31) decisionIndex = (nivel - 22) % DECISIONES_POR_ZONA[3].length;
        else if (nivel <= 41) decisionIndex = (nivel - 32) % DECISIONES_POR_ZONA[4].length;
        else                  decisionIndex = (nivel - 42) % DECISIONES_POR_ZONA[5].length;

        Object[] decision   = DECISIONES_POR_ZONA[zonaIndex][decisionIndex];
        String situacion    = (String) decision[0];
        String nombreA      = (String) decision[1];
        String nombreB      = (String) decision[2];
        String dialogoA     = (String) decision[3];
        String dialogoB     = (String) decision[4];

        final int bonusBTC      = 80 + (zonaIndex * 10);
        final int bonusSegundos = 6  + zonaIndex;

        // --- UI ---
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:" + C_FONDO + ";");
        Canvas fondo = crearCanvasMatrix();

        VBox panel = new VBox(16);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(700);
        panel.setPadding(new Insets(32, 40, 32, 40));
        panel.setStyle(
                "-fx-background-color:rgba(0,14,0,0.93);" +
                        "-fx-border-color:" + C_MAGENTA + ";-fx-border-width:2px;" +
                        "-fx-border-radius:6;-fx-background-radius:6;"
        );

        Label lblZona = new Label("◈  TERMINAL DE DECISIÓN  —  NODO #" + nivel);
        lblZona.setStyle("-fx-font-size:19px;-fx-font-weight:900;-fx-font-family:'Consolas';-fx-text-fill:" + C_MAGENTA + ";");
        lblZona.setEffect(new DropShadow(14, Color.web(C_MAGENTA)));

        Line sep = new Line(0, 0, 580, 0);
        sep.setStroke(Color.web(C_MAGENTA)); sep.setOpacity(0.4);

        Label lblSituacion = new Label(situacion);
        lblSituacion.setStyle("-fx-text-fill:#d0d0d0;-fx-font-size:13px;-fx-font-family:'Consolas';");
        lblSituacion.setWrapText(true); lblSituacion.setMaxWidth(630);

        Line sep2 = new Line(0, 0, 580, 0);
        sep2.setStroke(Color.web(C_CYAN)); sep2.setOpacity(0.2);

        // TARJETA A
        VBox cardA = new VBox(6); cardA.setMaxWidth(630); cardA.setCursor(Cursor.HAND);
        String estiloAN = "-fx-background-color:rgba(0,28,0,0.88);-fx-border-color:" + C_VERDE +
                ";-fx-border-width:1.5px;-fx-padding:14;-fx-background-radius:5;-fx-border-radius:5;";
        String estiloAH = "-fx-background-color:rgba(0,55,0,0.95);-fx-border-color:" + C_VERDE +
                ";-fx-border-width:2px;-fx-padding:14;-fx-background-radius:5;-fx-border-radius:5;";
        cardA.setStyle(estiloAN);

        Label lblTitA = new Label("[ A ]  " + nombreA + "   →  +" + bonusBTC + " BTC al completar");
        lblTitA.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:14px;-fx-font-family:'Consolas';-fx-font-weight:bold;");
        Label lblDialA = new Label(dialogoA);
        lblDialA.setStyle("-fx-text-fill:#888888;-fx-font-size:12px;-fx-font-family:'Consolas';-fx-font-style:italic;");
        lblDialA.setWrapText(true);
        cardA.getChildren().addAll(lblTitA, lblDialA);
        cardA.setOnMouseEntered(e -> { cardA.setStyle(estiloAH); lblDialA.setStyle(lblDialA.getStyle().replace("#888888","#cccccc")); });
        cardA.setOnMouseExited(e  -> { cardA.setStyle(estiloAN); lblDialA.setStyle(lblDialA.getStyle().replace("#cccccc","#888888")); });
        cardA.setOnMouseClicked(e -> {
            detenerAnimacionMatrix();
            bonusDecisionBTC = bonusBTC; bonusDecisionSegundos = 0; dialogoDecision = dialogoA;
            audio.playInfiltrado(); iniciarJuego(nivel);
        });

        // TARJETA B
        VBox cardB = new VBox(6); cardB.setMaxWidth(630); cardB.setCursor(Cursor.HAND);
        String estiloBN = "-fx-background-color:rgba(0,18,25,0.88);-fx-border-color:" + C_CYAN +
                ";-fx-border-width:1.5px;-fx-padding:14;-fx-background-radius:5;-fx-border-radius:5;";
        String estiloBH = "-fx-background-color:rgba(0,40,55,0.95);-fx-border-color:" + C_CYAN +
                ";-fx-border-width:2px;-fx-padding:14;-fx-background-radius:5;-fx-border-radius:5;";
        cardB.setStyle(estiloBN);

        Label lblTitB = new Label("[ B ]  " + nombreB + "   →  +" + bonusSegundos + "s por pregunta");
        lblTitB.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;-fx-font-family:'Consolas';-fx-font-weight:bold;");
        Label lblDialB = new Label(dialogoB);
        lblDialB.setStyle("-fx-text-fill:#888888;-fx-font-size:12px;-fx-font-family:'Consolas';-fx-font-style:italic;");
        lblDialB.setWrapText(true);
        cardB.getChildren().addAll(lblTitB, lblDialB);
        cardB.setOnMouseEntered(e -> { cardB.setStyle(estiloBH); lblDialB.setStyle(lblDialB.getStyle().replace("#888888","#cccccc")); });
        cardB.setOnMouseExited(e  -> { cardB.setStyle(estiloBN); lblDialB.setStyle(lblDialB.getStyle().replace("#cccccc","#888888")); });
        cardB.setOnMouseClicked(e -> {
            detenerAnimacionMatrix();
            bonusDecisionBTC = 0; bonusDecisionSegundos = bonusSegundos; dialogoDecision = dialogoB;
            audio.playInfiltrado(); iniciarJuego(nivel);
        });

        Button btnCancelar = btn("[ ABORTAR MISIÓN ]");
        btnCancelar.setOnAction(e -> { detenerAnimacionMatrix(); mostrarMapaArbol(); });

        panel.getChildren().addAll(lblZona, sep, lblSituacion, sep2, cardA, cardB, btnCancelar);

        VBox contenedor = new VBox();
        contenedor.setAlignment(Pos.CENTER);
        StackPane.setAlignment(contenedor, Pos.CENTER);
        contenedor.getChildren().add(panel);

        root.getChildren().addAll(fondo, contenedor);
        aplicarEfectoTema(root);
        escenaPrincipal.setRoot(root);
        fadeIn(panel, 350);
    }

    // =========================================================================
    // JUEGO
    // =========================================================================

    private void iniciarJuego(int nivel) {
        nivelActualJugando   = nivel;
        totalPreguntasActual = logica.getPreguntasPorNivel(nivel);
        preguntasPendientes  = totalPreguntasActual;
        alarmaSonando        = false;

        // Aplicar bonus de decisión al tiempo
        int tiempoEfectivo = TIEMPO_MAX + jugador.getBonusTiempo() + bonusDecisionSegundos;

        lblHeader = new Label(); lblTimer = new Label();
        barraTiempo = new ProgressBar(1.0);
        lblEnergia  = new Label("ENERGÍA:");
        lblEnergia.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:12px;");
        barraEnergia = new ProgressBar(jugador.getEnergia() / 100.0);
        barraEnergia.setPrefWidth(100); barraEnergia.setStyle("-fx-accent:" + C_CYAN + ";");

        txtLog      = new TextArea();
        txtRespuesta = new TextField();
        btnHack     = new Button("[ EJECUTAR CÓDIGO ]  ↵");
        panelReto   = new VBox(8); panelReto.setAlignment(Pos.CENTER);
        rootJuego   = new VBox(14); rootJuego.getStyleClass().add("root");
        rootJuego.setAlignment(Pos.TOP_CENTER); rootJuego.setPadding(new Insets(16));

        HBox hdr = new HBox(14); hdr.getStyleClass().add("cyber-panel");
        hdr.setAlignment(Pos.CENTER); hdr.setPadding(new Insets(10, 20, 10, 20));
        lblHeader.setStyle("-fx-font-size:13px;-fx-font-family:'Consolas';");
        lblTimer.setStyle("-fx-text-fill:" + C_ROJO + ";-fx-font-size:18px;-fx-font-weight:bold;");
        barraTiempo.setPrefWidth(200); actualizarHeader();

        Label lsep1 = new Label("|"); lsep1.setStyle("-fx-text-fill:#334433;");
        Label lsep2 = new Label("|"); lsep2.setStyle("-fx-text-fill:#334433;");
        hdr.getChildren().addAll(lblHeader, lsep1, lblEnergia, barraEnergia, lsep2, barraTiempo, lblTimer);

        btnOverclock  = btn("[ OVERCLOCK ] (50 EN)");
        btnBruteForce = btn("[ BRUTE FORCE ] (100 EN)");
        btnOverclock.setStyle(btnOverclock.getStyle() + "-fx-font-size:11px;-fx-text-fill:" + C_AMARILLO + ";");
        btnBruteForce.setStyle(btnBruteForce.getStyle() + "-fx-font-size:11px;-fx-text-fill:" + C_ROJO + ";");
        btnOverclock.setOnAction(e -> activarOverclock());
        btnBruteForce.setOnAction(e -> activarFuerzaBruta());
        actualizarBotonesHabilidad();
        HBox panelHabilidades = new HBox(15, btnOverclock, btnBruteForce); panelHabilidades.setAlignment(Pos.CENTER);

        VBox term = new VBox(10); term.getStyleClass().add("cyber-panel"); term.setAlignment(Pos.CENTER);
        Label ltt = new Label(">>> CONSOLA DE COMANDOS — NODO #" + nivel); ltt.getStyleClass().add("panel-title");
        txtLog.setEditable(false); txtLog.setPrefHeight(120);
        txtLog.setText("[SYSTEM] CONEXIÓN ESTABLECIDA — NODO " + nivel + "\n");

        // Mostrar diálogo del NPC elegido en la decisión
        if (!dialogoDecision.isEmpty()) {
            txtLog.appendText("[NPC] " + dialogoDecision + "\n");
        }

        term.getChildren().addAll(ltt, panelHabilidades, panelReto, txtLog);

        VBox inp = new VBox(12); inp.getStyleClass().add("cyber-panel"); inp.setAlignment(Pos.CENTER);
        txtRespuesta.setPromptText("_ INGRESE VALOR NUMÉRICO");
        txtRespuesta.setMaxWidth(400); txtRespuesta.setAlignment(Pos.CENTER);
        btnHack.getStyleClass().add("button-hack"); btnHack.setPrefWidth(420);
        btnHack.setStyle(btnHack.getStyle() + "-fx-font-size:15px;-fx-padding:11 0;");
        btnHack.setDefaultButton(true); btnHack.setOnAction(e -> verificarRespuesta());
        Label lInp = new Label("INGRESE RESULTADO:");
        lInp.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:12px;");
        inp.getChildren().addAll(lInp, txtRespuesta, btnHack);

        rootJuego.getChildren().addAll(hdr, term, inp);
        aplicarEfectoTema(rootJuego);
        escenaPrincipal.setRoot(rootJuego);
        iniciarMineriaPasiva();
        nuevoReto();
    }

    private void nuevoReto() {
        if (jugador.getIntegridad() <= 0) return;
        if (nivelActualJugando > 1 && new Random().nextInt(100) < 20) { iniciarBypassManual(); return; }
        int actual = (totalPreguntasActual - preguntasPendientes) + 1;
        String retoTexto = logica.generarReto(nivelActualJugando, actual, totalPreguntasActual);
        panelReto.getChildren().clear();
        VBox vista = construirVistaReto(retoTexto);
        panelReto.getChildren().add(vista);
        fadeIn(vista, 220);
        txtRespuesta.clear(); txtRespuesta.setDisable(false); txtRespuesta.requestFocus();
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (timerAnimacion != null) timerAnimacion.stop();
        tiempoRestante = TIEMPO_MAX + jugador.getBonusTiempo() + bonusDecisionSegundos;
        barraTiempo.setProgress(1.0); barraTiempo.setStyle("-fx-accent:" + C_VERDE + ";");
        rootJuego.getStyleClass().remove("alarma-roja"); alarmaSonando = false;

        timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            double prog = (double) tiempoRestante / (TIEMPO_MAX + jugador.getBonusTiempo() + bonusDecisionSegundos);
            lblTimer.setText(String.format("%02ds", tiempoRestante));
            barraTiempo.setProgress(prog);

            if (tiempoRestante > 5 && new Random().nextInt(100) < 5) generarEventoRed();

            if      (prog > 0.5)  barraTiempo.setStyle("-fx-accent:" + C_VERDE + ";");
            else if (prog > 0.25) barraTiempo.setStyle("-fx-accent:" + C_AMARILLO + ";");
            else {
                barraTiempo.setStyle("-fx-accent:" + C_ROJO + ";");
                if (!rootJuego.getStyleClass().contains("alarma-roja")) rootJuego.getStyleClass().add("alarma-roja");
                if (!alarmaSonando) { audio.playAmenaza(); alarmaSonando = true; }
            }
            if (tiempoRestante <= 0) procesarFallo("TIMEOUT");
        }));
        timerAnimacion.setCycleCount(Timeline.INDEFINITE); timerAnimacion.play();
    }

    private void verificarRespuesta() {
        String entrada = txtRespuesta.getText().trim(); if (entrada.isEmpty()) return;
        try {
            double res = Double.parseDouble(entrada);
            if (logica.verificar(res)) {
                audio.playSuccess(); preguntasPendientes--;
                jugador.cargarEnergia(25); actualizarBotonesHabilidad();
                txtLog.appendText("[OK] ✓ SECUENCIA VALIDADA. +25 ENERGÍA.\n");
                GestorDB.registrarHistorial(slotActual, nivelActualJugando, "EXITO", "CORRECTO");
                intentarDropLore();
                if (preguntasPendientes == 0) completarNodo();
                else { txtLog.appendText("[SYSTEM] " + preguntasPendientes + " secuencias restantes.\n"); nuevoReto(); }
            } else procesarFallo("HASH INVÁLIDO");
        } catch (NumberFormatException ex) { txtLog.appendText("[ERROR] ✗ Solo se aceptan números.\n"); audio.playError(); }
    }

    private void completarNodo() {
        if (timerAnimacion != null) timerAnimacion.stop();
        if (mineriaTimeline != null) mineriaTimeline.stop();
        // Aplicar bonus BTC de la decisión
        int recompensa = 100 + bonusDecisionBTC;
        jugador.sumarCriptos(recompensa);
        jugador.completarNivel(nivelActualJugando);
        GestorDB.guardarUsuario(jugador, slotActual);
        txtLog.appendText("[SUCCESS] ✓ NODO CAPTURADO. +" + recompensa + " BTC.\n");
        // Reset bonus
        bonusDecisionBTC = 0; bonusDecisionSegundos = 0; dialogoDecision = "";
        actualizarHeader();
        pausar(1800, this::mostrarMapaArbol);
    }

    private void procesarFallo(String motivo) {
        if (timerAnimacion != null) timerAnimacion.stop();
        audio.playError(); jugador.recibirDaño();
        GestorDB.registrarHistorial(slotActual, nivelActualJugando, "FALLO", motivo);
        txtLog.appendText("[FAIL] ✗ " + motivo + " — -34% HP.\n");
        GestorDB.guardarUsuario(jugador, slotActual); actualizarHeader();

        if (jugador.getIntegridad() <= 0) {
            if (mineriaTimeline != null) mineriaTimeline.stop();
            panelReto.getChildren().clear();
            Label lf = new Label("⚠  SYSTEM FAILURE  ⚠");
            lf.setStyle("-fx-text-fill:" + C_ROJO + ";-fx-font-size:30px;-fx-font-weight:bold;");
            lf.setEffect(new DropShadow(20, Color.web(C_ROJO)));
            panelReto.getChildren().add(lf);
            txtRespuesta.setDisable(true); btnHack.setDisable(true);
            txtLog.appendText("[CRITICAL] INTEGRIDAD 0% — PERFIL PURGADO.\n");
            GestorDB.borrarUsuario(slotActual);
            if (!rootJuego.getStyleClass().contains("alarma-roja")) rootJuego.getStyleClass().add("alarma-roja");
            pausar(3800, this::mostrarMenuPrincipal);
        } else {
            txtLog.appendText("[SYSTEM] HP: " + Math.max(0, jugador.getIntegridad()) + "%. Reintentando...\n");
            iniciarTemporizador();
        }
    }

    private void actualizarHeader() {
        if (lblHeader == null) return;
        double hp = jugador.getIntegridad();
        String c = hp > 60 ? C_VERDE : hp > 30 ? C_AMARILLO : C_ROJO;
        lblHeader.setText(estadoJugador() + "   NODO: " + nivelActualJugando);
        lblHeader.setStyle("-fx-font-size:13px;-fx-font-family:'Consolas';-fx-text-fill:" + c + ";");
    }

    private void actualizarBotonesHabilidad() {
        barraEnergia.setProgress(jugador.getEnergia() / 100.0);
        btnOverclock.setDisable(jugador.getEnergia() < 50);
        btnBruteForce.setDisable(jugador.getEnergia() < 100);
    }

    private void activarOverclock() {
        jugador.consumirEnergia(50); actualizarBotonesHabilidad();
        txtLog.appendText("\n[!] EJECUTANDO SCRIPT: OVERCLOCK\n>>> El temporizador fluye a la mitad de velocidad.\n");
        if (timerAnimacion != null) {
            timerAnimacion.stop();
            timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                tiempoRestante--;
                double prog = (double) tiempoRestante / (TIEMPO_MAX + jugador.getBonusTiempo() + bonusDecisionSegundos);
                lblTimer.setText(String.format("%02ds", tiempoRestante));
                barraTiempo.setProgress(prog);
                if (tiempoRestante <= 0) procesarFallo("TIMEOUT");
            }));
            timerAnimacion.setCycleCount(Timeline.INDEFINITE); timerAnimacion.play();
        }
    }

    private void activarFuerzaBruta() {
        jugador.consumirEnergia(100); actualizarBotonesHabilidad();
        txtLog.appendText("\n[!] ALERTA: FUERZA BRUTA APLICADA. SECUENCIA DESTRUIDA.\n");
        audio.playSuccess(); preguntasPendientes--;
        if (preguntasPendientes == 0) completarNodo(); else nuevoReto();
    }

    private void generarEventoRed() {
        int tipo = new Random().nextInt(2);
        if (tipo == 0) {
            Platform.runLater(() -> {
                txtLog.appendText("[WARN] ⚠ INFECCIÓN DETECTADA: Limpia tu consola.\n");
                txtRespuesta.setText(txtRespuesta.getText() + "0xERR");
                txtRespuesta.positionCaret(txtRespuesta.getText().length());
                audio.playAmenaza();
            });
        } else {
            Platform.runLater(() -> {
                txtLog.appendText("[WARN] ⚠ RASTREO CORPORATIVO: -3 Segundos.\n");
                tiempoRestante = Math.max(1, tiempoRestante - 3);
                audio.playAmenaza();
            });
        }
    }

    private void iniciarMineriaPasiva() {
        if (jugador != null && jugador.getMineros() > 0) {
            if (mineriaTimeline != null) mineriaTimeline.stop();
            mineriaTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
                int ganancia = jugador.getMineros() * 2;
                jugador.sumarCriptos(ganancia);
                if (txtLog != null) txtLog.appendText("[MINERO] + " + ganancia + " BTC generados.\n");
                actualizarHeader();
            }));
            mineriaTimeline.setCycleCount(Timeline.INDEFINITE); mineriaTimeline.play();
        }
    }

    private void intentarDropLore() {
        if (new Random().nextInt(100) < 15) {
            int fragmento = new Random().nextInt(5);
            if (!jugador.isLoreDesbloqueado(fragmento)) {
                jugador.desbloquearLore(fragmento);
                txtLog.appendText("\n[!] PAQUETE INTERCEPTADO: Fragmento de Datos #" + (fragmento+1) + " guardado en Bóveda.\n");
            }
        }
    }

    // =========================================================================
    // BYPASS MANUAL — FIX TEXTO DINÁMICO SEGÚN TEMA
    // =========================================================================

    private void iniciarBypassManual() {
        if (timerAnimacion != null) timerAnimacion.stop();
        panelReto.getChildren().clear(); txtRespuesta.setDisable(true); btnHack.setDisable(true);

        // FIX: color y nombre de zona según el tema activo
        String colorPrimario = "NEON";
        String nombreZona    = "ZONA VERDE";
        if (jugador != null) {
            switch (jugador.getTemaUI()) {
                case "AMBAR": colorPrimario = "#ffaa00"; nombreZona = "ZONA ÁMBAR"; break;
                case "AZUL":  colorPrimario = "#00ffff"; nombreZona = "ZONA CIAN";  break;
                default:      colorPrimario = C_VERDE;   nombreZona = "ZONA VERDE"; break;
            }
        }
        final String colorFinal = colorPrimario;

        Label lblAlerta = new Label(">>> FIREWALL FÍSICO DETECTADO <<<");
        lblAlerta.setStyle("-fx-text-fill:" + C_AMARILLO + ";-fx-font-size:18px;-fx-font-weight:bold;");
        Label lblInstruccion = new Label("SINCRONICE EL PULSO EN LA " + nombreZona + " PARA HACER BYPASS");
        lblInstruccion.setStyle("-fx-text-fill:white;-fx-font-size:13px;");
        ProgressBar escanner = new ProgressBar(0.0);
        escanner.setPrefWidth(400); escanner.setPrefHeight(25);
        escanner.setStyle("-fx-accent:" + colorFinal + "; -fx-control-inner-background:#001100;");
        Button btnInterceptar = btnAncho("[ INTERCEPTAR SEÑAL ]", 250);

        panelReto.getChildren().addAll(lblAlerta, lblInstruccion, escanner, btnInterceptar);
        txtLog.appendText("\n[!] INICIANDO BYPASS MANUAL. PREPARE SUS REFLEJOS.\n");

        animacionBypass = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            if (moviendoDerecha) { posicionBypass += 0.035; if (posicionBypass >= 1.0) moviendoDerecha = false; }
            else { posicionBypass -= 0.035; if (posicionBypass <= 0.0) moviendoDerecha = true; }
            escanner.setProgress(posicionBypass);
            boolean enZona = posicionBypass >= 0.42 && posicionBypass <= 0.58;
            escanner.setStyle("-fx-accent:" + (enZona ? colorFinal : "#003355") +
                    "; -fx-control-inner-background:#001100;");
        }));
        animacionBypass.setCycleCount(Timeline.INDEFINITE); animacionBypass.play();

        btnInterceptar.setOnAction(e -> {
            animacionBypass.stop(); btnInterceptar.setDisable(true);
            if (posicionBypass >= 0.42 && posicionBypass <= 0.58) {
                audio.playSuccess();
                txtLog.appendText("[OK] ✓ BYPASS FÍSICO EXITOSO. SECUENCIA SALTADA.\n");
                jugador.cargarEnergia(30); actualizarBotonesHabilidad(); intentarDropLore(); preguntasPendientes--;
                pausar(1500, () -> { txtRespuesta.setDisable(false); btnHack.setDisable(false); if (preguntasPendientes <= 0) completarNodo(); else nuevoReto(); });
            } else {
                audio.playError();
                txtLog.appendText("[FAIL] ✗ DESINCRONIZACIÓN. EL FIREWALL HA GOLPEADO.\n");
                procesarFallo("FALLO DE REFLEJOS");
                pausar(1500, () -> { if (jugador.getIntegridad() > 0) { txtRespuesta.setDisable(false); btnHack.setDisable(false); nuevoReto(); } });
            }
        });
    }

    // =========================================================================
    // RENDER DE RETOS
    // =========================================================================

    private VBox construirVistaReto(String reto) {
        VBox cont = new VBox(8); cont.setAlignment(Pos.CENTER);
        String[] partes = reto.split("\n>>> ", 2);
        Label lPre = new Label(partes[0]);
        lPre.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:13px;-fx-opacity:0.85;");
        cont.getChildren().add(lPre);
        String cuerpo = partes.length > 1 ? partes[1] : "";
        if (cuerpo.contains("d/dx") || cuerpo.contains("∫")) {
            String[] lin = cuerpo.split("\n", 2);
            Label lEtq = new Label(lin[0]);
            lEtq.setStyle("-fx-text-fill:" + C_AMARILLO + ";-fx-font-size:15px;-fx-font-weight:bold;");
            cont.getChildren().addAll(lEtq, renderizarNotacion(lin.length > 1 ? lin[1].trim() : cuerpo));
        } else {
            Label lExp = new Label(cuerpo); lExp.setId("lblReto"); lExp.setWrapText(true);
            lExp.setTextAlignment(TextAlignment.CENTER);
            cont.getChildren().add(lExp);
        }
        return cont;
    }

    private VBox renderizarNotacion(String expr) {
        VBox box = new VBox(6); box.setAlignment(Pos.CENTER); box.setPadding(new Insets(12, 24, 12, 24));
        box.setStyle("-fx-background-color:rgba(0,25,0,0.6);-fx-border-color:" + C_CYAN +
                ";-fx-border-width:1.5px;-fx-border-radius:5;-fx-background-radius:5;");
        box.setEffect(new DropShadow(16, Color.web(C_CYAN)));

        if (expr.contains("d/dx")) {
            String fun = (expr.contains("(") && expr.contains(")")) ? expr.substring(expr.indexOf("("), expr.lastIndexOf(")") + 1) : "";
            String eval = expr.contains("para x=") ? "evaluado en  " + expr.substring(expr.indexOf("para x=")) :
                    expr.contains("cuando x=") ? "evaluado en  " + expr.substring(expr.indexOf("cuando x=")) : "";
            HBox frac = new HBox(4); frac.setAlignment(Pos.CENTER);
            VBox df = new VBox(0); df.setAlignment(Pos.CENTER);
            df.getChildren().addAll(estilo("d", 22, "white"),
                    new Label("──") {{ setStyle("-fx-text-fill:white;-fx-font-size:14px;"); }},
                    estilo("dx", 18, "white"));
            frac.getChildren().addAll(df, estilo("  " + fun, 26, C_VERDE));
            box.getChildren().addAll(estilo("DERIVADA", 11, C_CYAN), frac);
            if (!eval.isEmpty()) box.getChildren().add(estilo(eval, 15, C_AMARILLO));
        } else if (expr.contains("∫")) {
            String limites = (expr.contains("[") && expr.contains("]")) ?
                    expr.substring(expr.indexOf("[") + 1, expr.indexOf("]")).trim() : "";
            String[] lp = limites.split(" a ");
            String limInf = lp.length > 0 ? lp[0].trim() : "0";
            String limSup = lp.length > 1 ? lp[1].trim() : "?";
            String intg = "";
            if (expr.contains("]") && expr.contains("dx")) {
                int a = expr.indexOf("]") + 1, b = expr.lastIndexOf("dx");
                if (b > a) intg = expr.substring(a, b).trim().replaceAll("[()]", "").trim();
            }
            HBox row = new HBox(4); row.setAlignment(Pos.CENTER);
            VBox simb = new VBox(0); simb.setAlignment(Pos.CENTER);
            simb.getChildren().addAll(estilo(limSup, 14, C_AMARILLO), estilo("∫", 52, "white"), estilo(limInf, 14, C_AMARILLO));
            row.getChildren().addAll(simb, estilo(" " + intg + " dx", 28, C_VERDE));
            box.getChildren().addAll(estilo("INTEGRAL DEFINIDA", 11, C_CYAN), row);
        }
        return box;
    }

    public static void main(String[] args) { launch(args); }
}