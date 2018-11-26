package applications;

import java.util.ArrayList;

import javax.swing.JFrame;

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
  private Content[] boardContents;
  private ArrayList<BoardSprite> contents;
  private Stage stage;
  private BernsteinSprite bernstdh;
  ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
  ContentFactory factory = new ContentFactory(finder);
  
  public static final int BKGD_WIDTH = 1200;
  public static final int BKGD_HEIGHT = 600;

  /**
   * 
   * @param stage
   */
  public Board(Stage stage, JFrame window)
  {
    String[] files = finder.loadResourceNames("content.txt");
    boardContents = factory.createContents(files, 4);
    this.stage = stage;

    // Add the player's character (i.e., Bernstein)
    bernstdh = new BernsteinSprite();
    bernstdh.setScale(1.5);
    stage.add(bernstdh);
    stage.addKeyListener(bernstdh);
    
    contents = new ArrayList<BoardSprite>();
  }

  /**
   * 
   * @return
   */
  public BoardSprite randomSprite()
  {
    int randCont = (int) (Math.random() * (boardContents.length - 1));
    
    BoardSprite c = new BoardSprite(boardContents[randCont]);
    c.setLocation(c.getX(), c.getY());
    
    contents.add(c);

    return c;
  }

  /**
   * 
   */
  public void handleTick(int time)
  {
    for (int i = 0; i < contents.size(); i++)
    {
      BoardSprite c = contents.get(i);
      
      if (c.intersects(bernstdh) && bernstdh.getDirection().equals("back"))
      {
        stage.remove(contents.get(i));
      }
    }

    if (time % 3000 == 0)
    {
      stage.add(randomSprite());
      stage.remove(bernstdh);
      stage.add(bernstdh);
    }
  }

  @Override
  protected TransformableContent getContent()
  {
    return randomSprite();
  }
}
