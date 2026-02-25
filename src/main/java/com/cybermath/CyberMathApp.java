package com.cybermath;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
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

    // =========================================================================
    // PALETA
    // =========================================================================
    private static final String C_VERDE    = "#00ff41";
    private static final String C_CYAN     = "#00ffff";
    private static final String C_MAGENTA  = "#ff00ff";
    private static final String C_ROJO     = "#ff003c";
    private static final String C_AMARILLO = "#ffff00";
    private static final String C_FONDO    = "#020a02";

    // =========================================================================
    // DEFINICIÓN DE RAMAS
    // { nivelInicio, nivelFin, etiqueta, colorHex }
    // =========================================================================
    private static final Object[][] RAMAS = {
            {  2, 11, "PHISHING",     "#ff6600" },
            { 12, 21, "FIREWALL",     "#00ccff" },
            { 22, 31, "CRIPTOGRAFÍA", "#cc00ff" },
            { 32, 41, "SQL INJECT",   "#ffcc00" },
            { 42, 50, "DDoS OMEGA",   "#ff0044" },
    };

    // =========================================================================
    // ATRIBUTOS
    // =========================================================================
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

    private Timeline timerAnimacion;
    private int      tiempoRestante;
    private int      TIEMPO_MAX          = 30;
    private int      preguntasPendientes;
    private int      totalPreguntasActual;
    private int      nivelActualJugando;
    private boolean  alarmaSonando       = false;

    // =========================================================================
    // ARRANQUE
    // =========================================================================

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
    public void stop() { GestorDB.cerrar(); }

    // =========================================================================
    // UTILIDADES
    // =========================================================================

    private void cargarCSS() {
        try {
            escenaPrincipal.getStylesheets().clear();
            escenaPrincipal.getStylesheets().add(
                    getClass().getResource("/estilos.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("[CSS] " + e.getMessage());
        }
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
    // MENÚ PRINCIPAL
    // =========================================================================

    private void mostrarMenuPrincipal() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:" + C_FONDO + ";");

        Canvas fondo = new Canvas();
        fondo.widthProperty().bind(escenaPrincipal.widthProperty());
        fondo.heightProperty().bind(escenaPrincipal.heightProperty());
        animarFondoMatrix(fondo);

        VBox panel = new VBox(16);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(460);
        panel.setPadding(new Insets(48, 56, 48, 56));
        panel.setStyle(
                "-fx-background-color:rgba(0,18,0,0.90);" +
                        "-fx-border-color:" + C_VERDE + ";" +
                        "-fx-border-width:2px;-fx-border-radius:6;-fx-background-radius:6;"
        );
        panel.setEffect(new DropShadow(50, Color.web(C_VERDE)));

        Label lblT = new Label("CYBERMATH");
        lblT.setStyle("-fx-font-size:52px;-fx-font-weight:900;-fx-font-family:'Consolas';" +
                "-fx-text-fill:" + C_VERDE + ";");
        Glow g = new Glow(0.9);
        DropShadow gds = new DropShadow(22, Color.web(C_VERDE)); gds.setInput(g);
        lblT.setEffect(gds);

        Label lblS = new Label(":: PROTOCOL ZERO ::");
        lblS.setStyle("-fx-font-size:15px;-fx-text-fill:" + C_CYAN + ";-fx-font-family:'Consolas';");
        Label lblV = new Label("v2.0  ·  UDES — Ingeniería de Sistemas");
        lblV.setStyle("-fx-font-size:11px;-fx-text-fill:#3a5a3a;-fx-font-family:'Consolas';");

        Line sep = new Line(0, 0, 360, 0);
        sep.setStroke(Color.web(C_VERDE)); sep.setOpacity(0.3);

        Button btnJugar  = btnAncho("[ INICIAR PROTOCOLO ]", 360);
        Button btnCargar = btnAncho("[ CARGAR PARTIDA ]",    360);
        Button btnConf   = btnAncho("[ CONFIGURACIÓN ]",     360);
        Button btnCred   = btnAncho("[ CRÉDITOS ]",          360);
        Button btnSalir  = btnAncho("[ DESCONECTAR ]",       360);

        btnJugar.setStyle(btnJugar.getStyle() + "-fx-font-size:17px;-fx-padding:13 0;");
        btnJugar.setOnAction(e  -> { audio.playInfiltrado(); mostrarIntroHistoria(); });
        btnCargar.setOnAction(e -> { audio.playInfiltrado(); mostrarSelectorSlots(); });
        btnConf.setOnAction(e   -> mostrarConfiguracion());
        btnCred.setOnAction(e   -> mostrarCreditos());
        btnSalir.setOnAction(e  -> ventana.close());

        Label lblSt = new Label("► SISTEMA ONLINE  |  NODOS DISPONIBLES: 50");
        lblSt.setStyle("-fx-text-fill:#1a3a1a;-fx-font-size:11px;-fx-font-family:'Consolas';");

        panel.getChildren().addAll(
                lblT, lblS, lblV, sep,
                btnJugar, btnCargar, btnConf, btnCred, btnSalir,
                lblSt
        );

        root.getChildren().addAll(fondo, panel);
        escenaPrincipal.setRoot(root);
        fadeIn(panel, 500);
    }

    private void animarFondoMatrix(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Random rand = new Random();
        String chars = "01アイウカ∫∑∂≠≈ABCDEF#@%";
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(55), e -> {
            double w = canvas.getWidth(), h = canvas.getHeight();
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
        tl.setCycleCount(Timeline.INDEFINITE); tl.play();
    }

    // =========================================================================
    // CONFIGURACIÓN
    // =========================================================================

    private void mostrarConfiguracion() {
        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(40));

        Label titulo = new Label("[ CONFIGURACIÓN DEL SISTEMA ]");
        titulo.setId("lblReto");

        VBox panel = new VBox(14);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setMaxWidth(500);
        panel.getStyleClass().add("cyber-panel");

        CheckBox chkS = new CheckBox("Efectos de sonido activados");
        chkS.setSelected(config.isSonidoActivado());
        chkS.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:13px;");
        chkS.setOnAction(e -> config.setSonidoActivado(chkS.isSelected()));

        CheckBox chkR = new CheckBox("Texto rápido (sin animación)");
        chkR.setSelected(config.isTextoRapido());
        chkR.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:13px;");
        chkR.setOnAction(e -> config.setTextoRapido(chkR.isSelected()));

        Label lblT = new Label("► TIEMPO POR PREGUNTA: " + TIEMPO_MAX + "s");
        lblT.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;");
        Slider sl = new Slider(15, 60, TIEMPO_MAX);
        sl.setMajorTickUnit(15); sl.setShowTickLabels(true);
        sl.setShowTickMarks(true); sl.setSnapToTicks(true);
        sl.valueProperty().addListener((o, ov, nv) -> {
            TIEMPO_MAX = nv.intValue();
            config.setTiempoMaximo(TIEMPO_MAX);
            lblT.setText("► TIEMPO POR PREGUNTA: " + TIEMPO_MAX + "s");
        });

        ToggleGroup tg = new ToggleGroup();
        HBox boxDif = new HBox(16);
        for (String d : new String[]{"NORMAL", "DIFÍCIL", "OMEGA"}) {
            RadioButton rb = new RadioButton(d);
            rb.setToggleGroup(tg);
            rb.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:13px;");
            if (d.equals(config.getDificultad())) rb.setSelected(true);
            rb.setOnAction(e -> config.setDificultad(d));
            boxDif.getChildren().add(rb);
        }

        Label lA = new Label("► AUDIO"); lA.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;");
        Label lT = new Label("► TEXTO"); lT.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;");
        Label lD = new Label("► DIFICULTAD"); lD.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;");

        panel.getChildren().addAll(lA, chkS, new Separator(), lT, chkR,
                new Separator(), lblT, sl, new Separator(), lD, boxDif);

        Button btnV = btnAncho("[ GUARDAR Y VOLVER ]", 300);
        btnV.setOnAction(e -> mostrarMenuPrincipal());

        root.getChildren().addAll(titulo, panel, btnV);
        escenaPrincipal.setRoot(root);
        fadeIn(root, 300);
    }

    // =========================================================================
    // CRÉDITOS
    // =========================================================================

    private void mostrarCreditos() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(40));

        Label titulo = new Label("[ CRÉDITOS DEL SISTEMA ]");
        titulo.setId("lblReto");

        VBox panel = new VBox(12);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(600);
        panel.getStyleClass().add("cyber-panel");

        String[][] datos = {
                {"DESARROLLO",  "Brandon Capacho  &  Daniel Perlaza"},
                {"INSTITUCIÓN", "Universidad de Santander — UDES"},
                {"PROGRAMA",    "Ingeniería de Sistemas"},
                {"TECNOLOGÍA",  "Java 17 · JavaFX · H2 · Maven"},
                {"VERSIÓN",     "2.0 — Protocol Zero"},
                {"TIPO",        "Videojuego Educativo — Lógica Matemática"},
        };
        for (String[] f : datos) {
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER);
            Label k = new Label(f[0] + " :");
            k.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:14px;-fx-min-width:140px;");
            Label v = new Label(f[1]);
            v.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:14px;-fx-font-weight:bold;");
            row.getChildren().addAll(k, v);
            panel.getChildren().add(row);
        }
        Label cita = new Label("\n\"La matemática es el lenguaje en que el universo está escrito.\"\n— Galileo Galilei");
        cita.setStyle("-fx-text-fill:#3a5a3a;-fx-font-size:13px;-fx-font-style:italic;");
        cita.setWrapText(true); cita.setTextAlignment(TextAlignment.CENTER);
        panel.getChildren().add(cita);

        Button btnV = btnAncho("[ VOLVER AL MENÚ ]", 300);
        btnV.setOnAction(e -> mostrarMenuPrincipal());
        root.getChildren().addAll(titulo, panel, btnV);
        escenaPrincipal.setRoot(root);
        fadeIn(root, 300);
    }

    // =========================================================================
    // INTRO + SELECTOR SLOTS
    // =========================================================================

    private void mostrarIntroHistoria() {
        audio.playIntro();
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(50));

        Label lbl = new Label(":: INICIALIZANDO KERNEL ::");
        lbl.setId("lblReto");

        TextArea txt = new TextArea();
        txt.setWrapText(true); txt.setEditable(false);
        txt.getStyleClass().add("text-area");
        txt.setStyle("-fx-font-size:16px;");
        txt.setPrefHeight(310); txt.setMaxWidth(820);

        Button btnSkip = btn("[ SKIP ]");
        btnSkip.setStyle(btnSkip.getStyle() + "-fx-opacity:0.55;-fx-font-size:12px;");
        Button btnSig = btnAncho("[ CONECTAR AL SISTEMA >> ]", 370);
        btnSig.setVisible(false);
        btnSkip.setOnAction(e -> mostrarSelectorSlots());
        btnSig.setOnAction(e  -> mostrarSelectorSlots());

        HBox bots = new HBox(20, btnSkip, btnSig); bots.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lbl, txt, bots);
        escenaPrincipal.setRoot(root);

        String historia =
                "AÑO 2088. LA RED GLOBAL 'THE HIVE' HA COLAPSADO.\n\n" +
                        "Corporaciones rivales han llenado el ciberespacio de malware,\n" +
                        "ransomware y trampas lógicas diseñadas para destruir cualquier\n" +
                        "intento de reconexión al sistema central.\n\n" +
                        "Eres un ARQUITECTO DE SISTEMAS RENEGADO.\n" +
                        "Tu única arma: la lógica matemática pura.\n\n" +
                        "Tu misión es restaurar los nodos corruptos resolviendo\n" +
                        "algoritmos bajo presión extrema. Cada error te cuesta integridad.\n\n" +
                        "⚠  ADVERTENCIA: Si la integridad llega a 0%, tu perfil\n" +
                        "    será PURGADO del servidor permanentemente.\n\n" +
                        "Estableciendo conexión cifrada... ████████ 100%\n" +
                        "ACCESO CONCEDIDO.";

        escribirTextoAnimado(txt, historia, () -> { btnSkip.setVisible(false); btnSig.setVisible(true); });
    }

    private void mostrarSelectorSlots() {
        VBox root = new VBox(28); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label(":: SELECCIONE PERFIL DE OPERADOR ::");
        titulo.setId("lblReto");
        Label sub = new Label("Elige una ranura de memoria");
        sub.setStyle("-fx-text-fill:#3a5a3a;-fx-font-size:13px;");

        HBox slots = new HBox(24); slots.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 3; i++) {
            final int slot = i;
            Usuario tmp = GestorDB.cargarUsuario(slot);
            if (tmp != null && tmp.getIntegridad() <= 0) { GestorDB.borrarUsuario(slot); tmp = null; }
            final Usuario guardado = tmp;
            VBox tarjeta = tarjetaSlot(slot, guardado);
            tarjeta.setOnMouseClicked(e -> {
                slotActual = slot;
                jugador = (guardado != null) ? guardado : new Usuario("HACKER_0" + slot);
                if (guardado == null) GestorDB.guardarUsuario(jugador, slotActual);
                audio.playInfiltrado();
                mostrarMapaArbol();
            });
            slots.getChildren().add(tarjeta);
        }

        Button btnV = btn("[ VOLVER AL MENÚ ]");
        btnV.setOnAction(e -> mostrarMenuPrincipal());
        root.getChildren().addAll(titulo, sub, slots, btnV);
        escenaPrincipal.setRoot(root);
        fadeIn(slots, 400);
    }

    private VBox tarjetaSlot(int slot, Usuario u) {
        VBox c = new VBox(10); c.setAlignment(Pos.CENTER);
        c.setPrefSize(210, 170); c.setPadding(new Insets(18)); c.setCursor(Cursor.HAND);
        boolean vacio = (u == null);
        String borde = vacio ? "#224422" : C_VERDE;
        String estiloN = "-fx-background-color:rgba(0,14,0,0.88);-fx-border-color:" + borde +
                ";-fx-border-width:2px;-fx-border-radius:5;-fx-background-radius:5;";
        c.setStyle(estiloN);
        c.setEffect(new DropShadow(18, Color.web(borde)));

        Label lblSl = new Label("MEMORIA_0" + slot);
        lblSl.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:12px;-fx-font-weight:bold;");

        if (vacio) {
            c.getChildren().addAll(lblSl,
                    estilo("[ VACÍA ]", 20, "#2a3a2a"),
                    estilo("Nueva partida", 12, "#3a5a3a"));
        } else {
            ProgressBar pb = new ProgressBar(u.getIntegridad() / 100.0);
            pb.setPrefWidth(160);
            String ca = u.getIntegridad() > 50 ? C_VERDE : C_ROJO;
            pb.setStyle("-fx-accent:" + ca + ";");
            c.getChildren().addAll(lblSl,
                    estilo(u.getNombre(),            16, "white"),
                    pb,
                    estilo("HP: "  + u.getIntegridad() + "%",  12, ca),
                    estilo("Nodos: "+ u.getNivelesSuperados()+"/50", 12, C_CYAN),
                    estilo("BTC: " + u.getCriptos(),  12, C_AMARILLO));
        }
        String estiloH = "-fx-background-color:rgba(0,35,0,0.92);-fx-border-color:" + C_CYAN +
                ";-fx-border-width:2px;-fx-border-radius:5;-fx-background-radius:5;";
        c.setOnMouseEntered(e -> c.setStyle(estiloH));
        c.setOnMouseExited(e  -> c.setStyle(estiloN));
        return c;
    }

    // =========================================================================
    // MAPA DE NODOS — ÁRBOL VERTICAL CENTRADO Y RESPONSIVO
    //
    //        [ NÚCLEO ]          ← nivel 1 centrado
    //       /  / | \  \
    //  [P] [F] [C] [S] [D]     ← primer nodo de cada rama
    //   |   |   |   |   |       ← nodos siguientes en columna vertical
    //  ...                       Etiquetas de rama debajo del 1er nodo
    // =========================================================================

    private boolean isDesbloqueado(int nivel) {
        if (nivel == 1) return true;
        if (nivel == 2 || nivel == 12 || nivel == 22 || nivel == 32 || nivel == 42)
            return jugador.isNivelCompletado(1);
        return jugador.isNivelCompletado(nivel - 1);
    }

    private void mostrarMapaArbol() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color:" + C_FONDO + ";");

        // ── Barra superior ──
        HBox topBar = new HBox(16); topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color:rgba(0,18,0,0.96);" +
                "-fx-border-color:" + C_VERDE + ";-fx-border-width:0 0 2px 0;");

        Label lblTit = new Label("◈  SISTEMA CENTRAL — MAPA DE NODOS");
        lblTit.setStyle("-fx-text-fill:" + C_VERDE + ";-fx-font-size:17px;" +
                "-fx-font-weight:bold;-fx-font-family:'Consolas';");
        lblTit.setEffect(new DropShadow(10, Color.web(C_VERDE)));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label lblEst = new Label(estadoJugador());
        lblEst.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:13px;-fx-font-family:'Consolas';");

        Button btnTienda = btn("[ MERCADO NEGRO ]");
        Button btnSalir  = btn("[ CERRAR SESIÓN ]");
        btnTienda.setOnAction(e -> mostrarTienda());
        btnSalir.setOnAction(e  -> mostrarSelectorSlots());
        topBar.getChildren().addAll(lblTit, sp, lblEst, btnTienda, btnSalir);
        layout.setTop(topBar);

        // ── Leyenda inferior ──
        HBox leyenda = new HBox(24); leyenda.setAlignment(Pos.CENTER);
        leyenda.setPadding(new Insets(8));
        leyenda.setStyle("-fx-background-color:rgba(0,10,0,0.85);");
        for (Object[] rama : RAMAS) {
            HBox it = itemLeyenda((String)rama[3], (String)rama[2]);
            leyenda.getChildren().add(it);
        }
        layout.setBottom(leyenda);

        // ── Pane del mapa — se redibuia al cambiar tamaño ──
        Pane mapaPane = new Pane();
        mapaPane.setStyle("-fx-background-color:" + C_FONDO + ";");

        // Redibujar en cada cambio de ancho de la escena
        escenaPrincipal.widthProperty().addListener((o, ov, nv) -> dibujarMapaEn(mapaPane));
        // Dibujo inicial diferido (el layout necesita un frame para medirse)
        Platform.runLater(() -> dibujarMapaEn(mapaPane));

        ScrollPane scroll = new ScrollPane(mapaPane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:" + C_FONDO + ";-fx-background-color:" + C_FONDO + ";");
        layout.setCenter(scroll);

        escenaPrincipal.setRoot(layout);
        fadeIn(mapaPane, 400);
    }

    /**
     * Dibuja todo el árbol en el Pane, calculando posiciones a partir del
     * ancho actual de la escena. Se llama cada vez que la ventana cambia de tamaño.
     *
     * Layout:
     *   - Nodo raíz centrado en (anchoTotal/2, Y_RAIZ)
     *   - 5 columnas equidistantes bajo el raíz
     *   - Dentro de cada columna: nodos apilados verticalmente
     *   - Etiqueta de la rama debajo del primer nodo de cada columna
     */
    private void dibujarMapaEn(Pane pane) {
        pane.getChildren().clear();

        final double MARGEN      = 30;
        final double Y_RAIZ      = 70;
        final double RAIZ_R      = 32;   // radio del nodo raíz
        final double NODO_R      = 18;   // radio nodos de rama
        final double SEP_V       = 62;   // separación vertical entre nodos
        final double CONECTOR_V  = 55;   // longitud del cable raíz → primer nodo

        double anchoTotal = Math.max(800, escenaPrincipal.getWidth() - MARGEN * 2);
        int    numRamas   = RAMAS.length;
        double colAncho   = anchoTotal / numRamas;

        // ── Nodo raíz ──
        double xRaiz = anchoTotal / 2.0;
        double yRaiz = Y_RAIZ;
        dibujarNodoHex(pane, xRaiz, yRaiz, 1, RAIZ_R, true, C_VERDE, "NÚCLEO");

        double yPrimerNodo = yRaiz + RAIZ_R + CONECTOR_V;

        for (int r = 0; r < numRamas; r++) {
            int    nivelIni  = (int)    RAMAS[r][0];
            int    nivelFin  = (int)    RAMAS[r][1];
            String colorRama = (String) RAMAS[r][3];
            String etiqueta  = (String) RAMAS[r][2];

            double colX = colAncho * r + colAncho / 2.0;

            // Cable del raíz al primer nodo de la rama
            boolean activo = isDesbloqueado(nivelIni);
            dibujarLinea(pane, xRaiz, yRaiz + RAIZ_R,
                    colX, yPrimerNodo - NODO_R,
                    activo ? colorRama : "#1c2a1c", activo);

            // Etiqueta de la rama (debajo del primer nodo)
            Label lblRama = new Label(etiqueta);
            lblRama.setStyle(
                    "-fx-text-fill:" + colorRama + ";-fx-font-size:10px;" +
                            "-fx-font-weight:bold;-fx-font-family:'Consolas';" +
                            "-fx-background-color:rgba(0,0,0,0.65);" +
                            "-fx-padding:2 6;-fx-background-radius:3;"
            );
            // Centrar la etiqueta bajo el primer nodo
            lblRama.setLayoutX(colX - 36);
            lblRama.setLayoutY(yPrimerNodo + NODO_R + 3);
            pane.getChildren().add(lblRama);

            // Nodos de la rama en columna vertical
            double yPrev = yPrimerNodo;
            for (int nivel = nivelIni; nivel <= nivelFin; nivel++) {
                double yNodo = yPrimerNodo + SEP_V * (nivel - nivelIni);
                // Cable entre nodos consecutivos
                if (nivel > nivelIni) {
                    boolean cab = isDesbloqueado(nivel);
                    dibujarLinea(pane, colX, yPrev + NODO_R,
                            colX, yNodo - NODO_R,
                            cab ? colorRama : "#1c2a1c", cab);
                }
                dibujarNodoHex(pane, colX, yNodo, nivel, NODO_R, isDesbloqueado(nivel), colorRama, null);
                yPrev = yNodo;
            }
        }

        // Ajustar alto del Pane para scroll
        double altoTotal = yPrimerNodo + SEP_V * 11 + 80;
        pane.setPrefSize(anchoTotal, altoTotal);
    }

    private void dibujarLinea(Pane pane, double x1, double y1,
                              double x2, double y2, String color, boolean activo) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web(color));
        l.setStrokeWidth(activo ? 2.2 : 1.2);
        l.setOpacity(activo ? 0.75 : 0.25);
        if (activo) l.setEffect(new DropShadow(5, Color.web(color)));
        pane.getChildren().add(0, l);
    }

    /**
     * Dibuja un nodo hexagonal en (cx, cy) con radio r.
     * Si etiqueta != null la muestra dentro (solo para el nodo raíz).
     * Si está completado muestra ✓; si no, no muestra número.
     */
    private void dibujarNodoHex(Pane pane, double cx, double cy, int nivel,
                                double r, boolean desbloqueado,
                                String colorStr, String etiqueta) {
        boolean hecho = jugador.isNivelCompletado(nivel);
        Color   color = Color.web(colorStr);

        Polygon hex = hexagono(r);
        hex.setLayoutX(cx); hex.setLayoutY(cy);

        if (desbloqueado) {
            hex.setFill(hecho
                    ? color.deriveColor(0, 1, 0.22, 0.92)
                    : color.deriveColor(0, 0.18, 0.07, 0.88));
            hex.setStroke(color);
            hex.setStrokeWidth(r > 20 ? 3 : 2);
            hex.setEffect(new DropShadow(hecho ? 20 : 10, color));
        } else {
            hex.setFill(Color.web("#0a0a0a"));
            hex.setStroke(Color.web("#202020"));
            hex.setOpacity(0.3);
        }

        StackPane stack = new StackPane(hex);
        stack.setLayoutX(cx - r);
        stack.setLayoutY(cy - r);
        stack.setPrefSize(r * 2, r * 2);

        if (hecho) {
            Label check = new Label("✓");
            check.setStyle("-fx-text-fill:" + colorStr + ";-fx-font-size:" +
                    (r > 25 ? 16 : 11) + "px;-fx-font-weight:bold;");
            stack.getChildren().add(check);
        } else if (etiqueta != null) {
            // Solo el raíz muestra texto
            Label lbl = new Label(etiqueta);
            lbl.setStyle("-fx-text-fill:white;-fx-font-size:9px;" +
                    "-fx-font-weight:bold;-fx-font-family:'Consolas';");
            lbl.setWrapText(true); lbl.setTextAlignment(TextAlignment.CENTER);
            lbl.setMaxWidth(r * 1.7);
            stack.getChildren().add(lbl);
        }

        if (desbloqueado) {
            String tipTxt = logica.getNombreRama(nivel) + "\nNodo #" + nivel +
                    (hecho ? "\n✓ COMPLETADO" : "\n► Clic para entrar");
            Tooltip tip = new Tooltip(tipTxt);
            tip.setStyle("-fx-background-color:#001100;-fx-text-fill:#00ff41;" +
                    "-fx-font-family:'Consolas';-fx-font-size:12px;");
            Tooltip.install(stack, tip);

            stack.setCursor(Cursor.HAND);
            final Color cFill = hecho
                    ? color.deriveColor(0, 1, 0.22, 0.92)
                    : color.deriveColor(0, 0.18, 0.07, 0.88);
            stack.setOnMouseEntered(ev -> hex.setFill(color.deriveColor(0, 1, 0.5, 0.9)));
            stack.setOnMouseExited(ev  -> hex.setFill(cFill));
            stack.setOnMouseClicked(ev -> mostrarBriefingMision(nivel));
        }

        pane.getChildren().add(stack);
    }

    /** Hexágono regular centrado en (0,0) con radio r. */
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
        Rectangle r = new Rectangle(12, 12);
        r.setFill(Color.web(color).deriveColor(0, 1, 0.2, 0.9));
        r.setStroke(Color.web(color)); r.setStrokeWidth(1.5);
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:11px;-fx-font-family:'Consolas';");
        item.getChildren().addAll(r, l); return item;
    }

    // =========================================================================
    // TIENDA
    // =========================================================================

    private void mostrarTienda() {
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label titulo = new Label("◈  MERCADO NEGRO — DEEP WEB");
        titulo.setStyle("-fx-font-size:26px;-fx-font-weight:900;" +
                "-fx-font-family:'Consolas';-fx-text-fill:" + C_MAGENTA + ";");
        titulo.setEffect(new DropShadow(15, Color.web(C_MAGENTA)));

        HBox panelEst = new HBox(30); panelEst.setAlignment(Pos.CENTER);
        panelEst.getStyleClass().add("cyber-panel");
        double hp = jugador.getIntegridad();
        String ca = hp > 60 ? C_VERDE : hp > 30 ? C_AMARILLO : C_ROJO;
        ProgressBar pbHP = new ProgressBar(hp / 100.0);
        pbHP.setPrefWidth(180); pbHP.setStyle("-fx-accent:" + ca + ";");
        panelEst.getChildren().addAll(
                estilo("BTC: " + jugador.getCriptos(), 18, C_AMARILLO),
                pbHP,
                estilo("HP: " + (int)hp + "%", 15, "white")
        );

        VBox lista = new VBox(12); lista.setAlignment(Pos.CENTER);
        for (int i = 0; i < tienda.getCatalogo().size(); i++) {
            ItemHardware it = tienda.getCatalogo().get(i);
            HBox fila = new HBox(20); fila.setAlignment(Pos.CENTER);
            fila.getStyleClass().add("cyber-panel"); fila.setMaxWidth(620);
            VBox info = new VBox(4);
            info.getChildren().addAll(
                    estilo(it.getNombre(), 14, "white"),
                    estilo("Costo: " + it.getPrecio() + " BTC", 13, C_AMARILLO)
            );
            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
            int idx = i; boolean puede = jugador.getCriptos() >= it.getPrecio();
            Button bc = btn(puede ? "[ COMPRAR ]" : "[ SIN FONDOS ]");
            bc.setDisable(!puede);
            bc.setOnAction(e -> {
                if (tienda.comprar(idx, jugador).contains("EXITOSA")) {
                    audio.playSuccess(); GestorDB.guardarUsuario(jugador, slotActual);
                } else audio.playError();
                mostrarTienda();
            });
            fila.getChildren().addAll(info, s, bc);
            lista.getChildren().add(fila);
        }

        Button btnV = btnAncho("[ VOLVER AL MAPA ]", 300);
        btnV.setOnAction(e -> mostrarMapaArbol());
        root.getChildren().addAll(titulo, panelEst, lista, btnV);
        escenaPrincipal.setRoot(root);
        fadeIn(root, 300);
    }

    // =========================================================================
    // BRIEFING
    // =========================================================================

    private void mostrarBriefingMision(int nivel) {
        VBox root = new VBox(18); root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root"); root.setPadding(new Insets(40));

        Label lTit = new Label("◈  INFORME DE MISIÓN"); lTit.setId("lblReto");
        Label lRama = new Label(logica.getNombreRama(nivel) + "  —  NODO #" + nivel);
        lRama.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:15px;-fx-font-family:'Consolas';");

        TextArea txt = new TextArea();
        txt.setWrapText(true); txt.setEditable(false);
        txt.getStyleClass().add("text-area");
        txt.setStyle("-fx-font-size:15px;");
        txt.setPrefHeight(250); txt.setMaxWidth(720);

        int pregs = logica.getPreguntasPorNivel(nivel);
        HBox infoDif = new HBox(28); infoDif.setAlignment(Pos.CENTER);
        infoDif.getStyleClass().add("cyber-panel"); infoDif.setMaxWidth(500);
        infoDif.getChildren().addAll(
                estilo("SECUENCIAS: " + pregs,  13, C_CYAN),
                estilo("TIEMPO: " + TIEMPO_MAX + "s", 13, C_CYAN),
                estilo("PENALIZACIÓN: -34% HP",  13, C_ROJO)
        );

        Button btnI = btnAncho("[ EJECUTAR HACKEO ]", 300);
        btnI.setStyle(btnI.getStyle() + "-fx-font-size:15px;-fx-padding:11 0;");
        btnI.setVisible(false);
        btnI.setOnAction(e -> iniciarJuego(nivel));
        Button btnC = btn("[ CANCELAR ]");
        btnC.setOnAction(e -> mostrarMapaArbol());
        HBox bots = new HBox(20, btnC, btnI); bots.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lTit, lRama, txt, infoDif, bots);
        escenaPrincipal.setRoot(root);
        fadeIn(root, 300);

        escribirTextoAnimado(txt,
                logica.getDescripcionMision(nivel) +
                        "\n\nSECUENCIAS A RESOLVER: " + pregs +
                        "\nDIFICULTAD: " + config.getDificultad(),
                () -> btnI.setVisible(true));
    }

    // =========================================================================
    // JUEGO ACTIVO
    // =========================================================================

    private void iniciarJuego(int nivel) {
        nivelActualJugando   = nivel;
        totalPreguntasActual = logica.getPreguntasPorNivel(nivel);
        preguntasPendientes  = totalPreguntasActual;
        alarmaSonando        = false;

        lblHeader    = new Label();
        lblTimer     = new Label();
        barraTiempo  = new ProgressBar(1.0);
        txtLog       = new TextArea();
        txtRespuesta = new TextField();
        btnHack      = new Button("[ EJECUTAR CÓDIGO ]  ↵");
        panelReto    = new VBox(8); panelReto.setAlignment(Pos.CENTER);
        rootJuego    = new VBox(14);
        rootJuego.getStyleClass().add("root");
        rootJuego.setAlignment(Pos.TOP_CENTER);
        rootJuego.setPadding(new Insets(16));

        HBox hdr = new HBox(14); hdr.getStyleClass().add("cyber-panel");
        hdr.setAlignment(Pos.CENTER); hdr.setPadding(new Insets(10, 20, 10, 20));
        lblHeader.setStyle("-fx-font-size:13px;-fx-font-family:'Consolas';");
        lblTimer.setStyle("-fx-text-fill:" + C_ROJO + ";-fx-font-size:18px;-fx-font-weight:bold;");
        barraTiempo.setPrefWidth(260); actualizarHeader();
        Label lsep = new Label("|"); lsep.setStyle("-fx-text-fill:#334433;");
        hdr.getChildren().addAll(lblHeader, lsep, barraTiempo, lblTimer);

        VBox term = new VBox(10); term.getStyleClass().add("cyber-panel");
        term.setAlignment(Pos.CENTER);
        Label ltt = new Label(">>> CONSOLA DE COMANDOS — NODO #" + nivel);
        ltt.getStyleClass().add("panel-title");
        txtLog.setEditable(false); txtLog.setPrefHeight(120);
        txtLog.setText("[SYSTEM] CONEXIÓN ESTABLECIDA — NODO " + nivel + "\n");
        term.getChildren().addAll(ltt, panelReto, txtLog);

        VBox inp = new VBox(12); inp.getStyleClass().add("cyber-panel");
        inp.setAlignment(Pos.CENTER);
        txtRespuesta.setPromptText("_ INGRESE VALOR NUMÉRICO");
        txtRespuesta.setMaxWidth(400); txtRespuesta.setAlignment(Pos.CENTER);
        btnHack.getStyleClass().add("button-hack");
        btnHack.setPrefWidth(420);
        btnHack.setStyle(btnHack.getStyle() + "-fx-font-size:15px;-fx-padding:11 0;");
        btnHack.setDefaultButton(true);
        btnHack.setOnAction(e -> verificarRespuesta());
        Label lInp = new Label("INGRESE RESULTADO:");
        lInp.setStyle("-fx-text-fill:" + C_CYAN + ";-fx-font-size:12px;");
        inp.getChildren().addAll(lInp, txtRespuesta, btnHack);

        rootJuego.getChildren().addAll(hdr, term, inp);
        escenaPrincipal.setRoot(rootJuego);
        nuevoReto();
    }

    private void nuevoReto() {
        if (jugador.getIntegridad() <= 0) return;
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
        tiempoRestante = TIEMPO_MAX;
        barraTiempo.setProgress(1.0);
        barraTiempo.setStyle("-fx-accent:" + C_VERDE + ";");
        rootJuego.getStyleClass().remove("alarma-roja");
        alarmaSonando = false;

        timerAnimacion = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            double prog = (double) tiempoRestante / TIEMPO_MAX;
            lblTimer.setText(String.format("%02ds", tiempoRestante));
            barraTiempo.setProgress(prog);
            if      (prog > 0.5)  barraTiempo.setStyle("-fx-accent:" + C_VERDE + ";");
            else if (prog > 0.25) barraTiempo.setStyle("-fx-accent:" + C_AMARILLO + ";");
            else {
                barraTiempo.setStyle("-fx-accent:" + C_ROJO + ";");
                if (!rootJuego.getStyleClass().contains("alarma-roja"))
                    rootJuego.getStyleClass().add("alarma-roja");
                if (!alarmaSonando) { audio.playAmenaza(); alarmaSonando = true; }
            }
            if (tiempoRestante <= 0) procesarFallo("TIMEOUT");
        }));
        timerAnimacion.setCycleCount(Timeline.INDEFINITE); timerAnimacion.play();
    }

    private void verificarRespuesta() {
        String entrada = txtRespuesta.getText().trim();
        if (entrada.isEmpty()) return;
        try {
            double res = Double.parseDouble(entrada);
            if (logica.verificar(res)) {
                audio.playSuccess(); preguntasPendientes--;
                txtLog.appendText("[OK] ✓ SECUENCIA VALIDADA.\n");
                GestorDB.registrarHistorial(slotActual, nivelActualJugando, "EXITO", "CORRECTO");
                if (preguntasPendientes == 0) {
                    timerAnimacion.stop();
                    jugador.sumarCriptos(100); jugador.completarNivel(nivelActualJugando);
                    GestorDB.guardarUsuario(jugador, slotActual);
                    txtLog.appendText("[SUCCESS] ✓ NODO CAPTURADO. +100 BTC.\n");
                    actualizarHeader(); pausar(1800, this::mostrarMapaArbol);
                } else {
                    txtLog.appendText("[SYSTEM] " + preguntasPendientes + " secuencias restantes.\n");
                    nuevoReto();
                }
            } else {
                procesarFallo("HASH INVÁLIDO");
            }
        } catch (NumberFormatException ex) {
            txtLog.appendText("[ERROR] ✗ Solo se aceptan números.\n"); audio.playError();
        }
    }

    private void procesarFallo(String motivo) {
        if (timerAnimacion != null) timerAnimacion.stop();
        audio.playError(); jugador.recibirDaño();
        GestorDB.registrarHistorial(slotActual, nivelActualJugando, "FALLO", motivo);
        txtLog.appendText("[FAIL] ✗ " + motivo + " — -34% HP.\n");
        GestorDB.guardarUsuario(jugador, slotActual); actualizarHeader();

        if (jugador.getIntegridad() <= 0) {
            panelReto.getChildren().clear();
            Label lf = new Label("⚠  SYSTEM FAILURE  ⚠");
            lf.setStyle("-fx-text-fill:" + C_ROJO + ";-fx-font-size:30px;-fx-font-weight:bold;");
            lf.setEffect(new DropShadow(20, Color.web(C_ROJO)));
            panelReto.getChildren().add(lf);
            txtRespuesta.setDisable(true); btnHack.setDisable(true);
            txtLog.appendText("[CRITICAL] INTEGRIDAD 0% — PERFIL PURGADO.\n");
            GestorDB.borrarUsuario(slotActual);
            if (!rootJuego.getStyleClass().contains("alarma-roja"))
                rootJuego.getStyleClass().add("alarma-roja");
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

    // =========================================================================
    // RENDERIZADO MATEMÁTICO (LaTeX-like)
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
            cont.getChildren().add(lEtq);
            cont.getChildren().add(renderizarNotacion(lin.length > 1 ? lin[1].trim() : cuerpo));
        } else {
            Label lExp = new Label(cuerpo);
            lExp.setId("lblReto"); lExp.setWrapText(true);
            lExp.setTextAlignment(TextAlignment.CENTER);
            cont.getChildren().add(lExp);
        }
        return cont;
    }

    private VBox renderizarNotacion(String expr) {
        VBox box = new VBox(6); box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12, 24, 12, 24));
        box.setStyle(
                "-fx-background-color:rgba(0,25,0,0.6);" +
                        "-fx-border-color:" + C_CYAN + ";-fx-border-width:1.5px;" +
                        "-fx-border-radius:5;-fx-background-radius:5;"
        );
        box.setEffect(new DropShadow(16, Color.web(C_CYAN)));

        if (expr.contains("d/dx")) {
            // Derivada: fracción d/dx + función + punto evaluación
            String fun = (expr.contains("(") && expr.contains(")"))
                    ? expr.substring(expr.indexOf("("), expr.lastIndexOf(")") + 1) : "";
            String eval = expr.contains("para x=")
                    ? "evaluado en  " + expr.substring(expr.indexOf("para x="))
                    : expr.contains("cuando x=")
                    ? "evaluado en  " + expr.substring(expr.indexOf("cuando x=")) : "";

            HBox frac = new HBox(4); frac.setAlignment(Pos.CENTER);
            VBox df = new VBox(0); df.setAlignment(Pos.CENTER);
            df.getChildren().addAll(
                    estilo("d",  22, "white"),
                    new Label("──") {{ setStyle("-fx-text-fill:white;-fx-font-size:14px;"); }},
                    estilo("dx", 18, "white")
            );
            frac.getChildren().addAll(df, estilo("  " + fun, 26, C_VERDE));

            box.getChildren().add(estilo("DERIVADA", 11, C_CYAN));
            box.getChildren().add(frac);
            if (!eval.isEmpty()) box.getChildren().add(estilo(eval, 15, C_AMARILLO));

        } else if (expr.contains("∫")) {
            // Integral: símbolo con límites arriba/abajo + integrando
            String limites = (expr.contains("[") && expr.contains("]"))
                    ? expr.substring(expr.indexOf("[") + 1, expr.indexOf("]")).trim() : "";
            String[] lp    = limites.split(" a ");
            String limInf  = lp.length > 0 ? lp[0].trim() : "0";
            String limSup  = lp.length > 1 ? lp[1].trim() : "?";
            String intg    = "";
            if (expr.contains("]") && expr.contains("dx")) {
                int a = expr.indexOf("]") + 1, b = expr.lastIndexOf("dx");
                if (b > a) intg = expr.substring(a, b).trim().replaceAll("[()]", "").trim();
            }

            HBox row = new HBox(4); row.setAlignment(Pos.CENTER);
            VBox simb = new VBox(0); simb.setAlignment(Pos.CENTER);
            simb.getChildren().addAll(
                    estilo(limSup, 14, C_AMARILLO),
                    estilo("∫",   52, "white"),
                    estilo(limInf, 14, C_AMARILLO)
            );
            row.getChildren().addAll(simb, estilo(" " + intg + " dx", 28, C_VERDE));

            box.getChildren().add(estilo("INTEGRAL DEFINIDA", 11, C_CYAN));
            box.getChildren().add(row);
        }
        return box;
    }

    // =========================================================================
    // ENTRY POINT
    // =========================================================================

    public static void main(String[] args) { launch(args); }
}