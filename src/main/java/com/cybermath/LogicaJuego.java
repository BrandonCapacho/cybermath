package com.cybermath;

import java.util.Random;

public class LogicaJuego {

    // --- Atributos ---
    private double respuestaCorrecta;
    private String categoriaActual;
    private final Random rand = new Random();

    // =========================================================================
    // MÉTODOS PÚBLICOS DE CONSULTA
    // =========================================================================

    public String getNombreRama(int nivel) {
        if (nivel == 1)        return "NÚCLEO DEL SISTEMA";
        if (nivel <= 11)       return "PROTOCOLO: PHISHING (Ingeniería Social)";
        if (nivel <= 21)       return "PROTOCOLO: FIREWALL (Filtrado de Red)";
        if (nivel <= 31)       return "PROTOCOLO: CRIPTOGRAFÍA (Cifrado de Datos)";
        if (nivel <= 41)       return "PROTOCOLO: SQL INJECTION (Bases de Datos)";
        return                        "AMENAZA OMEGA: DDoS (Saturación Cuántica)";
    }

    public String getDescripcionMision(int nivel) {
        if (nivel == 1)  return "ACCESO INICIAL:\nDemuestra tu capacidad de procesamiento lógico básico para acceder al sistema central.";
        if (nivel <= 11) return "AMENAZA: PHISHING.\n\nFiltra los correos maliciosos resolviendo secuencias aritméticas alternadas (Sumas y Restas rápidas).";
        if (nivel <= 21) return "AMENAZA: FIREWALL COMPROMETIDO.\n\nBloquea los puertos vulnerables analizando tráfico. Incluye operaciones combinadas de enrutamiento.";
        if (nivel <= 31) return "AMENAZA: INTERCEPTACIÓN DE DATOS.\n\nGenera llaves públicas y privadas utilizando multiplicaciones complejas y cifrado de matrices.";
        if (nivel <= 41) return "AMENAZA: SQL INJECTION.\n\nSanitiza las consultas a la base de datos resolviendo álgebra condicional y jerarquía de operaciones.";
        return                 "AMENAZA OMEGA: ATAQUE DDoS.\n\nSobrecarga masiva. Usa Análisis Matemático (Derivadas e Integrales) para predecir y desviar el flujo de tráfico.";
    }

    public int getPreguntasPorNivel(int nivel) {
        return (nivel == 1) ? 3 : 3 + (nivel % 10);
    }

    // =========================================================================
    // MOTOR PRINCIPAL DE GENERACIÓN DE RETOS
    // =========================================================================

    public String generarReto(int nivel, int preguntaActual, int totalPreguntas) {
        this.categoriaActual = getNombreRama(nivel);
        String prefix = "[" + categoriaActual + "] (" + preguntaActual + "/" + totalPreguntas + ")\n>>> ";

        // El caso base va primero para evitar recorrer condiciones innecesarias
        if (nivel == 1)  return generarNucleo(prefix);
        if (nivel <= 11) return generarPhishing(prefix, nivel);
        if (nivel <= 21) return generarFirewall(prefix);
        if (nivel <= 31) return generarCriptografia(prefix);
        if (nivel <= 41) return generarSQLInjection(prefix);
        return                  generarDDoS(prefix);
    }

    public boolean verificar(double input) {
        return Math.abs(input - respuestaCorrecta) < 0.1;
    }

    // =========================================================================
    // GENERADORES PRIVADOS POR RAMA
    // =========================================================================

    /** Nivel 1 — Sumas simples de acceso inicial */
    private String generarNucleo(String prefix) {
        int a = rand.nextInt(10) + 1;
        int b = rand.nextInt(10) + 1;
        this.respuestaCorrecta = a + b;
        return prefix + "ACCESO INICIAL: " + a + " + " + b;
    }

    /** Niveles 2–11 — Phishing: Sumas (66%) y Restas (33%) con dificultad creciente */
    private String generarPhishing(String prefix, int nivel) {
        if (rand.nextInt(3) <= 1) {
            int a = rand.nextInt(15 + nivel) + 5;
            int b = rand.nextInt(15 + nivel) + 5;
            this.respuestaCorrecta = a + b;
            return prefix + "VERIFICAR FIRMA: " + a + " + " + b;
        } else {
            int a = rand.nextInt(20 + nivel) + 10;
            int b = rand.nextInt(15) + 2;
            this.respuestaCorrecta = a - b;
            return prefix + "DESCARTAR PAQUETE: " + a + " - " + b;
        }
    }

