package com.sparkfengbo.airhockey

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.sparkfengbo.airhockey.util.LoggerConfig
import com.sparkfengbo.airhockey.util.ShaderHelper
import com.sparkfengbo.airhockey.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * http://media.pragprog.com/titles/kbogla/code/AirHockey1/src/com/airhockey/android/AirHockeyRenderer.java
 */
class AirHockeyRenderer(baseContext: Context) : GLSurfaceView.Renderer {

    companion object {
        var POSITION_COMPONENT_COUNT = 2
        var BYTES_PER_FLOAT = 4
        var U_COLOR = "u_Color"
        var A_POSITION = "a_Position"
    }

    private var vertexData: FloatBuffer
    private var context: Context = baseContext
    private var program = 0
    private var uColorLocation = 0
    private var aPositionLocation = 0


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
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                // Line 1
                -0.5f, 0f, 0.5f, 0f,
                // Mallets
                0f, -0.25f,
                0f, 0.25f
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

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        glDrawArrays(GL_TRIANGLES, 0, 6)

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw the first mallet blue.
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
        // Draw the second mallet red.
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        val vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader)
        val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader)

        var vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        var fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }
        glUseProgram(program)

        uColorLocation = glGetUniformLocation(program, U_COLOR)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData);

        glEnableVertexAttribArray(aPositionLocation);


    }


}