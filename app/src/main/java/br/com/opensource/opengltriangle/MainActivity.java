package br.com.opensource.opengltriangle;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    public class Triangle {

        // x, y, z
        private float vertices[] =
                {
                        -1.0f, 0.0f, 0.0f,  // vertices[0], bottom left
                        1.0f, 0.0f, 0.0f,   // vertices[1], bottom right
                        0.0f, 0.5f, 0.0f,   // vertices[2], middle top
                };

        // Just some random values *g*
        private float colors[] =
                {
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.5f, 0.0f, 1.0f,
                };

        // connect vertices[0] with vertices[1] and vertices[1] with vertices[2]
        // and vertices[2] with vertices[1] :-) */
        private short[] indices =
                {
                        0, 1, 2, 1
                };
        private FloatBuffer vertexBuffer;
        private FloatBuffer mColorBuffer;
        private ShortBuffer indexBuffer;

        public Triangle() {
            // Multiply with 4 because a float is 4 bytes
            ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            vertexBuffer = vbb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);

            // Multiply with 4 because a float is 4 bytes
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mColorBuffer = byteBuf.asFloatBuffer();
            mColorBuffer.put(colors);
            mColorBuffer.position(0);

            // Multiply with 2 because a short is 2 bytes
            ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
            ibb.order(ByteOrder.nativeOrder());
            indexBuffer = ibb.asShortBuffer();
            indexBuffer.put(indices);
            indexBuffer.position(0);
        }

        public void draw(GL10 gl) {
            gl.glFrontFace(GL10.GL_CCW);    // Counter-clockwise winding
            gl.glEnable(GL10.GL_CULL_FACE); // Enable face culling
            gl.glCullFace(GL10.GL_BACK);    // What faces to remove with the face culling

            // Enabled the vertices buffer for writing and to be used during rendering:
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            // Specifies the location and data format for rendering:
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

            gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);  // Disable the vertices buffer
            gl.glDisable(GL10.GL_CULL_FACE);                // Disable the face culling
        }
    }

        public class OpenGLRenderer implements GLSurfaceView.Renderer {
            // Initialize the triangle:
            Triangle triangle = new Triangle();

            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);  // background color (rgba)
                gl.glShadeModel(GL10.GL_SMOOTH);          // Enable Smooth Shading
                gl.glClearDepthf(1.0f);                   // Depth buffer setup
                gl.glEnable(GL10.GL_DEPTH_TEST);          // Enable depth testing
                gl.glDepthFunc(GL10.GL_LEQUAL);           // Type of depth testing
                gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
            }

            public void onDrawFrame(GL10 gl) {
                // Clears the screen and depth buffer:
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
                // Draw triangle
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -4);
                triangle.draw(gl);
            }

            @Override
            public void onSurfaceCreated(GL10 gl10, javax.microedition.khronos.egl.EGLConfig eglConfig) {

            }

            public void onSurfaceChanged(GL10 gl, int width, int height) {
                // Sets the current view port to the new size:
                gl.glViewport(0, 0, width, height);
                gl.glMatrixMode(GL10.GL_PROJECTION);  // Set projection matrix
                gl.glLoadIdentity();                  // Reset projection matrix
                // Calculate the aspect ratio of the window:
                GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
                gl.glMatrixMode(GL10.GL_MODELVIEW);   // Select modelview matrix
                gl.glLoadIdentity();                  // Reset modelview matrix
            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new OpenGLRenderer());
        setContentView(view);
    }
}