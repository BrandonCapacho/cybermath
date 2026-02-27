package com.cybermath;

public class Configuracion {

    private boolean sonidoActivado = true;
    private double  volumen        = 0.5; // Volumen inicial al 50%
    private boolean textoRapido    = false;
    private int     tiempoMaximo   = 30;
    private String  dificultad     = "NORMAL";

    public boolean isSonidoActivado()         { return sonidoActivado; }
    public void setSonidoActivado(boolean v)  { this.sonidoActivado = v; }

    public double getVolumen()                { return volumen; }
    public void setVolumen(double v)          { this.volumen = Math.max(0.0, Math.min(1.0, v)); }

    public boolean isTextoRapido()            { return textoRapido; }
    public void setTextoRapido(boolean v)     { this.textoRapido = v; }

    public int getTiempoMaximo()              { return tiempoMaximo; }
    public void setTiempoMaximo(int v)        { this.tiempoMaximo = Math.max(10, Math.min(60, v)); }

    public String getDificultad()             { return dificultad; }
    public void setDificultad(String v)       { this.dificultad = v; }
}