package com.cybermath;

import java.io.*;

public class GestorArchivos {

    // Método privado para obtener una ruta segura y absoluta en Windows/Mac
    private static String getRutaArchivo(int slot) {
        // "user.home" busca automáticamente la carpeta del usuario (Ej: C:\Users\brand)
        String rutaFolder = System.getProperty("user.home") + File.separator + "CyberMathSaves";
        File folder = new File(rutaFolder);

        // Si la carpeta no existe, la crea
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Retorna la ruta completa al archivo .dat
        return rutaFolder + File.separator + "slot_" + slot + ".dat";
    }

    public static void guardarUsuario(Usuario usuario, int slot) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getRutaArchivo(slot)))) {
            out.writeObject(usuario);
            System.out.println(">> [SISTEMA] Partida guardada correctamente en el Slot " + slot);
        } catch (Exception e) {
            System.out.println(">> [ERROR CRÍTICO AL GUARDAR]: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Usuario cargarUsuario(int slot) {
        File archivo = new File(getRutaArchivo(slot));
        if (archivo.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
                return (Usuario) in.readObject();
            } catch (Exception e) {
                System.out.println(">> [ERROR AL CARGAR]: " + e.getMessage());
            }
        }
        return null; // Retorna null si el slot está vacío o el archivo no existe
    }

    public static void borrarUsuario(int slot) {
        File archivo = new File(getRutaArchivo(slot));
        if (archivo.exists()) {
            archivo.delete();
            System.out.println(">> [SISTEMA] Perfil del Slot " + slot + " purgado.");
        }
    }
}