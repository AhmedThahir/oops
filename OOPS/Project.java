import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Timer;
import java.util.TimerTask;

interface Interface
{
  public void inc();
  public void update();
}

class Container
{
  ArrayList<Stat> stats = new ArrayList<Stat>();
  static boolean gameStatus = false;

  class Stat implements Interface
  {
    int count;
    String countText;
    String statName;

    JPanel panel;
    JLabel lb;
    JTextField tf;
    JButton incBtn, decBtn;

    Stat()
    {      
      lb = new JLabel();

      tf = new JTextField();
      tf.setEditable(false);

      panel = new JPanel();
      panel.setBackground(Color.gray);

      incBtn = new JButton("+1");
      decBtn = new JButton("-1");

      reset();
    }

    public void reset()
    {
      count = 00;
      update();
    }

    public void update()
    {
      countText = "";
      if(count < 10)
        countText += "0";
      countText += Integer.toString(count);
      
      tf.setText(countText);
    }

    public void inc()
    {
      if(gameStatus == true)
      {
        count++;
        update();
      } 
    }

    public void dec()
    {
      if(gameStatus == true)
      {
        if(count>0)
          count--;
        update();
      }
    }

  }

  class Points extends Stat
  {
    Points()
    {
      statName = "Points";
      lb = new JLabel(statName);
    } 
  }
  class Assists extends Stat
  {
    Assists()
    {
      statName = "Assists";
      lb = new JLabel(statName);
    }
  }

  class Fouls extends Stat
  {
    Fouls()
    {
      statName = "Fouls";
      lb = new JLabel(statName);
    }

    @Override
    public void inc()
    {
      if(gameStatus == true)
      {
        if(count < 5)
          count++;
          update();

        if(count == 5)
        {
          new msgBox();
          throw new FouledOutException();
        }
      }
    }
  }

  class GUI extends JFrame implements ActionListener
  {
    Points p = new Points();
    Assists a = new Assists();
    Fouls f = new Fouls();

    JLabel countDownText = new JLabel();
    JPanel countDownPanel = new JPanel();
    JButton startBtn;

    public GUI()
    {
      setLayout( new FlowLayout() );
      setTitle("Thahir OOPS"); 

      startBtn = new JButton("Start Game");
      startBtn.addActionListener(this);
      countDownPanel.add(startBtn);

      countDownPanel.add(countDownText);

      add(countDownPanel);

      stats.add( p );
      stats.add( a );
      stats.add( f );
      for(int i = 0; i < stats.size(); i++)
      {
        stats.get(i).incBtn.addActionListener(this);
        stats.get(i).decBtn.addActionListener(this);
        
        stats.get(i).panel.add(stats.get(i).lb);      
        stats.get(i).panel.add(stats.get(i).tf);
        stats.get(i).panel.add(stats.get(i).incBtn);
        stats.get(i).panel.add(stats.get(i).decBtn);

        add(stats.get(i).panel);
      }

      setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      setSize(250, 500);
      setVisible(true);
    }


    public void countDown()
    {
      gameStatus = true;
      
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() // anyonymous class
      {
        int m = 00, s = 10;
        String text;

        public void display()
        {
          text="";

          if(m<10)
            text += "0";
          text += Integer.toString(m);

          text += ":";

          if(s<10)
            text += "0";
          text += Integer.toString(s);

          countDownText.setText("Time left \t" + text);
        }

        public void update()
        {
          s--;
          if (s < 0)
          {
              m--;
              s = 59;
          }
          if (m < 0) {
              timer.cancel();
              gameStatus = false;
              new msgBox();
          }
        }

        public void run() {
          display();
          update();
        }
      }, 0, 1000);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
      if(e.getSource() == startBtn)
      {  
        for(int i = 0; i < stats.size(); i++)
          stats.get(i).reset();
        countDown();
      }
      if(e.getSource() == p.incBtn)
        p.inc();
      if(e.getSource() == p.decBtn)
        p.dec();
      if(e.getSource() == a.incBtn)
        a.inc();
      if(e.getSource() == a.decBtn)
        a.dec();
      if(e.getSource() == f.incBtn)
        f.inc();
      if(e.getSource() == f.decBtn)
        f.dec();
        
    }
  }

  class FouledOutException extends IllegalArgumentException
  {
    FouledOutException() {
      super("Fouled out of the game with 5 fouls");
    }
  }

  class msgBox extends JFrame
  {
    msgBox()
    {
        if(gameStatus == true)
        {
          gameStatus = false;
          String text = "Player Summary\n";
    
          for(int i = 0; i < stats.size(); i++)
          {
            text += stats.get(i).statName;
            text += ": ";
            text += stats.get(i).countText;      
            text += "\n";
          }
    
          JOptionPane.showMessageDialog(this, text);
        }
    }
  }
}

class Project
{
  public static void main(String args[])
  {
    new Container().new GUI();
  }
  
}