// muhit

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener{
  class Block {
    int x;
    int y;
    int width;
    int height;
    Image image;

    int startX;
    int startY;
    char direction = 'U'; // U D L R
    int velocityX = 0;
    int velocityY = 0;

    Block(Image image, int x, int y, int width, int height) {
      this.image = image;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.startX = x;
      this.startY = y;
    }
    
    void updateDirection(char direction) {
      this.direction = direction;
      updateVelocity();
    }
   
    void updateVelocity() {
      if (this.direction == 'U') {
        this.velocityX = 0;
        this.velocityY = -tileSize / 4;
      } else if (this.direction == 'D') {
        this.velocityX = 0;
        this.velocityY = tileSize / 4;
      } else if (this.direction == 'L') {
        this.velocityX = -tileSize / 4;
        this.velocityY = 0;
      } else if (this.direction == 'R') {
        this.velocityX = tileSize / 4;
        this.velocityY = 0;
      }
    }
  }

  private int rowCount = 21;
  private int columnCount = 19;
  private int tileSize = 32;
  private int boardWidth = columnCount * tileSize;
  private int boardHeight = rowCount * tileSize;

  private Image wallImage;
  private Image blueGhostImage;
  private Image orangeGhostImage;
  private Image pinkGhostImage;
  private Image redGhostImage;

  private Image pacmanUpImage;
  private Image pacmanDownImage;
  private Image pacmanLeftImage;
  private Image pacmanRightImage;

  private String[] tileMap = {
      "XXXXXXXXXXXXXXXXXXX",
      "X        X        X",
      "X XXXXX  X XXX XX X",
      "X                 X",
      "X XX X XXXXX X XX X",
      "X    X       X    X",
      "XXXX XXXX XXXX XXXX",
      "OOOX X       X XOOO",
      "XXXX X XXrXX X XXXX",
      "O       bpo       O",
      "XXXX X XXXXX X XXXX",
      "OOOX X       X XOOO",
      "XXXX X XXXXX X XXXX",
      "X        X        X",
      "X XX XXX X XXX XX X",
      "X  X     P     X  X",
      "XX X X XXXXX X X XX",
      "X    X   X   X    X",
      "X XXXXXX X XXXXXX X",
      "X                 X",
      "XXXXXXXXXXXXXXXXXXX"
  };
  
  HashSet<Block> walls;
  HashSet<Block> foods;
  HashSet<Block> ghosts;
  Block pacman;

  Timer gameLoop;

  PacMan() {
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setBackground(Color.BLACK);
    addKeyListener(this);
    setFocusable(true);

    // load 
    wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
    blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
    orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
    pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
    redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

    pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
    pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
    pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
    pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

    loadMap();

    //How long it takes to start timer, milliseconds gone between frames
    gameLoop = new Timer(50, this); // 20fps (1000/50)
    gameLoop.start();
    
  }

  public void loadMap() {
    walls = new HashSet<Block>();
    foods = new HashSet<Block>();
    ghosts = new HashSet<Block>();

    for (int r = 0; r < rowCount; r++) {
      for (int c = 0; c < columnCount; c++) {
        String row = tileMap[r];
        char tileMapChar = row.charAt(c);

        int x = c * tileSize;
        int y = r * tileSize;

        if (tileMapChar == 'X') { // block wall
          Block wall = new Block(wallImage, x, y, tileSize, tileSize);
          walls.add(wall);
        } else if (tileMapChar == 'b') { // blue ghost
          Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
          ghosts.add(ghost);
        } else if (tileMapChar == 'o') { // orange ghost
          Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
          ghosts.add(ghost);
        } else if (tileMapChar == 'p') { // pink ghost
          Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
          ghosts.add(ghost);
        } else if (tileMapChar == 'r') { // red ghost
          Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
          ghosts.add(ghost);
        } else if (tileMapChar == 'P') { // pacman
          pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
        } else if (tileMapChar == ' ') { // food
          Block food = new Block(null, x + 14, y + 14, 4, 4);
          foods.add(food);
        }
      }
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }
 
  public void draw(Graphics g) {
    g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

    for (Block ghost : ghosts) {
      g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
    }

    for (Block wall : walls) {
      g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
    }

    g.setColor(Color.WHITE);
    for (Block food : foods) {
      g.fillRect(food.x, food.y, food.width, food.height);
    }
  }

  public void move() {
    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;

    //check wall collisions
    for(Block wall: walls){
        if(collision(pacman,wall)){
          pacman.x -= pacman.velocityX;
          pacman.y -= pacman.velocityY;
          break;
        }
    }
  }

  public boolean collision(Block a,Block b){
    return a.x<b.x+b.width && 
           a.x+a.width>b.x && 
           a.y<b.y+b.height && 
           a.y+a.height>b.y;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    move();
    repaint();
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {
    // System.out.println("KeyEvent: "+ e.getKeyCode());
    if (e.getKeyCode() == KeyEvent.VK_UP) {
      pacman.updateDirection('U');
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      pacman.updateDirection('D');
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      pacman.updateDirection('L');
    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
      pacman.updateDirection('R');
    }
  }

}



