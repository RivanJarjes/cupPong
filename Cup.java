public class Cup extends Entity{
    private static final double[][] cupv = {
        // Cup geometry...
        // Top Ring
        {  1.0,    -1,  0.0   },  // 0
        {  0.707,  -1,  0.707 },  // 1
        {  0.0,    -1,  1.0   },  // 2
        { -0.707,  -1,  0.707 },  // 3
        { -1.0,    -1,  0.0   },  // 4
        { -0.707,  -1, -0.707 },  // 5
        {  0.0,    -1, -1.0   },  // 6
        {  0.707,  -1, -0.707 },  // 7,

        // Bottom Ring
        {  0.7,    0,  0.0   },  // 8
        {  0.495,  0,  0.495 },  // 9
        {  0.0,    0,  0.7   },  // 10
        { -0.495,  0,  0.495 },  // 11
        { -0.7,    0,  0.0   },  // 12
        { -0.495,  0, -0.495 },  // 13
        {  0.0,    0, -0.7   },  // 14
        {  0.495,  0, -0.495 },  // 15

        // White Lip Ring
        {  1.1,   -1.1,  0.0    },  // 16
        {  0.777, -1.1,  0.777  },  // 17
        {  0.0,   -1.1,  1.1    },  // 18
        { -0.777, -1.1,  0.777  },  // 19
        { -1.1,   -1.1,  0.0    },  // 20
        { -0.777, -1.1, -0.777  },  // 21
        {  0.0,   -1.1, -1.1    },  // 22
        {  0.777, -1.1, -0.777  },  // 23
    };

    private static final Helpers.Face[] cupf = {
        // Cup bottom
        new Helpers.Face(new int[]{8,9,10,11,12,13,14,15}, Helpers.color.darkerRed),
        // Cup side
        new Helpers.Face(new int[]{0,1,9,8},   Helpers.color.red),
        new Helpers.Face(new int[]{1,2,10,9},  Helpers.color.darkRed),
        new Helpers.Face(new int[]{2,3,11,10}, Helpers.color.red),
        new Helpers.Face(new int[]{3,4,12,11}, Helpers.color.darkRed),
        new Helpers.Face(new int[]{4,5,13,12}, Helpers.color.red),
        new Helpers.Face(new int[]{5,6,14,13}, Helpers.color.darkRed),
        new Helpers.Face(new int[]{6,7,15,14}, Helpers.color.red),
        new Helpers.Face(new int[]{7,0,8,15},  Helpers.color.darkRed),

        // Lip ring
        new Helpers.Face(new int[]{0,1,17,16},     Helpers.color.white),
        new Helpers.Face(new int[]{1,2,18,17},     Helpers.color.white),
        new Helpers.Face(new int[]{2,3,19,18},     Helpers.color.white),
        new Helpers.Face(new int[]{3,4,20,19},     Helpers.color.white),
        new Helpers.Face(new int[]{4,5,21,20},     Helpers.color.white),
        new Helpers.Face(new int[]{5,6,22,21},     Helpers.color.white),
        new Helpers.Face(new int[]{6,7,23,22},     Helpers.color.white),
        new Helpers.Face(new int[]{7,0,16,23},     Helpers.color.white),
    };
    
    private boolean hit = false; // Flag to track if cup has been hit
    
    public Cup() {
        super(cupv, cupf);
    }

    public Cup(Helpers.Vector3 p) {
        super(cupv, cupf);
        position = p;
        moved = true;
        update();
    }
    
    // Method to check if cup has been hit
    public boolean isHit() {
        return hit;
    }
    
    // Method to mark cup as hit
    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
