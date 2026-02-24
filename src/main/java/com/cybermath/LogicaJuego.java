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
        if (nivel <= 11) return "AMENAZA: PHISHING.\n\nEl atacante envía correos falsos para robar credenciales. Filtra las firmas maliciosas resolviendo algoritmos de suma.";
        if (nivel <= 21) return "AMENAZA: FIREWALL COMPROMETIDO.\n\nBloquea los puertos vulnerables calculando las diferencias de paquetes de red (Restas).";
        if (nivel <= 31) return "AMENAZA: INTERCEPTACIÓN DE DATOS.\n\nGenera llaves públicas y privadas multiplicando matrices de números primos para asegurar la conexión.";
        if (nivel <= 41) return "AMENAZA: SQL INJECTION.\n\nSanitiza las consultas a la base de datos resolviendo operaciones mixtas antes de que roben la información.";
        return "AMENAZA OMEGA: ATAQUE DDoS.\n\nSobrecarga masiva detectada. El servidor colapsará. Usa Análisis Matemático (Derivadas e Integrales) para predecir y desviar el flujo de tráfico.";
    }

    public int getPreguntasPorNivel(int nivel) {
        return (nivel == 1) ? 3 : 3 + (nivel % 10);
    }

    public String generarReto(int nivel, int preguntaActual, int totalPreguntas) {
        Random rand = new Random();
        this.categoriaActual = getNombreRama(nivel);
        String prefix = "[" + categoriaActual + "] (" + preguntaActual + "/" + totalPreguntas + ")\n>>> ";

        if (nivel < 12) {
            int a = rand.nextInt(15) + 2; int b = rand.nextInt(15) + 2;
            this.respuestaCorrecta = a + b; return prefix + "VERIFICAR PAQUETE: " + a + " + " + b;
        } else if (nivel < 22) {
            int a = rand.nextInt(20) + 10; int b = rand.nextInt(15) + 2;
            this.respuestaCorrecta = a - b; return prefix + "FILTRAR TRÁFICO: " + a + " - " + b;
        } else if (nivel < 32) {
            int a = rand.nextInt(10) + 2; int b = rand.nextInt(10) + 2;
            this.respuestaCorrecta = a * b; return prefix + "GENERAR HASH: " + a + " * " + b;
        } else if (nivel < 42) {
            int a = rand.nextInt(6) + 2; int b = rand.nextInt(6) + 2; int c = rand.nextInt(10) + 1;
            this.respuestaCorrecta = (a * b) + c; return prefix + "SANITIZAR QUERY: (" + a + " * " + b + ") + " + c;
        } else {
            // NIVELES 42-50: DERIVADAS E INTEGRALES
            if (rand.nextBoolean()) {
                int a = rand.nextInt(4) + 1; int b = rand.nextInt(5) + 1; int c = rand.nextInt(3) + 1;
                this.respuestaCorrecta = (2 * a * c) + b;
                return prefix + "DESVIAR TRÁFICO:\nEvaluar d/dx (" + a + "x² + " + b + "x) cuando x=" + c;
            } else {
                int a = (rand.nextInt(4) + 1) * 2; int c = rand.nextInt(4) + 1;
                this.respuestaCorrecta = (a / 2.0) * (c * c);
                return prefix + "PREDECIR FLUJO:\nResolver ∫ [0 a " + c + "] (" + a + "x) dx";
            }
        }
    }

    public boolean verificar(double input) { return Math.abs(input - respuestaCorrecta) < 0.1; }
}