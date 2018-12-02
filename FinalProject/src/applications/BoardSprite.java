package applications;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import visual.dynamic.described.*;
import visual.statik.sampled.TransformableContent;

/**
 * Class that describes a sprite on the whiteboard.
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/30/2018
 */
public class BoardSprite extends RuleBasedSprite
{
  private int x, y;
  private int width, height;
  private int points;
  private float opacity, opacityDecrease;
  private visual.statik.sampled.TransformableContent content;

  public BoardSprite(TransformableContent content, Boolean gain)
  {
    super(content);
    this.content = content;

    opacity = 1f;
    opacityDecrease = (float) Math.random();
    points = 0;

    // Get the width and height of the content
    width = (int) content.getBounds2D(false).getWidth();
    height = (int) content.getBounds2D(false).getHeight();

    // Generate the x position of the content randomly across the width of the whiteboard
    x = (int) (Math.random() * ((BernstdhBoard.BKGD_WIDTH - 75) - width) + 25);

    if(gain)
    {
      // Randomly determine the y position on whiteboard
      // Contents higher up on the board are worth more
      if (Math.round(Math.random()) == 0)
      {
        y = 275;
      }
      else
      {
        y = 175;
        points += 100;
      }

      // Increase point value depending on opacityDecrease
      if (opacityDecrease < 0.05)
        points += 300;
      else if (opacityDecrease < 0.1)
        points += 250;
      else if (opacityDecrease < 0.25)
        points += 200;
      else if (opacityDecrease < 0.5)
        points += 150;
      else if (opacityDecrease < 0.75)
        points += 100;
      else
        points += 50;

      // Increase point value depending on width of content
      if (width >= 400)
        points += 200;
      else if (width >= 300)
        points += 150;
      else if (width >= 200)
        points += 100;
      else
        points += 50;

      // Increase point value depending on height of content
      if (height >= 100)
        points += 100;
      else if (height >= 75)
        points += 75;
      else if (height >= 50)
        points += 50;
      else
        points += 25;
    }
    if(!gain)
    {
      // Randomly determine the y position on whiteboard
      // Contents higher up on the board are worth more
      if (Math.round(Math.random()) == 0)
      {
        y = 275;
      }
      else
      {
        y = 175;
        points -= 100;
      }

      // Increase point value depending on opacityDecrease
      if (opacityDecrease < 0.05)
        points -= 300;
      else if (opacityDecrease < 0.1)
        points -= 250;
      else if (opacityDecrease < 0.25)
        points -= 200;
      else if (opacityDecrease < 0.5)
        points -= 150;
      else if (opacityDecrease < 0.75)
        points -= 100;
      else
        points -= 50;

      // Increase point value depending on width of content
      if (width >= 400)
        points -= 200;
      else if (width >= 300)
        points -= 150;
      else if (width >= 200)
        points -= 100;
      else
        points -= 50;

      // Increase point value depending on height of content
      if (height >= 100)
        points -= 100;
      else if (height >= 75)
        points -= 75;
      else if (height >= 50)
        points -= 50;
      else
        points -= 25;
    }
  }

  /**
   * Create new X for BoardSprite.
   */
  public void newX()
  {
    x = (int) (Math.random() * (BernstdhBoard.BKGD_WIDTH - width));
  }

  /**
   * Gets the number of points.
   * 
   * @return points content is worth
   */
  public int getPoints()
  {
    return points;
  }

  /**
   * Returns X value of BoardSprite.
   * 
   * @return x position
   */
  public int getX()
  {
    return x;
  }

  /**
   * Returns Y value of BoardSprite.
   * 
   * @return y position
   */
  public int getY()
  {
    return y;
  }

  @Override
  public void handleTick(int time)
  {
    Iterator<Sprite> i = antagonists.iterator();
    Sprite bernstdh;

    while (i.hasNext())
    {
      bernstdh = i.next();

      if (intersectsBernstein(bernstdh) && opacity > 0)
      {
        erase();
      }
    }
  }

  /**
   * Returns true if this BoardSprite intersects with another Sprite.
   * 
   * @return true if intersects, false otherwise
   */
  public boolean intersects(Sprite s)
  {
    boolean retval;
    double maxx, maxy, minx, miny;
    double maxxO, maxyO, minxO, minyO;
    Rectangle2D r;

    retval = true;

    r = getBounds2D(true);
    minx = r.getX();
    miny = r.getY();
    maxx = minx + r.getWidth();
    maxy = miny + r.getHeight();

    r = s.getBounds2D(true);
    minxO = r.getX();
    minyO = r.getY();
    maxxO = minxO + r.getWidth();
    maxyO = minyO + r.getHeight();

    if ((maxx < minxO) || (minx > maxxO) || (maxy < minyO) || (miny > maxyO))
      retval = false;

    return retval;
  }

  /**
   * Returns boolean depending if this sprite intersects with Bernstein sprite.
   * 
   * @param s
   *          a BoardSprite
   * @return true if instersects with Bernstein, false otherwise
   */
  public boolean intersectsBernstein(Sprite s)
  {
    boolean retval;
    double maxx, maxy, minx, miny;
    double maxxO, maxyO, minxO, minyO;
    Rectangle2D r;

    retval = true;

    // Get the bounding box of the eraser portion of the Bernstein sprite
    r = s.getBounds2D(true);
    minx = r.getX() + r.getBounds2D().getWidth() - 50;
    miny = r.getY();
    maxx = r.getX() + r.getBounds2D().getWidth() - 20;
    maxy = miny + 30;

    // Get the bounding box of the content
    r = getBounds2D(true);
    minxO = r.getX();
    minyO = r.getY();
    maxxO = minxO + r.getWidth();
    maxyO = minyO + r.getHeight();

    if ((maxx < minxO) || (minx > maxxO) || (maxy < minyO) || (miny > maxyO))
      retval = false;

    // If the necessary bounding boxes intersect and the Bernstein sprite is in the correct position
    return retval && (((BernsteinSprite) s).getDirection() == BernsteinSprite.BACK
        && ((BernsteinSprite) s).getPosition() == BernsteinSprite.ERASE2);
  }

  /**
   * Erasing effect when Bernstein erases BoardSprite.
   */
  public void erase()
  {
    opacity -= opacityDecrease;

    if (opacity < 0)
      opacity = 0;

    Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
    content.setComposite(comp);
  }

  /**
   * Resets the opacity of the BoardSprite.
   */
  public void resetOpacity()
  {
    opacity = 1f;
  }

  /**
   * Returns the opacity of the BoardSprite.
   * 
   * @return current opacity level
   */
  public float getOpacity()
  {
    return opacity;
  }
}