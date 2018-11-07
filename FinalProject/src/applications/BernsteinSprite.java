package applications;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import io.ResourceFinder;
import visual.statik.sampled.*;
import visual.dynamic.described.*;

/**
 * The main character in the game Flakey.
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
  private boolean nearStreetlight, tongueOnPole, tongueOut;
  private int lastTickTime, tongueRetractionTime, xBernstein, yBernstein;
  private int direction, position;
  private Content[][] images;

 

  private static final int LEFT = 0;
  private static final int RIGHT = 1;

  private static final int LEFT_FORWARD = 0;
  private static final int RIGHT_FORWARD = 1;
  private static final int UPRIGHT = 2;

  private static final int[] SEQUENCE = {UPRIGHT, RIGHT_FORWARD, UPRIGHT, LEFT_FORWARD};

  /**
   * Explicit Value Constructor
   *
   * @param tree
   *          The tree that he might bump into
   * @param streetlight
   *          The streetlight that he might bump into
   */
  public BernsteinSprite()
  {
    super();

    xBernstein = 300;
    yBernstein = 115;
    

    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    images = factory.createContents("mick.png", 3, 4, 4);
    direction = RIGHT;
    position = 0;

    setLocation(xBernstein, yBernstein);
    setVisible(true);
  }

  /**
   * Invoked when a key is pressed (required by KeyListener).
   *
   * @param ke          The GameButtonEvent
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
   * @param ke          The GameButtonEvent
   */
  public void keyReleased(KeyEvent ke)
  {
  }

  /**
   * Invoked when a key is pressed and released (required by KeyListener).
   *
   * @param ke          The GameButtonEvent
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
    tongueOut = true;
    //tongueRetractionTime = lastTickTime + TONGUE_TIME;
  }

  /**
   * Handle a LEFT GameButtonEvent.
   */
  public void handleLeft()
  {
    if (xBernstein > 40)
      xBernstein -= 10;

    // Handle normal movement
    if (direction == LEFT)
    {
      increasePosition();
    }
    else // Facing RIGHT
    {
      direction = LEFT;
      position = UPRIGHT;
    }

  }

  /**
   * Handle a RIGHT GameButtonEvent.
   */
  public void handleRight()
  {
    if (!nearStreetlight)
      xBernstein += 10;

    if (direction == RIGHT)
    {
      increasePosition();
    }
    else // Facing LEFT
    {
      direction = RIGHT;
      position = UPRIGHT;
    }

  }

  /**
   * Increase the position index.
   */
  private void increasePosition()
  {
    position = (position + 1) % SEQUENCE.length;
  }

  /**
   * Render this SpriteView.
   *
   * @param g          The rendering engine to use
   */
  public void render(Graphics g)
  {
    int tx, ty;

    /**if (tongueOnPole)
    {
      tongues[RIGHT].render(g);
    }
    else if (tongueOut)
    {
      tx = xMick + TONGUE_X[direction];
      ty = yMick + TONGUE_Y[SEQUENCE[position]];

      tongues[direction].setLocation(tx, ty);
      tongues[direction].render(g);

     
    }**/

    super.render(g);

  }

  /**
   * Handle a tick event.
   *
   * @param time          The current time (in milliseconds)
   */
  public void handleTick(int time)
  {
    lastTickTime = time;

    if (xBernstein > 580)
      nearStreetlight = true;
    else
      nearStreetlight = false;

  


    setLocation(xBernstein, yBernstein);
  }
}