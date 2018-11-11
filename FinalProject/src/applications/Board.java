package applications;

import java.awt.geom.Rectangle2D;

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
    /*
     * System.out.println(bernstdh.getBounds2D().getWidth());
     * System.out.println(bernstdh.getBounds2D().getHeight());
     * System.out.println(bernstdh.getBounds2D().getX());
     * System.out.println(bernstdh.getBounds2D().getY());
     */
  }

  /**
   * 
   * @return
   */
  public Content randomWord()
  {

    int randCont = (int) (Math.random() * 14);
    int randX = (int) (Math.random() * (1100 - boardContents[randCont].getBounds2D().getWidth())
        + 50);
    int randY = 275;

    boardContents[randCont].setLocation(randX, randY);

    return boardContents[randCont];
  }

  /**
   * 
   */
  public void handleTick(int time)
  {
    for (int i = 0; i < boardContents.length; i++)
    {
      if (intersects(boardContents[i], bernstdh) && bernstdh.getPosition().equals("back"))
      {
        stage.remove(boardContents[i]);
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
    minx = r.getX() + 90;
    miny = r.getY();
    maxx = minx + 55;
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