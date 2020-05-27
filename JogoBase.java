import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.concurrent.TimeUnit;


class JogoBase extends JFrame {
  final int ALTURA = 920;
  final int LARGURA = 1400;
  final int FUNDO = 0;
  final int GOLEIRO1_PARADO = 1;
  final int GOLEIRO2_PARADO = 2;
  final int GOLEIRO1_CIMA = 3;
  final int GOLEIRO2_CIMA = 4;
  final int GOLEIRO1_BAIXO = 5;
  final int GOLEIRO2_BAIXO = 6;
  final int TRAVE_ESQUERDA = 7;
  final int TRAVE_DIREITA = 8;
  final int BOLA = 9;
  int estadoGoleiro1 = GOLEIRO1_PARADO;
  int estadoGoleiro2 = GOLEIRO2_PARADO;
  int pontosGoleiro1 = 0;
  int pontosGoleiro2 = 0;

  int coordXGoleiro1;
  int coordXGoleiro2;
  int coordYGoleiro1;
  int coordYGoleiro2;
  boolean CIMA_1 = false, CIMA_2 = false, BAIXO_1 = false, BAIXO_2 = false;

  //COUNTDOWN
  boolean inicio = true;
  int valorCountdown;


  Image img[] = new Image[10];
  Desenho des = new Desenho();

  class Desenho extends JPanel {

    Desenho() {
      try {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        img[FUNDO] = ImageIO.read(new File("Images/Soccer_field.png"));
        img[TRAVE_ESQUERDA] = ImageIO.read(new File("Images/Post_left.png")).getScaledInstance(63, 900, Image.SCALE_DEFAULT);
        img[TRAVE_DIREITA] = ImageIO.read(new File("Images/Post_right.png")).getScaledInstance(63, 900, Image.SCALE_DEFAULT);

        img[GOLEIRO1_PARADO] = ImageIO.read(new File("Images/Goalkeeper-Blue.png")).getScaledInstance(100, 125, Image.SCALE_DEFAULT);
        img[GOLEIRO2_PARADO] = ImageIO.read(new File("Images/Goalkeeper-Red.png")).getScaledInstance(100, 125, Image.SCALE_DEFAULT);
        img[GOLEIRO1_CIMA] = ImageIO.read(new File("Images/Goalkeeper-Blue_Up.png")).getScaledInstance(100, 125, Image.SCALE_DEFAULT);
        img[GOLEIRO2_CIMA] = ImageIO.read(new File("Images/Goalkeeper-Red_Up.png")).getScaledInstance(100, 125, Image.SCALE_DEFAULT);
        img[GOLEIRO1_BAIXO] = ImageIO.read(new File("Images/Goalkeeper-Blue_Down.png")).getScaledInstance(100, 125, Image.SCALE_DEFAULT);
        img[GOLEIRO2_BAIXO] = ImageIO.read(new File("Images/Goalkeeper-Red_Down.png")).getScaledInstance(100, 125, Image.SCALE_DEFAULT);
        img[BOLA] = ImageIO.read(new File("Images/Soccer_ball.png")).getScaledInstance(70, 70, Image.SCALE_DEFAULT);

        coordXGoleiro1 = img[TRAVE_DIREITA].getWidth(this)/2; //OU: 35
        coordXGoleiro2 = LARGURA - (int)(2.2*img[TRAVE_DIREITA].getWidth(this)); //OU: LARGURA - img[TRAVE_DIREITA].getWidth(this) - 80
        coordYGoleiro1 = (ALTURA - img[GOLEIRO1_PARADO].getHeight(this))/2;
        coordYGoleiro2 = (ALTURA - img[GOLEIRO2_PARADO].getHeight(this))/2;
      }
       catch (IOException e) {
        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      //Estatico
      g.drawImage(img[FUNDO], 0, 0, getSize().width, getSize().height, this);

      //Dinamico
      g.drawImage(img[estadoGoleiro1], coordXGoleiro1, coordYGoleiro1, this);
      g.drawImage(img[estadoGoleiro2], coordXGoleiro2, coordYGoleiro2, this);
      g.drawImage(img[BOLA], 300, getSize().height - img[BOLA].getHeight(this)-300, this);

      //Estatico
      g.drawImage(img[TRAVE_ESQUERDA], 20, 15, this);
      g.drawImage(img[TRAVE_DIREITA], getSize().width - img[TRAVE_DIREITA].getWidth(this) - 30, 15, this);

      //DESENHA PLACAR
      g.setColor(new Color(255, 255, 255, 180));
      g.setFont(new Font("arial", Font.BOLD, 36));
      g.drawString(String.valueOf(pontosGoleiro1), (int)(LARGURA/2) + (int)(LARGURA/12/2) - 25, (int)(ALTURA/2) - 20);
      g.setColor(new Color(255, 255, 255, 180));
      g.setFont(new Font("arial", Font.BOLD, 36));
      g.drawString(String.valueOf(pontosGoleiro2), (int)(LARGURA/2)-(int)(LARGURA/12/2) - 20, (int)(ALTURA/2) - 20);

      if(inicio){
        g.setColor(Color.BLACK);
        g.setFont(new Font("arial", Font.BOLD, 115));
        g.drawString(String.valueOf(valorCountdown), (int)(LARGURA/2)-50, (int)(ALTURA/2));
        g.setColor(Color.BLUE);
        g.setFont(new Font("arial", Font.BOLD, 100));
        g.drawString(String.valueOf(valorCountdown), (int)(LARGURA/2)-50, (int)(ALTURA/2));
      }

      Toolkit.getDefaultToolkit().sync();
    }
  }

