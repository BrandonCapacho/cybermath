package com.cybermath;
import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre;
    private int integridad;
    private int criptos;
    private int nivelMaximo; // El nivel más alto desbloqueado

    public Usuario(String nombre) {
        this.nombre = nombre;
        this.integridad = 100;
        this.criptos = 0;
        this.nivelMaximo = 1; // Inicia en el nivel 1
    }

    public void completarNivel(int nivel) {
        if (nivel == this.nivelMaximo && this.nivelMaximo < 50) {
            this.nivelMaximo++;
        }
    }

    public int getNivelMaximo() { return nivelMaximo; }
    public String getNombre() { return nombre; }
    public int getIntegridad() { return integridad; }
    public void recibirDaño() { this.integridad -= 34; }
    public int getCriptos() { return criptos; }
    public void sumarCriptos(int cant) { this.criptos += cant; }
    public void gastarCriptos(int cantidad) { this.criptos -= cantidad; }
    public void repararTotalmente() { this.integridad = 100; }
}