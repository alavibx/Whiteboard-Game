package applications;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.geom.Rectangle2D;

import visual.dynamic.described.RuleBasedSprite;
import visual.dynamic.described.Sprite;
import visual.statik.sampled.TransformableContent;

/**
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/13/2018
 */
public class BoardSprite extends RuleBasedSprite
{
  private int x, y;
  private int width, height;
  private int points;
  private float opacity;
  private visual.statik.sampled.TransformableContent content;

  public BoardSprite(TransformableContent content)
  {
    super(content);
    
    this.content = content;
    
    opacity = 1f;
    points = 0;
    
    // Get the width and height of the content
    width = (int) content.getBounds2D(false).getWidth();
    height = (int) content.getBounds2D(false).getHeight();

    // Generate the x of the content randomly across the width of the whiteboard
    x = (int) (Math.random() * ((Board.bkgd_width - 75) - width) + 25);
    
    if (Math.round(Math.random()) == 0)
    {
      y = 275;
      points += 50;
    }
    else
    {
      y = 175;
      points += 150;
    }
    
    if (width >= 400)
      points += 200;
    else if (width >= 300)
      points += 150;
    else if (width >= 200)
      points += 100;
    else
      points += 50;
    
    if (height >= 100)
      points += 100;
    else if (height >= 75)
      points += 75;
    else if (height >= 50)
      points += 50;
    else
      points += 25;
  }
  
  /**
   * 
   */
  public void newX()
  {
    x = (int) (Math.random() * (Board.bkgd_width - width));
  }
  
  /**
   * 
   * @return
   */
  public int getPoints()
  {
    return points;
  }
  
  /**
   * 
   * @return
   */
  public int getX()
  {
    return x;
  }
  
  /**
   * 
   * @return
   */
  public int getY()
  {
    return y;
  }

  @Override
  public void handleTick(int time)
  {
    // TODO Auto-generated method stub

  }
  
  /**
   * 
   */
  public boolean intersects(Sprite s)
  {
    boolean          retval;
    double           maxx, maxy, minx, miny;
    double           maxxO, maxyO, minxO, minyO;
    Rectangle2D      r;

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

    if ( (maxx < minxO) || (minx > maxxO) ||
        (maxy < minyO) || (miny > maxyO) ) retval = false;

    return retval;
  }

  /**
   * 
   * @param s
   * @return
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
    
    return retval;
  }
  
  public void erase()
  {
    opacity -= .25;
    Composite comp = AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER,
        opacity);
    content.setComposite(comp);
  }
  
  public void resetOpacity()
  {
    opacity = 1f;
  }
  
  public float getOpacity()
  {
    return opacity;
  }
}
