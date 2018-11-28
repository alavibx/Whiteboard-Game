package applications;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import io.ResourceFinder;
import visual.statik.sampled.*;
import visual.dynamic.described.*;

/**
 * The main character in the game Bernstdh-board.
 *
 * Bernstein is a Sprite that responds to user interaction.
 *
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/13/2018
 */
public class BernsteinSprite extends AbstractSprite implements KeyListener
{
  private boolean nearLeftEdge, nearRightEdge, nearTop;
  private int jumpTime, xBernstein, yBernstein;
  private int direction, position;
  private int totalPoints;
  private Content[][] images;

  // directions
  public static final int RIGHT = 0;
  public static final int LEFT = 1;
  public static final int BACK = 2;

  // positions
  private static final int DIR1 = 0;
  private static final int DIR2 = 1;
  private static final int DIR3 = 2;
  private static final int DIR4 = 3;

  public static final int ERASE1 = 0;
  public static final int ERASE2 = 1;

  public static final int UPRIGHT = 4;

  private static final int[] SEQUENCE = {DIR1, DIR2, DIR3, DIR4, UPRIGHT};

  /**
   * Explicit Value Constructor
   */
  public BernsteinSprite()
  {
    super();

    xBernstein = 300;
    yBernstein = 275;

    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    images = factory.createContents("bernstein_sprites.png", 3, 5, 4);
    direction = RIGHT;
    position = 4;

    jumpTime = 0;

    setLocation(xBernstein, yBernstein);
    setVisible(true);
  }

  /**
   * Invoked when a key is pressed (required by KeyListener).
   *
   * @param ke
   *          The GameButtonEvent
   */
  public void keyPressed(KeyEvent ke)
  {
    int keyCode;
    keyCode = ke.getKeyCode();

    if ((keyCode == KeyEvent.VK_KP_RIGHT) || (keyCode == KeyEvent.VK_RIGHT))
      handleRight();
    else if ((keyCode == KeyEvent.VK_KP_LEFT) || (keyCode == KeyEvent.VK_LEFT))
      handleLeft();
    else if ((keyCode == KeyEvent.VK_KP_UP) || (keyCode == KeyEvent.VK_UP))
      handleJump();
    else if (keyCode == KeyEvent.VK_SPACE)
    {
      handleFire();
    }
  }

  /**
   * Invoked when a key is released (required by KeyListener).
   *
   * @param ke
   *          The GameButtonEvent
   */
  public void keyReleased(KeyEvent ke)
  {
    position = UPRIGHT;
  }

  /**
   * Invoked when a key is pressed and released (required by KeyListener).
   *
   * @param ke
   *          The GameButtonEvent
   */
  public void keyTyped(KeyEvent ke)
  {
  }

  /**
   * Returns the x position of Bernstein on the GUI.
   * 
   * @return
   */
  public int getX()
  {
    return xBernstein;
  }

  /**
   * Returns the y position of Bernstein on the GUI.
   * 
   * @return
   */
  public int getY()
  {
    return yBernstein;
  }

  /**
   * Get the bounding rectangle for this Sprite
   *
   * @return The bounding rectangle
   */
  public Rectangle2D getBounds2D()
  {
    return images[direction][SEQUENCE[position]].getBounds2D();
  }

  /**
   * Get the visual content associated with this Sprite (required by Sprite)
   */
  public visual.statik.TransformableContent getContent()
  {
    return images[direction][SEQUENCE[position]];
  }

  /**
   * Handle a FIRE GameButtonEvent.
   */
  public void handleFire()
  {
    direction = BACK;
    position = ERASE1;
    position = ERASE2;
  }

  /**
   * Handle a JUMP GameButtonEvent.
   */
  public void handleJump()
  {
    if (!nearTop)
    {
      direction = BACK;
      position = ERASE1;
      yBernstein -= 100;
    }

    jumpTime = 2100;
  }

  /**
   * Handle a LEFT GameButtonEvent.
   */
  public void handleLeft()
  {
    if (!nearLeftEdge)
      xBernstein -= 15;

    // Handle normal movement
    if (direction == LEFT)
    {
      increasePosition();
    }
    else // Facing RIGHT
    {
      direction = LEFT;
      position = DIR1;
    }
  }

  /**
   * Handle a RIGHT GameButtonEvent.
   */
  public void handleRight()
  {
    if (!nearRightEdge)
    {
      xBernstein += 15;
    }

    if (direction == RIGHT)
    {
      increasePosition();
    }
    else // Facing LEFT
    {
      direction = RIGHT;
      position = DIR1;
    }
  }

  /**
   * Increase the position index.
   */
  private void increasePosition()
  {
    position = (position + 1) % (SEQUENCE.length - 1);
  }

  /**
   * Render this SpriteView.
   *
   * @param g
   *          The rendering engine to use
   */
  public void render(Graphics g)
  {
    super.render(g);
  }

  /**
   * Handle a tick event.
   *
   * @param time
   *          The current time (in milliseconds)
   */
  public void handleTick(int time)
  {
    if (jumpTime > 0)
      jumpTime -= 150;

    // Keep Bernstein from walking off the edge of the screen
    if (xBernstein > Board.bkgd_width - 175)
      nearRightEdge = true;
    else
      nearRightEdge = false;

    if (xBernstein > 0)
      nearLeftEdge = false;
    else
      nearLeftEdge = true;

    // Keep Bernstein from jumping too high
    if (yBernstein < 200)
      nearTop = true;
    else
      nearTop = false;

    // Make Bernstein "slide" down
    if (jumpTime < 300 && jumpTime > 0)
      yBernstein += 10;

    // Set Bernstein back to his original y position
    if (jumpTime == 0)
    {
      yBernstein = 275;
    }

    setLocation(xBernstein, yBernstein);
  }

  /**
   * Returns the direction number of Bernstein.
   * 
   * @return Returns the direction of Bernstein
   */
  public int getDirection()
  {
    int dir;

    if (direction == RIGHT)
    {
      dir = RIGHT;
    }
    else if (direction == LEFT)
    {
      dir = LEFT;
    }
    else if (direction == BACK)
    {
      dir = BACK;
    }
    else
    {
      dir = UPRIGHT;
    }

    return dir;
  }
  
  /**
   * Returns the position number of Bernstein.
   * 
   * @return The current position of Bernstein
   */
  public int getPosition()
  {
    return position;
  }
}
