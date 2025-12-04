package com.example.practica_2sf;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Obstacle extends Object3D {
    private float speed;  // Velocidad de movimiento
    private float angle;  // Ángulo actual
    private float rotationSpeed; // Velocidad de rotación
    private float scale;  // Escala
    private float x, y, z; // Posición

    public Obstacle(Context context, int modelResourceId) {
        super(context, modelResourceId);
        this.speed = 0.0f;
        this.angle = 0.0f;
        this.rotationSpeed = 0.0f;
        this.scale = 1.0f;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void updatePosition() {
        this.z += speed; // Mueve el obstáculo hacia la cámara
        if (this.z > 10.0f) {
            // Si pasa del rango visible, reinicia la posición
            this.z = (float) (Math.random() * 13 - 25);
            this.x = (float) (Math.random() * 28 - 14);
            this.y = (float) (Math.random() * 12 - 6);
        }
    }

    public void updateRotation() {
        this.angle += rotationSpeed;
        if (this.angle > 360.0f) {
            this.angle -= 360.0f;
        } else if (this.angle < 0.0f) {
            this.angle += 360.0f; // Evita ángulos negativos
        }
    }

    @Override
    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glScalef(scale, scale, scale);
        gl.glRotatef(angle, 0, 1, 0);
        gl.glRotatef(angle / 2, 1, 0, 0);
        super.draw(gl); // Llama al método de dibujo de la clase base
        gl.glPopMatrix();
    }
}
