package com.cybermath;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre;
    private int integridad;
    private int criptos;
    private int nivelActual;

    public Usuario(String nombre) {
        this.nombre = nombre;
        this.integridad = 100;
        this.criptos = 0;
        this.nivelActual = 1; // Inicia en el nivel 1
    }

    // --- MÉTODOS DE LA TIENDA ---
    public void gastarCriptos(int cantidad) {
        this.criptos -= cantidad;
    }

    public void repararTotalmente() {
        this.integridad = 100;
    }

    // ¡AQUÍ ESTÁ EL MÉTODO QUE FALTABA PARA CURAR EL 30%!
    public void curar(int cantidad) {
        this.integridad = Math.min(100, this.integridad + cantidad);
    }

    // --- GETTERS Y SETTERS GENERALES ---
    public String getNombre() { return nombre; }
    public int getIntegridad() { return integridad; }
    public void recibirDaño() { this.integridad -= 34; }
    public int getCriptos() { return criptos; }
    public void sumarCriptos(int cant) { this.criptos += cant; }

    // --- MÉTODOS PARA AVANZAR EN EL MAPA ---
    public int getNivelMaximo() {
        return nivelActual;
    }

    public void completarNivel(int nivel) {
        // Solo sube de nivel si completó el nivel más alto que tenía desbloqueado
        if (nivel == this.nivelActual) {
            this.nivelActual++;
        }
    }
}