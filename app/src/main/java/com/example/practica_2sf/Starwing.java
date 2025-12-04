package com.example.practica_2sf;

import android.content.Context;

public class Starwing extends Object3D {

    private float posY;
    private float posX;
    private float tiltHorizontal;
    private float tiltVertical;
    private boolean isIdle = true;

    public Starwing(Context ctx, int resourceId) {
        super(ctx, resourceId);
    }

    public void actualizarPosicionY(float nuevaY) {
        // Limita entre -6 y 5
        this.posY = limitarRango(nuevaY, -6f, 5f);
    }

    public void actualizarPosicionX(float nuevaX) {
        // Limita entre -13 y 13
        this.posX = limitarRango(nuevaX, -13f, 13f);
    }

    public void definirEstadoInactivo(boolean estado) {
        this.isIdle = estado;
    }

    public boolean estaInactiva() {
        return this.isIdle;
    }

    public void ajustarInclinacionHorizontal(float inclinacion) {
        // Limita entre -25º y 25º
        this.tiltHorizontal = limitarRango(inclinacion, -25f, 25f);
    }

    public void ajustarInclinacionVertical(float inclinacion) {
        // Limita entre -25º y 25º
        this.tiltVertical = limitarRango(inclinacion, -25f, 25f);
    }

    public float obtenerPosicionY() {
        return this.posY;
    }

    public float obtenerPosicionX() {
        return this.posX;
    }

    public float obtenerInclinacionHorizontal() {
        return this.tiltHorizontal;
    }

    public float obtenerInclinacionVertical() {
        return this.tiltVertical;
    }

    public float calcularFluctuacionVertical() {
        // Añade un movimiento oscilatorio en Y basado en el tiempo
        return this.posY + (float) Math.sin(2 * Math.PI * System.nanoTime() / 2_000_000_000.0) * 0.1f;
    }

    private float limitarRango(float valor, float minimo, float maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }
}

