package com.cybermath;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class GestorSonido {

    private double volumen = 0.5;
    private AudioClip bgmActual;

    public void setVolumen(double v) {
        this.volumen = v;
        if (bgmActual != null && bgmActual.isPlaying()) bgmActual.setVolume(v * 0.6); // BGM un poco más bajo que los SFX
    }

    public void reproducir(String nombreArchivo) {
        try {
            URL ruta = getClass().getResource("/sounds/" + nombreArchivo);
            if (ruta != null) {
                AudioClip clip = new AudioClip(ruta.toExternalForm());
                clip.setVolume(this.volumen);
                clip.play();
            }
        } catch (Exception e) { System.out.println(">> [AUDIO] Error: " + e.getMessage()); }
    }

    public void reproducirBGM(int track) {
        if (bgmActual != null) bgmActual.stop();
        try {
            // Reutilizamos audios existentes para no crashear si no tienes los mp3 nuevos aún
            String archivo = (track == 1) ? "infiltrated.mp3" : (track == 2) ? "boot.mp3" : null;
            if (archivo == null) return;

            URL ruta = getClass().getResource("/sounds/" + archivo);
            if (ruta != null) {
                bgmActual = new AudioClip(ruta.toExternalForm());
                bgmActual.setVolume(this.volumen * 0.6);
                bgmActual.setCycleCount(AudioClip.INDEFINITE);
                bgmActual.play();
            }
        } catch (Exception e) {}
    }

    public void detenerBGM() {
        if (bgmActual != null) bgmActual.stop();
    }

    public void playSuccess() { reproducir("success.mp3"); }
    public void playError() { reproducir("error.mp3"); }
    public void playIntro() { reproducir("boot.mp3"); }
    public void playAmenaza() { reproducir("amenaza.mp3"); }
    public void playInfiltrado() { reproducir("infiltrated.mp3"); }
}