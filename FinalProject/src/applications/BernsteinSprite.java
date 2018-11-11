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
 * Mick is a Sprite that responds to user interaction. It is an example of a Sprite that uses
 * multiple pieces of visual content that vary depending on the state (e.g., walking left, walking
 * right, etc...).
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class BernsteinSprite extends AbstractSprite implements KeyListener
{
  private boolean nearLeftEdge, nearRightEdge;
  private int lastTickTime, xBernstein, yBernstein;
  private int direction, position;
  private Content[][] images;

  // directions
  private static final int RIGHT = 0;
  private static final int LEFT = 1;
  private static final int BACK = 2;

  // positions
  private static final int DIR1 = 0;
  private static final int DIR2 = 1;
  private static final int DIR3 = 2;
  private static final int DIR4 = 3;

  private static final int ERASE1 = 0;
  private static final int ERASE2 = 1;
  private static final int ERASE3 = 2;
  private static final int ERASE4 = 3;
  private static final int ERASE5 = 4;

  private static final int UPRIGHT = 4;

  private static final int[] SEQUENCE = {DIR1, DIR2, DIR3, DIR4, UPRIGHT};
  private static final int[] SEQUENCE_ERASE = {ERASE1, ERASE2, ERASE3, ERASE4, ERASE5};

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
    else if (keyCode == KeyEvent.VK_SPACE)
      handleFire();
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
    position = ERASE3;
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
    int tx, ty;

    /**
     * if (tongueOnPole) { tongues[RIGHT].render(g); } else if (tongueOut) { tx = xMick +
     * TONGUE_X[direction]; ty = yMick + TONGUE_Y[SEQUENCE[position]];
     * 
     * tongues[direction].setLocation(tx, ty); tongues[direction].render(g);
     * 
     * 
     * }
     **/

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
    lastTickTime = time;

    if (xBernstein > 1045)
      nearRightEdge = true;
    else
      nearRightEdge = false;

    if (xBernstein > 0)
      nearLeftEdge = false;
    else
      nearLeftEdge = true;

    setLocation(xBernstein, yBernstein);
  }
}
