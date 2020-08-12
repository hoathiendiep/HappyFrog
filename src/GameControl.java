
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Lenovo
 */
public class GameControl extends JFrame implements ActionListener, KeyListener {

    private static int pnt = 0;
    private static JButton btnPause = new JButton("Pause");
    private static JButton btnSave = new JButton("Save");
    private static JLabel lblPoint = new JLabel("Points: " + pnt);
    private static JButton btnExit = new JButton("Exit");
    private static JPanel gameScreen = new JPanel();
    private Timer timer = new Timer(15, this);
    private static JLabel frog = new JLabel(new ImageIcon("frog.PNG"));
    private static ArrayList<JButton> pipeList = new ArrayList<>();

    public GameControl() {
        setSize(800, 600);
        setTitle("Happy Frog");
        setLayout(null);
        addKeyListener(this);
        addBtn();
        addSprites();
        controlBtn();
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void addBtn() {
        gameScreen.setBounds(10, 10, getWidth() - 30, getHeight() * 5 / 6);
        gameScreen.setLayout(null);
        gameScreen.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        JPanel btnSetting = new JPanel();
        btnSetting.setLayout(new GridLayout(1, 4, getWidth() / 7, getWidth() / 7));
        btnSetting.setBounds(10, getHeight() * 7 / 8, getWidth() - 30, getHeight() / 24);
        btnSetting.add(btnPause);
        btnSetting.add(btnSave);
        btnSetting.add(lblPoint);
        btnSetting.add(btnExit);
        btnPause.setEnabled(false);
        btnSave.setEnabled(false);
        btnPause.setFocusable(false);
        btnSave.setFocusable(false);
        btnExit.setFocusable(false);
        this.add(btnSetting);
    }

    private void addSprites() {
        frog.setBounds(gameScreen.getWidth() / 3, gameScreen.getHeight() / 2, gameScreen.getHeight() / 10, gameScreen.getHeight() / 10);
        frog.setBorder(BorderFactory.createLineBorder(Color.black));
        gameScreen.add(frog);
        this.add(gameScreen);
        addPipe(5);
    }

    private void addPipe(int n) {
        Random rand = new Random();
        int space;
        int heightPipe = rand.nextInt(gameScreen.getHeight() / 2) + 30;
        int spaceBetweenPipe;

        for (int i = 0; i < n; i++) {
            JButton pipeUp = new JButton();
            JButton pipeDown = new JButton();
            space = rand.nextInt(frog.getHeight() * 3) + frog.getHeight() + 40;
            spaceBetweenPipe = rand.nextInt(gameScreen.getWidth() / 12 * 2) + 10 + gameScreen.getWidth() / 12 * 2;
            if (pipeList.size() == 0) {
                pipeUp.setBounds(gameScreen.getWidth() / 2, 0, gameScreen.getWidth() / 12, heightPipe);
                pipeList.add(pipeUp);
            } else {
                pipeUp.setBounds(pipeList.get(pipeList.size() - 1).getX() + spaceBetweenPipe, 0, gameScreen.getWidth() / 12, heightPipe);
                pipeList.add(pipeUp);
            }
            pipeDown.setBounds(pipeList.get(pipeList.size() - 1).getX(), pipeList.get(pipeList.size() - 1).getHeight() + space, gameScreen.getWidth() / 12, gameScreen.getHeight() - (pipeList.get(pipeList.size() - 1).getHeight() + space));
            pipeList.add(pipeDown);
            gameScreen.add(pipeUp);
            gameScreen.add(pipeDown);
            pipeDown.setFocusable(false);
            pipeUp.setFocusable(false);
        }
    }

    private void movingPipes() {
        for (int i = 0; i < pipeList.size(); i += 2) {
            if (checkPnt(pipeList.get(i))) {
                lblPoint.setText("Points: " + ++pnt);
            }
            if (pipeList.get(i).getX() + pipeList.get(i).getWidth() < 0) {
                gameScreen.remove(pipeList.get(i));
                gameScreen.remove(pipeList.get(i + 1));
                pipeList.remove(pipeList.get(i));
                pipeList.remove(pipeList.get(i));
                addPipe(1);
            }
        }
        for (JButton pipe : pipeList) {
            pipe.setLocation(pipe.getX() - 1, pipe.getY());
            if (checkCollide(pipe)) {
                timer.stop();
                gameOver();
                break;
            }
        }
    }

    private void gameOver() {
        if (pnt >= 10 && pnt < 20) {
            JOptionPane.showConfirmDialog(this, "You got a bronze medal!!!", "Congratulations", -1);
        } else if (pnt >= 20 && pnt <= 30) {
            JOptionPane.showConfirmDialog(this, "You got a silver medal!!!", "Congratulations", -1);
        } else if (pnt > 30 && pnt < 40) {
            JOptionPane.showConfirmDialog(this, "You got a gold medal!!!", "Congratulations", -1);
        } else if (pnt >= 40) {
            JOptionPane.showConfirmDialog(this, "You got a plentinum medal!!!", "Congratulations", -1);
        } else {
            JOptionPane.showConfirmDialog(this, "You got no medal :<", "Game over:(((", -1);
        }
        int reply;
        if (isSaved) {
            Object[] ques = {"Restart", "New Game", "Exit"};
            reply = JOptionPane.showOptionDialog(null, "Select one: ", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, ques, ques[0]);
            canSave = false;
            switch (reply) {
                case 0:
                    getDataFromFile();
                    break;
                case 1:
                    newGame();
                    deleteFile();
                    isSaved = false;
                    break;
                default:
                    System.exit(0);
            }
        } else {
            Object[] ques = {"New Game", "Exit"};
            reply = JOptionPane.showOptionDialog(null, "Select one: ", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, ques, ques[0]);
            if (reply == 1) {
                System.exit(0);
            } else {
                newGame();
            }
        }

    }

    private void newGame() {
        canSave = false;
        fallspeed = 0;
        pnt = 0;
        lblPoint.setText(("Points: " + pnt));
        pipeList.clear();
        gameScreen.removeAll();
        btnSave.setEnabled(false);
        btnPause.setEnabled(false);
        addSprites();
        gameScreen.revalidate();
        gameScreen.repaint();
    }

    private boolean checkCollide(JButton pipe) {
        if (frog.getY() + frog.getHeight() >= gameScreen.getHeight()) {
            return true;
        }
        if (frog.getBounds().intersects(pipe.getBounds())) {
            return true;
        }
        return false;
    }

    private boolean checkPnt(JButton pipe) {
        if (frog.getX() == pipe.getX() + pipe.getWidth()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        GameControl gameControl = new GameControl();
        gameControl.setVisible(true);
        if (new File("frog.txt").exists() && new File("pipes.txt").exists() && new File("pnt.txt").exists()) {
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to restart the game?", "Inform", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                canSave = false;
                gameControl.getDataFromFile();
            } else {
                gameControl.deleteFile();
            }
        }
    }

    public static double fallspeed = 0;
    public static boolean canSave = false;
    public static boolean isSaved = false;

    private void controlBtn() {
        btnPause.addActionListener((e) -> {
            if (e.getActionCommand().equals("Pause")) {
                btnPause.setText("Continue");
                timer.stop();
            }
            if (e.getActionCommand().equals("Continue")) {
                timer.start();
                btnPause.setText("Pause");
            }
        });
        btnSave.addActionListener((e) -> {
            if (timer.isRunning()) {
                timer.stop();
            }
            saveGame();
            int reply = JOptionPane.showConfirmDialog(this, "Do you want to continue? ", "Save success!", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            isSaved = true;
            canSave = false;
            btnPause.setText("Continue");
        });
        btnExit.addActionListener((e) -> {
            btnExit();
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnExit();
            }
        });
    }

    private void btnExit() {
        if (canSave) {
            if (timer.isRunning()) {
                timer.stop();
            }
            int reply = JOptionPane.showConfirmDialog(this, "Do you want to save your current Game?", "Question", JOptionPane.YES_NO_CANCEL_OPTION);
            if (reply != JOptionPane.CANCEL_OPTION) {
                if (reply == JOptionPane.YES_OPTION) {
                    saveGame();
                }
                System.exit(0);
            } else {
                btnPause.setText("Continue");
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movingPipes();
        fallspeed += 0.5;
        frog.setLocation(frog.getX(), (int) (frog.getY() + fallspeed));

    }

    private void saveGame() {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("frog.txt"));
            os.writeObject(frog);
            os = new ObjectOutputStream(new FileOutputStream("pipes.txt"));
            os.writeObject(pipeList);
            os = new ObjectOutputStream(new FileOutputStream("pnt.txt"));
            os.writeInt(pnt);
            os.close();
        } catch (Exception ex) {
            Logger.getLogger(GameControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDataFromFile() {
        ArrayList<Object> lstSaved = new ArrayList<>();
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream("pipes.txt"));
            lstSaved.add(os.readObject());
            os = new ObjectInputStream(new FileInputStream("frog.txt"));
            lstSaved.add(os.readObject());
            os = new ObjectInputStream(new FileInputStream("pnt.txt"));
            lstSaved.add(os.readInt());
            os.close();

            gameScreen.removeAll();
            pipeList = (ArrayList<JButton>) lstSaved.get(0);
            frog = (JLabel) lstSaved.get(1);
            pnt = (int) lstSaved.get(2);
            for (JButton pipe : pipeList) {
                gameScreen.add(pipe);
            }
            gameScreen.add(frog);
            lblPoint.setText("Points: " + pnt);
            gameScreen.revalidate();
            gameScreen.repaint();
            isSaved = true;
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(null, "Can't open the file", "Information", -1);
            deleteFile();
            isSaved = false;
        }
    }

    private void deleteFile() {
        new File("pipes.txt").delete();
        new File("frog.txt").delete();
        new File("pnt.txt").delete();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!timer.isRunning()) {
                canSave = true;
                timer.start();
                btnPause.setEnabled(true);
                btnSave.setEnabled(true);
                if (btnPause.getText().equals("Continue")) {
                    btnPause.setText("Pause");
                }
            }
            fallspeed = -5;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
