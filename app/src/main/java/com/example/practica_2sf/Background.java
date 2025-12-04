package com.example.practica_2sf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;




import java.io.IOException;


public class Background {

    private FloatBuffer vertexBuffer; // Buffer para vértices
    private FloatBuffer textureBuffer; // Buffer para coordenadas de textura
    private int[] textureIDs = new int[1]; // ID de la textura
    private int textureResource; // Recurso de textura
    private Context context; // Contexto de Android

    // Constructor
    public Background(Context context, int textureResource) {
        this.context = context;
        this.textureResource = textureResource;

        // Definir los vértices para un rectángulo que cubre el fondo
        float[] vertices = {
                -1.5f, -1.0f, 0f, // Inferior izquierda
                1.5f, -1.0f, 0f, // Inferior derecha
                -1.5f,  1.0f, 0f, // Superior izquierda
                1.5f,  1.0f, 0f  // Superior derecha
        };

        // Coordenadas de textura
        float[] textureCoords = {
                0.0f, 1.0f, // Inferior izquierda
                1.0f, 1.0f, // Inferior derecha
                0.0f, 0.0f, // Superior izquierda
                1.0f, 0.0f  // Superior derecha
        };

        // Crear el buffer para los vértices
        ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Crear el buffer para las coordenadas de textura
        ByteBuffer tb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tb.order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);
    }

    // Método para cargar la textura desde los recursos

    // Método para dibujar el fondo
    public void draw(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND); // Habilitar blending
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); // Configurar blending

        gl.glEnable(GL10.GL_TEXTURE_2D); // Habilitar texturas
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]); // Asociar la textura

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); // Habilitar array de vértices
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY); // Habilitar array de textura

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // Asociar buffer de vértices
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer); // Asociar buffer de textura

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4); // Dibujar el fondo como un rectángulo

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); // Deshabilitar array de vértices
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY); // Deshabilitar array de textura
        gl.glDisable(GL10.GL_TEXTURE_2D); // Deshabilitar texturas
        gl.glDisable(GL10.GL_BLEND); // Deshabilitar blending
    }

    // Cargar textura desde un recurso
    public void loadTexture(GL10 gl, Context context) {
        gl.glGenTextures(1, textureIDs, 0); // Generar ID de textura

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]); // Asociar ID de textura
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);


        // Abrir imagen de textura desde recursos
        InputStream istream = context.getResources().openRawResource(textureResource);
        Bitmap bitmap;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Asegura soporte para canal alfa
            bitmap = BitmapFactory.decodeStream(istream, null, options);

        } finally {
            try {
                istream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Aplicar la imagen a la textura
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    // Método para obtener el recurso de textura asociado
    public int getTextureResource() {
        return textureResource;
    }



}