    /** Niveles 12–21 — Firewall: Sumas, Restas y Multiplicaciones de red */
    private String generarFirewall(String prefix) {
        int tipoOp = rand.nextInt(3);
        if (tipoOp == 0) {
            int a = rand.nextInt(30) + 10;
            int b = rand.nextInt(30) + 10;
            this.respuestaCorrecta = a + b;
            return prefix + "FILTRAR TRÁFICO: " + a + " + " + b;
        } else if (tipoOp == 1) {
            int a = rand.nextInt(40) + 20;
            int b = rand.nextInt(20) + 5;
            this.respuestaCorrecta = a - b;
            return prefix + "BLOQUEAR PUERTO: " + a + " - " + b;
        } else {
            int a = rand.nextInt(8) + 2;
            int b = rand.nextInt(8) + 2;
            this.respuestaCorrecta = a * b;
            return prefix + "ENRUTAR IP: " + a + " * " + b;
        }
    }

    /** Niveles 22–31 — Criptografía: Multiplicaciones y jerarquía básica */
    private String generarCriptografia(String prefix) {
        if (rand.nextInt(3) <= 1) {
            int a = rand.nextInt(12) + 3;
            int b = rand.nextInt(12) + 3;
            this.respuestaCorrecta = a * b;
            return prefix + "GENERAR HASH: " + a + " * " + b;
        } else {
            int a = rand.nextInt(5) + 2;
            int b = rand.nextInt(5) + 2;
            int c = rand.nextInt(10) + 1;
            this.respuestaCorrecta = (a * b) + c;
            return prefix + "CIFRAR LLAVE: (" + a + " * " + b + ") + " + c;
        }
    }

    /** Niveles 32–41 — SQL Injection: Expresiones mixtas de 3 o 4 variables */
    private String generarSQLInjection(String prefix) {
        int tipoOp = rand.nextInt(3);
        int a = rand.nextInt(10) + 2;
        int b = rand.nextInt(10) + 2;
        int c = rand.nextInt(20) + 2;
        if (tipoOp == 0) {
            this.respuestaCorrecta = (a * b) - c;
            return prefix + "SANITIZAR QUERY: (" + a + " * " + b + ") - " + c;
        } else if (tipoOp == 1) {
            this.respuestaCorrecta = a * (b + c);
            return prefix + "EVADIR INYECCIÓN: " + a + " * (" + b + " + " + c + ")";
        } else {
            int d = rand.nextInt(5) + 1;
            this.respuestaCorrecta = (a * b) + (c * d);
            return prefix + "REPARAR TABLA: (" + a + " * " + b + ") + (" + c + " * " + d + ")";
        }
    }

    /**
     * Niveles 42+ — DDoS: Derivadas e Integrales alternadas.
     * NOTA: en los casos de integral, 'a' siempre es par para garantizar resultado entero.
     */
    private String generarDDoS(String prefix) {
        int tipoOp = rand.nextInt(3);
        if (tipoOp == 0) {
            // Derivada: d/dx(ax² + bx) evaluada en x=c → resultado = 2ac + b
            int a = rand.nextInt(4) + 1;
            int b = rand.nextInt(5) + 1;
            int c = rand.nextInt(3) + 1;
            this.respuestaCorrecta = (2 * a * c) + b;
            return prefix + "DESVIAR TRÁFICO:\nEvaluar d/dx (" + a + "x² + " + b + "x) para x=" + c;
        } else if (tipoOp == 1) {
            // Integral definida ∫[0,c] ax dx → resultado = (a/2) * c²
            int a = (rand.nextInt(4) + 1) * 2; // par garantizado
            int c = rand.nextInt(4) + 1;
            this.respuestaCorrecta = (a / 2.0) * (c * c);
            return prefix + "PREDECIR FLUJO:\nResolver ∫ [0 a " + c + "] (" + a + "x) dx";
        } else {
            // Integral definida ∫[b,c] ax dx → resultado = (a/2)(c² - b²)
            int a = (rand.nextInt(3) + 1) * 2; // par garantizado
            int b = rand.nextInt(2) + 1;
            int c = b + rand.nextInt(3) + 1;   // c siempre > b
            this.respuestaCorrecta = ((a / 2.0) * (c * c)) - ((a / 2.0) * (b * b));
            return prefix + "MITIGAR BOTNET:\nResolver ∫ [" + b + " a " + c + "] (" + a + "x) dx";
        }
    }
    // =========================================================================
    // MODO SUPERVIVENCIA: DEEP WEB
    // =========================================================================
    public String generarRetoDeepWeb(int oleada) {
        this.categoriaActual = "DEEP WEB — OLEADA " + oleada;
        String prefix = "[DEEP WEB] (Oleada " + oleada + ")\n>>> ";

        // Aumenta la dificultad base según la oleada
        int dificultadVirtual = Math.min(50, 5 + (oleada * 2));

        int tipo = rand.nextInt(5);
        if (tipo == 0) return generarPhishing(prefix, dificultadVirtual);
        if (tipo == 1) return generarFirewall(prefix);
        if (tipo == 2) return generarCriptografia(prefix);
        if (tipo == 3) return generarSQLInjection(prefix);
        return generarDDoS(prefix);
    }
}