

import java.util.Random;

public class Ball extends Entity {
    // Physics constants
    private static final double GRAVITY = 0.005; // Self-explanatory
    private static final double BOUNCE_FACTOR = 0.8;  // How much it bounces
    private static final double DRAG_FACTOR = 0.998; // Air-resistance
    private static final double FLOOR_Y = 0.0; // Where the floor's Y-coordinate is at
    private static final double RICOCHET_RANDOMNESS = 0.225; // Small random factor for realistic bounces
    private static final Random random = new Random();
    
    // Out of bounds constants
    private static final double MIN_Z = -5;
    private static final double MAX_Z = 15;
    private static final double MAX_Y = 10;
    private static final double LEFTMOST_CUP_X = 0.0;     // X position of leftmost cup
    private static final double RIGHTMOST_CUP_X = 7.5;    // X position of rightmost cup
    
    // Original starting position when respawns
    private static final double ORIGIN_X = 3.75;
    private static final double ORIGIN_Y = -4;
    private static final double ORIGIN_Z = 14.3;
    
    // Ball properties
    private final double radius = 0.20;
    private boolean active;
    private boolean insideCup = false;
    private Cup containerCup = null;
    private boolean stuckToBottom = false;
    
    // Timer for automatic respawn
    private long activationTime = 0;
    private static final long MAX_ACTIVE_TIME_MS = 8000; // 8 seconds, might be too much sometimes but safe
    
    // Ball geometry data, got the data from blender
    private static final double[][] ballv = {
        {  0.05044353,  0.00000000,  0.24485802 },//  0
        {  0.05044353,  0.00000000, -0.24485802 },//  1
        { -0.05044353,  0.00000000,  0.24485802 },//  2
        { -0.05044353,  0.00000000, -0.24485802 },//  3
        {  0.24485802,  0.05044353,  0.00000000 },//  4
        {  0.24485802, -0.05044353,  0.00000000 },//  5
        { -0.24485802,  0.05044353,  0.00000000 },//  6
        { -0.24485802, -0.05044353,  0.00000000 },//  7
        {  0.00000000,  0.24485802,  0.05044353 },//  8
        {  0.00000000,  0.24485802, -0.05044353 },//  9
        {  0.00000000, -0.24485802,  0.05044353 },// 10
        {  0.00000000, -0.24485802, -0.05044353 },// 11
        {  0.10088705,  0.08165056,  0.21357546 },// 12
        {  0.10088705,  0.08165056, -0.21357546 },// 13
        {  0.10088705, -0.08165056,  0.21357546 },// 14
        {  0.10088705, -0.08165056, -0.21357546 },// 15
        { -0.10088705,  0.08165056,  0.21357546 },// 16
        { -0.10088705,  0.08165056, -0.21357546 },// 17
        { -0.10088705, -0.08165056,  0.21357546 },// 18
        { -0.10088705, -0.08165056, -0.21357546 },// 19
        {  0.21357546,  0.10088705,  0.08165056 },// 20
        {  0.21357546,  0.10088705, -0.08165056 },// 21
        {  0.21357546, -0.10088705,  0.08165056 },// 22
        {  0.21357546, -0.10088705, -0.08165056 },// 23
        { -0.21357546,  0.10088705,  0.08165056 },// 24
        { -0.21357546,  0.10088705, -0.08165056 },// 25
        { -0.21357546, -0.10088705,  0.08165056 },// 26
        { -0.21357546, -0.10088705, -0.08165056 },// 27
        {  0.08165056,  0.21357546,  0.10088705 },// 28
        {  0.08165056,  0.21357546, -0.10088705 },// 29
        {  0.08165056, -0.21357546,  0.10088705 },// 30
        {  0.08165056, -0.21357546, -0.10088705 },// 31
        { -0.08165056,  0.21357546,  0.10088705 },// 32
        { -0.08165056,  0.21357546, -0.10088705 },// 33
        { -0.08165056, -0.21357546,  0.10088705 },// 34
        { -0.08165056, -0.21357546, -0.10088705 },// 35
        {  0.05044353,  0.16300462,  0.18229291 },// 36
        {  0.05044353,  0.16300462, -0.18229291 },// 37
        {  0.05044353, -0.16300462,  0.18229291 },// 38
        {  0.05044353, -0.16300462, -0.18229291 },// 39
        { -0.05044353,  0.16300462,  0.18229291 },// 40
        { -0.05044353,  0.16300462, -0.18229291 },// 41
        { -0.05044353, -0.16300462,  0.18229291 },// 42
        { -0.05044353, -0.16300462, -0.18229291 },// 43
        {  0.18229291,  0.05044353,  0.16300462 },// 44
        {  0.18229291,  0.05044353, -0.16300462 },// 45
        {  0.18229291, -0.05044353,  0.16300462 },// 46
        {  0.18229291, -0.05044353, -0.16300462 },// 47
        { -0.18229291,  0.05044353,  0.16300462 },// 48
        { -0.18229291,  0.05044353, -0.16300462 },// 49
        { -0.18229291, -0.05044353,  0.16300462 },// 50
        { -0.18229291, -0.05044353, -0.16300462 },// 51
        {  0.16300462,  0.18229291,  0.05044353 },// 52
        {  0.16300462,  0.18229291, -0.05044353 },// 53
        {  0.16300462, -0.18229291,  0.05044353 },// 54
        {  0.16300462, -0.18229291, -0.05044353 },// 55
        { -0.16300462,  0.18229291,  0.05044353 },// 56
        { -0.16300462,  0.18229291, -0.05044353 },// 57
        { -0.16300462, -0.18229291,  0.05044353 },// 58
        { -0.16300462, -0.18229291, -0.05044353 },// 59   
    };
    
