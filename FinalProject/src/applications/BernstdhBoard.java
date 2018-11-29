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
public class BernstdhBoard extends JApplication implements KeyListener, MetronomeListener, ComponentListener
{
  public static final int BKGD_WIDTH = 1345;
  public static final int BKGD_HEIGHT = 880;
  
  private Content bkgd, bb;
  private JPanel contentPane;
  private boolean isPaused, gameStarted;
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
    ContentFactory factory = new ContentFactory(finder);

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
    stageView.addComponentListener(this);
    centerPanel.add(stageView, BorderLayout.CENTER);
    contentPane.add(centerPanel);

    // Set the background image
    bkgd = factory.createContent("bkgd.png", 4);
    bkgd.setScale(1.0, 1.0);
    bkgd.setLocation(0, 0);
    stage.add(bkgd);

    // Add the mainscreen display
    bb = factory.createContent("bernstdh_mainscreen.png", 4);
    bb.setScale(1.05, 1.05);
    bb.setLocation(40, 20);
    stage.add(bb);

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

    if ((keyCode == KeyEvent.VK_ENTER) && isPaused == false && gameStarted == true)
    {
      stage.stop();
      isPaused = true;
    }

    if ((keyCode == KeyEvent.VK_ENTER) && isPaused == true && gameStarted == true)
    {
      stage.start();
      isPaused = false;
    }

    if ((keyCode == KeyEvent.VK_ENTER) && isPaused == false && gameStarted == false)
    {
      gameStarted = true;
      board = new Board(stage);

      stage.getMetronome().addListener(this);

      // Play game music, if the file is available. Otherwise, continue with no music.
      try
      {
        BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("game.wav"));
        AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
        gameClip = AudioSystem.getClip();
        gameClip.open(ais);
        gameClip.start();

        mainClip.stop();
        mainClip.close();

        gameClip.loop(Clip.LOOP_CONTINUOUSLY);
      }
      catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
      {
        // Do nothing
      }

      stage.remove(bb);
      stage.add(board);
    }
  }

  /**
   * Person has released key.
   * @param arg0
   */
  public void keyReleased(KeyEvent arg0)
  {
    // Unimplemented
  }

  /**
   * Person has pressed on a key.
   * @param arg0
   */
  public void keyTyped(KeyEvent arg0)
  {
    // Unimplemented
  }

  /**
   * 
   */
  @Override
  public void handleTick(int arg0)
  {
    if (board != null)
      score.setText("SCORE: " + board.getTotalPoints());
  }

  @Override
  public void componentHidden(ComponentEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void componentMoved(ComponentEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void componentResized(ComponentEvent arg0)
  {
    // Set the location of the "main screen" to be in the center of the window at all times
    //stageView.setLocation((mainWindow.getWidth() - width)/2, (mainWindow.getHeight() - height)/2);
    //stage.repaint();
  }

  @Override
  public void componentShown(ComponentEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }
}
