package com.cybermath;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String  nombre;
    private int     integridad;
    private int     criptos;
    private boolean[] nivelesCompletados;

    private int     energia;
    private int     minerosBTC;
    private int     bonusTiempo;
    private String  temaUI;
    private boolean[] temasDesbloqueados;
    private boolean[] loreDesbloqueado;

    private int     deepWebScore;
    private boolean[] pistasDesbloqueadas;

    // Sistema de castigo: lista de niveles temporalmente bloqueados
    private List<Integer> nivelesCastigados;

    public Usuario(String nombre) {
        this.nombre              = nombre;
        this.integridad          = 100;
        this.criptos             = 0;
        this.energia             = 0;
        this.minerosBTC          = 0;
        this.bonusTiempo         = 0;
        this.temaUI              = "NEON";
        this.nivelesCompletados  = new boolean[55];
        this.loreDesbloqueado    = new boolean[5];
        this.temasDesbloqueados  = new boolean[]{true, false, false};
        this.deepWebScore        = 0;
        this.pistasDesbloqueadas = new boolean[3];
        this.pistasDesbloqueadas[0] = true;
        this.nivelesCastigados   = new ArrayList<>();
    }

    public String  getNombre()       { return nombre; }
    public int     getIntegridad()   { return integridad; }
    public int     getCriptos()      { return criptos; }
    public int     getEnergia()      { return energia; }
    public int     getMineros()      { return minerosBTC; }
    public int     getBonusTiempo()  { return bonusTiempo; }
    public String  getTemaUI()       { return temaUI; }
    public int     getDeepWebScore() { return deepWebScore; }

    public void setIntegridad(int v)   { this.integridad  = Math.max(0, Math.min(100, v)); }
    public void setCriptos(int v)      { this.criptos     = Math.max(0, v); }
    public void setEnergia(int v)      { this.energia     = Math.max(0, Math.min(100, v)); }
    public void setMineros(int v)      { this.minerosBTC  = Math.max(0, v); }
    public void setBonusTiempo(int v)  { this.bonusTiempo = Math.max(0, v); }
    public void setTemaUI(String t)    { this.temaUI      = t; }
    public void setDeepWebScore(int v) { this.deepWebScore = Math.max(this.deepWebScore, v); }

    // -10% HP por derrota (nuevo sistema)
    public void recibirDaño()            { this.integridad = Math.max(0, this.integridad - 10); }
    // -34% HP legacy (modos especiales)
    public void recibirDañoFuerte()      { this.integridad = Math.max(0, this.integridad - 34); }

    public void curar(int cantidad)      { this.integridad = Math.min(100, this.integridad + cantidad); }
    public void repararTotalmente()      { this.integridad = 100; }
    public void sumarCriptos(int cant)   { this.criptos   += cant; }
    public void gastarCriptos(int cant)  { this.criptos    = Math.max(0, this.criptos - cant); }
    public void cargarEnergia(int cant)  { this.energia    = Math.min(100, this.energia + cant); }
    public void consumirEnergia(int cant){ this.energia    = Math.max(0, this.energia - cant); }
    public void agregarMinero()          { this.minerosBTC++; }
    public void mejorarRAM()             { this.bonusTiempo += 2; }

    public boolean isNivelCompletado(int nivel) {
        if (nivel < 0 || nivel >= nivelesCompletados.length) return false;
        return nivelesCompletados[nivel];
    }
    public void completarNivel(int nivel) {
        if (nivel >= 0 && nivel < nivelesCompletados.length) {
            nivelesCompletados[nivel] = true;
            nivelesCastigados.remove(Integer.valueOf(nivel));
        }
    }
    public int getNivelesSuperados() {
        int count = 0;
        for (boolean b : nivelesCompletados) if (b) count++;
        return count;
    }

    // =========================================================================
    // SISTEMA DE CASTIGO
    // =========================================================================

    /**
     * Penalización por derrota:
     * - -10% integridad
     * - integridad >= 30: bloquea los últimos 3 nodos completados antes del nivel fallado
     * - integridad < 30: bloquea el capítulo completo del nivel fallado
     */
    public void aplicarCastigo(int nivelFallado) {
        recibirDaño();
        if (integridad < 30) {
            int[] rango = getRangoCapitulo(nivelFallado);
            for (int n = rango[0]; n <= rango[1]; n++) {
                if (isNivelCompletado(n) && !nivelesCastigados.contains(n))
                    nivelesCastigados.add(n);
            }
        } else {
            int bloqueados = 0;
            for (int n = nivelFallado - 1; n >= 1 && bloqueados < 3; n--) {
                if (isNivelCompletado(n) && !nivelesCastigados.contains(n)) {
                    nivelesCastigados.add(n);
                    bloqueados++;
                }
            }
        }
    }

    public boolean isNivelCastigado(int nivel)  { return nivelesCastigados.contains(nivel); }
    public void liberarCastigo(int nivel)        { nivelesCastigados.remove(Integer.valueOf(nivel)); }
    public void liberarTodosLosCastigos()        { nivelesCastigados.clear(); }
    public List<Integer> getNivelesCastigados()  { return new ArrayList<>(nivelesCastigados); }
    public void setNivelesCastigados(List<Integer> lista) {
        this.nivelesCastigados = lista != null ? lista : new ArrayList<>();
    }

    private int[] getRangoCapitulo(int nivel) {
        if (nivel <= 1)  return new int[]{1,  1};
        if (nivel <= 11) return new int[]{2,  11};
        if (nivel <= 21) return new int[]{12, 21};
        if (nivel <= 31) return new int[]{22, 31};
        if (nivel <= 41) return new int[]{32, 41};
        return                  new int[]{42, 50};
    }

    // =========================================================================
    // LORE, TEMAS, MÚSICA
    // =========================================================================

    public boolean isLoreDesbloqueado(int index) {
        if (index < 0 || index >= loreDesbloqueado.length) return false;
        return loreDesbloqueado[index];
    }
    public void desbloquearLore(int index) {
        if (index >= 0 && index < loreDesbloqueado.length) loreDesbloqueado[index] = true;
    }

    public boolean isTemaDesbloqueado(String tema) {
        switch (tema) {
            case "AMBAR": return temasDesbloqueados[1];
            case "AZUL":  return temasDesbloqueados[2];
            default:      return true;
        }
    }
    public void desbloquearTema(String tema) {
        if (tema.equals("AMBAR")) temasDesbloqueados[1] = true;
        if (tema.equals("AZUL"))  temasDesbloqueados[2] = true;
    }

    public boolean isPistaDesbloqueada(int index) {
        if (index < 0 || index >= pistasDesbloqueadas.length) return false;
        return pistasDesbloqueadas[index];
    }
    public void desbloquearPista(int index) {
        if (index >= 0 && index < pistasDesbloqueadas.length) pistasDesbloqueadas[index] = true;
    }

    // =========================================================================
    // GOD MODE
    // =========================================================================
    public void activarGodMode() {
        this.integridad = 100;
        this.criptos    = 999999;
        this.energia    = 100;
        this.minerosBTC = 50;
        this.bonusTiempo = 60;
        for (int i = 0; i <= 50; i++) this.nivelesCompletados[i] = true;
        for (int i = 0; i < 5;  i++) this.loreDesbloqueado[i]   = true;
        for (int i = 0; i < 3;  i++) this.temasDesbloqueados[i] = true;
        for (int i = 0; i < 3;  i++) this.pistasDesbloqueadas[i] = true;
        this.nivelesCastigados.clear();
    }
}