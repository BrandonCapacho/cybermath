package com.cybermath;

import java.util.Random;

public class LogicaJuego {
    private double respuestaCorrecta;

    /**
     * Genera un reto matemático aleatorio.
     * @return El texto del reto para mostrar en la interfaz.
     */
    public String generarReto() {
        Random rand = new Random();
        int a = rand.nextInt(15) + 2;
        int b = rand.nextInt(15) + 2;

        // Simulamos un descifrado de hash aritmético
        this.respuestaCorrecta = a + b;
        return ">>> BYPASS REQUERIDO: " + a + " + " + b + " = ?";
    }

    /**
     * Valida si la entrada del usuario coincide con la respuesta calculada.
     */
    public boolean verificar(double input) {
        return Math.abs(input - respuestaCorrecta) < 0.1;
    }
}