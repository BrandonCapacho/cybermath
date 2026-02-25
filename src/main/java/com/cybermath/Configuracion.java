package com.cybermath;

/**
 * Clase de configuración del juego.
 * Almacena preferencias del jugador en memoria durante la sesión.
 * Se puede extender fácilmente para persistencia en archivo .properties.
 */
public class Configuracion {

    private boolean sonidoActivado = true;
    private boolean textoRapido    = false;
    private int     tiempoMaximo   = 30;
    private String  dificultad     = "NORMAL";

    // --- Getters y Setters ---

    public boolean isSonidoActivado()         { return sonidoActivado; }
    public void setSonidoActivado(boolean v)  { this.sonidoActivado = v; }

    public boolean isTextoRapido()            { return textoRapido; }
    public void setTextoRapido(boolean v)     { this.textoRapido = v; }

    public int getTiempoMaximo()              { return tiempoMaximo; }
    public void setTiempoMaximo(int v)        { this.tiempoMaximo = Math.max(10, Math.min(60, v)); }

    public String getDificultad()             { return dificultad; }
    public void setDificultad(String v)       { this.dificultad = v; }
}