package com.ma.schiffeversenken.view.opengl;

import android.opengl.GLES20;
import android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by xps on 15.11.2014.
 */
public class Triangle {

    private FloutBuffer vertexBuffer;
    private float vertices[] =

    {
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.0f, -0.5f, 0.0f
    } ;

    private float color[] = new float[]{0.0f, 0.6f, 1.0f, 1.0f};

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {"+
                    " gl_Position  vPosition;"+
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {"+
                    " gl_FragColor  vColor;"+
                    "}";
    private int shaderProgram;

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,shaderCode);
        GLES.glCompileShader(shader);
        return shader;

    }

    public Triangle(){
        ByteBurffer bb ByteBuffer.allocateDirect(vertices.length *4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer bb.asFloatbuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        int vertexShader loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
    int fragmentShader  LoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        shaderProgram GLES20.glCreateProgram();
    GLES20.glAttachShader(shaderProgram, vertexShader);
    GLES20.glAttachShader(shaderProgram,fragmentShader);
    GLES20.gllinkProgram(shaderProgram);

    }

    public void draw() {
        GLES20.glUseProgram(shaderProgram);

        int positionAttrib  GLES20.glGetAttriblocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionAttrib);

        GLES20.glVertexAttribPointer(positionAttrib, 3, GLES20.GL_FLOAT,false,0,vertexBuffer);

        int colerUniform GLES20.glGetUniformLocation(shaderProgram,"vColor");

        GLES20.glUniform4fv(colorUniform,1,color,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,vertices.length/3);
        GLES20.glDisableVertexAttribArray(positionAttrib);

    }


}

