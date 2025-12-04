package com.example.practica_2sf;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.util.ArrayList;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private float width, height;
    private Light light;

    Context context;
    int angle = 0;
    long startTime;

    // Objeto de la pantalla
    private Background fons;
    private boolean isOrthographicView = false; // Controla si la vista es ortogonal

    private Starwing nau;
    private Dots dots;
    private ArrayList<Object3D> obstacles;
    private Background HUD1;
    private Background HUD2;
    private Background HUD3;
    private Background HUDWin;
    private boolean hudVisible = true; // Controla la visibilidad de los HUDs
    private int frameCounter = 0; // Contador de fotogramas
    private static final int CHANGE_INTERVAL_FRAMES = 60; // Cambiar cada 60 fotogramas
    private int maxAsteroidsOnScreen = 10; // Máximo de obstaculos
    private boolean isPOV = false; // Modo cámara: false = normal, true = POV

    public MyGLRenderer(Context context) {
        this.context = context;
    }

    public void toggleCameraMode() {
        isPOV = !isPOV; // Cambiar entre los modos
    }

    private int currentWin = 0;            // Índice actual de la textura


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Configuracion inicial
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_NORMALIZE);

        // Ini luz
        light = new Light(gl, GL10.GL_LIGHT0);
        light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
        light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f});
        light.setDiffuseColor(new float[]{1, 1, 1});

        // Objetos principales
        nau = new Starwing(context, R.raw.starwing);
        nau.loadTexture(gl, context, R.raw.textnave);
        obstacles = new ArrayList<>();
        initObstacles(gl);;
        fons = new Background(context, R.raw.fons);
        fons.loadTexture(gl, context);
        HUD1 = new Background(context, R.raw.escut0);
        HUD1.loadTexture(gl, context);
        HUD2 = new Background(context, R.raw.vida0);
        HUD2.loadTexture(gl, context);
        HUD3 = new Background(context, R.raw.energia0);
        HUD3.loadTexture(gl, context);
        HUDWin = new Background(context, R.raw.final0);
        HUDWin.loadTexture(gl, context);
        dots = new Dots();

        startTime = System.currentTimeMillis();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float) width / height;

        this.width = width;
        this.height = height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, 60, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();                 // Reset
    }

    private void setPerspectiveProjection(GL10 gl) {
        isOrthographicView = false; // Establece que la vista no es ortogonal
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
        gl.glDepthMask(true);  // disable writes to Z-Buffer

        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix

        // Use perspective projection
        GLU.gluPerspective(gl, 60, (float) width / height, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();                 // Reset
    }

    private void setOrthographicProjection(GL10 gl) {
        isOrthographicView = true; // Establece que la vista es ortogonal
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-5, 5, -4, 4, -5, 5);
        gl.glDepthMask(false);  // Deshabilita escritura en el buffer Z
        gl.glDisable(GL10.GL_DEPTH_TEST);  // Deshabilita pruebas de profundidad
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }


    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (System.currentTimeMillis() - startTime > 45000) {
            setPerspectiveProjection(gl);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            setOrthographicProjection(gl);
            drawHUDWin(gl);
            if (angle == 0) {
                updateHUDWin();
            }
            angle = (++angle) % 15;
        } else {
            setPerspectiveProjection(gl);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();

            //Obstaculos
            drawObstacles(gl);

            setupCamera(gl);
            drawNau(gl);

            // Dibujar dots
            dots.updateDots();  // Actualitza les posicions dels punts
            dots.drawDots(gl);  // Dibuixa els punts

            // Dibujar fondo
            drawBackground(gl);
            setOrthographicProjection(gl);

            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL10.GL_DEPTH_TEST); // Evita que el HUD sea afectado por la profundidad

            // Dibujar los HUDs con el método genérico
            frameCounter++;
            if (frameCounter % CHANGE_INTERVAL_FRAMES == 0) {
                HUD1 = updateHUD(gl, HUD1, getTextureByState(new int[]{
                        R.raw.escut0, R.raw.escut1, R.raw.escut2, R.raw.escut3, R.raw.escut4}));

                HUD2 = updateHUD(gl, HUD2, getTextureByState(new int[]{
                        R.raw.vida0, R.raw.vida1, R.raw.vida2, R.raw.vida3, R.raw.vida4, R.raw.vida5}));

                HUD3 = updateHUD(gl, HUD3, getTextureByState(new int[]{
                        R.raw.energia0, R.raw.energia1, R.raw.energia2, R.raw.energia3, R.raw.energia4}));
            }
            // Dibujar los HUDs
          //  drawHUD(gl, HUD1, HUD1.getTextureResource(), 3.5f, 3.0f, 0.675f, 0.60f); // HUD Escudo
          //  drawHUD(gl, HUD2, HUD2.getTextureResource(), 3.5f, 1.7f, 0.675f, 0.60f); // HUD Vida
         //   drawHUD(gl, HUD3, HUD3.getTextureResource(), -3.5f, -3.5f, 0.675f, 0.60f); // HUD Energía
