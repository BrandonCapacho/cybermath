package com.cybermath;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LogicaJuego {
    private double respuestaCorrecta;
    private Map<Integer, String[]> misiones = new HashMap<>();

    public LogicaJuego() {
        // Estructura: {Título de Misión, Descripción Técnica / CyberDato}
        misiones.put(1, new String[]{"INYECCIÓN SQL", "Objetivo: Bypassear validación de formulario. Los atacantes insertan código malicioso para leer bases de datos."});
        misiones.put(2, new String[]{"ATAQUE DoS", "Objetivo: Mitigar saturación de peticiones. El Denial of Service busca inhabilitar un servidor por sobrecarga."});
        misiones.put(3, new String[]{"CRACKEO DE HASH", "Objetivo: Revertir cifrado MD5. Las funciones hash convierten datos en huellas digitales únicas."});
        misiones.put(10, new String[]{"RANSOMWARE", "Objetivo: Desencriptar archivos secuestrados. El software malicioso cifra datos y exige un rescate."});
    }

    public String[] getInfoMision(int nivel) {
        return misiones.getOrDefault(nivel, new String[]{"NODO_DESCONOCIDO", "Protocolo de datos no identificado. Procede con precaución."});
    }

    public String generarReto(int nivel) {
        Random rand = new Random();
        // Dificultad incremental: Nivel 1 (2-16), Nivel 50 (2-65)
        int rango = 15 + nivel;
        int a = rand.nextInt(rango) + 2;
        int b = rand.nextInt(rango) + 2;

        if (nivel < 10) {
            this.respuestaCorrecta = a + b;
            return ">>> DECRYPT: " + a + " + " + b + " = ?";
        } else if (nivel < 25) {
            this.respuestaCorrecta = a - b;
            return ">>> FILTER_PACKET: " + a + " - " + b + " = ?";
        } else {
            this.respuestaCorrecta = (a * 2) + b;
            return ">>> OVERRIDE_KERNEL: (" + a + " * 2) + " + b + " = ?";
        }
    }

    public boolean verificar(double input) {
        return Math.abs(input - respuestaCorrecta) < 0.1;
    }
}