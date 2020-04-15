package com.sparkfengbo.airhockey

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import com.sparkfengbo.airhockey.objects.Mallet
import com.sparkfengbo.airhockey.objects.Table
import com.sparkfengbo.airhockey.program.ColorShaderProgram
import com.sparkfengbo.airhockey.program.TextureShaderProgram
import com.sparkfengbo.airhockey.util.*
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
        //        var POSITION_COMPONENT_COUNT = 2
        var POSITION_COMPONENT_COUNT = 4
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
    private val modelMatrix = FloatArray(16)

    private var table: Table? = null
    private var mallet: Mallet? = null
    private var textureProgram: TextureShaderProgram? = null
    private var colorProgram: ColorShaderProgram? = null
    private var texture = 0

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
//        var tableVerticesWithTriangles: FloatArray = floatArrayOf(
//                // Order of coordinates: X, Y, R, G, B
//                // Triangle Fan
//                0f, 0f, 1f, 1f, 1f,
//                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
//                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
//                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
//                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
//                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
//
//                // Line 1
//                -0.5f, 0f, 1f, 0f, 0f,
//                0.5f, 0f, 1f, 0f, 0f,
//
//                // Mallets
//                0f, -0.4f, 0f, 0f, 1f,
//                0f, 0.4f, 1f, 0f, 0f
//        )

        var tableVerticesWithTriangles: FloatArray = floatArrayOf(
                // Order of coordinates: X, Y, Z, W, R, G, B
                // Triangle Fan
                0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
                0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
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

        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT)
        // Draw the table.
        textureProgram?.useProgram();
        textureProgram?.setUniforms(projectionMatrix, texture)
        table?.bindData(textureProgram);
        table?.draw();

        // Draw the mallets.
        colorProgram?.useProgram()
        colorProgram?.setUniforms(projectionMatrix)

        mallet?.bindData(colorProgram)
        mallet?.draw();

//        glClear(GL_COLOR_BUFFER_BIT)
//
//        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
//
//        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
//        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
//
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
//        glDrawArrays(GL_LINES, 6, 2)
//
//        // Draw the first mallet blue.
//        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
//        glDrawArrays(GL_POINTS, 8, 1)
//        // Draw the second mallet red.
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
//        glDrawArrays(GL_POINTS, 9, 1)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        /*
        final float aspectRatio = width > height ?
            (float) width / (float) height :
            (float) height / (float) width;

        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0,
                -aspectRatio, aspectRatio,
                -1f, 1f,
                -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0,
                -1f, 1f,
                -aspectRatio, aspectRatio,
                -1f, 1f);
        }
        */
        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat()
                / height.toFloat(), 1f, 10f)

        /*
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2f);
        */
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet()
        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)


////        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
//        val vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader)
//        val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader)
//
//        var vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
//        var fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
//
//        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
//
//        if (LoggerConfig.ON) {
//            ShaderHelper.validateProgram(program)
//        }
//        glUseProgram(program)
//
//        aColorLocation = glGetAttribLocation(program, A_COLOR)
////        uColorLocation = glGetUniformLocation(program, U_COLOR)
//        aPositionLocation = glGetAttribLocation(program, A_POSITION)
//
//        vertexData.position(0)
//        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
//                false, STRIDE, vertexData)
//        glEnableVertexAttribArray(aPositionLocation)
//
//        vertexData.position(POSITION_COMPONENT_COUNT)
//        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
//                false, STRIDE, vertexData)
//
//        glEnableVertexAttribArray(aColorLocation)
//
//
//        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
    }
}