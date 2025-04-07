
import java.awt.Color;

public class Helpers {
    public static class color {
        public static final Color red = Color.decode("#ff0800");
        public static final Color darkRed = Color.decode("#f20a02");
        public static final Color darkerRed = Color.decode("#b80000");
        public static final Color tableGreen = Color.decode("#2f966b");
        public static final Color background = Color.decode("#dcbb88");
        public static final Color ball = Color.decode("#fcac65");
        public static final Color white = Color.WHITE;
    }

    public static class Mat4 {
        private double[][] m;  // m[row][col], 4x4
    
        public Mat4() {
            // Initialize as identity by default
            m = new double[4][4];
            for(int i = 0; i < 4; i++) {
                m[i][i] = 1.0;
            }
        }
    
        public Mat4(double[][] values) {
            // Assuming values is already 4x4
            this.m = values;
        }
    
        public static Mat4 createIdentity() {
            return new Mat4(); // calls the default constructor (identity)
        }
    
        public static Mat4 createTranslation(double tx, double ty, double tz) {
            Mat4 result = new Mat4();
            // Identity with [0][3], [1][3], [2][3] set to tx, ty, tz
            result.m[0][3] = tx;
            result.m[1][3] = ty;
            result.m[2][3] = tz;
            return result;
        }
    
        public static Mat4 createScale(double sx, double sy, double sz) {
            Mat4 result = new Mat4();
            result.m[0][0] = sx;
            result.m[1][1] = sy;
            result.m[2][2] = sz;
            return result;
        }
    
        public static Mat4 createRotationX(double angleRad) {
            Mat4 result = new Mat4();
            double c = Math.cos(angleRad);
            double s = Math.sin(angleRad);
    
            // X-rotation affects y/z rows & columns
            result.m[1][1] = c;
            result.m[1][2] = -s;
            result.m[2][1] = s;
            result.m[2][2] = c;
            return result;
        }
    
        public static Mat4 createRotationY(double angleRad) {
            Mat4 result = new Mat4();
            double c = Math.cos(angleRad);
            double s = Math.sin(angleRad);
    
            // Y-rotation affects x/z
            result.m[0][0] = c;
            result.m[0][2] = s;
            result.m[2][0] = -s;
            result.m[2][2] = c;
            return result;
        }
    
        public static Mat4 createRotationZ(double angleRad) {
            Mat4 result = new Mat4();
            double c = Math.cos(angleRad);
            double s = Math.sin(angleRad);
    
            // Z-rotation affects x/y
            result.m[0][0] = c;
            result.m[0][1] = -s;
            result.m[1][0] = s;
            result.m[1][1] = c;
            return result;
        }
    
        /**
         * Multiply two 4x4 matrices: out = a * b
         */
        public static Mat4 multiply(Mat4 a, Mat4 b) {
            Mat4 result = new Mat4();
            // Set to zero first
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    result.m[i][j] = 0;
                }
            }
    
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    double sum = 0;
                    for(int k = 0; k < 4; k++) {
                        sum += a.m[i][k] * b.m[k][j];
                    }
                    result.m[i][j] = sum;
                }
            }
            return result;
        }
    
        // pply a 4x4 matrix to a vector (x, y, z, w)Typically w=1 for positions, w=0 for directions
        public static double[] apply(Mat4 matrix, double[] vec4) {
            if(vec4.length < 4) {
                throw new IllegalArgumentException("Vector must have length 4");
            }
            double[] result = new double[4];
    
            for(int i = 0; i < 4; i++) {
                result[i] = matrix.m[i][0] * vec4[0]
                          + matrix.m[i][1] * vec4[1]
                          + matrix.m[i][2] * vec4[2]
                          + matrix.m[i][3] * vec4[3];
            }
            return result;
        }
    
        // Accessor for debugging or advanced usage
        public double[][] getArray() {
            return m;
        }

        public static Mat4 createPerspective(double fovRadians, double aspect, double zNear, double zFar) {
            double f = 1.0 / Math.tan(fovRadians / 2.0);
        
            double[][] p = new double[4][4];
            p[0][0] = f / aspect;
            p[1][1] = f;
            p[2][2] = (zFar + zNear) / (zNear - zFar);
            p[2][3] = (2 * zFar * zNear) / (zNear - zFar);
            p[3][2] = -1;
            p[3][3] = 0;
        
            return new Mat4(p);
        }
    }

    public static class Face {
        int[] indices;
        Color color;
        public Face(int[] i, Color c) {
            this.indices = i;
            this.color = c;
        }
    }

    public static class VertexData {
        double x, y, z;
        Color color;
        public VertexData(double x, double y, double z, Color c) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = c;
        }
    }

    public static class Vector3 {
        double x, y, z;

        public Vector3() { x = y = z = 0; }
        public Vector3(double x, double y, double z) {
            this. x = x; this.y = y; this.z = z;
        }

        public Vector3 plus(Vector3 o) {
            return new Vector3(x+o.x, y+o.y, z+o.z);
        }

        public Vector3 minus(Vector3 o) {
            return new Vector3(x-o.x, y-o.y, z-o.z);
        }

        public Vector3 mult(double v) {
            return new Vector3(x*v, y*v, z*v);
        }

        public double dot(Vector3 o) {
            return x*o.x + y*o.y + z*o.z;
        }

        public double lengthSquared() {
            return x * x + y * y + z * z;
        }

        public double length() { return Math.sqrt(lengthSquared()); }

        public Vector3 addInPlace(Vector3 o) { 
            x += o.x; y += o.y; z += o.z;  return this;
        }
        public Vector3 multInPlace(double s) { 
            x *= s; y *= s; z *= s;  return this;
        }
        public Vector3 normalized() {
            double len = length();
            return (len == 0) ? new Vector3() : mult(1.0/len);
        }
    }
}
