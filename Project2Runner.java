public class Project2Runner {
    
    /*
     * Name: Rivan
     * Student ID: 501298613
     * 
     ******** Project Description ********
     * 
     * This project is a 3D cup pong game where players attempt to throw a ball into 
     * red cups arranged in a triangle formation. The game uses a custom 3D rendering 
     * engine to display cups and a ball that follow realistic physics. Players can 
     * throw the ball by dragging and releasing the mouse, with the direction and 
     * power of the throw determined by the drag motion. When a ball lands in a cup, 
     * the cup is marked as hit and removed from play. The goal is to hit all cups. 
     * The game also features a camera mode that allows players to move around the 
     * scene using WASD keys and mouse controls to view the game from different angles.
     * 
     ******** Swing Requirement ********
     * 
     * The project satisfies the Swing requirement by using at least 3 unique Swing 
     * components: JFrame, JPanel, and JButton/JRadioButton. The main game window is 
     * created using JFrame, the rendering surface is a custom JPanel subclass, 
     * and user controls include a JButton for restarting the game and JRadioButtons
     * for switching between game and camera modes.
     * 
     ******** 2D Graphics Requirement ********
     *
     * The project satisfies the 2D Graphics requirement by using a custom rendering 
     * JPanel that implements the paintComponent method. The panel uses Java's 2D 
     * graphics to render a 3D scene by implementing a custom rasterization algorithm 
     * that converts 3D vertices to 2D screen coordinates. It draws polygons using 
     * BufferedImage and Graphics2D objects and applies a z-buffer algorithm for proper depth handling.
     * 
     ******** Event Listener Requirement ********
     *
     * The project satisfies the Event Listener requirement by implementing multiple 
     * listener interfaces. The Renderer class implements ActionListener to handle animation timer 
     * events, KeyListener to process keyboard input for camera movement, MouseListener to detect 
     * mouse clicks for throwing the ball, and MouseMotionListener to track mouse movement for camera 
     * control. Additionally, the restart button uses an ActionListener to reset the game state, and 
     * the radio buttons use ActionListeners to switch between game modes.
     */

    public static void main(String[] args) {
        Renderer.main(args);
    }
}
