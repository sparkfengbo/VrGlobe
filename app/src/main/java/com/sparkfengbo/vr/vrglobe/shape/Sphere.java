package com.sparkfengbo.vr.vrglobe.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by fengbo on 2018/11/12.
 * <p>
 *
 * 球坐标公式:
 * <p>
 * x=rsinθcosφ.
 * <p>
 * y=rsinθsinφ.
 * <p>
 * z=rcosθ.
 * <p>
 *
 * <a href='https://blog.csdn.net/yarkey09/article/details/36627693'></a>
 */

public class Sphere {
    private static final float zOrderStep = 2.0f;

    private float R;
    private FloatBuffer mVerticeBuf;
    private float[][] vertices = new float[32][3];


    public Sphere(float R) {
        this.R = R;
        init();
    }

    public void init() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * vertices[0].length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVerticeBuf = byteBuffer.asFloatBuffer();
    }


    public void draw(GL10 gl) {
        float theta;        //z轴正向夹角
        float phi;          //顶点到xy平面投影与x轴夹角


        float cos, sin;
        float r1, r2;
        float h1, h2;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);


        for (theta = -90.0f; theta < 90.0f; theta += zOrderStep) {
            int n = 0;

            r1 = (float) Math.cos(theta * Math.PI / 180.0);
            r2 = (float) Math.cos((theta + zOrderStep) * Math.PI / 180.0);
            h1 = (float) Math.sin(theta * Math.PI / 180.0);
            h2 = (float) Math.sin((theta + zOrderStep) * Math.PI / 180.0);

            // 固定纬度, 360 度旋转遍历一条纬线
            for (phi = 0.0f; phi <= 360.0f; phi += zOrderStep) {

                cos = (float) Math.cos(phi * Math.PI / 180.0);
                sin = -(float) Math.sin(phi * Math.PI / 180.0);

                vertices[n][0] = (r2 * cos);
                vertices[n][1] = (h2);
                vertices[n][2] = (r2 * sin);
                vertices[n + 1][0] = (r1 * cos);
                vertices[n + 1][1] = (h1);
                vertices[n + 1][2] = (r1 * sin);

                mVerticeBuf.put(vertices[n]);
                mVerticeBuf.put(vertices[n + 1]);

                n += 2;

                if (n > 31) {
                    mVerticeBuf.position(0);

                    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticeBuf);
                    gl.glNormalPointer(GL10.GL_FLOAT, 0, mVerticeBuf);
                    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, n);

                    n = 0;
                    phi -= zOrderStep;
                }

            }
            mVerticeBuf.position(0);

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticeBuf);
            gl.glNormalPointer(GL10.GL_FLOAT, 0, mVerticeBuf);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, n);
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }
}
