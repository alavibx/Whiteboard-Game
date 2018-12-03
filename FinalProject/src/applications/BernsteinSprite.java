package applications;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import io.ResourceFinder;
import visual.statik.sampled.*;
import visual.dynamic.described.*;

/**
 * The main character in the game Bernstdh-board.
 *
 * Bernstein is a Sprite that responds to user interaction.
 *
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/30/2018
 */
public class BernsteinSprite extends AbstractSprite implements KeyListener
{
  private boolean nearLeftEdge, nearRightEdge, nearTop;
  private int jumpTime, xBernstein, yBernstein;
  private int direction, position;
  private Content[][] images;
  private Clip eraseClip;

  // directions
  public static final int RIGHT = 0;
  public static final int LEFT = 1;
  public static final int BACK = 2;

  // positions
  private static final int DIR1 = 0;
  private static final int DIR2 = 1;
  private static final int DIR3 = 2;
  private static final int DIR4 = 3;

  public static final int UPRIGHT = 4;

  public static final int ERASE1 = 0;
  public static final int ERASE2 = 1;

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

    // Get images based on number of rows and columns with a transparent background
    images = factory.createContents("bernstein_sprites.png", 3, 5, 4);
    direction = RIGHT;
    position = UPRIGHT;

    jumpTime = 0;

    setLocation(xBernstein, yBernstein);
    setVisible(true);
    
    try
    {
      BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("erase.wav"));
      AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
      
      System.out.print("HERE");

      eraseClip = AudioSystem.getClip();
      eraseClip.open(ais);
    }
    catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
    {
      // DO NOTHING
    }
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
    else if (keyCode == KeyEvent.VK_SHIFT)
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
   * Handle a FIRE GameButtonEvent. Bernstein performs an "erase" action.
   */
  public void handleFire()
  {
    eraseClip.start();
    eraseClip.setMicrosecondPosition(0);
    direction = BACK;
    position = ERASE1;
    position = ERASE2;
  }

  /**
   * Handle a JUMP GameButtonEvent. Bernstein performs a "jump" action.
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
   * Handle a LEFT GameButtonEvent. Berstein "moves" left.
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
   * Handle a RIGHT GameButtonEvent. Bernstein "moves" right.
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
    if (xBernstein > BernstdhBoard.BKGD_WIDTH - 175)
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
   * Returns the number associated with the direction of Bernstein.
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
   * Returns the number associate with the position of Bernstein.
   * 
   * @return The current position of Bernstein
   */
  public int getPosition()
  {
    return position;
  }
}
