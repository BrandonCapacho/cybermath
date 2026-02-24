package com.cybermath;
import java.util.Random;

public class LogicaJuego {
    private double respuestaCorrecta;
    private String categoriaActual;

    public String getNombreRama(int nivel) {
        if (nivel == 1) return "NÚCLEO DEL SISTEMA";
        if (nivel >= 2 && nivel <= 11) return "PROTOCOLO: PHISHING (Ingeniería Social)";
        if (nivel >= 12 && nivel <= 21) return "PROTOCOLO: FIREWALL (Filtrado de Red)";
        if (nivel >= 22 && nivel <= 31) return "PROTOCOLO: CRIPTOGRAFÍA (Cifrado de Datos)";
        if (nivel >= 32 && nivel <= 41) return "PROTOCOLO: SQL INJECTION (Bases de Datos)";
        return "AMENAZA OMEGA: DDoS (Saturación Cuántica)";
    }

    public String getDescripcionMision(int nivel) {
        if (nivel == 1) return "ACCESO INICIAL:\nDemuestra tu capacidad de procesamiento lógico básico para acceder al sistema central.";
        if (nivel <= 11) return "AMENAZA: PHISHING.\n\nFiltra los correos maliciosos resolviendo secuencias aritméticas alternadas (Sumas y Restas rápidas).";
        if (nivel <= 21) return "AMENAZA: FIREWALL COMPROMETIDO.\n\nBloquea los puertos vulnerables analizando tráfico. Incluye operaciones combinadas de enrutamiento.";
        if (nivel <= 31) return "AMENAZA: INTERCEPTACIÓN DE DATOS.\n\nGenera llaves públicas y privadas utilizando multiplicaciones complejas y cifrado de matrices.";
        if (nivel <= 41) return "AMENAZA: SQL INJECTION.\n\nSanitiza las consultas a la base de datos resolviendo álgebra condicional y jerarquía de operaciones.";
        return "AMENAZA OMEGA: ATAQUE DDoS.\n\nSobrecarga masiva. Usa Análisis Matemático (Derivadas e Integrales) para predecir y desviar el flujo de tráfico.";
    }

    public int getPreguntasPorNivel(int nivel) {
        return (nivel == 1) ? 3 : 3 + (nivel % 10);
    }

    public String generarReto(int nivel, int preguntaActual, int totalPreguntas) {
        Random rand = new Random();
        this.categoriaActual = getNombreRama(nivel);
        String prefix = "[" + categoriaActual + "] (" + preguntaActual + "/" + totalPreguntas + ")\n>>> ";
        int tipoOp = rand.nextInt(3);

        // Rama 1: Phishing -> Alterna Sumas (66%) y Restas (33%)
        if (nivel >= 2 && nivel <= 11) {
            if (tipoOp <= 1) {
                int a = rand.nextInt(15 + nivel) + 5; int b = rand.nextInt(15 + nivel) + 5;
                this.respuestaCorrecta = a + b; return prefix + "VERIFICAR FIRMA: " + a + " + " + b;
            } else {
                int a = rand.nextInt(20 + nivel) + 10; int b = rand.nextInt(15) + 2;
                this.respuestaCorrecta = a - b; return prefix + "DESCARTAR PAQUETE: " + a + " - " + b;
            }
        }
        // Rama 2: Firewall -> Sumas, Restas y Multiplicaciones de red
        else if (nivel >= 12 && nivel <= 21) {
            if (tipoOp == 0) {
                int a = rand.nextInt(30) + 10; int b = rand.nextInt(30) + 10;
                this.respuestaCorrecta = a + b; return prefix + "FILTRAR TRÁFICO: " + a + " + " + b;
            } else if (tipoOp == 1) {
                int a = rand.nextInt(40) + 20; int b = rand.nextInt(20) + 5;
                this.respuestaCorrecta = a - b; return prefix + "BLOQUEAR PUERTO: " + a + " - " + b;
            } else {
                int a = rand.nextInt(8) + 2; int b = rand.nextInt(8) + 2;
                this.respuestaCorrecta = a * b; return prefix + "ENRUTAR IP: " + a + " * " + b;
            }
        }
        // Rama 3: Criptografía -> Multiplicaciones y jerarquía básica
        else if (nivel >= 22 && nivel <= 31) {
            if (tipoOp <= 1) {
                int a = rand.nextInt(12) + 3; int b = rand.nextInt(12) + 3;
                this.respuestaCorrecta = a * b; return prefix + "GENERAR HASH: " + a + " * " + b;
            } else {
                int a = rand.nextInt(5) + 2; int b = rand.nextInt(5) + 2; int c = rand.nextInt(10) + 1;
                this.respuestaCorrecta = (a * b) + c; return prefix + "CIFRAR LLAVE: (" + a + " * " + b + ") + " + c;
            }
        }
        // Rama 4: SQL Injection -> Operaciones mixtas de 3 variables
        else if (nivel >= 32 && nivel <= 41) {
            int a = rand.nextInt(10) + 2; int b = rand.nextInt(10) + 2; int c = rand.nextInt(20) + 2;
            if (tipoOp == 0) {
                this.respuestaCorrecta = (a * b) - c; return prefix + "SANITIZAR QUERY: (" + a + " * " + b + ") - " + c;
            } else if (tipoOp == 1) {
                this.respuestaCorrecta = a * (b + c); return prefix + "EVADIR INYECCIÓN: " + a + " * (" + b + " + " + c + ")";
            } else {
                int d = rand.nextInt(5) + 1;
                this.respuestaCorrecta = (a * b) + (c * d); return prefix + "REPARAR TABLA: (" + a + " * " + b + ") + (" + c + " * " + d + ")";
            }
        }
        // Rama 5: DDoS -> Derivadas e Integrales alternadas
        else if (nivel >= 42) {
            if (tipoOp == 0) {
                int a = rand.nextInt(4) + 1; int b = rand.nextInt(5) + 1; int c = rand.nextInt(3) + 1;
                this.respuestaCorrecta = (2 * a * c) + b;
                return prefix + "DESVIAR TRÁFICO:\nEvaluar d/dx (" + a + "x² + " + b + "x) para x=" + c;
            } else if (tipoOp == 1) {
                int a = (rand.nextInt(4) + 1) * 2; int c = rand.nextInt(4) + 1;
                this.respuestaCorrecta = (a / 2.0) * (c * c);
                return prefix + "PREDECIR FLUJO:\nResolver ∫ [0 a " + c + "] (" + a + "x) dx";
            } else {
                int a = (rand.nextInt(3) + 1) * 2; int b = rand.nextInt(2) + 1; int c = b + rand.nextInt(3) + 1;
                this.respuestaCorrecta = ((a / 2.0) * (c * c)) - ((a / 2.0) * (b * b));
                return prefix + "MITIGAR BOTNET:\nResolver ∫ [" + b + " a " + c + "] (" + a + "x) dx";
            }
        }

        // Nivel 1 (Base - Sumas Fáciles)
        int a = rand.nextInt(10) + 1; int b = rand.nextInt(10) + 1;
        this.respuestaCorrecta = a + b; return prefix + "ACCESO INICIAL: " + a + " + " + b;
    }

    public boolean verificar(double input) { return Math.abs(input - respuestaCorrecta) < 0.1; }
}