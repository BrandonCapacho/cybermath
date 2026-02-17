package com.cybermath;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class GestorSonido {

    public void reproducir(String nombreArchivo) {
        try {
            // Busca el archivo en resources/sounds/
            URL ruta = getClass().getResource("/sounds/" + nombreArchivo);
            if (ruta != null) {
                AudioClip clip = new AudioClip(ruta.toExternalForm());
                clip.play();
            } else {
                System.out.println(">> [AUDIO] No se encontró: " + nombreArchivo);
            }
        } catch (Exception e) {
            System.out.println(">> [AUDIO] Error: " + e.getMessage());
        }
    }

    public void playSuccess() { reproducir("success.mp3"); }
    public void playError() { reproducir("error.mp3"); }
    public void playIntro() { reproducir("boot.mp3"); }
}