    private static final Helpers.Face[] ballf = {
        // 20 hexagons
        new Helpers.Face(new int[]{ 0,  2, 18, 42, 38, 14}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 1,  3, 17, 41, 37, 13}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 2,  0, 12, 36, 40, 16}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 3,  1, 15, 39, 43, 19}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 4,  5, 23, 47, 45, 21}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 5,  4, 20, 44, 46, 22}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 6,  7, 26, 50, 48, 24}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 7,  6, 25, 49, 51, 27}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 8,  9, 33, 57, 56, 32}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 9,  8, 28, 52, 53, 29}, Helpers.color.ball),
        new Helpers.Face(new int[]{10, 11, 31, 55, 54, 30}, Helpers.color.ball),
        new Helpers.Face(new int[]{11, 10, 34, 58, 59, 35}, Helpers.color.ball),
        new Helpers.Face(new int[]{12, 44, 20, 52, 28, 36}, Helpers.color.ball),
        new Helpers.Face(new int[]{13, 37, 29, 53, 21, 45}, Helpers.color.ball),
        new Helpers.Face(new int[]{14, 38, 30, 54, 22, 46}, Helpers.color.ball),
        new Helpers.Face(new int[]{15, 47, 23, 55, 31, 39}, Helpers.color.ball),
        new Helpers.Face(new int[]{16, 40, 32, 56, 24, 48}, Helpers.color.ball),
        new Helpers.Face(new int[]{17, 49, 25, 57, 33, 41}, Helpers.color.ball),
        new Helpers.Face(new int[]{18, 50, 26, 58, 34, 42}, Helpers.color.ball),
        new Helpers.Face(new int[]{19, 43, 35, 59, 27, 51}, Helpers.color.ball),
    
        // 12 pentagons
        new Helpers.Face(new int[]{ 0, 14, 46, 44, 12}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 1, 13, 45, 47, 15}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 2, 16, 48, 50, 18}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 3, 19, 51, 49, 17}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 4, 21, 53, 52, 20}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 5, 22, 54, 55, 23}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 6, 24, 56, 57, 25}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 7, 27, 59, 58, 26}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 8, 32, 40, 36, 28}, Helpers.color.ball),
        new Helpers.Face(new int[]{ 9, 29, 37, 41, 33}, Helpers.color.ball),
        new Helpers.Face(new int[]{10, 30, 38, 42, 34}, Helpers.color.ball),
        new Helpers.Face(new int[]{11, 35, 43, 39, 31}, Helpers.color.ball)
    };

    // Create new ball at given position
    public Ball(Helpers.Vector3 p) {
        super(ballv, ballf);
        this.position = p;
        moved = true;
        velocity.z = -0.05;
        velocity.y = -0.06;
        super.update();
        active = false;
    }

    // Getters and setters
    public boolean getActive() { return active; }
    public double getRadius() { return radius; }
    public boolean isInsideCup() { return insideCup; }
    public boolean isStuckToBottom() { return stuckToBottom; }
    public Cup getContainerCup() { return containerCup; }
    
    public void setActive(boolean active) { 
        this.active = active; 
        if (active) {
            this.activationTime = System.currentTimeMillis();
        }
    }
    
    public void setVelocityX(double vx) { velocity.x = vx; }
    public void setVelocityY(double vy) { velocity.y = vy; }
    public void setVelocityZ(double vz) { velocity.z = vz; }
    
    // Check if ball has been active too long / game has been soft locked
    public boolean hasBeenActiveTooLong() {
        if (!active) return false;
        
        long currentTime = System.currentTimeMillis();
        long activeTime = currentTime - activationTime;
        return activeTime > MAX_ACTIVE_TIME_MS;
    }
    
    // Check if ball is out of bounds
    private boolean isOutOfBounds() {
        // Check standard boundaries (too high, extreme left/right/front/back, going away from cups, behind starting point)
        boolean leftOfCups = position.x < LEFTMOST_CUP_X - radius * 6;
        boolean rightOfCups = position.x > RIGHTMOST_CUP_X + radius * 6;
        boolean behindStartPosition = position.z > ORIGIN_Z;
        return  position.y > MAX_Y || leftOfCups || rightOfCups || 
            behindStartPosition || position.z < MIN_Z || position.z > MAX_Z;
    }
    
    // respawns ball
    public void reset() {
        position.x = ORIGIN_X;
        position.y = ORIGIN_Y;
        position.z = ORIGIN_Z;
        velocity.x = 0;
        velocity.y = -0.06;
        velocity.z = -0.13;
        moved = true;
        active = false;
        insideCup = false;
        containerCup = null;
        stuckToBottom = false;
        activationTime = 0;
        
        updateVertices();
    }

    @Override
    public void update() {
        if (!active) return;

        if (stuckToBottom && containerCup != null) {
            handleStuckToBottom();
            return;
        }
        
        // Apply physics
        applyPhysics();
        
        // Check if out of bounds using tighter boundaries
        if (isOutOfBounds()) {
            reset();
            return;
        }
        
        // Check floor first
        if (checkFloorCollision()) {
            return;
        }
        
        // Step-based collision detection
        handleCollisions();
        
        // Safety check... most of the time
        enforceNoWallClipping();
        
        // Mark as moved
        moved = true;
        updateVertices();
    }
    
    // if already flagged ball in bottom of cup, make sure it sticks there (or at least tries to these physics kinda suck)
    private void handleStuckToBottom() {
        position.y = containerCup.position.y - radius;
        velocity.x = 0;
        velocity.y = 0;
        velocity.z = 0;
        moved = true;
        updateVertices();
    }
    
    // Apply the physics each frame
    private void applyPhysics() {
        // Apply air resistance to slow down the ball
        velocity.multInPlace(DRAG_FACTOR);
        // apply gravity
        velocity.y += GRAVITY;
    }
    
    // Check if collides with floor (y = 0)
    private boolean checkFloorCollision() {
        if (position.y + velocity.y + radius >= FLOOR_Y) {
            // Don't go through floor
            position.y = FLOOR_Y - radius;
            
            // Check if inside a cup on the floor
            for (Entity entity : Renderer.CUPS) {
                if (isDeactivatedCup(entity)) continue;
                
                Cup cup = (Cup) entity;
                double dx = position.x - cup.position.x;
                double dz = position.z - cup.position.z;
                double dist = Math.sqrt(dx*dx + dz*dz);
                
                // If inside bottom radius of cup
                if (dist < 0.7) {
                    freezeBallInCup(cup);
                    return true;
                }
            }
            
            // Regular floor bounce
            if (velocity.y > 0) {
                velocity.y = -velocity.y * BOUNCE_FACTOR;
                velocity.x *= BOUNCE_FACTOR * 0.95;
                velocity.z *= BOUNCE_FACTOR * 0.95;
            }
        }
        return false;
    }
    
    // initializes flag that ball hits cup
    private void freezeBallInCup(Cup cup) {
        velocity.x = 0;
        velocity.y = 0;
        velocity.z = 0;
        insideCup = true;
        containerCup = cup;
        stuckToBottom = true;
        
        cup.setHit(true);
        reset();
        
        moved = true;
        updateVertices();
    }
    
    // handle collisions (again mostly.. clipping is the end of me)
    private void handleCollisions() {
        final int STEPS = 10;
        double stepX = velocity.x / STEPS;
        double stepY = velocity.y / STEPS;
        double stepZ = velocity.z / STEPS;
        boolean collided = false;
        
        for (int step = 0; step < STEPS; step++) {
            // Take one small step
            double tempX = position.x + stepX;
            double tempY = position.y + stepY;
            double tempZ = position.z + stepZ;
            
            // Check all cups for collisions at this position
            for (Entity entity : Renderer.CUPS) {
                if (isDeactivatedCup(entity)) continue;
                
                Cup cup = (Cup) entity;
                
                // Skip if not at cup height
                if (!isAtCupHeight(tempY, cup)) {
                    continue;
                }
                
                // Calculate cup properties
                double cupX = cup.position.x;
                double cupY = cup.position.y;
                double cupZ = cup.position.z;
                double wallRadius = calculateCupWallRadius(tempY, cupY);
                
                // Distance to cup center
                double dx = tempX - cupX;
                double dz = tempZ - cupZ;
                double dist = Math.sqrt(dx*dx + dz*dz);
                
                // Collision detection
                if (detectWallCollision(dist, wallRadius)) {
                    handleWallCollision(dist, dx, dz, wallRadius, cupX, cupZ);
                    
                    // Update temp position and step sizes
                    tempX = position.x;
                    tempZ = position.z;
                    
                    stepX = velocity.x / STEPS;
                    stepY = velocity.y / STEPS;
                    stepZ = velocity.z / STEPS;
                    
                    collided = true;
                    break;
                }
                else if (detectEnteredCup(dist, wallRadius, tempY, cupY)) {
                    handleEnteredCup(cup);
                    
                    // Recalculate step sizes
                    stepX = velocity.x / STEPS;
                    stepY = velocity.y / STEPS;
                    stepZ = velocity.z / STEPS;
                    break;
                }
                else if (insideCup && cup == containerCup) {
                    handleInsideCupCollision(tempY, cupY, dist);
                    
                    // If ball became stuck, don't continue
                    if (stuckToBottom) {
                        break;
                    }
                    
                    // Step might have been adjusted - update temp Y
                    tempY = position.y;
                }
            }
            
            // If we hit something, don't continue with this step
            if (collided || stuckToBottom) {
                break;
            }
            
            // No collision detected, apply this step
            position.x = tempX;
            position.y = tempY;
            position.z = tempZ;
        }
    }
    
    // check if cup is still active
    private boolean isDeactivatedCup(Entity entity) {
        return entity == null || 
               entity.position.y < -10 || 
               (entity instanceof Cup && ((Cup)entity).isHit() && entity != containerCup);
    }
    
    // check if ball is at the same height as the cup
    private boolean isAtCupHeight(double ballY, Cup cup) {
        return !(ballY < cup.position.y - radius*2.5 || ballY > cup.position.y + 3.0 + radius*2.5);
    }
    
    // calculate the cup wall radius based on ball height relative to cup position
    private double calculateCupWallRadius(double ballY, double cupY) {
        double wallRadius;
        double relHeight = Math.max(0, Math.min(1, ballY - cupY));
        
        // Base wall radius (tapers from 0.7 at bottom to 1.0 at top)Good i
        wallRadius = 0.7 + (1.0 - 0.7) * relHeight;
        
        // For heights above the cup, maintain top radius
        if (ballY > cupY + 1.0) {
            wallRadius = 1.0; // Use top radius for extended collision area
            
            // Create a gradually thickening wall above the cup
            double heightAboveCup = ballY - (cupY + 1.0);
            double extraThickness = heightAboveCup * 0.1;
            wallRadius += Math.min(extraThickness, 0.2);
        }
        
        return wallRadius;
    }
    
    // Checks if touching wall
    private boolean detectWallCollision(double dist, double wallRadius) {
        double SAFE_MARGIN = radius * 2.0;
        return Math.abs(dist - wallRadius) < SAFE_MARGIN;
    }
    
    // Handle wall collision with enhanced ricochet
    private void handleWallCollision(double dist, double dx, double dz, double wallRadius, 
                                    double cupX, double cupZ) {
        // Calculate normal (unit vector pointing outward from cup center)
        double nx = dx / Math.max(dist, 0.001);
        double nz = dz / Math.max(dist, 0.001);
        
        // Add slight randomness to normal for more realistic bounces
        nx += (random.nextDouble() - 0.5) * RICOCHET_RANDOMNESS;
        nz += (random.nextDouble() - 0.5) * RICOCHET_RANDOMNESS;
        
        // Normalize the normal vector again after adding randomness
        double normalMagnitude = Math.sqrt(nx*nx + nz*nz);
        nx /= normalMagnitude;
        nz /= normalMagnitude;
        
        boolean isInsideCup = dist < wallRadius - radius;
        double safeRadius;
        
        if (isInsideCup || (insideCup && containerCup != null)) {
            // Inside cup - push away from wall inward
            safeRadius = wallRadius - radius * 2.4;
            position.x = cupX + nx * safeRadius;
            position.z = cupZ + nz * safeRadius;
            
            // Reflect velocity inward with enhanced bounce
            double dot = velocity.x * (-nx) + velocity.z * (-nz);
            velocity.x = (velocity.x - 2 * dot * (-nx)) * 0.75; // Increased bounce factor from 0.6
            velocity.z = (velocity.z - 2 * dot * (-nz)) * 0.75;
            
            // Add slight upward boost for more dynamic bounces inside cup
            velocity.y -= random.nextDouble() * 0.01;
        } else {
            // Outside cup - push away outward
            safeRadius = wallRadius + radius * 2.4;
            position.x = cupX + nx * safeRadius;
            position.z = cupZ + nz * safeRadius;
            
            // Reflect velocity outward with enhanced bounce
            double dot = velocity.x * nx + velocity.z * nz;
            
            // Increase the reflection strength for more dramatic ricochets
            double bounceFactor = BOUNCE_FACTOR + (random.nextDouble() * 0.15); // Randomized 0.8-0.95 bounce
            velocity.x = (velocity.x - 2 * dot * nx) * bounceFactor;
            velocity.z = (velocity.z - 2 * dot * nz) * bounceFactor;
            
            // Add slight vertical bounce for more dynamic bounces
            velocity.y -= random.nextDouble() * 0.02; 
        }
    }
    
    // Detect if ball entered cup
    private boolean detectEnteredCup(double dist, double wallRadius, double ballY, double cupY) {
        return !insideCup && dist < wallRadius - radius && ballY < cupY + 1.0;
    }
    
    // Handle entering cup
    private void handleEnteredCup(Cup cup) {
        insideCup = true;
        containerCup = cup;
        velocity.multInPlace(0.6);
    }
    
    // Handle collision when inside cup with enhanced ricochet
    private void handleInsideCupCollision(double tempY, double cupY, double dist) {
        // Check if hitting bottom
        if (tempY + radius > cupY) {
            tempY = cupY - radius;
            position.y = tempY;
            
            if (Math.abs(velocity.y) < 0.01) {
                // Stick to bottom
                stuckToBottom = true;
                velocity.x = 0;
                velocity.y = 0;
                velocity.z = 0;
            } else {
                // Bounce off bottom with enhanced bounce
                velocity.y = -velocity.y * 0.5; // Increased from 0.3 for more bounce
                
                // Add slight horizontal movement for more interesting bounces
                velocity.x += (random.nextDouble() - 0.5) * 0.02;
                velocity.z += (random.nextDouble() - 0.5) * 0.02;
            }
        }
        
        // Check if trying to exit from top
        if (tempY > cupY + 1.0 - radius) {
            // Only allow exit if close to center
            if (dist < 0.3) {
                insideCup = false;
                containerCup = null;
            } else {
                // Bounce off top with enhanced bounce
                position.y = cupY + 1.0 - radius;
                velocity.y = -velocity.y * BOUNCE_FACTOR * 1.1; // Enhanced top bounce
                
                // Add slight horizontal movement when hitting the cup rim
                velocity.x += (random.nextDouble() - 0.5) * 0.03;
                velocity.z += (random.nextDouble() - 0.5) * 0.03;
            }
        }
    }
    
    // Final safety check to prevent the ball from ever being inside a wall
    private void enforceNoWallClipping() {
        // Stop any crazy velocities (emergency safety)
        if (Math.abs(velocity.x) > 0.5 || Math.abs(velocity.z) > 0.5) {
            velocity.x *= 0.5;
            velocity.z *= 0.5;
        }
        
        for (Entity entity : Renderer.CUPS) {
            if (isDeactivatedCup(entity)) continue;
            
            Cup cup = (Cup) entity;
            
            double cupX = cup.position.x;
            double cupY = cup.position.y;
            double cupZ = cup.position.z;
            
            // Special case for container cup
            if (insideCup && cup == containerCup) {
                handleContainerCupClipping(cupX, cupY, cupZ);
                continue;
            }
            
            // Skip if not at cup height
            if (!isAtCupHeight(position.y, cup)) {
                continue;
            }
            
            // Calculate cup properties
            double dx = position.x - cupX;
            double dz = position.z - cupZ;
            double dist = Math.sqrt(dx*dx + dz*dz);
            double wallRadius = calculateCupWallRadius(position.y, cupY);
            
            // Check if too close to wall
            handleGeneralCupClipping(dist, dx, dz, wallRadius, cupX, cupY, cupZ);
        }
    }
    
    // Handles clipping and ricochet with the cup
    private void handleContainerCupClipping(double cupX, double cupY, double cupZ) {
        double dx = position.x - cupX;
        double dz = position.z - cupZ;
        double dist = Math.sqrt(dx*dx + dz*dz);
        
        // Calculate cup wall radius
        double wallRadius = calculateCupWallRadius(position.y, cupY);
        
        // If too close to wall from inside
        double INNER_MARGIN = radius * 1.5;
        if (dist > wallRadius - INNER_MARGIN) {
            // Calculate normal (pointing inward from wall)
            double nx = dx / Math.max(dist, 0.001);
            double nz = dz / Math.max(dist, 0.001);
            
            // Add slight randomness for more realistic bounces
            nx += (random.nextDouble() - 0.5) * RICOCHET_RANDOMNESS;
            nz += (random.nextDouble() - 0.5) * RICOCHET_RANDOMNESS;
            
            // Normalize after adding randomness
            double normalMagnitude = Math.sqrt(nx*nx + nz*nz);
            nx /= normalMagnitude;
            nz /= normalMagnitude;
            
            // Push ball inward
            double safeRadius = wallRadius - INNER_MARGIN * 1.2;
            position.x = cupX + nx * safeRadius;
            position.z = cupZ + nz * safeRadius;
            
            // Reflect velocity inward with enhanced bounce
            double dot = velocity.x * (-nx) + velocity.z * (-nz);
            velocity.x = (velocity.x - 2 * dot * (-nx)) * 0.75; // Increased from 0.6
            velocity.z = (velocity.z - 2 * dot * (-nz)) * 0.75;
            
            // Add slight vertical movement for more dynamic bounces
            velocity.y -= random.nextDouble() * 0.01;
        }
    }
    
    // handle general cup clipping issues with enhanced ricochet
    private void handleGeneralCupClipping(double dist, double dx, double dz, double wallRadius, 
                                        double cupX, double cupY, double cupZ) {
        // Check if too close to wall
        double SAFE_MARGIN = radius * 2.0;
        if (Math.abs(dist - wallRadius) < SAFE_MARGIN) {
            // Calculate normal
            double nx = dx / Math.max(dist, 0.001);
            double nz = dz / Math.max(dist, 0.001);
            
            // Add slight randomness to normal for more realistic bounces
            nx += (random.nextDouble() - 0.5) * RICOCHET_RANDOMNESS;
            nz += (random.nextDouble() - 0.5) * RICOCHET_RANDOMNESS;
            
            // normalize the normal vector again after adding randomness
            double normalMagnitude = Math.sqrt(nx*nx + nz*nz);
            nx /= normalMagnitude;
            nz /= normalMagnitude;
            
            if (dist < wallRadius) {
                // inside cup - push inward
                double safeRadius = wallRadius - SAFE_MARGIN * 1.2;
                position.x = cupX + nx * safeRadius;
                position.z = cupZ + nz * safeRadius;
                
                // reflect velocity with enhanced ricochet
                double dot = velocity.x * (-nx) + velocity.z * (-nz);
                velocity.x = (velocity.x - 2 * dot * (-nx)) * 0.75; // Increased from 0.6
                velocity.z = (velocity.z - 2 * dot * (-nz)) * 0.75;
                
                // add slight vertical boost
                velocity.y -= random.nextDouble() * 0.015;
            } else {
                // Outside cup - push outward
                double safeRadius = wallRadius + SAFE_MARGIN * 1.2;
                position.x = cupX + nx * safeRadius;
                position.z = cupZ + nz * safeRadius;
                
                // Reflect velocity with enhanced ricochet
                double dot = velocity.x * nx + velocity.z * nz;
                
                // Dynamic bounce factor based on velocity for more dramatic ricochets
                double speed = Math.sqrt(velocity.x*velocity.x + velocity.z*velocity.z);
                double bounceFactor = BOUNCE_FACTOR + (Math.min(speed, 0.2) * 0.5); // Higher velocity = more bounce
                
                velocity.x = (velocity.x - 2 * dot * nx) * bounceFactor;
                velocity.z = (velocity.z - 2 * dot * nz) * bounceFactor;
                
                // Add slight vertical bounce
                velocity.y -= random.nextDouble() * 0.02;
            }
            
            // add extra damping near top of cup, but with more dynamic bounces
            applyExtraDamping(cupY);
        }
        
        // emergency check for positions very close to cup center
        handleExtremePositionError(dist, dx, dz, wallRadius, cupX, cupZ);
    }
    
    // apply extra damping near top of cup with enhanced bounces
    private void applyExtraDamping(double cupY) {
        if (position.y > cupY + 0.8) {
            // Near top - add some damping but allow more dynamic bounces
            velocity.multInPlace(0.8); // Reduced damping from 0.7
            
            // If very close to top rim, add more interesting dynamics
            if (Math.abs(position.y - (cupY + 1.0)) < 0.2) {
                // Add slight horizontal movement for more dynamic rim bounces
                velocity.x += (random.nextDouble() - 0.5) * 0.02;
                velocity.z += (random.nextDouble() - 0.5) * 0.02;
                
                // Apply some damping, but less than before
                velocity.multInPlace(0.75); // Reduced from 0.6
            }
        }
    }
    
    // handle extreme position errors (too close to cup center)
    private void handleExtremePositionError(double dist, double dx, double dz, 
                                          double wallRadius, double cupX, double cupZ) {
        if (dist < radius) {
            // Too close to cup center - push out radially
            double angle = Math.atan2(dz, dx);
            position.x = cupX + Math.cos(angle) * (wallRadius + radius * 2.0);
            position.z = cupZ + Math.sin(angle) * (wallRadius + radius * 2.0);
            
            // Kill velocity
            velocity.x = 0;
            velocity.z = 0;
        }
    }
    
    // update vertex positions based on ball position
    private void updateVertices() {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i][0] = ogVertices[i][0] + position.x;
            vertices[i][1] = ogVertices[i][1] + position.y;
            vertices[i][2] = ogVertices[i][2] + position.z;
        }
    }
}


