package com.cybermath;

/**
 * Modelo del jugador.
 * Ya no implementa Serializable — la persistencia es gestionada por GestorDB (H2).
 */
public class Usuario {

    private String  nombre;
    private int     integridad;
    private int     criptos;
    private boolean[] nivelesCompletados;

    public Usuario(String nombre) {
        this.nombre             = nombre;
        this.integridad         = 100;
        this.criptos            = 0;
        this.nivelesCompletados = new boolean[55]; // capacidad para 50 niveles + margen
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public String  getNombre()     { return nombre; }
    public int     getIntegridad() { return integridad; }
    public int     getCriptos()    { return criptos; }

    // -------------------------------------------------------------------------
    // Setters (necesarios para GestorDB al reconstruir desde la BD)
    // -------------------------------------------------------------------------
    public void setIntegridad(int v) { this.integridad = Math.max(0, Math.min(100, v)); }
    public void setCriptos(int v)    { this.criptos    = Math.max(0, v); }

    // -------------------------------------------------------------------------
    // Mecánicas de juego
    // -------------------------------------------------------------------------
    public void recibirDaño()              { this.integridad = Math.max(0, this.integridad - 34); }
    public void curar(int cantidad)        { this.integridad = Math.min(100, this.integridad + cantidad); }
    public void repararTotalmente()        { this.integridad = 100; }
    public void sumarCriptos(int cant)     { this.criptos   += cant; }
    public void gastarCriptos(int cant)    { this.criptos    = Math.max(0, this.criptos - cant); }

    // -------------------------------------------------------------------------
    // Progreso de niveles
    // -------------------------------------------------------------------------
    public boolean isNivelCompletado(int nivel) {
        if (nivel < 0 || nivel >= nivelesCompletados.length) return false;
        return nivelesCompletados[nivel];
    }

    public void completarNivel(int nivel) {
        if (nivel >= 0 && nivel < nivelesCompletados.length)
            nivelesCompletados[nivel] = true;
    }

    public int getNivelesSuperados() {
        int count = 0;
        for (boolean b : nivelesCompletados) if (b) count++;
        return count;
    }
}