package com.cybermath;

public class Usuario {

    private String  nombre;
    private int     integridad;
    private int     criptos;
    private boolean[] nivelesCompletados;

    private int energia;
    private int minerosBTC;
    private int bonusTiempo;
    private String temaUI;
    private boolean[] loreDesbloqueado;

    // --- NUEVO: DEEP WEB Y MÚSICA ---
    private int deepWebScore;
    private boolean[] pistasDesbloqueadas;

    public Usuario(String nombre) {
        this.nombre             = nombre;
        this.integridad         = 100;
        this.criptos            = 0;
        this.energia            = 0;
        this.minerosBTC         = 0;
        this.bonusTiempo        = 0;
        this.temaUI             = "NEON";
        this.nivelesCompletados = new boolean[55];
        this.loreDesbloqueado   = new boolean[5];

        this.deepWebScore       = 0;
        this.pistasDesbloqueadas = new boolean[3];
        this.pistasDesbloqueadas[0] = true; // Pista base siempre desbloqueada
    }

    public String  getNombre()     { return nombre; }
    public int     getIntegridad() { return integridad; }
    public int     getCriptos()    { return criptos; }
    public int     getEnergia()    { return energia; }
    public int     getMineros()    { return minerosBTC; }
    public int     getBonusTiempo(){ return bonusTiempo; }
    public String  getTemaUI()     { return temaUI; }
    public int     getDeepWebScore() { return deepWebScore; }

    public void setIntegridad(int v) { this.integridad = Math.max(0, Math.min(100, v)); }
    public void setCriptos(int v)    { this.criptos    = Math.max(0, v); }
    public void setEnergia(int v)    { this.energia    = Math.max(0, Math.min(100, v)); }
    public void setMineros(int v)    { this.minerosBTC = Math.max(0, v); }
    public void setBonusTiempo(int v){ this.bonusTiempo = Math.max(0, v); }
    public void setTemaUI(String t)  { this.temaUI     = t; }
    public void setDeepWebScore(int v){ this.deepWebScore = Math.max(this.deepWebScore, v); } // Guarda solo el récord máximo

    public void recibirDaño()              { this.integridad = Math.max(0, this.integridad - 34); }
    public void curar(int cantidad)        { this.integridad = Math.min(100, this.integridad + cantidad); }
    public void repararTotalmente()        { this.integridad = 100; }
    public void sumarCriptos(int cant)     { this.criptos   += cant; }
    public void gastarCriptos(int cant)    { this.criptos    = Math.max(0, this.criptos - cant); }
    public void cargarEnergia(int cant)    { this.energia    = Math.min(100, this.energia + cant); }
    public void consumirEnergia(int cant)  { this.energia    = Math.max(0, this.energia - cant); }
    public void agregarMinero()            { this.minerosBTC++; }
    public void mejorarRAM()               { this.bonusTiempo += 2; }

    public boolean isNivelCompletado(int nivel) {
        if (nivel < 0 || nivel >= nivelesCompletados.length) return false;
        return nivelesCompletados[nivel];
    }
    public void completarNivel(int nivel) {
        if (nivel >= 0 && nivel < nivelesCompletados.length) nivelesCompletados[nivel] = true;
    }
    public int getNivelesSuperados() {
        int count = 0;
        for (boolean b : nivelesCompletados) if (b) count++;
        return count;
    }

    public boolean isLoreDesbloqueado(int index) {
        if (index < 0 || index >= loreDesbloqueado.length) return false;
        return loreDesbloqueado[index];
    }
    public void desbloquearLore(int index) {
        if (index >= 0 && index < loreDesbloqueado.length) loreDesbloqueado[index] = true;
    }

    public boolean isPistaDesbloqueada(int index) {
        if (index < 0 || index >= pistasDesbloqueadas.length) return false;
        return pistasDesbloqueadas[index];
    }
    public void desbloquearPista(int index) {
        if (index >= 0 && index < pistasDesbloqueadas.length) pistasDesbloqueadas[index] = true;
    }

    // --- EL GOD MODE ---
    public void activarGodMode() {
        this.integridad = 100;
        this.criptos = 999999;
        this.energia = 100;
        this.minerosBTC = 50;
        this.bonusTiempo = 60;
        for (int i = 0; i <= 50; i++) this.nivelesCompletados[i] = true;
        for (int i = 0; i < 5; i++) this.loreDesbloqueado[i] = true;
        for (int i = 0; i < 3; i++) this.pistasDesbloqueadas[i] = true;
    }
}