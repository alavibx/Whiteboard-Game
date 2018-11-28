package applications;

import java.util.ArrayList;

import io.ResourceFinder;
import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

/**
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/13/2018
 */
public class Board extends AbstractSprite
{
  private ArrayList<BoardSprite> contents;
  private BernsteinSprite bernstdh;
  private Content[] boardContents;
  private Stage stage;
  private int totalPoints;

  private ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
  private ContentFactory factory = new ContentFactory(finder);

  public static int bkgd_width;
  public static int bkgd_height;

  /**
   * 
   * @param stage
   */
  public Board(Stage stage)
  {
    // Get the content images from the files in the resources folder and store them in array
    String[] files = finder.loadResourceNames("content.txt");
    boardContents = factory.createContents(files, 4);

    // Get the stage
    this.stage = stage;

    // Add the player's character (i.e., Bernstein)
    bernstdh = new BernsteinSprite();
    bernstdh.setScale(1.5);
    stage.add(bernstdh);
    stage.addKeyListener(bernstdh);

    // Get the width and height of the stage
    bkgd_width = stage.getView().getWidth();
    bkgd_height = stage.getView().getHeight();

    // Initialize an arraylist to hold all of the contents that occupy the board
    contents = new ArrayList<BoardSprite>();
  }

  /**
   * 
   * @return
   */
  public BoardSprite randomSprite()
  {
    // Get a random index number
    int randCont = (int) (Math.random() * (boardContents.length - 1));

    // Create a random board sprite from the array of contents
    BoardSprite c = new BoardSprite(boardContents[randCont]);

    c.setLocation(c.getX(), c.getY());

    // Add the board sprite to the array list of board-occupying contents
    contents.add(c);

    return c;
  }

  /**
   * Removes contents from the whiteboard if Bernstein's eraser intersects with them.
   * 
   * Adds a new random board sprite every 3 seconds.
   */
  public void handleTick(int time)
  {
    for (int i = 0; i < contents.size(); i++)
    {
      if (contents.get(i).intersectsBernstein(bernstdh)
          && bernstdh.getDirection() == BernsteinSprite.BACK
          && bernstdh.getPosition() == BernsteinSprite.ERASE2)
      {
        
        contents.get(i).erase();
        
        
        if (contents.get(i).getOpacity() == 0)
        {
          stage.remove(contents.get(i));
          totalPoints += contents.get(i).getPoints();
          contents.get(i).resetOpacity();
          contents.remove(i);
        }
          
        stage.repaint();
      }
    }

    if (time % 3000 == 0)
    {
      stage.add(randomSprite());
      stage.remove(bernstdh);
      stage.add(bernstdh);
    }
  }

  /**
   * Returns the total number of points obtained.
   * 
   * @return totalPoints
   */
  public int getTotalPoints()
  {
    return totalPoints;
  }

  @Override
  protected TransformableContent getContent()
  {
    return randomSprite();
  }
}
