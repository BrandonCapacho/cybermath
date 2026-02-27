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
    // =========================================================================
// GENERADORES PRIVADOS POR RAMA — LogicaJuego.java
// Reemplaza los métodos existentes por estos en tu clase LogicaJuego
// =========================================================================

    /** Nivel 1 — Sumas simples de acceso inicial */
    private String generarNucleo(String prefix) {
        String[] flavors = {
                "PROBANDO ACCESO AL NÚCLEO PRIMORDIAL — ",
                "AUTENTICANDO CREDENCIALES DE OPERADOR — ",
                "VERIFICANDO PROTOCOLO DE ENTRADA — "
        };
        int a = rand.nextInt(10) + 1;
        int b = rand.nextInt(10) + 1;
        this.respuestaCorrecta = a + b;
        return prefix + flavors[rand.nextInt(flavors.length)] + a + " + " + b;
    }

    /** Niveles 2–11 — Phishing: Sumas (66%) y Restas (33%) con dificultad creciente */
    private String generarPhishing(String prefix, int nivel) {
        String[] flavorsVerificar = {
                "FILTRANDO CORREO DE CEO FALSO — ",
                "VALIDANDO FIRMA DIGITAL SOSPECHOSA — ",
                "ANALIZANDO CABECERA SMTP ADULTERADA — ",
                "VERIFICANDO HASH DE REMITENTE — ",
                "AUTENTICANDO CERTIFICADO SSL FALSO — "
        };
        String[] flavorsDescartar = {
                "BLOQUEANDO SPEAR PHISHING CORPORATIVO — ",
                "ELIMINANDO PAYLOAD MALICIOSO — ",
                "DESCARTANDO ADJUNTO .EXE DISFRAZADO — ",
                "NEUTRALIZANDO LINK DE REDIRECCIÓN — ",
                "PURGANDO SCRIPT OCULTO EN HTML — "
        };

        if (rand.nextInt(3) <= 1) {
            int a = rand.nextInt(15 + nivel) + 5;
            int b = rand.nextInt(15 + nivel) + 5;
            this.respuestaCorrecta = a + b;
            String flavor = flavorsVerificar[rand.nextInt(flavorsVerificar.length)];
            return prefix + flavor + a + " + " + b;
        } else {
            int a = rand.nextInt(20 + nivel) + 10;
            int b = rand.nextInt(15) + 2;
            this.respuestaCorrecta = a - b;
            String flavor = flavorsDescartar[rand.nextInt(flavorsDescartar.length)];
            return prefix + flavor + a + " - " + b;
        }
    }

    /** Niveles 12–21 — Firewall: Sumas, Restas y Multiplicaciones de red */
    private String generarFirewall(String prefix) {
        String[] flavorsSuma = {
                "CALCULANDO ANCHO DE BANDA DEL TÚNEL — ",
                "MIDIENDO LATENCIA DE PAQUETES ENTRANTES — ",
                "SUMANDO REGLAS DE TABLA ACL — "
        };
        String[] flavorsResta = {
                "BLOQUEANDO PUERTOS VULNERABLES — ",
                "REDUCIENDO SUPERFICIE DE ATAQUE — ",
                "ELIMINANDO REGLAS OBSOLETAS DEL FIREWALL — "
        };
        String[] flavorsMult = {
                "ENRUTANDO TRÁFICO POR CANALES CIFRADOS — ",
                "CALCULANDO MÁSCARAS DE SUBRED — ",
                "MULTIPLICANDO RUTAS DE ESCAPE IP — "
        };

        int tipoOp = rand.nextInt(3);
        if (tipoOp == 0) {
            int a = rand.nextInt(30) + 10;
            int b = rand.nextInt(30) + 10;
            this.respuestaCorrecta = a + b;
            return prefix + flavorsSuma[rand.nextInt(flavorsSuma.length)] + a + " + " + b;
        } else if (tipoOp == 1) {
            int a = rand.nextInt(40) + 20;
            int b = rand.nextInt(20) + 5;
            this.respuestaCorrecta = a - b;
            return prefix + flavorsResta[rand.nextInt(flavorsResta.length)] + a + " - " + b;
        } else {
            int a = rand.nextInt(8) + 2;
            int b = rand.nextInt(8) + 2;
            this.respuestaCorrecta = a * b;
            return prefix + flavorsMult[rand.nextInt(flavorsMult.length)] + a + " * " + b;
        }
    }

    /** Niveles 22–31 — Criptografía: Multiplicaciones y jerarquía básica */
    private String generarCriptografia(String prefix) {
        String[] flavorsMult = {
                "GENERANDO LLAVE RSA DE SESIÓN — ",
                "CALCULANDO MÓDULO DE CIFRADO AES — ",
                "PROCESANDO SEED DE ENTROPÍA — ",
                "CONSTRUYENDO BLOQUE DE HASH SHA-256 — "
        };
        String[] flavorsJerarquia = {
                "CIFRANDO PAQUETE CON LLAVE PÚBLICA — ",
                "APLICANDO FUNCIÓN DE DERIVACIÓN PBKDF2 — ",
                "COMBINANDO VECTORES DE INICIALIZACIÓN — "
        };

        if (rand.nextInt(3) <= 1) {
            int a = rand.nextInt(12) + 3;
            int b = rand.nextInt(12) + 3;
            this.respuestaCorrecta = a * b;
            String flavor = flavorsMult[rand.nextInt(flavorsMult.length)];
            return prefix + flavor + a + " * " + b;
        } else {
            int a = rand.nextInt(5) + 2;
            int b = rand.nextInt(5) + 2;
            int c = rand.nextInt(10) + 1;
            this.respuestaCorrecta = (a * b) + c;
            String flavor = flavorsJerarquia[rand.nextInt(flavorsJerarquia.length)];
            return prefix + flavor + "(" + a + " * " + b + ") + " + c;
        }
    }

    /** Niveles 32–41 — SQL Injection: Expresiones mixtas de 3 o 4 variables */
    private String generarSQLInjection(String prefix) {
        String[] flavorsTipo0 = {
                "SANITIZANDO PARÁMETRO WHERE CLAUSE — ",
                "NEUTRALIZANDO UNION SELECT MALICIOSO — ",
                "ESCAPANDO COMILLAS EN PREPARED STATEMENT — "
        };
        String[] flavorsTipo1 = {
                "BLOQUEANDO DROP TABLE EN TIEMPO REAL — ",
                "FILTRANDO CONCATENACIÓN DE QUERY DINÁMICA — ",
                "VALIDANDO INPUT DE FORMULARIO CRÍTICO — "
        };
        String[] flavorsTipo2 = {
                "REPARANDO ÍNDICE DE BASE DE DATOS CORROMPIDA — ",
                "RECONSTRUYENDO TABLA TRAS INYECCIÓN — ",
                "NORMALIZANDO ESQUEMA POST-ATAQUE — "
        };

        int tipoOp = rand.nextInt(3);
        int a = rand.nextInt(10) + 2;
        int b = rand.nextInt(10) + 2;
        int c = rand.nextInt(20) + 2;

        if (tipoOp == 0) {
            this.respuestaCorrecta = (a * b) - c;
            return prefix + flavorsTipo0[rand.nextInt(flavorsTipo0.length)] + "(" + a + " * " + b + ") - " + c;
        } else if (tipoOp == 1) {
            this.respuestaCorrecta = a * (b + c);
            return prefix + flavorsTipo1[rand.nextInt(flavorsTipo1.length)] + a + " * (" + b + " + " + c + ")";
        } else {
            int d = rand.nextInt(5) + 1;
            this.respuestaCorrecta = (a * b) + (c * d);
            return prefix + flavorsTipo2[rand.nextInt(flavorsTipo2.length)] + "(" + a + " * " + b + ") + (" + c + " * " + d + ")";
        }
    }

    /**
     * Niveles 42+ — DDoS: Derivadas e Integrales alternadas.
     * NOTA: en los casos de integral, 'a' siempre es par para garantizar resultado entero.
     */
    private String generarDDoS(String prefix) {
        String[] flavorsDerivada = {
                "MODELANDO CURVA DE TRÁFICO ENTRANTE — \n",
                "CALCULANDO TASA DE CAMBIO DEL ANCHO DE BANDA — \n",
                "PREDICIENDO PICO DE SATURACIÓN DE RED — \n"
        };
        String[] flavorsIntegralSimple = {
                "MIDIENDO VOLUMEN TOTAL DE PAQUETES MALICIOSOS — \n",
                "CALCULANDO ÁREA BAJO LA CURVA DE ATAQUE — \n",
                "INTEGRANDO FLUJO DE BOTNET EN EL TIEMPO — \n"
        };
        String[] flavorsIntegralDoble = {
                "ANALIZANDO INTERVALO CRÍTICO DE SATURACIÓN — \n",
                "ESTIMANDO DAÑO ACUMULADO EN VENTANA DE ATAQUE — \n",
                "MITIGANDO OLEADA CUÁNTICA DE PAQUETES — \n"
        };

        int tipoOp = rand.nextInt(3);
        if (tipoOp == 0) {
            int a = rand.nextInt(4) + 1;
            int b = rand.nextInt(5) + 1;
            int c = rand.nextInt(3) + 1;
            this.respuestaCorrecta = (2 * a * c) + b;
            String flavor = flavorsDerivada[rand.nextInt(flavorsDerivada.length)];
            return prefix + flavor + "Evaluar d/dx (" + a + "x² + " + b + "x) para x=" + c;
        } else if (tipoOp == 1) {
            int a = (rand.nextInt(4) + 1) * 2;
            int c = rand.nextInt(4) + 1;
            this.respuestaCorrecta = (a / 2.0) * (c * c);
            String flavor = flavorsIntegralSimple[rand.nextInt(flavorsIntegralSimple.length)];
            return prefix + flavor + "Resolver ∫ [0 a " + c + "] (" + a + "x) dx";
        } else {
            int a = (rand.nextInt(3) + 1) * 2;
            int b = rand.nextInt(2) + 1;
            int c = b + rand.nextInt(3) + 1;
            this.respuestaCorrecta = ((a / 2.0) * (c * c)) - ((a / 2.0) * (b * b));
            String flavor = flavorsIntegralDoble[rand.nextInt(flavorsIntegralDoble.length)];
            return prefix + flavor + "Resolver ∫ [" + b + " a " + c + "] (" + a + "x) dx";
        }
    }

    // =========================================================================
    // MODO SUPERVIVENCIA: DEEP WEB
    // =========================================================================
    public String generarRetoDeepWeb(int oleada) {
        this.categoriaActual = "DEEP WEB — OLEADA " + oleada;
        String prefix = "[DEEP WEB] (Oleada " + oleada + ")\n>>> ";

        int dificultadVirtual = Math.min(50, 5 + (oleada * 2));

        int tipo = rand.nextInt(5);
        if (tipo == 0) return generarPhishing(prefix, dificultadVirtual);
        if (tipo == 1) return generarFirewall(prefix);
        if (tipo == 2) return generarCriptografia(prefix);
        if (tipo == 3) return generarSQLInjection(prefix);
        return generarDDoS(prefix);
    }

}