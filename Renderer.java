
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


public class Renderer extends JPanel
        implements ActionListener, KeyListener, MouseMotionListener, MouseListener {

    // Constants
    private static final int MIN_DRAG_PIXELS = 30;
    private static final long MIN_DRAG_TIME_MS = 40;
    private static final double NEAR_PLANE = 0.1;
    private static final double MIN_X = -10;
    private static final double MAX_X = 15;
    private static final double MIN_Z = -5;
    private static final double MAX_Z = 15;
    private static final double MAX_Y = 10;
    
    // Game state
    private boolean thrown;
    private double throwVX, throwVY;
    private Point throwStartPt;
    private long throwStartTime;
    private static Ball ball;
    public static Entity[] CUPS;
    private static Entity[] ENTITIES;
    
    // Timer
    private final Timer timer;

    // Camera variables
    private boolean cameraControl = false;
    private double cameraX = 3.75;
    private double cameraY = -6.50;
    private double cameraZ = 15.75;
    private double yaw = -3.14;
    private double pitch = -0.65;
    
    // Original camera position and orientation for game mode
    private final double originalCameraX = 3.75;
    private final double originalCameraY = -6.50;
    private final double originalCameraZ = 15.75;
    private final double originalYaw = -3.14;
    private final double originalPitch = -0.65;
    
    // Saved camera position for camera mode
    private double savedCameraX = 3.75;
    private double savedCameraY = -6.50;
    private double savedCameraZ = 15.75; 
    private double savedYaw = -3.14;
    private double savedPitch = -0.65;
    
    // Movement variables
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean movingUp = false;
    private boolean movingDown = false;
    private final double moveSpeed = 0.1;
    private final double mouseSensitivity = 0.01;
    
    // Mouse drag state
    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging = false;

    // Geometry data
    private static final double[][] floorv = {
        { -11.25, 0.000001, -5.25 },
        { -11.25, 0.000001, 18.75 },
        { 18.75, 0.000001, 18.75 },
        { 18.75, 0.000001, -5.25 }
    };

    private static final Helpers.Face[] floorf = {
        new Helpers.Face(new int[]{0, 3, 2, 1}, Helpers.color.tableGreen)
    };

    private static final double[][] wfloorv = {
        { -12.25, 0.000002, -6.25 },
        { -12.25, 0.000002, 19.75 },
        { 19.75, 0.000002, 19.75 },
        { 19.75, 0.000002, -6.25 }
    };

    private static final Helpers.Face[] wfloorf = {
        new Helpers.Face(new int[]{0, 3, 2, 1}, Helpers.color.white)
    };

    private static final double[][] wstripev = {
        { 3.625, 0.0000005, -6.25 },
        { 3.625, 0.0000005, 19.75 },
        { 3.875, 0.0000005, 19.75 },
        { 3.875, 0.0000005, -6.25 }
    };

    private static final Entity floor = new Entity(floorv, floorf);
    private static final Entity wfloor = new Entity(wfloorv, wfloorf);
    private static final Entity wstripe = new Entity(wstripev, wfloorf);

    static {
        initializeGame();
    }
    
    // Initialize the game entities
    private static void initializeGame() {
        CUPS = new Entity[]{
            new Cup(),
            new Cup(new Helpers.Vector3(2.5, 0, 0)),
            new Cup(new Helpers.Vector3(5, 0, 0)),
            new Cup(new Helpers.Vector3(7.5, 0, 0)),
            new Cup(new Helpers.Vector3(1.25, 0, 2.5)),
            new Cup(new Helpers.Vector3(3.75, 0, 2.5)),
            new Cup(new Helpers.Vector3(6.25, 0, 2.5)),
            new Cup(new Helpers.Vector3(2.5, 0, 5)),
            new Cup(new Helpers.Vector3(5, 0, 5)),
            new Cup(new Helpers.Vector3(3.75, 0, 7.5))
        };
        
        ball = new Ball(new Helpers.Vector3(3.75, -4, 14.3));
        
        ENTITIES = new Entity[CUPS.length + 4];
        ENTITIES[0] = floor;
        ENTITIES[1] = wfloor;
        ENTITIES[2] = wstripe;
        System.arraycopy(CUPS, 0, ENTITIES, 3, CUPS.length);
        ENTITIES[ENTITIES.length - 1] = ball;
    }

    public Renderer() {
        timer = new Timer(16, this);
        timer.start();
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // Game state getters and setters
    public boolean isThrown() { return thrown; }
    public double getThrowVX() { return throwVX; }
    public double getThrowVY() { return throwVY; }
    public void resetThrow() { thrown = false; }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        checkAndRespawnBall();
        checkAllCupsHit();
        
        updateEntities();
        
        // 2 game modes
        if (cameraControl) {
            updateCamera();
        } else if (thrown) {
            thrown = false;
            ((Ball)ball).setActive(true);
        }
    }
    
    private void updateEntities() {
        for (Entity e: ENTITIES) {
            if (e != null) {
                e.update();
            }
        }
    }

    private void updateCamera() {
        // WASD camera support
        double forwardDirX = Math.sin(yaw);
        double forwardDirZ = Math.cos(yaw);
        double rightDirX = Math.sin(yaw + Math.PI / 2);
        double rightDirZ = Math.cos(yaw + Math.PI / 2);

        if (movingForward) {
            cameraX += forwardDirX * moveSpeed;
            cameraZ += forwardDirZ * moveSpeed;
        }
        if (movingBackward) {
            cameraX -= forwardDirX * moveSpeed;
            cameraZ -= forwardDirZ * moveSpeed;
        }
        if (movingLeft) {
            cameraX += rightDirX * moveSpeed;
            cameraZ += rightDirZ * moveSpeed;
        }
        if (movingRight) {
            cameraX -= rightDirX * moveSpeed;
            cameraZ -= rightDirZ * moveSpeed;
        }
        if (movingUp) {
            cameraY -= moveSpeed;
        }
        if (movingDown) {
            cameraY += moveSpeed;
        }
    }

    // Check if ball needs to be respawned (game-side, not entity-side)
    private void checkAndRespawnBall() {
        Ball ballObj = (Ball)ball;
        if (!ballObj.getActive()) {
            return;
        }
        
        boolean outOfBounds = 
            ball.position.x < MIN_X || ball.position.x > MAX_X ||
            ball.position.z < MIN_Z || ball.position.z > MAX_Z ||
            ball.position.y > MAX_Y;
            
        boolean stopped = isBallStopped(ballObj);
        boolean activeTooLong = ballObj.hasBeenActiveTooLong();
        
        
        boolean inCup = checkBallInCup(ballObj);
        if (inCup) {
            return;
        }
        
        if (outOfBounds || stopped || activeTooLong) {
            respawnBall(ballObj);
        }
    }
    
    // Check if ball is stopped
    private boolean isBallStopped(Ball ballObj) {
        if (!ballObj.isInsideCup() && Math.abs(ball.position.y + ballObj.getRadius() - 0.0) < 0.01) {
            double velocityMagnitude = 
                Math.sqrt(ball.velocity.x * ball.velocity.x + 
                        ball.velocity.y * ball.velocity.y + 
                        ball.velocity.z * ball.velocity.z);
            
            boolean stopped = velocityMagnitude < 0.005;
            
            return stopped;
        }
        return false;
    }
    
    // Check if ball is in cup
    private boolean checkBallInCup(Ball ballObj) {
        boolean inCup = ballObj.isInsideCup() && ballObj.isStuckToBottom();
        if (inCup) {
            Cup cup = ballObj.getContainerCup();

            if (cup != null && !cup.isHit()) 
                cup.setHit(true);
            
            respawnBall(ballObj);
            return true;
        }
        return false;
    }
    
    // Respawn ball
    private void respawnBall(Ball ballObj) {
        ballObj.reset();
        ball.moved = true;
    }

    // Win message
    private void checkAllCupsHit() {
        boolean allCupsHit = true;
        for (Entity entity : CUPS) {
            if (entity != null && entity instanceof Cup && !((Cup)entity).isHit()) {
                allCupsHit = false;
                break;
            }
        }
        
        showWinMessage = allCupsHit;
    }

    // Add field at the top of the class
    private boolean showWinMessage = false;

    // Drawing both 3D polygons and Swing components
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int width = getWidth();
        int height = getHeight();
        
        BufferedImage colorBuffer = createColorBuffer(width, height);
        double[][] zBuffer = createZBuffer(width, height);
        
        Helpers.Mat4 viewMatrix = createViewMatrix();
        Helpers.Mat4 projMatrix = createProjectionMatrix(width, height);
        
        renderEntities(colorBuffer, zBuffer, viewMatrix, projMatrix);
        
        drawFinalImage(g, colorBuffer, width, height);
    }
    
    // Creates the canvas where all the 3D rendering is going to happen
    private BufferedImage createColorBuffer(int width, int height) {
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer.setRGB(x, y, Helpers.color.background.getRGB());
            }
        }
        return buffer;
    }
    
    // Uses a z-buffer as the depth technique (previously tried painters.. dont recommend) to track what objects are in front of others
    private double[][] createZBuffer(int width, int height) {
        double[][] buffer = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = Double.POSITIVE_INFINITY;
            }
        }
        return buffer;
    }
    
    // Creates the matrix that transforms objects into the view space, creates a translation and rotation matrix in this case and combines
    private Helpers.Mat4 createViewMatrix() {
        Helpers.Mat4 trans = Helpers.Mat4.createTranslation(-cameraX, -cameraY, -cameraZ);
        Helpers.Mat4 rotY = Helpers.Mat4.createRotationY(-yaw);
        Helpers.Mat4 rotX = Helpers.Mat4.createRotationX(-pitch);
        return Helpers.Mat4.multiply(rotX, Helpers.Mat4.multiply(rotY, trans));
    }
    
    // Projects 3d points into a 2d screen
    private Helpers.Mat4 createProjectionMatrix(int width, int height) {
        double aspect = (double) width / (double) height;
        // FOV of 60!
        return Helpers.Mat4.createPerspective(Math.toRadians(60), aspect, NEAR_PLANE, 100.0);
    }
    
    // Goes through each entity when rendering
    private void renderEntities(BufferedImage colorBuffer, double[][] zBuffer, Helpers.Mat4 viewMatrix, Helpers.Mat4 projMatrix) {
        for (Entity entity : ENTITIES) {
            if (entity == null) continue;
            
            // Ignore hit cups
            if (entity instanceof Cup && ((Cup)entity).isHit()) continue;
            
            renderEntity(entity, colorBuffer, zBuffer, viewMatrix, projMatrix);
        }
    }
    
    // Gets the face and vertices data from an entity, creates a polygon, and rasterizes it
    private void renderEntity(Entity entity, BufferedImage colorBuffer, double[][] zBuffer, 
                             Helpers.Mat4 viewMatrix, Helpers.Mat4 projMatrix) {
        Helpers.Face[] faces = entity.getFaces();
        double[][] vertices = entity.getVertices();
        
        for (Helpers.Face face : faces) {
            List<Helpers.VertexData> viewVerts = transformToViewSpace(face, vertices, viewMatrix);
            List<Helpers.VertexData> clipped = clipAgainstNearPlane(viewVerts, NEAR_PLANE);
            
            if (clipped.size() < 3) {
                continue;
            }
            
            List<Helpers.VertexData> projectedVerts = projectVertices(clipped, projMatrix, 
                                                            colorBuffer.getWidth(), 
                                                            colorBuffer.getHeight());
            
            rasterizeFace(projectedVerts, colorBuffer, zBuffer);
        }
    }
    
    // Applies matrix transformation onto the projected view and adds to list of vertices
    private List<Helpers.VertexData> transformToViewSpace(Helpers.Face face, double[][] vertices, Helpers.Mat4 viewMatrix) {
        List<Helpers.VertexData> viewVerts = new ArrayList<>();
        
        for (int idx : face.indices) {
            double[] v = vertices[idx];
            double[] vec4 = { v[0], v[1], v[2], 1.0 };
            double[] out = Helpers.Mat4.apply(viewMatrix, vec4);
            
            viewVerts.add(new Helpers.VertexData(out[0], out[1], out[2], face.color));
        }
        
        return viewVerts;
    }
    

    // Creates the perspective effect onto projected 3d vertices in 2d view
    private List<Helpers.VertexData> projectVertices(List<Helpers.VertexData> vertices, Helpers.Mat4 projMatrix, 
                                           int width, int height) {
        List<Helpers.VertexData> projectedVerts = new ArrayList<>();
        
        for (Helpers.VertexData vv : vertices) {
            // Convert the 3D vertex into a 4D coordinate vector
            double[] v4 = { vv.x, vv.y, vv.z, 1.0 };
            // Transform the vertex using the projection matrix
            double[] clip = Helpers.Mat4.apply(projMatrix, v4);
            
            // Perform the perspective divide
            double ndcX = clip[0] / clip[3];
            double ndcY = clip[1] / clip[3];
            double ndcZ = clip[2] / clip[3];
            
            // Map the NDC coordinates to screen space coordinates
            double sx = (ndcX * 0.5 + 0.5) * width;
            double sy = (-(ndcY * 0.5) + 0.5) * height;
            // Stretches out the x component
            sx = (sx - width * 0.5) * 0.7 + width * 0.5;
            
            // Compute depth which is just negative of the z coordinate
            double depth = -ndcZ;
            
            projectedVerts.add(new Helpers.VertexData(sx, sy, depth, vv.color));
        }
        
        return projectedVerts;
    }
    
    // Turns complex polygons into different triangles
    private void rasterizeFace(List<Helpers.VertexData> projectedVerts, BufferedImage colorBuffer, 
                             double[][] zBuffer) {
        for (int i = 1; i < projectedVerts.size() - 1; i++) {
            Helpers.VertexData v0 = projectedVerts.get(0);
            Helpers.VertexData v1 = projectedVerts.get(i);
            Helpers.VertexData v2 = projectedVerts.get(i+1);
            
            rasterizeTriangle(v0, v1, v2, colorBuffer, zBuffer);
        }
    }

    // If too close, this method will clip the vertice and prevent artifacts
    private List<Helpers.VertexData> clipAgainstNearPlane(List<Helpers.VertexData> inVerts, double nearZ) {
        List<Helpers.VertexData> outVerts = new ArrayList<>();
        if (inVerts.isEmpty()) return outVerts;

        for (int i = 0; i < inVerts.size(); i++) {
            Helpers.VertexData curr = inVerts.get(i);
            Helpers.VertexData next = inVerts.get((i+1) % inVerts.size());

            boolean currIn = (curr.z >= nearZ);
            boolean nextIn = (next.z >= nearZ);

            if (currIn) {
                outVerts.add(curr);
            }

            // If one vertex is inside and one is outside (edge crosses the near plane)
            if (currIn != nextIn) {
                // Interpolate coordinates at inersection point
                double t = (nearZ - curr.z) / (next.z - curr.z);
                double ix = curr.x + t * (next.x - curr.x);
                double iy = curr.y + t * (next.y - curr.y);
                double iz = nearZ;
                Color ic = curr.color;

                outVerts.add(new Helpers.VertexData(ix, iy, iz, ic));
            }
        }

        return outVerts;
    }

    private void rasterizeTriangle(Helpers.VertexData v0, Helpers.VertexData v1, Helpers.VertexData v2,
                                 BufferedImage colorBuffer, double[][] zBuffer) {
        // Find the bounding box of the triangle
        int minX = (int)Math.floor(Math.min(v0.x, Math.min(v1.x, v2.x)));
        int maxX = (int)Math.ceil(Math.max(v0.x, Math.max(v1.x, v2.x)));
        int minY = (int)Math.floor(Math.min(v0.y, Math.min(v1.y, v2.y)));
        int maxY = (int)Math.ceil(Math.max(v0.y, Math.max(v1.y, v2.y)));

        // Clamp to screen boundaries
        minX = Math.max(minX, 0);  
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, colorBuffer.getWidth() - 1);
        maxY = Math.min(maxY, colorBuffer.getHeight() - 1);

        // For each pixel in the bounding box
        for (int py = minY; py <= maxY; py++) {
            for (int px = minX; px <= maxX; px++) {
                double w0 = edgeFunction(v1.x, v1.y, v2.x, v2.y, px, py);
                double w1 = edgeFunction(v2.x, v2.y, v0.x, v0.y, px, py);
                double w2 = edgeFunction(v0.x, v0.y, v1.x, v1.y, px, py);

                // Check if the pixel is inside the triangle
                boolean inside = (w0 >= 0 && w1 >= 0 && w2 >= 0) ||
                               (w0 <= 0 && w1 <= 0 && w2 <= 0);
                if (inside) {
                    w0 = Math.abs(w0);
                    w1 = Math.abs(w1);
                    w2 = Math.abs(w2);
                    double sum = w0 + w1 + w2;

                    w0 /= sum;
                    w1 /= sum;
                    w2 /= sum;

                    double depth = (v0.z * w0) + (v1.z * w1) + (v2.z * w2);

                    // Check z-buffer for visibility
                    if (depth < zBuffer[py][px]) {
                        zBuffer[py][px] = depth;
                        colorBuffer.setRGB(px, py, v0.color.getRGB());
                    }
                }
            }
        }
    }

    private double edgeFunction(double x0, double y0, double x1, double y1, double x2, double y2) {
        return (x2 - x0)*(y1 - y0) - (y2 - y0)*(x1 - x0);
    }
    
    private void drawFinalImage(Graphics g, BufferedImage colorBuffer, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(colorBuffer, 0, 0, null);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        
        if (showWinMessage) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.drawString("CONGRATULATIONS! ALL CUPS CLEARED!", width/2 - 350, height/2);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
        } else if (cameraControl) {
            g2.drawString("WASD - Move, Space/Shift - Up/Down", width/2 - 240, 50);
        } else if (!((Ball)ball).getActive()) {
            g2.drawString("READY TO THROW!", width/2 - 100, 50);
        }
        
        g2.dispose();
    }

    // Input handling methods
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:  movingForward = true;  break;
            case KeyEvent.VK_S:  movingBackward = true; break;
            case KeyEvent.VK_A:  movingLeft = true;     break;
            case KeyEvent.VK_D:  movingRight = true;    break;
            case KeyEvent.VK_SPACE: movingUp = true;    break;
            case KeyEvent.VK_SHIFT: movingDown = true;  break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:  movingForward = false; break;
            case KeyEvent.VK_S:  movingBackward = false;break;
            case KeyEvent.VK_A:  movingLeft = false;    break;
            case KeyEvent.VK_D:  movingRight = false;   break;  
            case KeyEvent.VK_F3: cameraControl = !cameraControl; break;
            case KeyEvent.VK_SPACE: movingUp = false;   break;
            case KeyEvent.VK_SHIFT: movingDown = false; break;
            case KeyEvent.VK_R: ((Ball)ENTITIES[ENTITIES.length - 1]).reset(); break;
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        dragging = true;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        
        Ball ballObj = (Ball)ball;
        boolean ballIsReady = !ballObj.getActive();
        
        // If in game: initiates throw timer to get velocity, else used for camera
        if (!cameraControl && SwingUtilities.isLeftMouseButton(e) && !thrown && ballIsReady) {
            throwStartPt = e.getPoint();
            throwStartTime = System.nanoTime();
        } 
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;

        Ball ballObj = (Ball)ball;
        // If in game: processes the throw
        if (!cameraControl && throwStartPt != null && !thrown && !ballObj.getActive()) {
            processThrow(e, ballObj);
        }
    }
    
    // Gets velocity for throw and check is its valid
    private void processThrow(MouseEvent e, Ball ballObj) {
        Point endPt = e.getPoint();
        long endTime = System.nanoTime();
        int dy = throwStartPt.y - endPt.y;
        int dx = endPt.x - throwStartPt.x;
        long dtMs = (endTime - throwStartTime) / 1_000_000;

        if (dy >= MIN_DRAG_PIXELS && dtMs >= MIN_DRAG_TIME_MS) {
            calculateAndApplyThrowVelocities(dx, dy, dtMs, ballObj);
            thrown = true;
        }
        
        throwStartPt = null;
    }
    
    // Calculates the velocities of the throw
    private void calculateAndApplyThrowVelocities(int dx, int dy, long dtMs, Ball ballObj) {
        double dt = dtMs / 1000.0;
        throwVX = dx / dt;
        throwVY = dy / dt;
        
        double dragDistance = Math.min(Math.max(dy, 0), 300);
        double mappedVY = -(dragDistance / 2100.0);
        
        double throwStrength = dragDistance / 250.0;
        double mappedVZ = -0.05 - (throwStrength * 0.17);
        
        double scaledX = dx / 4000.0;
        
        if (Math.abs(dx) > 200) {
            double extraInfluence = Math.min(Math.abs(dx) - 200, 400) / 400.0 * 0.05;
            scaledX += Math.signum(scaledX) * extraInfluence;
        }
        
        if (Math.abs(dx) > 100) {
            double directionInfluence = Math.min(Math.abs(dx) - 100, 300) / 300.0 * 0.025;
            mappedVZ -= (dx > 0) ? directionInfluence : -directionInfluence;
        }
        
        mappedVZ = Math.max(Math.min(mappedVZ, -0.05), -0.225);
        mappedVY = Math.max(Math.min(mappedVY, 0), -0.13);
        scaledX = Math.max(Math.min(scaledX, 0.09), -0.09);
        
        ballObj.setVelocityY(mappedVY);
        ballObj.setVelocityZ(mappedVZ);
        ballObj.setVelocityX(scaledX);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!cameraControl) return;

        if (dragging) {
            int x = e.getX();
            int y = e.getY();
            int dx = lastMouseX - x;
            int dy = lastMouseY - y;

            yaw += dx * mouseSensitivity;
            pitch += dy * mouseSensitivity;

            double maxPitch = Math.toRadians(89.0);
            pitch = Math.max(Math.min(pitch, maxPitch), -maxPitch);

            lastMouseX = x;
            lastMouseY = y;
        }
    }
    
    // Unused event methods
    @Override public void keyTyped(KeyEvent e) { }
    @Override public void mouseMoved(MouseEvent e) { }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cup Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 900);

        Renderer renderer = new Renderer();
        frame.add(renderer);
        
        // Create a transparent panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setOpaque(false);
        
        // Create restart button
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setBackground(new Color(0, 150, 0));
        restartButton.setForeground(Color.BLACK);
        restartButton.setFocusPainted(false);
        
        // Position the restart button at the top left
        restartButton.setBounds(10, 10, 150, 40);
        
        // Add action listener to restart the game
        restartButton.addActionListener(e -> renderer.restartGame());
        
        // Add restart button to the control panel
        controlPanel.add(restartButton);
        
        // Create radio buttons for game modes
        JRadioButton gameMode = new JRadioButton("Game Mode");
        JRadioButton cameraMode = new JRadioButton("Camera Mode");
        
        // Configure radio buttons
        gameMode.setFont(new Font("Arial", Font.BOLD, 14));
        cameraMode.setFont(new Font("Arial", Font.BOLD, 14));
        gameMode.setForeground(Color.BLACK);
        cameraMode.setForeground(Color.BLACK);
        
        // Make radio buttons look better with transparent background
        gameMode.setOpaque(false);
        cameraMode.setOpaque(false);
        
        // Create a button group so only one can be selected at a time
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(gameMode);
        modeGroup.add(cameraMode);
        
        // Set Game Mode as selected by default
        gameMode.setSelected(true);
        
        // Position radio buttons in the top right
        gameMode.setBounds(620, 10, 150, 20);
        cameraMode.setBounds(620, 35, 150, 20);
        
        // Add action listeners to toggle camera mode
        gameMode.addActionListener(e -> {
            if (gameMode.isSelected()) {
                renderer.setCameraControl(false);
                renderer.requestFocusInWindow();
            }
        });
        
        cameraMode.addActionListener(e -> {
            if (cameraMode.isSelected()) {
                renderer.setCameraControl(true);
                renderer.requestFocusInWindow();
            }
        });
        
        // Make sure the radio buttons dont stay in focus
        gameMode.setFocusable(false);
        cameraMode.setFocusable(false);
        restartButton.setFocusable(false);
        
        // Add radio buttons to the control panel
        controlPanel.add(gameMode);
        controlPanel.add(cameraMode);
        
        // Set the glass pane to our control panel
        frame.setGlassPane(controlPanel);
        controlPanel.setVisible(true);
        
        frame.setVisible(true);
    }

    // Restart game
    public void restartGame() {
        // Reset the ball
        ((Ball)ball).reset();
        
        // Reset all cups
        for (Entity entity : CUPS) {
            if (entity instanceof Cup) {
                ((Cup)entity).setHit(false);
                
                if (entity.position.y < -10) {
                    entity.position.y = 0;
                    entity.moved = true;
                }
            }
        }
        
        repaint();
    }

    public void setCameraControl(boolean control) {
        // If switching from camera mode to game mode, save the current position
        if (cameraControl && !control) {
            savedCameraX = cameraX;
            savedCameraY = cameraY;
            savedCameraZ = cameraZ;
            savedYaw = yaw;
            savedPitch = pitch;
            
            // Restore original position for game mode
            cameraX = originalCameraX;
            cameraY = originalCameraY;
            cameraZ = originalCameraZ;
            yaw = originalYaw;
            pitch = originalPitch;
        } 
        // If switching from game mode to camera mode, restore saved position
        else if (!cameraControl && control) {
            cameraX = savedCameraX;
            cameraY = savedCameraY;
            cameraZ = savedCameraZ;
            yaw = savedYaw;
            pitch = savedPitch;
        }
        
        // Update camera control state
        cameraControl = control;
        
        // Keep in focus
        requestFocusInWindow();
    }
}
