package com.cybermath;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class GestorSonido {

    private double volumen = 0.5; // Volumen por defecto

    public void setVolumen(double v) {
        this.volumen = v;
    }

    public void reproducir(String nombreArchivo) {
        try {
            URL ruta = getClass().getResource("/sounds/" + nombreArchivo);
            if (ruta != null) {
                AudioClip clip = new AudioClip(ruta.toExternalForm());
                clip.setVolume(this.volumen); // Aquí aplicamos el volumen
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
    public void playAmenaza() { reproducir("amenaza.mp3"); }
    public void playInfiltrado() { reproducir("infiltrated.mp3"); }
}