package com.cybermath;
import java.util.Random;

public class LogicaJuego {
    private double respuestaCorrecta;
    private String categoriaActual;

    public String getNombreRama(int nivel) {
        if (nivel == 1) return "NÚCLEO DEL SISTEMA";
        if (nivel >= 2 && nivel <= 11) return "PROTOCOLO: PHISHING";
        if (nivel >= 12 && nivel <= 21) return "PROTOCOLO: FIREWALL";
        if (nivel >= 22 && nivel <= 31) return "PROTOCOLO: CRIPTOGRAFÍA";
        if (nivel >= 32 && nivel <= 41) return "PROTOCOLO: SQL INJECTION";
        return "PROTOCOLO: ATAQUE DDoS";
    }

    // --- NUEVO: HISTORIA EDUCATIVA ---
    public String getDescripcionMision(int nivel) {
        if (nivel == 1) return "ACCESO INICIAL:\nEl Núcleo es el corazón del sistema. Antes de enfrentar amenazas reales, debes demostrar que puedes procesar cálculos básicos de binario y lógica simple.\n\nOBJETIVO: Sincronizar el reloj del CPU.";

        if (nivel >= 2 && nivel <= 11) return "ALERTA DE SEGURIDAD: PHISHING DETECTADO.\n\nConcepto: El Phishing es una técnica de ingeniería social usada para engañar a usuarios y robar datos confidenciales (contraseñas, tarjetas) mediante correos falsos.\n\nMISIÓN: Identificar y filtrar los correos maliciosos resolviendo las sumas de verificación.";

        if (nivel >= 12 && nivel <= 21) return "ALERTA DE SEGURIDAD: FIREWALL COMPROMETIDO.\n\nConcepto: Un Firewall es una barrera de seguridad que monitorea y controla el tráfico de red entrante y saliente basado en reglas de seguridad preestablecidas.\n\nMISIÓN: Reconfigurar las reglas de filtrado calculando las diferencias de paquetes (Restas).";

        if (nivel >= 22 && nivel <= 31) return "ALERTA DE SEGURIDAD: DATOS SIN CIFRAR.\n\nConcepto: La Criptografía protege la información transformándola en un formato ilegible para terceros. Solo quien tenga la clave matemática puede leerla.\n\nMISIÓN: Generar claves de encriptación mediante productos factoriales (Multiplicación).";

        if (nivel >= 32 && nivel <= 41) return "ALERTA CRÍTICA: INYECCIÓN SQL.\n\nConcepto: SQL Injection es una vulnerabilidad donde un atacante interfiere con las consultas que una aplicación hace a su base de datos, permitiendo ver datos que no debería.\n\nMISIÓN: Sanitarizar las entradas de la base de datos resolviendo operaciones mixtas.";

        return "AMENAZA NIVEL OMEGA: ATAQUE DDoS MASIVO.\n\nConcepto: Un ataque de Denegación de Servicio Distribuido (DDoS) intenta hacer que un servicio online no esté disponible abrumándolo con tráfico de múltiples fuentes.\n\nMISIÓN: Mitigar la carga del servidor resolviendo algoritmos complejos bajo presión extrema.";
    }

    public int getPreguntasPorNivel(int nivel) {
        if (nivel == 1) return 3;
        int base = 3;
        int incremento = (nivel % 10) + 1;
        return base + incremento;
    }

    public String generarReto(int nivel, int preguntaActual, int totalPreguntas) {
        Random rand = new Random();
        this.categoriaActual = getNombreRama(nivel);
        int dificultad = nivel + preguntaActual;
        int a = rand.nextInt(5 + dificultad) + 2;
        int b = rand.nextInt(5 + dificultad) + 2;

        String prefix = "[" + categoriaActual + "] (" + preguntaActual + "/" + totalPreguntas + ") >>> ";

        if (nivel < 12) {
            this.respuestaCorrecta = a + b;
            return prefix + "VERIFICAR PAQUETE: " + a + " + " + b;
        } else if (nivel < 22) {
            this.respuestaCorrecta = (a + b) - b;
            return prefix + "FILTRAR TRÁFICO: " + (a+b) + " - " + b;
        } else if (nivel < 32) {
            this.respuestaCorrecta = a * b;
            return prefix + "GENERAR HASH: " + a + " * " + b;
        } else {
            int c = rand.nextInt(5) + 1;
            this.respuestaCorrecta = (a * b) + c;
            return prefix + "SANITIZAR QUERY: (" + a + " * " + b + ") + " + c;
        }
    }

    public boolean verificar(double input) {
        return Math.abs(input - respuestaCorrecta) < 0.1;
    }
}