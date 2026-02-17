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
        this.nivelActual = 1;
    }
    public void gastarCriptos(int cantidad) {
        this.criptos -= cantidad;
    }

    public void repararTotalmente() {
        this.integridad = 100; // Restaura la vida al máximo
    }
    // Getters y Setters
    public String getNombre() { return nombre; }
    public int getIntegridad() { return integridad; }
    public void recibirDaño() { this.integridad -= 34; }
    public int getCriptos() { return criptos; }
    public void sumarCriptos(int cant) { this.criptos += cant; }
}