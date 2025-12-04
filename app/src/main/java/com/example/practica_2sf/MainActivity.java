package com.example.practica_2sf;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.media.MediaPlayer;

public class MainActivity extends Activity {

    private GLSurfaceView glView;   // Vista principal
    private MyGLRenderer myGLRenderer;
    private MediaPlayer mediaPlayer;

    private boolean hudVisible = true; // Estado para mostrar/ocultar HUDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar GLSurfaceView y Renderer
        glView = new GLSurfaceView(this);
        glView.setRenderer(myGLRenderer = new MyGLRenderer(this));
        this.setContentView(glView);

        // Configurar la música de fondo
        mediaPlayer = MediaPlayer.create(this, R.raw.musica);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();

        // Pausar la música al pausar la actividad
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();

        // Reanudar la música al reanudar la actividad
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Liberar los recursos del reproductor de música
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX(); // Coordenada X del toque
        float y = e.getY(); // Coordenada Y del toque

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // Normalizar las coordenadas y actualizar la posición de la nave
                float normalizedX = (x / (float) glView.getWidth()) * 40 - 20;
                float normalizedY = -((y / (float) glView.getHeight()) * 22.5f - 11.25f);

                myGLRenderer.getNau().actualizarPosicionX(normalizedX);
                myGLRenderer.getNau().actualizarPosicionY(normalizedY);
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Alternar entre los modos de cámara
            myGLRenderer.toggleCameraMode();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Alternar visibilidad de los HUDs (mostrar/ocultar)
            hudVisible = !hudVisible;
            myGLRenderer.setHudVisible(hudVisible);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
