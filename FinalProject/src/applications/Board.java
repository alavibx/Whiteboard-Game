package applications;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import io.ResourceFinder;
import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ImageFactory;

/**
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 12/2/2018
 */
public class Board extends AbstractSprite
{
  private ArrayList<BoardSprite> contents;
  private BernsteinSprite bernstdh;
  private BufferedImage[] boardSprites;
  private BufferedImage[] negativeSprites;
  private Stage stage;
  private int totalPoints, speed, decrementSpeed;
  private boolean gameWon, gameLost;
  private int gameTime;
  private int increaseTime;

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

    // Get the content images that subtracts points if erased
    String[] minusFiles = finder.loadResourceNames("nice.txt");
    negativeSprites = new BufferedImage[minusFiles.length];

    // Store the buffered images of board sprites in an array
    for (int i = 0; i < files.length; i++)
    {
      boardSprites[i] = factory.createBufferedImage(files[i], 4);
    }

    // Store the buffered images of minus Sprites in an array
    for (int x = 0; x < minusFiles.length; x++)
    {
      negativeSprites[x] = factory.createBufferedImage(minusFiles[x], 5);
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

    decrementSpeed = 75;
    speed = 3075; // 3 seconds

    gameWon = false;
    gameLost = false;
  }

  /**
   * Create a random sprite to add to the board.
   * 
   * @return BoardSprite next random sprite
   */
  public BoardSprite randomSprite()
  {
    // Determine whether to use negative sprite or nice sprite
    int type = (int) (Math.random() * (10));

    if (type > 2)
    {

      // Get a random index number
      int randCont = (int) (Math.random() * (boardSprites.length - 1));

      Content c = new Content(boardSprites[randCont], 0, 0);

      // Create a random board sprite from the array of contents
      BoardSprite b = new BoardSprite(c, true);

      b.setLocation(b.getX(), b.getY());
      b.addAntagonist(bernstdh);

      // Add the board sprite to the array list of board-occupying contents
      contents.add(b);
      return b;
    }
    else
    {
      // Get a random index number
      int randCont = (int) (Math.random() * (negativeSprites.length - 1));

      Content c = new Content(negativeSprites[randCont], 0, 0);

      // Create a random board sprite from the array of contents
      BoardSprite b = new BoardSprite(c, false);

      b.setLocation(b.getX(), b.getY());
      b.addAntagonist(bernstdh);

      // Add the board sprite to the array list of board-occupying contents
      contents.add(b);
      return b;
    }
  }

  /**
   * Removes contents from the whiteboard if Bernstein's eraser intersects with them.
   * 
   * Adds a new random board sprite every 3 seconds.
   */
  public void handleTick(int time)
  {
    increaseTime++;

    if (increaseTime % 12 == 0)
    {
      gameTime++;
    }

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

    if (time % speed == 0)
    {
      stage.add(randomSprite());
      stage.remove(bernstdh);
      stage.add(bernstdh);
    }

    if (time % 5000 == 0)
    {
      for (int x = 0; x < contents.size(); x++)
      {
        if (contents.get(x).getGain() == false)
          stage.remove(contents.get(x));
      }
    }

    if (time % 3075 == 0 && contents.size() > 0)
    {
      if (speed > 300)
      {
        speed -= decrementSpeed;
      }
      else
        speed = 300;
    }

    if (totalPoints >= 50000)
    {
      gameWon = true;
    }
    
    contents.trimToSize();

    if (contents.size() > 15)
    {
      gameLost = true;
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

  public boolean gameWon()
  {
    return gameWon;
  }

  public boolean gameLost()
  {
    return gameLost;
  }

  public int gameTime()
  {
    return gameTime;
  }

  /**
   * Hides the contents on the board.
   */
  public void hide()
  {
    for (BoardSprite c : contents)
    {
      c.setVisible(false);
    }

    bernstdh.setVisible(false);
  }

  /**
   * Shows the contents on the board.
   */
  public void show()
  {
    for (BoardSprite c : contents)
    {
      c.setVisible(true);
    }

    bernstdh.setVisible(true);
  }
}
