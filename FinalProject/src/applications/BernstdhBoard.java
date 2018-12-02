package applications;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.*;
import javax.swing.*;

import app.JApplication;
import event.MetronomeListener;
import io.ResourceFinder;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

/**
 * A game.
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/29/2018
 */
public class BernstdhBoard extends JApplication implements KeyListener, MetronomeListener
{
  public static final int BKGD_WIDTH = 1345;
  public static final int BKGD_HEIGHT = 880;

  private Content bkgd, main, help, about;
  private JPanel contentPane;
  private boolean isPaused, gameStarted, replay;
  private JLabel score;
  private Board board;
  private JFrame mainWindow;
  private ResourceFinder finder;
  private Clip mainClip, gameClip;
  private VisualizationView stageView;

  Stage stage;

  /**
   * Contructs a new BernstdhBoard of the size width x height.
   * 
   * @param width
   *          Width of the GUI
   * @param height
   *          Height of the GUI
   */
  public BernstdhBoard(int width, int height)
  {
    super(width, height);
  }

  /**
   * The entry-point into the application.
   * 
   * @param args
   *          NOT USED
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InvocationTargetException, InterruptedException
  {
    SwingUtilities.invokeAndWait(new BernstdhBoard(BKGD_WIDTH - 20, BKGD_HEIGHT + 20));
  }

  /**
   * This method is called just before the main window is first made visible and initializes all of
   * the components of the window.
   */
  @Override
  public void init()
  {
    // SET THE VISUAL COMPONENTS OF THE GUI
    // Get the content pane of the window
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

    // Get the main window from the content pane (originally set in JApplication)
    mainWindow = (JFrame) SwingUtilities.getWindowAncestor(contentPane);
    mainWindow.setResizable(true);
    mainWindow.setTitle("Bernstdh-Board");
    mainWindow.setMinimumSize(new Dimension(BKGD_WIDTH, BKGD_HEIGHT));

    finder = ResourceFinder.createInstance(resources.Marker.class);

    JPanel topPanel = new JPanel(new GridLayout(0, 1));
    topPanel.setBackground(Color.WHITE);
    score = new JLabel("Welcome to ISAT 236", SwingConstants.CENTER);
    score.setFont(new Font(score.getFont().getName(), Font.ITALIC, 20));
    topPanel.add(score);
    contentPane.add(topPanel);

    // Create and add the stage
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(Color.WHITE);
    stage = new Stage(75);
    stage.setBackground(Color.WHITE);

    stageView = stage.getView();
    centerPanel.add(stageView, BorderLayout.CENTER);
    contentPane.add(centerPanel);

    setImages();

    stage.addKeyListener(this);
    stage.getMetronome().addListener(this);

    // Play main menu music, if the file is available. Otherwise, continue with no musi
    try
    {
      BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("main.wav"));
      AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

      mainClip = AudioSystem.getClip();
      mainClip.open(ais);
      mainClip.start();
      mainClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
    {
      // Do nothing
    }

    // Make the view "visible"
    stageView.setBounds(0, 0, BKGD_WIDTH, BKGD_HEIGHT);

    stage.start();
    isPaused = false;
    gameStarted = false;
    replay = false;
  }

  public void setImages()
  {
    ContentFactory factory = new ContentFactory(finder);

    // Set the background image
    bkgd = factory.createContent("bkgd.png", 4);
    bkgd.setScale(1.0, 1.0);
    bkgd.setLocation(0, 0);
    stage.add(bkgd);

    // Add the mainscreen display
    main = factory.createContent("mainscreen.png", 4);
    main.setScale(1.0, 1.0);
    main.setLocation(0, 0);
    stage.add(main);

    help = factory.createContent("helpdescription.png", 4);
    help.setScale(1.0);
    help.setLocation(0, 0);

    about = factory.createContent("aboutdescription.png", 4);
    about.setScale(1.0);
    about.setLocation(0, 0);
  }

  /**
   * Responds to events when the "Enter" key is pressed. Different events occur under different
   * conditions.
   * 
   * @param ke
   *          A key event
   */
  public void keyPressed(KeyEvent ke)
  {
    int keyCode;
    keyCode = ke.getKeyCode();

    if ((keyCode == KeyEvent.VK_SPACE) && !isPaused && gameStarted)
    {
      pauseGame();
    }

    if ((keyCode == KeyEvent.VK_SPACE) && isPaused && gameStarted)
    {
      resumeGame();
    }

    if ((keyCode == KeyEvent.VK_SPACE) && !isPaused && !gameStarted)
    {
      startGame();
    }

    if ((keyCode == KeyEvent.VK_BACK_SPACE))
    {
      if (!gameStarted)
      {
        toMainMenu();
      }
      else
      {
        endGame();
      }
    }
  }

  /**
   * Ends the current game and returns to the main menu.
   */
  public void endGame()
  {
    board.setVisible(false);
    stage.remove(board);
    board = null;

    gameClip.stop();
    mainClip.start();

    gameStarted = false;
    isPaused = false;
    
    toMainMenu();
  }

  public void toMainMenu()
  {
    score.setText("WELCOME TO ISAT 236");

    stage.clear();
    stage.add(bkgd);
    stage.add(main);
    
    stage.start();

    // stage.remove(help);
    // stage.remove(about);

  }

  /**
   * Helper method for SHIFT KEY event.
   * 
   * Pauses the game.
   */
  public void pauseGame()
  {
    stage.stop();
    isPaused = true;
  }

  /**
   * Helper method for SHIFT KEY event.
   * 
   * Resumes the game.
   */
  public void resumeGame()
  {
    stage.start();
    isPaused = false;
  }

  /**
   * Helper method for SHIFT KEY event.
   * 
   * Starts the game.
   */
  public void startGame()
  {
    gameStarted = true;
    board = new Board(stage);

    stage.getMetronome().addListener(this);

    if (!replay)
      initializeGameMusic();

    stage.remove(main);
    stage.add(board);
  }

  public void initializeGameMusic()
  {
    // Play game music, if the file is available. Otherwise, continue with no music.
    try
    {
      BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("game.wav"));
      AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
      gameClip = AudioSystem.getClip();
      gameClip.open(ais);
      gameClip.start();

      mainClip.stop();

      gameClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
    {
      // Do nothing
    }
  }

  /**
   * 
   */
  @Override
  public void handleTick(int arg0)
  {
    if (board != null)
    {

      score.setText("SCORE: " + board.getTotalPoints());

      if (board.gameWon())
      {
        stage.stop();

        int dialogRes = JOptionPane.showConfirmDialog(contentPane,
            "You won!\n Would you like to play again?");

        if (dialogRes == JOptionPane.YES_OPTION)
        {
          endGame();
        }
        else if (dialogRes == JOptionPane.NO_OPTION)
        {
          System.exit(0);
        }
      }
      
      if (board.gameLost())
      {
        stage.stop();

        int dialogRes = JOptionPane.showConfirmDialog(contentPane,
            "You lost!\n Would you like to play again?");

        if (dialogRes == JOptionPane.YES_OPTION)
        {
          endGame();
        }
        else if (dialogRes == JOptionPane.NO_OPTION)
        {
          System.exit(0);
        }
      }
    }

  }

  /*
   * 
   * 
   * *********************** UNUSED METHODS - NEEDED FOR INTERFACES ***********************
   * 
   * 
   */

  /**
   * Person has released key.
   * 
   * @param arg0
   */
  public void keyReleased(KeyEvent arg0)
  {
  }

  /**
   * Person has pressed on a key.
   * 
   * @param arg0
   */
  public void keyTyped(KeyEvent arg0)
  {
  }
}
