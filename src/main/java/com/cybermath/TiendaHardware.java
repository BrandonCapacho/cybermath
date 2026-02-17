package com.cybermath;

import java.util.ArrayList;
import java.util.List;

public class TiendaHardware {
    private List<ItemHardware> catalogo;

    public TiendaHardware() {
        catalogo = new ArrayList<>();
        // Items temáticos de hardware para tu juego
        catalogo.add(new ItemHardware("Módulo RAM 8GB", 150, "REPARAR"));
        catalogo.add(new ItemHardware("SSD NVMe", 300, "MEJORAR_VELOCIDAD"));
    }

    public String comprar(int indice, Usuario comprador) {
        if (indice < 0 || indice >= catalogo.size()) return "ID Inválido.";

        ItemHardware item = catalogo.get(indice);

        if (comprador.getCriptos() >= item.getPrecio()) {
            comprador.gastarCriptos(item.getPrecio());
            if (item.getEfecto().equals("REPARAR")) {
                comprador.repararTotalmente();
            }
            return ">> COMPRA EXITOSA: " + item.getNombre();
        }
        return ">> ERROR: CRIPTOS INSUFICIENTES";
    }

    public List<ItemHardware> getCatalogo() { return catalogo; }
}

// Clase de apoyo para los objetos de la tienda
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