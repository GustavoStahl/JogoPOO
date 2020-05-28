import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.concurrent.TimeUnit;

class JogoBase extends JFrame {
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

  //RESOLUCAO
  int LARGURA;
  int ALTURA;

  //SOM HABILITADO
  int comSom;

  //COUNTDOWN
  boolean inicio;
  int valorCountdown;

  //IMAGENS
  Image img[] = new Image[10];

  //SONS
  File inicioSom;
  File countdownSom;

  Desenho des;
  Goleiro goleiro1;
  Goleiro goleiro2;

  class Desenho extends JPanel {

    Desenho() {
      try {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        //CARREGA IMAGENS
        img[FUNDO] = ImageIO.read(new File("Images/Soccer_field.png"));
        img[TRAVE_ESQUERDA] = ImageIO.read(new File("Images/Post_left.png")).getScaledInstance((63*LARGURA)/1400, (900*ALTURA)/920, Image.SCALE_DEFAULT);
        img[TRAVE_DIREITA] = ImageIO.read(new File("Images/Post_right.png")).getScaledInstance((63*LARGURA)/1400, (900*ALTURA)/920, Image.SCALE_DEFAULT);

        img[GOLEIRO1_PARADO] = ImageIO.read(new File("Images/Goalkeeper-Blue.png")).getScaledInstance((100*LARGURA)/1400, (125*ALTURA)/920, Image.SCALE_DEFAULT);
        img[GOLEIRO2_PARADO] = ImageIO.read(new File("Images/Goalkeeper-Red.png")).getScaledInstance((100*LARGURA)/1400, (125*ALTURA)/920, Image.SCALE_DEFAULT);
        img[GOLEIRO1_CIMA] = ImageIO.read(new File("Images/Goalkeeper-Blue_Up.png")).getScaledInstance((100*LARGURA)/1400, (125*ALTURA)/920, Image.SCALE_DEFAULT);
        img[GOLEIRO2_CIMA] = ImageIO.read(new File("Images/Goalkeeper-Red_Up.png")).getScaledInstance((100*LARGURA)/1400, (125*ALTURA)/920, Image.SCALE_DEFAULT);
        img[GOLEIRO1_BAIXO] = ImageIO.read(new File("Images/Goalkeeper-Blue_Down.png")).getScaledInstance((100*LARGURA)/1400, (125*ALTURA)/920, Image.SCALE_DEFAULT);
        img[GOLEIRO2_BAIXO] = ImageIO.read(new File("Images/Goalkeeper-Red_Down.png")).getScaledInstance((100*LARGURA)/1400, (125*ALTURA)/920, Image.SCALE_DEFAULT);
        img[BOLA] = ImageIO.read(new File("Images/Soccer_ball.png")).getScaledInstance((70*LARGURA)/1400, (70*ALTURA)/920, Image.SCALE_DEFAULT);

        //CARREGA SONS
        countdownSom = new File("countdownSound.wav");
        inicioSom = new File("startSound.wav");

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
      g.drawImage(img[goleiro1.estado], goleiro1.coordX, goleiro1.coordY, this);
      g.drawImage(img[goleiro2.estado], goleiro2.coordX, goleiro2.coordY, this);
      g.drawImage(img[BOLA], 300, getSize().height - img[BOLA].getHeight(this)-300, this);

      //Estatico
      g.drawImage(img[TRAVE_ESQUERDA], 20, 15, this);
      g.drawImage(img[TRAVE_DIREITA], getSize().width - img[TRAVE_DIREITA].getWidth(this) - 30, 15, this);

      //DESENHA PLACAR
      g.setColor(new Color(255, 255, 255, 180));
      g.setFont(new Font("arial", Font.BOLD, 36));
      g.drawString(String.valueOf(goleiro1.pontos), (int)(LARGURA/2) + (int)(LARGURA/12/2) - 25, (int)(ALTURA/2) - 20);
      g.setColor(new Color(255, 255, 255, 180));
      g.setFont(new Font("arial", Font.BOLD, 36));
      g.drawString(String.valueOf(goleiro2.pontos), (int)(LARGURA/2)-(int)(LARGURA/12/2) - 20, (int)(ALTURA/2) - 20);

      //DESENHA CONTADOR
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

  JogoBase(int largura_temp, int altura_temp, int comSom_temp) {
    super("Trabalho");
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    ALTURA = altura_temp;
    LARGURA = largura_temp;
    comSom = comSom_temp;
    des = new Desenho();
    //CRIA CLASSE DOS GOLEIROS
    goleiro1 = new Goleiro(GOLEIRO1_PARADO, img[TRAVE_DIREITA].getWidth(this)/2, (ALTURA - img[GOLEIRO1_PARADO].getHeight(this))/2);
    goleiro2 = new Goleiro(GOLEIRO2_PARADO, LARGURA - (int)(2.2*img[TRAVE_DIREITA].getWidth(this)), (ALTURA - img[GOLEIRO2_PARADO].getHeight(this))/2);
    add(des);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
    contagemProcesso();
    //NECESSARIO PARA CONSEGUIR MOVER OS DOIS GOLEIROS AO MESMO TEMPO
    addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_Q){
          goleiro1.cima = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_W){
          goleiro1.baixo = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_O){
          goleiro2.cima = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_P){
          goleiro2.baixo = true;
        }
      }
    //NECESSARIO PARA CONSEGUIR MOVER OS DOIS GOLEIROS AO MESMO TEMPO
      public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_Q){
          goleiro1.cima = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_W){
          goleiro1.baixo = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_O){
          goleiro2.cima = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_P){
          goleiro2.baixo = false;
        }
      }
    });

    //REDESENHA JOGO A CADA INTERACAO
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

  class Goleiro{
    int posicaoXInicial;
    int posicaoYInicial;
    int coordX;
    int coordY;
    int estado;
    int pontos = 0;
    boolean cima = false;
    boolean baixo = false;

    Goleiro(int estado, int posicaoXInicial, int posicaoYInicial){
      this.estado = estado;
      this.posicaoXInicial = this.coordX = posicaoXInicial;
      this.posicaoYInicial = this.coordY = posicaoYInicial;
    }
  }

  public void moveGoleiro(){
    //MOVIMENTOS PARA O GOLEIRO 1
    if(goleiro1.cima && goleiro1.coordY >= (70*ALTURA)/920){
      if(goleiro1.estado == GOLEIRO1_PARADO){
        goleiro1.estado = GOLEIRO1_CIMA;
      }
      else{
        goleiro1.estado = GOLEIRO1_PARADO;
      }
      goleiro1.coordY-=20;
    }
    if(goleiro1.baixo && goleiro1.coordY <= (745*ALTURA)/920){
      if(goleiro1.estado == GOLEIRO1_PARADO){
        goleiro1.estado = GOLEIRO1_BAIXO;
      }
      else{
        goleiro1.estado = GOLEIRO1_PARADO;
      }
      goleiro1.coordY+=20;
    }

    //MOVIMENTOS PARA O GOLEIRO 2
    if(goleiro2.cima && goleiro2.coordY >= (70*ALTURA)/920){
      if(goleiro2.estado == GOLEIRO2_PARADO){
        goleiro2.estado = GOLEIRO2_CIMA;
      }
      else{
        goleiro2.estado = GOLEIRO2_PARADO;
      }
      goleiro2.coordY-=20;
    }
    if(goleiro2.baixo && goleiro2.coordY <= (745*ALTURA)/920){
      if(goleiro2.estado == GOLEIRO2_PARADO){
        goleiro2.estado = GOLEIRO2_BAIXO;
      }
      else{
        goleiro2.estado = GOLEIRO2_PARADO;
      }
      goleiro2.coordY+=20;
    }
  }

  //CONTADOR DO CONTADOR | REDESENHA A CADA INTERACAO
  void contagem(boolean inicio){
    if(inicio){
      for(int i=3; i>0; i--){
        playSound(countdownSom);
        des.repaint();
        valorCountdown = i;
        sleep(900);
      }
      valorCountdown = 3;
    }
  }

  //RESPOSAVEL POR TODOS OS SONS
  public void playSound(File Sound){
    if(comSom == 0){
      try{
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(Sound));
        clip.start();
      } catch (Exception e) {
        System.out.print(e);
      }
}
  }

  //CONTADOR
  public void contagemProcesso(){
    sleep(700);
    inicio = true;
    contagem(inicio);
    inicio = false;
    des.repaint();
    playSound(inicioSom);
  }

  //FUNCAO PAUSA
  public void sleep(int tempo){
    try{
      TimeUnit.MILLISECONDS.sleep(tempo);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
  }

  static public void main(String[] args) {
    Menu menu = new Menu();
    while(true){
      //PRINT NECESSARIO PRO PROGRAMA FUNCIONAR
      System.out.print("");
      if(menu.INICIA){
        new JogoBase(menu.resX[menu.optOpcoes[0]], menu.resY[menu.optOpcoes[0]], menu.optOpcoes[2]);
        menu.INICIA = false;
      }
    }
  }
}
