package com.sparkfengbo.airhockey

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer : GLSurfaceView.Renderer {

    companion object {
        var POSITION_COMPONENT_COUNT = 2

        var BYTES_PER_FLOAT = 4

    }

    private var vertexData : FloatBuffer

    init {
        var tableVertices: FloatArray = floatArrayOf(
                0f, 0f,
                0f, 14f,
                9f, 14f,
                9f, 0f
        )

        /**
         * 逆时针方向
         * 比较好区分 三角形的朝向
         */
        var tableVerticesWithTriangles: FloatArray = floatArrayOf(
                // Triangle 1
                0f, 0f,
                9f, 14f,
                0f, 14f,
                // Triangle 2
                0f, 0f,
                9f, 0f,
                9f, 14f,

                // Line 1
                0f, 7f, 9f, 7f,
                // Mallets
                4.5f, 2f, 4.5f, 12f
        )


        /**
         * 为什么不能直接用Float数组？
         * OPenGL运行在Native层，Float数组在JVM中
         */
        vertexData = ByteBuffer
                //allocateDirect:  this memory will not be managed by the garbage collector
                .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)











    }


    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }


}