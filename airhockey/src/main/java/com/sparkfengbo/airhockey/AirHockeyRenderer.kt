package com.sparkfengbo.airhockey

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
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
        var A_COLOR = "a_Color"
        var U_MATRIX = "u_Matrix";
        var COLOR_COMPONENT_COUNT = 3
        var STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT 
    }

    private var vertexData: FloatBuffer
    private var context: Context = baseContext
    private var program = 0
    private var uColorLocation = 0
    private var aPositionLocation = 0
    private var aColorLocation = 0
    private var uMatrixLocation = 0
    private var projectionMatrix = FloatArray(16)

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
                // Order of coordinates: X, Y, R, G, B
                // Triangle Fan
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
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

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6) 

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f) 
        glDrawArrays(GL_LINES, 6, 2) 

        // Draw the first mallet blue.
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f) 
        glDrawArrays(GL_POINTS, 8, 1) 
        // Draw the second mallet red.
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f) 
        glDrawArrays(GL_POINTS, 9, 1) 

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val aspectRatio =
                if (width > height)
                        width.toFloat() / height.toFloat()
                else
                        height.toFloat() / width.toFloat()

        if (width > height) { // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else { // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
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

        aColorLocation = glGetAttribLocation(program, A_COLOR)
//        uColorLocation = glGetUniformLocation(program, U_COLOR)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        vertexData.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData)
        glEnableVertexAttribArray(aPositionLocation)

        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData)

        glEnableVertexAttribArray(aColorLocation)


        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
    }
}