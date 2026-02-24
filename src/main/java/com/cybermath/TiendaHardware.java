package com.cybermath;

import java.util.ArrayList;
import java.util.List;

public class TiendaHardware {
    private List<ItemHardware> catalogo;

    public TiendaHardware() {
        catalogo = new ArrayList<>();
        catalogo.add(new ItemHardware("PARCHE DE KERNEL (Recupera 30% HP)", 150, "CURAR_30"));
        catalogo.add(new ItemHardware("BACKUP DEL SISTEMA (Restaura 100% HP)", 400, "CURAR_100"));
    }

    public String comprar(int indice, Usuario comprador) {
        if (indice < 0 || indice >= catalogo.size()) return "ID Inválido.";
        ItemHardware item = catalogo.get(indice);

        if (comprador.getCriptos() >= item.getPrecio()) {
            comprador.gastarCriptos(item.getPrecio());

            if (item.getEfecto().equals("CURAR_30")) {
                comprador.curar(30);
            } else if (item.getEfecto().equals("CURAR_100")) {
                comprador.repararTotalmente();
            }
            return ">> COMPRA EXITOSA: " + item.getNombre() + " APLICADO.";
        }
        return ">> ERROR: FONDOS BTC INSUFICIENTES.";
    }

    public List<ItemHardware> getCatalogo() { return catalogo; }
}

class ItemHardware {
    private String nombre;
    private int precio;
    private String efecto;

    public ItemHardware(String nombre, int precio, String efecto) {
        this.nombre = nombre;
        this.precio = precio;
        this.efecto = efecto;
    }
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public String getEfecto() { return efecto; }
}