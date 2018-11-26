package applications;

import java.awt.geom.Rectangle2D;

import visual.dynamic.described.RuleBasedSprite;
import visual.dynamic.described.Sprite;
import visual.statik.TransformableContent;

/**
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/13/2018
 */
public class BoardSprite extends RuleBasedSprite
{
  private int x, y;

  public BoardSprite(TransformableContent content)
  {
    super(content);

    // Generate the x of the content randomly across the width of the whiteboard
    x = (int) (Math.random() * (Board.BKGD_WIDTH - content.getBounds2D(false).getWidth()));
    
    if (Math.round(Math.random()) == 0)
      y = 275;
    else
      y = 175;
  }
  
  public void newX()
  {
    x = (int) (Math.random() * (Board.BKGD_WIDTH - content.getBounds2D(false).getWidth()));
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }

  @Override
  public void handleTick(int time)
  {
    // TODO Auto-generated method stub

  }
  
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

  public boolean intersectsBernstein(Sprite s)
  {
    boolean retval;
    double maxx, maxy, minx, miny;
    double maxxO, maxyO, minxO, minyO;
    Rectangle2D r;

    retval = true;

    // Get the bounding box of the sprite
    r = s.getBounds2D(true);
    minx = r.getX() + 175;
    miny = r.getY();
    maxx = minx + 20;
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
}
