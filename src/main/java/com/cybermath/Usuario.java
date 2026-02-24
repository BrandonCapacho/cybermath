package com.cybermath;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 2L; // Versión actualizada

    private String nombre;
    private int integridad;
    private int criptos;
    private boolean[] nivelesCompletados; // ¡Ahora rastrea múltiples ramas!

    public Usuario(String nombre) {
        this.nombre = nombre;
        this.integridad = 100;
        this.criptos = 0;
        this.nivelesCompletados = new boolean[55]; // Capacidad para 50 niveles
    }

    public void gastarCriptos(int cantidad) { this.criptos -= cantidad; }
    public void repararTotalmente() { this.integridad = 100; }
    public void curar(int cantidad) { this.integridad = Math.min(100, this.integridad + cantidad); }

    public String getNombre() { return nombre; }
    public int getIntegridad() { return integridad; }
    public void recibirDaño() { this.integridad -= 34; }
    public int getCriptos() { return criptos; }
    public void sumarCriptos(int cant) { this.criptos += cant; }

    // --- NUEVA LÓGICA DE MAPA ---
    public boolean isNivelCompletado(int nivel) {
        if (nivel < 0 || nivel >= nivelesCompletados.length) return false;
        return nivelesCompletados[nivel];
    }

    public void completarNivel(int nivel) {
        if (nivel >= 0 && nivel < nivelesCompletados.length) {
            this.nivelesCompletados[nivel] = true;
        }
    }

    public int getNivelesSuperados() {
        int count = 0;
        for (boolean b : nivelesCompletados) if(b) count++;
        return count;
    }
}