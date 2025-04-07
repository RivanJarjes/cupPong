public class Entity {
    // Fields
    protected final double[][] ogVertices;  // Original vertices
    protected double[][] vertices;          // Original vertices + current pos
    protected final Helpers.Face[] faces;           // Faces for rendering
    protected Helpers.Vector3 position;             // Current position
    protected Helpers.Vector3 velocity;             // Current velocity
    protected boolean moved;                // Flag to optimize vertex updates

    // Creates new entity with given vertices and faces containing indices and colours
    public Entity(double[][] v, Helpers.Face[] f) {
        this.ogVertices = v;
        this.faces = f;
        this.vertices = new double[v.length][3];
        
        initializeVertices();
        
        position = new Helpers.Vector3();
        velocity = new Helpers.Vector3(0, 0, 0);
        moved = true;
        update();
    }

    // make a copy of OG vertices into current vertices
    private void initializeVertices() {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i][0] = ogVertices[i][0];
            vertices[i][1] = ogVertices[i][1];
            vertices[i][2] = ogVertices[i][2];
        }
    }

    // get vertices for rendering
    public double[][] getVertices() {
        return vertices;
    }

    // get faces for rendering
    public Helpers.Face[] getFaces() {
        return faces;
    }

    // update method, called each time interval/frame
    public void update() {
        updatePosition();
        updateVertexPositions();
    }
    
    // update position if velocity != 0
    protected void updatePosition() {
        if (velocity.x != 0 || velocity.y != 0 || velocity.z != 0) {
            position.x += velocity.x;
            position.y += velocity.y;
            position.z += velocity.z;
            moved = true;
        }
    }
    
    // if moved, update position
    protected void updateVertexPositions() {
        if (moved) {
            for (int i = 0; i < vertices.length; i++) {
                vertices[i][0] = ogVertices[i][0] + position.x;
                vertices[i][1] = ogVertices[i][1] + position.y;
                vertices[i][2] = ogVertices[i][2] + position.z;
            }
            moved = false;
        }
    }
}
