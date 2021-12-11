import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Timer;
import java.util.TimerTask;

interface Interface {
  public void reset();
  public void update();
  public void inc();
  public void dec();
}

class Container {
  ArrayList<Stat> stats = new ArrayList<Stat>();
  static boolean gameStatus = false;
  static int rating;
  Timer timer = new Timer();

  public void rating() {
    rating = 0;
    if (stats.get(0).count < 1)
      rating++;
    else if (stats.get(0).count <= 3) // 2-3
      rating++;
    else if (stats.get(0).count <= 5) // 4-5
      rating += 2;
    else // 6 and above
      rating += 3;

    if (stats.get(1).count <= 1)
    ;
    else if (stats.get(1).count <= 3) // 2-3
      rating++;
    else // 4 and above
      rating += 2;

    if (stats.get(2).count == 4)
      rating--;
    else if (stats.get(2).count == 5)
      rating -= 2;

    if (rating < 0)
      rating = 0;
    if (rating > 5)
      rating = 5;
  }

  abstract class Stat implements Interface {
    int count;
    String countText;
    String statName;

    JPanel panel;
    JLabel lb;
    JTextField tf;
    JButton incBtn, decBtn,
    inc2Btn, inc3Btn;

    public Stat() {
      tf = new JTextField();
      tf.setEditable(false);

      panel = new JPanel();
      panel.setBackground(Color.gray);

      incBtn = new JButton("+1");
      decBtn = new JButton("-1");

      reset();
    }

    public void reset() {
      count = 00;
      update();
    }

    public void update() {
      countText = "";
      if (count < 10)
        countText += "0";
      countText += Integer.toString(count);

      tf.setText(countText);
    }

    public void inc() {
      if (gameStatus == true) {
        count++;
        update();
      }
    }

    public void dec() {
      if (gameStatus == true) {
        if (count > 0)
          count--;
        update();
      }
    }

  }

  class Points extends Stat {
    public Points() {
      inc2Btn = new JButton("+2");
      inc3Btn = new JButton("+3");
      statName = "Points";
      lb = new JLabel(statName);
    }
  }
  class Assists extends Stat {
    public Assists() {
      statName = "Assists";
      lb = new JLabel(statName);
    }
  }

  class Fouls extends Stat {
    public Fouls() {
      statName = "Fouls";
      lb = new JLabel(statName);
    }

    @Override
    public void inc() {
      if (gameStatus == true) {
        if (count < 5)
          count++;
        update();

        if (count == 5) {
          new End();
          throw new FouledOutException();
        }
      }
    }
  }

  class GUI extends JFrame implements ActionListener {
    Points p = new Points();
    Assists a = new Assists();
    Fouls f = new Fouls();

    JLabel countDownText = new JLabel();
    JPanel countDownPanel = new JPanel();
    JButton startBtn;

    public GUI() {
      setLayout(new FlowLayout());
      setTitle("Thahir OOPS");

      startBtn = new JButton("Start Game");
      startBtn.addActionListener(this);
      countDownPanel.add(startBtn);

      countDownPanel.add(countDownText);

      add(countDownPanel);

      stats.add(p);
      stats.add(a);
      stats.add(f);
      for (int i = 0; i < stats.size(); i++) {
        stats.get(i).decBtn.addActionListener(this);
        stats.get(i).incBtn.addActionListener(this);

        stats.get(i).panel.add(stats.get(i).lb);
        stats.get(i).panel.add(stats.get(i).tf);

        stats.get(i).panel.add(stats.get(i).incBtn);

        if (stats.get(i).statName.equals("Points")) {
          stats.get(i).inc2Btn.addActionListener(this);
          stats.get(i).inc3Btn.addActionListener(this);
          stats.get(i).panel.add(stats.get(i).inc2Btn);
          stats.get(i).panel.add(stats.get(i).inc3Btn);
        }

        stats.get(i).panel.add(stats.get(i).decBtn);

        add(stats.get(i).panel);
      }

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(450, 500);
      setVisible(true);
    }

    public void countDown() {
      gameStatus = true;

      timer.scheduleAtFixedRate(new TimerTask()
        {
          int m = 00, s = 10;
          String text;

          public void display() {
            text = "";

            if (m < 10)
              text += "0";
            text += Integer.toString(m);

            text += ":";

            if (s < 10)
              text += "0";
            text += Integer.toString(s);

            countDownText.setText("Time left \t" + text);
          }

          public void update() {
            s--;
            if (s < 0) {
              m--;
              s = 59;
            }
            if (m < 0) {
              new End();
            }
          }

          public void run() {
            display();
            update();
          }
        }, 0, 1000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if ((e.getSource() == startBtn) && gameStatus == false) {
        for (int i = 0; i < stats.size(); i++)
          stats.get(i).reset();
        countDown();
      }
      if (e.getSource() == p.incBtn)
        p.inc();
      if (e.getSource() == p.inc2Btn) {
        p.inc();
        p.inc();
      }
      if (e.getSource() == p.inc3Btn) {
        p.inc();
        p.inc();
        p.inc();
      }
      if (e.getSource() == p.decBtn)
        p.dec();

      if (e.getSource() == a.incBtn)
        a.inc();
      if (e.getSource() == a.decBtn)
        a.dec();
      if (e.getSource() == f.incBtn)
        f.inc();
      if (e.getSource() == f.decBtn)
        f.dec();
    }
  }

  class FouledOutException extends IllegalArgumentException {
    FouledOutException() {
      super("Fouled out of the game with 5 fouls");
    }
  }

  class End extends JFrame {
    End() {
      if (gameStatus == true) {
        gameStatus = false;
        timer.cancel();
        String text = "Player Summary\n";

        for (int i = 0; i < stats.size(); i++) {
          text += stats.get(i).statName;
          text += ": ";
          text += stats.get(i).countText;
          text += "\n";
        }

        rating();
        text += "Rating: " + Integer.toString(rating) + "/5";

        JOptionPane.showMessageDialog(this, text);
      }
    }
  }
}

class Project {
  public static void main(String args[]) {
    new Container().new GUI();
  }

}