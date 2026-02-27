package com.cybermath;

import java.util.ArrayList;
import java.util.List;

public class TiendaHardware {
    private List<ItemHardware> catalogo;

    public TiendaHardware() {
        catalogo = new ArrayList<>();
        catalogo.add(new ItemHardware("PARCHE DE KERNEL (Recupera 30% HP)", 150, "CURAR_30"));
        catalogo.add(new ItemHardware("BACKUP DEL SISTEMA (Restaura 100% HP)", 400, "CURAR_100"));
        catalogo.add(new ItemHardware("MÓDULO RAM (Agrega +2s a cada reto)", 300, "UPGRADE_RAM"));
        catalogo.add(new ItemHardware("BOTNET MINERO (Genera BTC pasivamente)", 500, "ADD_MINER"));
        catalogo.add(new ItemHardware("TEMA UI: ÁMBAR RETRO (Años 80)", 250, "TEMA_AMBAR"));
        catalogo.add(new ItemHardware("TEMA UI: CIANITO CORPORATIVO", 250, "TEMA_AZUL"));
        catalogo.add(new ItemHardware("CASETTE: SYNTHWAVE OVERDRIVE", 200, "PISTA_1"));
        catalogo.add(new ItemHardware("CASETTE: DARK ELECTRO VOID", 200, "PISTA_2"));
    }

    public String comprar(int indice, Usuario comprador) {
        if (indice < 0 || indice >= catalogo.size()) return "ID Inválido.";
        ItemHardware item = catalogo.get(indice);

        if (comprador.getCriptos() >= item.getPrecio()) {
            comprador.gastarCriptos(item.getPrecio());
            switch (item.getEfecto()) {
                case "CURAR_30": comprador.curar(30); break;
                case "CURAR_100": comprador.repararTotalmente(); break;
                case "UPGRADE_RAM": comprador.mejorarRAM(); break;
                case "ADD_MINER": comprador.agregarMinero(); break;
                case "TEMA_AMBAR": comprador.setTemaUI("AMBAR"); break;
                case "TEMA_AZUL": comprador.setTemaUI("AZUL"); break;
                case "PISTA_1": comprador.desbloquearPista(1); break;
                case "PISTA_2": comprador.desbloquearPista(2); break;
            }
            return ">> COMPRA EXITOSA: " + item.getNombre() + " APLICADO.";
        }
        return ">> ERROR: FONDOS BTC INSUFICIENTES.";
    }

    public List<ItemHardware> getCatalogo() { return catalogo; }
}

class ItemHardware {
    private String nombre; private int precio; private String efecto;
    public ItemHardware(String n, int p, String e) { this.nombre = n; this.precio = p; this.efecto = e; }
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public String getEfecto() { return efecto; }
}