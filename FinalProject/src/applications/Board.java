package applications;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Iterator;

import io.ResourceFinder;
import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;
import visual.statik.sampled.ImageFactory;

/**
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/29/2018
 */
public class Board extends AbstractSprite
{
  private ArrayList<BoardSprite> contents;
  private BernsteinSprite bernstdh;
  private BufferedImage[] boardSprites;
  private Stage stage;
  private int totalPoints;

  private ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
  private ImageFactory factory = new ImageFactory(finder);

  /**
   * 
   * @param stage
   */
  public Board(Stage stage)
  {
    // Get the content images from the files in the resources folder and store them in array
    String[] files = finder.loadResourceNames("content.txt");
    boardSprites = new BufferedImage[files.length];
    
    // Store the buffered images of board sprites in an array
    for (int i = 0; i < files.length; i++)
    {
      boardSprites[i] = factory.createBufferedImage(files[i], 4);
    }

    // Get the stage
    this.stage = stage;

    // Add the player's character (i.e., Bernstein)
    bernstdh = new BernsteinSprite();
    bernstdh.setScale(1.5);
    stage.add(bernstdh);
    stage.addKeyListener(bernstdh);

    // Initialize an arraylist to hold all of the contents that occupy the board
    contents = new ArrayList<BoardSprite>();
  }

  /**
   * Create a random sprite to add to the board.
   * 
   * @return BoardSprite next random sprite
   */
  public BoardSprite randomSprite()
  {
    // Get a random index number
    int randCont = (int) (Math.random() * (boardSprites.length - 1));
    
    Content c = new Content(boardSprites[randCont], 0, 0);

    // Create a random board sprite from the array of contents
    BoardSprite b = new BoardSprite(c);

    b.setLocation(b.getX(), b.getY());
    b.addAntagonist(bernstdh);

    // Add the board sprite to the array list of board-occupying contents
    contents.add(b);

    return b;
  }

  /**
   * Removes contents from the whiteboard if Bernstein's eraser intersects with them.
   * 
   * Adds a new random board sprite every 3 seconds.
   */
  public void handleTick(int time)
  {
    Iterator<BoardSprite> it = contents.iterator();

    for (int i = 0; i < contents.size(); i++)
    {
      // If the content has completely disappeared, remove it from the stage and the array list
      if (contents.get(i).getOpacity() <= 0)
      {
        stage.remove(contents.get(i));
        totalPoints += contents.get(i).getPoints();
        contents.get(i).resetOpacity();
        contents.remove(i);
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