// Modificar la parte del dibujo de HUDs para verificar visibilidad
            if (hudVisible) {
                drawHUD(gl, HUD1, HUD1.getTextureResource(), 3.5f, 3.0f, 0.675f, 0.60f); // HUD Escudo
                drawHUD(gl, HUD2, HUD2.getTextureResource(), 3.5f, 1.7f, 0.675f, 0.60f); // HUD Vida
                drawHUD(gl, HUD3, HUD3.getTextureResource(), -3.5f, -3.5f, 0.675f, 0.60f); // HUD Energía
            }

            gl.glEnable(GL10.GL_DEPTH_TEST); // Vuelve a habilitar el test de profundidad después
           //updateHUD();
            gl.glDisable(GL10.GL_BLEND);
            angle = (++angle) % 360;
        }
    }

    private void setupCamera(GL10 gl) {
        if (isPOV) {
            GLU.gluLookAt(gl,
                    nau.obtenerPosicionX(), nau.obtenerPosicionY() + 5, 10, // Cámara detrás de la nave
                    nau.obtenerPosicionX(), nau.obtenerPosicionY(), 0,      // Mirando hacia adelante
                    0, 1, 0);
        } else {
            GLU.gluLookAt(gl,
                    0, 0, 10,
                    0, 0, 0,
                    0, 1, 0);
        }
    }

    private void drawNau(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(nau.obtenerPosicionX(), nau.calcularFluctuacionVertical(), 0);
        gl.glRotatef(nau.obtenerInclinacionVertical(), 1, 0, 0);
        gl.glRotatef(nau.obtenerInclinacionHorizontal(), 0, 0, 1);
        gl.glScalef(2.5f, 2.5f, 2.5f);
        nau.draw(gl);
        gl.glPopMatrix();
    }

    private void drawBackground(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, -10);
        gl.glScalef(22, 13.375f, 1);
        fons.draw(gl);
        gl.glPopMatrix();
    }

    private void initObstacles(GL10 gl) {
        int astronautModel = R.raw.astronaute; // Modelo para el astronauta
        int asteroidModel = R.raw.asteroideee; // Modelo para el asteroide
        int astronautTexture = R.raw.textastronauta; // Textura para el astronauta
        int asteroidTexture = R.raw.texturameteo; // Textura para el asteroide

        obstacles.clear(); // Reiniciar la lista de obstaculos
        for (int i = 0; i < maxAsteroidsOnScreen; i++) {
            // Posiciones y características aleatorias
            float x = (float) (Math.random() * 28 - 14);
            float y = (float) (Math.random() * 12 - 6);
            float z = (float) (Math.random() * 13 - 25);
            float speed = (float) (Math.random() * 0.1 + 0.05);
            float scale = (float) (Math.random() * 1.5 + 0.5);
            float rotationSpeed = 1;//(float) (Math.random() * 2 - 1); // Velocidad de rotación (-1 a 1)
            float initialAngle = 45;//(float) (Math.random() * 360); // Ángulo inicial aleatorio


            // Decidir el modelo y la textura en función de si el índice es par o impar
            int model = (i % 2 == 0) ? astronautModel : asteroidModel;
            int texture = (i % 2 == 0) ? astronautTexture : asteroidTexture;

            // Crear obstáculo
            Obstacle obstacle = new Obstacle(context, model);
            obstacle.setPosition(x, y, z);
            obstacle.setSpeed(speed);
            obstacle.setScale(scale);
            obstacle.setAngle(initialAngle);
            obstacle.setRotationSpeed(rotationSpeed);
            obstacle.loadTexture(gl, context, texture); // Asignar textura

            obstacles.add(obstacle);
        }
    }

    private void drawObstacles(GL10 gl) {
        for (Object3D asteroid : obstacles) {
            if (asteroid instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) asteroid;
                obstacle.updatePosition();  // Actualiza posición
                obstacle.updateRotation();  // Actualiza rotación
                obstacle.draw(gl);          // Dibuja el obstáculo
            }
        }
    }


    // Actualizar el HUD
    private Background updateHUD(GL10 gl, Background hud, int newTextureResource) {
        // Si el HUD no existe o tiene una textura diferente, lo actualizamos
        if (hud == null || hud.getTextureResource() != newTextureResource) {
            hud = new Background(context, newTextureResource);
            hud.loadTexture(gl, context);
        }
        return hud; // Devuelve el objeto HUD actualizado
    }


    private void drawHUD(GL10 gl, Background hud, int textureResource, float x, float y, float scaleX, float scaleY) {
        if (hud == null || hud.getTextureResource() != textureResource) { // Solo cambia si es necesario
            hud = new Background(context, textureResource);
            hud.loadTexture(gl, context);
        }
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0); // Posición personalizada
        gl.glScalef(scaleX, scaleY, 1); // Escala personalizada
        hud.draw(gl);
        gl.glPopMatrix();
    }

    private int getTextureByState(int[] textures) {
        int randomIndex = (int) (Math.random() * textures.length); // Selecciona un índice aleatorio dentro del rango de texturas
        return textures[randomIndex]; // Devuelve una textura aleatoria
    }


    private void drawHUDWin(GL10 gl) {
        // Obtener la textura correspondiente al estado actual
        int winTexture = getWinTextureByState(currentWin, new int[]{
                R.raw.final0, R.raw.final1, R.raw.final2,
                R.raw.final3, R.raw.final4, R.raw.final5, R.raw.final6
        });

        // Si HUDWin no existe o necesita actualizar su textura
        if (HUDWin == null || HUDWin.getTextureResource() != winTexture) {
            HUDWin = new Background(context, winTexture);
            HUDWin.loadTexture(gl, context);
        }

        // Dibujar el HUDWin
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0); // Centrado en pantalla
        gl.glScalef(2, 2, 1);    // Escala del HUD
        HUDWin.draw(gl);
        gl.glPopMatrix();
    }


    private void updateHUDWin() {
        // Avanzar el índice actual normalmente
        currentWin++;

        // Cuando se alcanza el último índice, rotar entre las últimas tres texturas
        if (currentWin >= 7) { // 7 es el número total de texturas
            currentWin = 3;    // Reiniciar al índice 4 para rotar entre final4, final5, final6
        }
    }

    private int getWinTextureByState(int state, int[] textures) {
        // Devolver la textura correspondiente al estado actual
        return textures[state % textures.length];
    }

    public Starwing getNau() {
        return nau;
    }
    public void setHudVisible(boolean visible) {
        this.hudVisible = visible;
    }




}