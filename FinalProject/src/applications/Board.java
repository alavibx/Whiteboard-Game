package applications;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;

import io.ResourceFinder;
import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Sprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

/**
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/11/2018
 */
public class Board extends AbstractSprite
{

  private Content[] boardContents;
  private ArrayList<Content> contents;
  private Stage stage;
  private BernsteinSprite bernstdh;
  ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
  ContentFactory factory = new ContentFactory(finder);

  /**
   * 
   * @param stage
   */
  public Board(Stage stage)
  {
    String[] files = finder.loadResourceNames("content.txt");
    boardContents = factory.createContents(files, 4);
    this.stage = stage;

    // Add the player's character (i.e., Bernstein)

    bernstdh = new BernsteinSprite();
    bernstdh.setScale(1.5);
    stage.add(bernstdh);
    stage.addKeyListener(bernstdh);
    
    contents = new ArrayList<Content>();
  }

  /**
   * 
   * @return
   */
  public Content randomWord()
  {

    int randCont = (int) (Math.random() * 13);
    int randX = (int) (Math.random() * (1100 - boardContents[randCont].getBounds2D().getWidth())
        + 50);
    int randY = 275;

    Content c = boardContents[randCont];
    c.setLocation(randX, randY);
    
    contents.add(c);

    return contents.get(contents.size()-1);
  }

  /**
   * 
   */
  public void handleTick(int time)
  {
    for (int i = 0; i < contents.size(); i++)
    {
      if (intersects(contents.get(i), bernstdh) && bernstdh.getDirection().equals("back"))
      {
        stage.remove(contents.get(i));
      }
    }

    if (time % 3000 == 0)
    {
      stage.add(randomWord());
      stage.remove(bernstdh);
      stage.add(bernstdh);
    }

  }

  public boolean intersects(Content c, Sprite s)
  {
    boolean retval;
    double maxx, maxy, minx, miny;
    double maxxO, maxyO, minxO, minyO;
    Rectangle2D r;

    retval = true;

    // Get the bounding box of the sprite
    r = s.getBounds2D(true);
    minx = r.getX() + 115;
    miny = r.getY();
    maxx = minx + 40;
    maxy = miny + 40;

    // Get the bounding box of the content
    r = c.getBounds2D(true);
    minxO = r.getX();
    minyO = r.getY();
    maxxO = minxO + r.getWidth();
    maxyO = minyO + r.getHeight();

    if ((maxx < minxO) || (minx > maxxO) || (maxy < minyO) || (miny > maxyO))
      retval = false;

    return retval;
  }

  @Override
  protected TransformableContent getContent()
  {
    // TODO Auto-generated method stub
    return randomWord();
  }
}