  JogoBase() {
    super("Trabalho");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    add(des);
    pack();
    setVisible(true);
    countdown(inicio);
    inicio = false;
    addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_Q){
          CIMA_1 = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_W){
          BAIXO_1 = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_O){
          CIMA_2 = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_P){
          BAIXO_2 = true;
        }
      }

      public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_Q){
          CIMA_1 = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_W){
          BAIXO_1 = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_O){
          CIMA_2 = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_P){
          BAIXO_2 = false;
        }
      }
    });   

    Timer timer = new Timer(25, new ActionListener(){
        public void actionPerformed(ActionEvent e){
          if(!inicio){
            moveGoleiro();
            des.repaint();
          }
        }
    });
    timer.start();
  }

  public void moveGoleiro(){
    //MOVIMENTOS PARA O GOLEIRO 1
    if(CIMA_1 && coordYGoleiro1 >= 60){
      if(estadoGoleiro1 == GOLEIRO1_PARADO){
        estadoGoleiro1 = GOLEIRO1_CIMA;
      }
      else{
        estadoGoleiro1 = GOLEIRO1_PARADO;
      }
      coordYGoleiro1-=20;
    }
    if(BAIXO_1 && coordYGoleiro1 <= 745){
      if(estadoGoleiro1 == GOLEIRO1_PARADO){
        estadoGoleiro1 = GOLEIRO1_BAIXO;
      }
      else{
        estadoGoleiro1 = GOLEIRO1_PARADO;
      }
      coordYGoleiro1+=20;
    }

    //MOVIMENTOS PARA O GOLEIRO 2
    if(CIMA_2 && coordYGoleiro2 >= 60){
      if(estadoGoleiro2 == GOLEIRO2_PARADO){
        estadoGoleiro2 = GOLEIRO2_CIMA;
      }
      else{
        estadoGoleiro2 = GOLEIRO2_PARADO;
      }
      coordYGoleiro2-=20;
    }
    if(BAIXO_2 && coordYGoleiro2 <= 745){
      if(estadoGoleiro2 == GOLEIRO2_PARADO){
        estadoGoleiro2 = GOLEIRO2_BAIXO;
      }
      else{
        estadoGoleiro2 = GOLEIRO2_PARADO;
      }
      coordYGoleiro2+=20;
    }
  }

  void countdown(boolean inicio){
    if(inicio){
      for(int i=3; i>0; i--){
        des.repaint();
        valorCountdown = i;
        try{
          TimeUnit.MILLISECONDS.sleep(800);
        }
        catch(InterruptedException e){
          e.printStackTrace();
        }
      }
      valorCountdown = 3;
      des.repaint();
    }
  }

  static public void main(String[] args) {
    // Menu menu = new Menu();
    // while(true){
    //   System.out.print(menu.INICIA);
    //   if(menu.INICIA){
        new JogoBase();
    //     menu.INICIA = false;
    //   }
    // }
  }
}
