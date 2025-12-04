package com.example.practica_2sf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Dots {
    private static final int MAX_DOTS = 200; // Número de puntos
    private static final float START_Z = -50f; // Posición inicial Z
    private static final float CAMERA_Z = 10f; // Posición límite Z (frontal)
    private static final float POINT_SIZE = 5.0f; // Tamaño de los puntos
    private static final float SPEED = 1.0f; // Velocidad de movimiento
    private List<float[]> dots; // Lista de puntos (posición X, Y, Z)

    public Dots() {
        dots = new ArrayList<>();
        initializeDots();
    }

    private void initializeDots() {
        dots.clear();
        for (int i = 0; i < MAX_DOTS; i++) {
            float x = (float) (Math.random() * 20 - 10); // Posición X aleatoria (-10 a 10)
            float y = (float) (Math.random() * 20 - 10); // Posición Y aleatoria (-10 a 10)
            float z = START_Z + (float) (Math.random() * 20); // Posición Z inicial
            dots.add(new float[]{x, y, z});
        }
    }

    public void updateDots() {
        for (float[] point : dots) {
            point[2] += SPEED;
        }

        // Cada frame, añade un nuevo punto al fondo
        if (dots.size() < MAX_DOTS) {
            float x = (float) (Math.random() * 20 - 10);
            float y = (float) (Math.random() * 20 - 10);
            float z = START_Z;
            dots.add(new float[]{x, y, z});
        }

        // Eliminar puntos que han pasado la cámara
        dots.removeIf(point -> point[2] > CAMERA_Z);
    }


    public void drawDots(GL10 gl) {
        for (float[] point : dots) {
            drawDot(gl, point[0], point[1], point[2]);
        }
    }

    private void drawDot(GL10 gl, float x, float y, float z) {
        float[] vertex = {x, y, z};

        // Preparar buffer de vértices
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        // Establecer color y tamaño del punto
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Blanco
        gl.glPointSize(POINT_SIZE);

        // Dibujar punto
        gl.glDrawArrays(GL10.GL_POINTS, 0, 1);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
