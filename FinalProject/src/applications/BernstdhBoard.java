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
 * @version 12/3/2018
 */
public class BernstdhBoard extends JApplication
    implements KeyListener, MetronomeListener, MouseListener
{
  public static final int BKGD_WIDTH = 1345;
  public static final int BKGD_HEIGHT = 880;

  public static boolean isMuted;

  private Content bkgd, main, help, about;
  private Content[] helpButton, playButton, aboutButton;
  private boolean helpDisplayed, aboutDisplayed;
  private boolean helpPressed, playPressed, pausePressed, returnHPressed, returnAPressed,
      aboutPressed;
  private JPanel contentPane;
  private boolean isPaused, gameStarted;
  private Board board;
  private JFrame mainWindow;
  private ResourceFinder finder;
  private FloatControl mainVolume, gameVolume;
  private Clip mainClip, gameClip;
  private VisualizationView stageView;

  private JLabel score;
  private JLabel time;
  private int currentBestScore, currentBestTime;

  private Stage stage;

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
    currentBestScore = 0;
    currentBestTime = 0;

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
    score = new JLabel("Welcome to ISAT/CS 236", SwingConstants.CENTER);
    time = new JLabel("Current Best Score: -----", SwingConstants.CENTER);

    score.setFont(new Font(score.getFont().getName(), Font.ITALIC, 20));
    time.setFont(new Font(time.getFont().getName(), Font.ITALIC, 20));
    topPanel.add(score);
    topPanel.add(time);
    contentPane.add(topPanel);

    // Create and add the stage
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(Color.WHITE);
    stage = new Stage(75);
    stage.setBackground(Color.WHITE);

    stageView = stage.getView();

    centerPanel.add(stageView, BorderLayout.CENTER);
    contentPane.add(centerPanel);

    // Initialize buttons
    helpButton = new Content[4];
    playButton = new Content[4];
    aboutButton = new Content[4];

    // Set up the images for the GUI
    setImages();

    stage.addKeyListener(this);
    stage.addMouseListener(this);
    stage.getMetronome().addListener(this);

    // Play main menu music, if the file is available. Otherwise, continue with no musi
    try
    {
      BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("main.wav"));
      AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

      mainClip = AudioSystem.getClip();
      mainClip.open(ais);

      mainVolume = (FloatControl) mainClip.getControl(FloatControl.Type.MASTER_GAIN);
      mainVolume.setValue(-10f);

      mainClip.start();
      mainClip.loop(Clip.LOOP_CONTINUOUSLY);

      bis = new BufferedInputStream(finder.findInputStream("game.wav"));
      ais = AudioSystem.getAudioInputStream(bis);
      gameClip = AudioSystem.getClip();
      gameClip.open(ais);

      gameVolume = (FloatControl) gameClip.getControl(FloatControl.Type.MASTER_GAIN);
      gameVolume.setValue(-10f);
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
    isMuted = false;

    helpDisplayed = false;
    aboutDisplayed = false;
  }

  /**
   * Responds to events when the keys are pressed. Different events occur under different
   * conditions.
   * 
   * @param ke
   *          A key event
   */
  public void keyPressed(KeyEvent ke)
  {
    int keyCode;
    keyCode = ke.getKeyCode();

    if ((keyCode == KeyEvent.VK_ENTER))
    {
      if (isMuted)
      {
        gameVolume.setValue(-10f);
        mainVolume.setValue(-10f);
        isMuted = false;
      }
      else if (!isMuted)
      {
        gameVolume.setValue(-80f);
        mainVolume.setValue(-80f);
        isMuted = true;
      }
    }

    // Handle the PAUSE, RESUME, and PLAY
    if ((keyCode == KeyEvent.VK_SPACE))
    {
      playHandler();
    }

    // Handle the HELP menu
    if ((keyCode == KeyEvent.VK_CONTROL))
    {
      helpHandler();
    }

    // Handle the ABOUT Menu
    if ((keyCode == KeyEvent.VK_ALT))
    {
      aboutHandler();
    }

    // Handle the RETURN from a menu or the game
    if ((keyCode == KeyEvent.VK_BACK_SPACE))
    {
      playPressed = false;
      pausePressed = true;
      helpPressed = false;
      returnHPressed = true;
      aboutPressed = false;
      returnAPressed = true;

      if (!gameStarted)
      {
        showMainMenu();
      }
      else if (!helpDisplayed && !aboutDisplayed && gameStarted)
      {
        endGame();
      }
      else
      {
        resumeGame();
      }
    }
  }

  /**
   * Person has released key.
   * 
   * @param arg0
   */
  public void keyReleased(KeyEvent ke)
  {
    int keyCode;
    keyCode = ke.getKeyCode();

    if ((keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ALT
        || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_BACK_SPACE
        || keyCode == KeyEvent.VK_ENTER))
    {
      if (keyCode != KeyEvent.VK_ENTER)
      {
        releaseHandler();
      }
    }
  }

  /**
   * Responds to events when the mouse is pressed over a button. Different events occur under
   * different conditions.
   * 
   * @param me
   *          A mouse event
   */
  @Override
  public void mousePressed(MouseEvent me)
  {
    int x = me.getX();
    int y = me.getY();

    double playX, playY, playW, playH;
    double helpX, helpY, helpW, helpH;
    double aboutX, aboutY, aboutW, aboutH;

    playX = playButton[1].getBounds2D().getX();
    playY = playButton[1].getBounds2D().getY();
    playW = playButton[1].getBounds2D().getWidth();
    playH = playButton[1].getBounds2D().getHeight();

    helpX = helpButton[3].getBounds2D().getX();
    helpY = helpButton[3].getBounds2D().getY();
    helpW = helpButton[3].getBounds2D().getWidth();
    helpH = helpButton[3].getBounds2D().getHeight();

    aboutX = aboutButton[1].getBounds2D().getX();
    aboutY = aboutButton[1].getBounds2D().getY();
    aboutW = aboutButton[1].getBounds2D().getWidth();
    aboutH = aboutButton[1].getBounds2D().getHeight();

    // Handle PLAY when PRESSED
    if (x >= playX && x <= playX + playW && y >= playY && y <= playY + playH)
    {
      playHandler();
    }

    // Handle HELP MENU when PRESSED
    if (x >= helpX && x <= helpX + helpW && y >= helpY && y <= helpY + helpH)
    {
      helpHandler();
    }

    // Handle ABOUT MENU when PRESSED
    if (x >= aboutX && x <= aboutX + aboutW && y >= aboutY && y <= aboutY + aboutH)
    {
      aboutHandler();
    }
  }

  /**
   * Responds to events when the mouse is released. Different events occur under different
   * conditions.
   * 
   * @param me
   *          A mouse event
   */
  @Override
  public void mouseReleased(MouseEvent e)
  {
    releaseHandler();
  }

  /**
   * Handles every tick of the metronome.
   */
  @Override
  public void handleTick(int arg0)
  {
    if (board != null)
    {
      score.setText("SCORE: " + board.getTotalPoints());
      time.setText("TIME: " + board.gameTime() + " seconds");

      if (board.gameWon())
      {
        if (currentBestTime == 0)
        {
          currentBestTime = board.gameTime();
        }

        if (currentBestScore < board.getTotalPoints())
        {
          currentBestScore = board.getTotalPoints();

          if (currentBestTime < board.gameTime())
          {
            currentBestTime = board.gameTime();
          }
        }

        stage.stop();

        int dialogRes = JOptionPane.showConfirmDialog(contentPane,
            "You won!\n Would you like to play again?");

        if (dialogRes == JOptionPane.YES_OPTION)
        {
          endGame();
          return;
        }
        else if (dialogRes == JOptionPane.NO_OPTION)
        {
          System.exit(0);
        }
      }

      if (board.gameLost())
      {
        if (currentBestScore < board.getTotalPoints())
        {
          currentBestScore = board.getTotalPoints();
        }

        stage.stop();

        int dialogRes = JOptionPane.showConfirmDialog(contentPane,
            "You lost!\n Would you like to play again?");

        if (dialogRes == JOptionPane.YES_OPTION)
        {
          endGame();
          return;
        }
        else if (dialogRes == JOptionPane.NO_OPTION)
        {
          System.exit(0);
        }
      }
    }
  }

  /******************************************************************************************/
  /************************************* HELPER METHODS *************************************/
  /******************************************************************************************/

  /**
   * Ends the current game and returns to the main menu.
   */
  public void endGame()
  {
    playPressed = false;
    pausePressed = true;
    helpPressed = false;
    returnHPressed = true;
    aboutPressed = false;
    returnAPressed = true;

    board.setVisible(false);
    stage.remove(board);
    board = null;

    gameClip.stop();
    mainClip.start();

    showMainMenu();
  }

  /**
   * Helper method for returning to main menu screen.
   */
  public void showMainMenu()
  {
    score.setText("WELCOME TO ISAT/CS 236");
    time.setText("Current Best Score: " + currentBestScore);

    stage.clear();
    stage.add(bkgd);
    stage.add(main);

    aboutDisplayed = false;
    helpDisplayed = false;
    gameStarted = false;
    isPaused = false;

    playPressed = false;
    aboutPressed = false;
    helpPressed = false;

    showButtons();
  }

  /**
   * Helper method for CTRL KEY event.
   */
  public void showHelpMenu()
  {
    if (board != null)
      board.hide();

    stage.remove(main);
    stage.remove(about);
    stage.add(help);

    showButtons();

    helpDisplayed = true;
    aboutDisplayed = false;

    playPressed = false;
    pausePressed = true;
    helpPressed = true;
    returnHPressed = false;
    aboutPressed = false;
    returnAPressed = true;

    if (!isMuted)
      gameVolume.setValue(-10f);

    if (gameStarted)
      isPaused = true;
  }

  /**
   * Helper method for ALT KEY event.
   */
  public void showAboutMenu()
  {
    if (board != null)
      board.hide();

    stage.remove(main);
    stage.remove(help);
    stage.add(about);

    showButtons();

    helpDisplayed = false;
    aboutDisplayed = true;

    playPressed = false;
    pausePressed = true;
    helpPressed = false;
    returnHPressed = true;
    aboutPressed = true;
    returnAPressed = false;

    if (!isMuted)
      gameVolume.setValue(-10f);

    if (gameStarted)
      isPaused = true;
  }

  /**
   * Helper method for SPACE KEY event.
   * 
   * Pauses the game.
   */
  public void pauseGame()
  {
    gameVolume.setValue(-80f);

    stage.getMetronome().stop();

    if (helpPressed)
    {
      stage.add(help);
    }
    else if (aboutPressed)
    {
      stage.add(about);
    }

    isPaused = true;
  }

  /**
   * Helper method for SPACE KEY event.
   * 
   * Resumes the game.
   */
  public void resumeGame()
  {
    if (isMuted)
      gameVolume.setValue(-80f);
    else
      gameVolume.setValue(-10f);

    board.show();

    if (helpDisplayed)
    {
      stage.remove(help);
    }
    else if (aboutDisplayed)
    {
      stage.remove(about);
    }

    stage.getMetronome().start();

    showButtons();

    isPaused = false;
    helpDisplayed = false;
    aboutDisplayed = false;
  }

  /**
   * Helper method for SPACE KEY event.
   * 
   * Starts the game.
   */
  public void startGame()
  {
    showButtons();

    if (aboutDisplayed)
    {
      stage.remove(about);
      aboutDisplayed = false;
    }

    if (helpDisplayed)
    {
      stage.remove(help);
      helpDisplayed = false;
    }

    initializeGameMusic();

    gameStarted = true;
    board = new Board(stage);

    stage.remove(main);
    stage.add(board);

    stage.start();
  }

  /**
   * Render the music that plays when the game begins.
   */
  public void initializeGameMusic()
  {
    if (gameClip != null)
    {
      gameClip.start();

      mainClip.stop();

      gameClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  /**
   * Helper method for ABOUT display.
   */
  public void aboutHandler()
  {
    if (!aboutPressed && returnAPressed)
    {
      stage.add(aboutButton[0]);
      stage.add(aboutButton[1]);
      stage.add(aboutButton[2]);
      stage.remove(aboutButton[3]);

      aboutPressed = true;
      returnAPressed = false;

      helpPressed = false;
      returnHPressed = true;

      playPressed = false;
      pausePressed = true;
    }
    else if (!aboutPressed && !returnAPressed)
    {
      stage.add(aboutButton[0]);
      stage.remove(aboutButton[1]);
      stage.remove(aboutButton[2]);
      stage.remove(aboutButton[3]);
    }

    if (aboutDisplayed && !gameStarted)
    {
      showMainMenu();
    }
    else if (!aboutDisplayed && !gameStarted)
    {
      showAboutMenu();
    }
    else if (aboutDisplayed && gameStarted)
    {
      resumeGame();
      stage.remove(about);
    }
    else if (!aboutDisplayed && gameStarted)
    {
      pauseGame();
      showAboutMenu();
    }
  }

  /**
   * Helper method for HELP display.
   */
  public void helpHandler()
  {
    if (!helpPressed && returnHPressed)
    {
      stage.add(helpButton[0]);
      stage.add(helpButton[1]);
      stage.add(helpButton[2]);
      stage.remove(helpButton[3]);

      helpPressed = true;
      returnHPressed = false;

      aboutPressed = false;
      returnAPressed = true;

      playPressed = false;
      pausePressed = true;
    }
    else if (!helpPressed && !returnHPressed)
    {
      stage.add(helpButton[0]);
      stage.remove(helpButton[1]);
      stage.remove(helpButton[2]);
      stage.remove(helpButton[3]);
    }

    if (helpDisplayed && !gameStarted)
    {
      showMainMenu();
    }
    else if (!helpDisplayed && !gameStarted)
    {
      showHelpMenu();
    }
    else if (helpDisplayed && gameStarted)
    {
      resumeGame();
      stage.remove(help);
    }
    else if (!helpDisplayed && gameStarted)
    {
      pauseGame();
      showHelpMenu();
    }
  }

  /**
   * Helper method for PLAY.
   */
  public void playHandler()
  {
    if (!playPressed && pausePressed)
    {
      stage.add(playButton[0]);
      stage.add(playButton[1]);
      stage.add(playButton[2]);
      stage.remove(playButton[3]);

      playPressed = true;
      pausePressed = false;

      helpPressed = false;
      returnHPressed = true;

      aboutPressed = false;
      returnAPressed = true;
    }
    else if (!playPressed && !pausePressed)
    {
      stage.add(playButton[0]);
      stage.remove(playButton[1]);
      stage.remove(playButton[2]);
      stage.remove(playButton[3]);
    }

    if (!gameStarted && !isPaused)
      startGame();
    else if (gameStarted && !isPaused)
      pauseGame();
    else if (gameStarted && isPaused)
      resumeGame();
  }

  /**
   * Helper method for displaying buttons.
   */
  public void showButtons()
  {
    // Show HELP button
    if (!helpPressed && returnHPressed)
    {
      stage.add(helpButton[0]);
      stage.add(helpButton[1]);
      stage.add(helpButton[2]);
      stage.add(helpButton[3]);
    }
    else if (helpPressed && !returnHPressed)
    {
      stage.remove(helpButton[0]);
      stage.remove(helpButton[1]);
      stage.add(helpButton[2]);
      stage.remove(helpButton[3]);
    }
    else if (!helpPressed && !returnHPressed)
    {
      stage.add(helpButton[0]);
      stage.remove(helpButton[1]);
      stage.remove(helpButton[2]);
      stage.remove(helpButton[3]);
    }

    // Show PLAY button
    if (!playPressed && pausePressed)
    {
      stage.add(playButton[0]);
      stage.add(playButton[1]);
      stage.add(playButton[2]);
      stage.add(playButton[3]);
    }
    else if (playPressed && !pausePressed)
    {
      stage.remove(playButton[0]);
      stage.remove(playButton[1]);
      stage.add(playButton[2]);
      stage.remove(playButton[3]);
    }
    else if (!playPressed && !pausePressed)
    {
      stage.add(playButton[0]);
      stage.remove(playButton[1]);
      stage.remove(playButton[2]);
      stage.remove(playButton[3]);
    }

    // Show ABOUT button
    if (!aboutPressed && returnAPressed)
    {
      stage.add(aboutButton[0]);
      stage.add(aboutButton[1]);
      stage.add(aboutButton[2]);
      stage.add(aboutButton[3]);
    }
    else if (aboutPressed && !returnAPressed)
    {
      stage.remove(aboutButton[0]);
      stage.remove(aboutButton[1]);
      stage.add(aboutButton[2]);
      stage.remove(aboutButton[3]);
    }
    else if (!aboutPressed && !returnAPressed)
    {
      stage.add(aboutButton[0]);
      stage.remove(aboutButton[1]);
      stage.remove(aboutButton[2]);
      stage.remove(aboutButton[3]);
    }
  }

  /**
   * Helper method for initializing the images of the GUI.
   */
  public void setImages()
  {
    ContentFactory factory = new ContentFactory(finder);

    // Set the background image
    bkgd = factory.createContent("bkgd.png", 4);
    bkgd.setScale(1.0, 1.0);
    bkgd.setLocation(0, 0);
    stage.add(bkgd);

    // Add the main menu display
    main = factory.createContent("mainscreen.png", 4);
    main.setScale(1.0, 1.0);
    main.setLocation(0, 0);
    stage.add(main);

    // Add the help display
    help = factory.createContent("helpdescription.png", 4);
    help.setScale(1.0);
    help.setLocation(0, 0);

    // Add the about display
    about = factory.createContent("aboutdescription.png", 4);
    about.setScale(1.0);
    about.setLocation(0, 0);

    /****** Get images for buttons *******/
    /* INITIALIZE HELP BUTTON */
    helpButton[0] = factory.createContent("return_depressed.png", 4);
    helpButton[1] = factory.createContent("return_default.png", 4);
    helpButton[2] = factory.createContent("help_depressed.png", 4);
    helpButton[3] = factory.createContent("help_default.png", 4);

    for (Content b : helpButton)
    {
      b.setScale(1.0);
      b.setLocation(75, 15);
      stage.add(b);
    }

    /* INITIALIZE PLAY BUTTON */
    playButton[0] = factory.createContent("pause_depressed.png", 4);
    playButton[1] = factory.createContent("pause_default.png", 4);
    playButton[2] = factory.createContent("play_depressed.png", 4);
    playButton[3] = factory.createContent("play_default.png", 4);

    for (Content b : playButton)
    {
      b.setScale(1.0);
      b.setLocation(BKGD_WIDTH / 2 - b.getBounds2D().getWidth() / 2, 15);
      stage.add(b);
    }

    /* INITIALIZE ABOUT BUTTON */
    aboutButton[0] = factory.createContent("return_depressed.png", 4);
    aboutButton[1] = factory.createContent("return_default.png", 4);
    aboutButton[2] = factory.createContent("about_depressed.png", 4);
    aboutButton[3] = factory.createContent("about_default.png", 4);

    for (Content b : aboutButton)
    {
      b.setScale(1.0);
      b.setLocation(BKGD_WIDTH - b.getBounds2D().getWidth() - 75, 15);
      stage.add(b);
    }

    helpPressed = false;
    aboutPressed = false;
    playPressed = false;

    pausePressed = true;
    returnHPressed = true;
    returnAPressed = true;
  }

  /**
   * Helper method for handling when mouse and keys are released.
   */
  public void releaseHandler()
  {
    // Handle PLAY when RELEASED
    if (playPressed && !pausePressed)
    {
      stage.add(playButton[0]);
      stage.add(playButton[1]);
      stage.remove(playButton[2]);
      stage.remove(playButton[3]);

      playPressed = false;
      pausePressed = false;
    }
    else if (!playPressed && !pausePressed)
    {
      stage.add(playButton[0]);
      stage.add(playButton[1]);
      stage.add(playButton[2]);
      stage.add(playButton[3]);

      playPressed = false;
      pausePressed = true;
    }

    // Handle HELP MENU when RELEASED
    if (helpPressed && !returnHPressed)
    {
      stage.add(helpButton[0]);
      stage.add(helpButton[1]);
      stage.remove(helpButton[2]);
      stage.remove(helpButton[3]);

      helpPressed = false;
      returnHPressed = false;
    }
    else if (!helpPressed && !returnHPressed)
    {
      stage.add(helpButton[0]);
      stage.add(helpButton[1]);
      stage.add(helpButton[2]);
      stage.add(helpButton[3]);

      helpPressed = false;
      returnHPressed = true;
    }

    // Handle ABOUT MENU when RELEASED
    if (aboutPressed && !returnAPressed)
    {
      stage.add(aboutButton[0]);
      stage.add(aboutButton[1]);
      stage.remove(aboutButton[2]);
      stage.remove(aboutButton[3]);

      aboutPressed = false;
      returnAPressed = false;
    }
    else if (!aboutPressed && !returnAPressed)
    {
      stage.add(aboutButton[0]);
      stage.add(aboutButton[1]);
      stage.add(aboutButton[2]);
      stage.add(aboutButton[3]);

      aboutPressed = false;
      returnAPressed = true;
    }
  }

  /*
   * **************************************************************************************
   * **************************************************************************************
   * *********************** UNUSED METHODS - NEEDED FOR INTERFACES ***********************
   * **************************************************************************************
   * **************************************************************************************
   */

  /**
   * Person has pressed on a key.
   * 
   * @param arg0
   */
  public void keyTyped(KeyEvent arg0)
  {
  }

  @Override
  public void mouseClicked(MouseEvent arg0)
  {
  }

  @Override
  public void mouseEntered(MouseEvent arg0)
  {
  }

  @Override
  public void mouseExited(MouseEvent arg0)
  {
  }
}
