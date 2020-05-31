import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.geom.Line2D;

import java.util.Random;
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

  volatile boolean jogoAtivo = true;

  // RESOLUCAO
  int LARGURA;
  int ALTURA;

  // SOM HABILITADO
  boolean comSom;

  //
  boolean optBolas;

  // COUNTDOWN
  boolean inicio;
  int valorCountdown;

  // IMAGENS
  Image img[] = new Image[10];

  // SONS
  File inicioSom;
  File countdownSom;
  File torcida;
  File somBola1;
  File somBola2;
  File pontoFeito;
  PlaySound loopTorcida;

  Desenho des;
  Goleiro goleiro1;
  Goleiro goleiro2;
  Bola bola1;
  Bola bola2;

  // Hitboxes
  Line2D hitLineGoleiroEsqFrente;
  Line2D hitLineGoleiroDirFrente;
  Rectangle hitBola1EsqSup, hitBola1EsqInf, hitBola1DirSup, hitBola1DirInf, hitBola2EsqSup, hitBola2EsqInf,
      hitBola2DirSup, hitBola2DirInf, hitboxGoleiroEsqFrente, hitboxGoleiroDirFrente;
  Point pontoEsquerdoSup, pontoDireitoSup;

  class Desenho extends JPanel {

    Desenho() {
      try {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        // CARREGA IMAGENS
        img[FUNDO] = ImageIO.read(new File("Imagens/Soccer_field.png"));
        img[TRAVE_ESQUERDA] = ImageIO.read(new File("Imagens/Post_left.png")).getScaledInstance((63 * LARGURA) / 1400,
            (900 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[TRAVE_DIREITA] = ImageIO.read(new File("Imagens/Post_right.png")).getScaledInstance((63 * LARGURA) / 1400,
            (900 * ALTURA) / 920, Image.SCALE_DEFAULT);

        img[GOLEIRO1_PARADO] = ImageIO.read(new File("Imagens/Goalkeeper-Blue.png"))
            .getScaledInstance((100 * LARGURA) / 1400, (125 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[GOLEIRO2_PARADO] = ImageIO.read(new File("Imagens/Goalkeeper-Red.png"))
            .getScaledInstance((100 * LARGURA) / 1400, (125 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[GOLEIRO1_CIMA] = ImageIO.read(new File("Imagens/Goalkeeper-Blue_Up.png"))
            .getScaledInstance((100 * LARGURA) / 1400, (125 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[GOLEIRO2_CIMA] = ImageIO.read(new File("Imagens/Goalkeeper-Red_Up.png"))
            .getScaledInstance((100 * LARGURA) / 1400, (125 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[GOLEIRO1_BAIXO] = ImageIO.read(new File("Imagens/Goalkeeper-Blue_Down.png"))
            .getScaledInstance((100 * LARGURA) / 1400, (125 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[GOLEIRO2_BAIXO] = ImageIO.read(new File("Imagens/Goalkeeper-Red_Down.png"))
            .getScaledInstance((100 * LARGURA) / 1400, (125 * ALTURA) / 920, Image.SCALE_DEFAULT);
        img[BOLA] = ImageIO.read(new File("Imagens/Soccer_ball.png")).getScaledInstance((70 * LARGURA) / 1400,
            (70 * ALTURA) / 920, Image.SCALE_DEFAULT);

        // CARREGA SONS
        countdownSom = new File("Sons/countdownSound.wav");
        inicioSom = new File("Sons/startSound.wav");
        torcida = new File("Sons/crowd.wav");
        somBola1 = new File("Sons/ballKick1.wav");
        somBola2 = new File("Sons/ballKick2.wav");
        pontoFeito = new File("Sons/winSound.wav");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro",
            JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);

      // Estatico
      g.drawImage(img[FUNDO], 0, 0, getSize().width, getSize().height, this);

      // Dinamico
      g.drawImage(img[goleiro1.estado], goleiro1.coordX, goleiro1.coordY, this);
      g.drawImage(img[goleiro2.estado], goleiro2.coordX, goleiro2.coordY, this);
      if (!bola1.started) {
        bola1.iniciaBola();
        bola1.started = true;
      }
      g.drawImage(img[BOLA], bola1.coordX, bola1.coordY, this);
      if (optBolas) {
        if (!bola2.started) {
          bola2.iniciaBola();
          bola2.started = true;
        }
        g.drawImage(img[BOLA], bola2.coordX, bola2.coordY, this);
      }

      // Estatico
      g.drawImage(img[TRAVE_ESQUERDA], 20, 15, this);
      g.drawImage(img[TRAVE_DIREITA], getSize().width - img[TRAVE_DIREITA].getWidth(this) - 30, 15, this);

      // DESENHA PLACAR
      g.setColor(new Color(255, 255, 255, 180));
      g.setFont(new Font("arial", Font.BOLD, 36));
      g.drawString(String.valueOf(goleiro2.pontos), (int) (LARGURA / 2) + (int) (LARGURA / 12 / 2) - 25,
          (int) (ALTURA / 2) - 15);
      g.setColor(new Color(255, 255, 255, 180));
      g.setFont(new Font("arial", Font.BOLD, 36));
      g.drawString(String.valueOf(goleiro1.pontos), (int) (LARGURA / 2) - (int) (LARGURA / 12 / 2) - 20,
          (int) (ALTURA / 2) - 15);

      // DESENHA CONTADOR
      if (inicio) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("arial", Font.BOLD, 115));
        g.drawString(String.valueOf(valorCountdown), (int) (LARGURA / 2) - 50, (int) (ALTURA / 2));
        g.setColor(Color.BLUE);
        g.setFont(new Font("arial", Font.BOLD, 100));
        g.drawString(String.valueOf(valorCountdown), (int) (LARGURA / 2) - 50, (int) (ALTURA / 2));
      }
      // Hitboxes
      // * Goleiros
      // * * Esquerda
      hitLineGoleiroEsqFrente = new Line2D.Double(
          goleiro1.coordX + img[goleiro1.estado].getWidth(this) - (int) (20. / 800 * LARGURA), goleiro1.coordY,
          goleiro1.coordX + img[goleiro1.estado].getWidth(this) - (int) (20. / 800 * LARGURA),
          goleiro1.coordY + img[goleiro1.estado].getHeight(this));
      hitboxGoleiroEsqFrente = new Rectangle(
          goleiro1.coordX + img[goleiro1.estado].getWidth(this) - (int) (20. / 800 * LARGURA), goleiro1.coordY,
          (int) (20. / 800 * LARGURA), img[goleiro1.estado].getHeight(this));
      // * * Direita
      hitLineGoleiroDirFrente = new Line2D.Double(goleiro2.coordX, goleiro2.coordY, goleiro2.coordX,
          goleiro2.coordY + img[goleiro2.estado].getHeight(this));
      hitboxGoleiroDirFrente = new Rectangle(goleiro2.coordX, goleiro2.coordY, (int) (20. / 800 * LARGURA),
          img[goleiro2.estado].getHeight(this));

      // * Bolas
      // * * Bola 1
      hitBola1EsqSup = new Rectangle(bola1.coordX, bola1.coordY + img[BOLA].getHeight(this) / 4, 3, 3);
      hitBola1EsqInf = new Rectangle(bola1.coordX, bola1.coordY + img[BOLA].getHeight(this) * 3 / 4, 3, 3);
      hitBola1DirSup = new Rectangle(bola1.coordX + img[BOLA].getWidth(this),
          bola1.coordY + img[BOLA].getHeight(this) / 4, 3, 3);
      hitBola1DirInf = new Rectangle(bola1.coordX + img[BOLA].getWidth(this),
          bola1.coordY + img[BOLA].getHeight(this) * 3 / 4, 3, 3);
      // * * Bola 2
      if (optBolas) {
        hitBola2EsqSup = new Rectangle(bola2.coordX, bola2.coordY + img[BOLA].getHeight(this) / 4, 3, 3);
        hitBola2EsqInf = new Rectangle(bola2.coordX, bola2.coordY + img[BOLA].getHeight(this) * 3 / 4, 3, 3);
        hitBola2DirSup = new Rectangle(bola2.coordX + img[BOLA].getWidth(this),
            bola2.coordY + img[BOLA].getHeight(this) / 4, 3, 3);
        hitBola2DirInf = new Rectangle(bola2.coordX + img[BOLA].getWidth(this),
            bola2.coordY + img[BOLA].getHeight(this) * 3 / 4, 3, 3);
      }
      Graphics2D g2d = (Graphics2D) g;
      g2d.setColor(Color.RED);
      // g2d.draw(hitBola2DirInf);
      // g2d.draw(hitBola2DirSup);
      // g2d.draw(hitBola2EsqInf);
      // g2d.draw(hitBola2EsqSup);
      Toolkit.getDefaultToolkit().sync();
    }
  }

  JogoBase(int largura_temp, int altura_temp, int comSom_temp, int optBolas_temp) {
    super("Trabalho");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // PEGA VALORES PARA RESOLUCAO
    ALTURA = altura_temp;
    LARGURA = largura_temp;

    // DEFINE QUANTIDADE DE BOLAS
    if (optBolas_temp == 1)
      optBolas = true;

    des = new Desenho();
    if (comSom_temp == 1) {
      comSom = true;
    }

    // CRIA CLASSE DOS GOLEIROS E BOLAS
    goleiro1 = new Goleiro(GOLEIRO1_PARADO, img[TRAVE_DIREITA].getWidth(this) / 2,
        (ALTURA - img[GOLEIRO1_PARADO].getHeight(this)) / 2);
    goleiro2 = new Goleiro(GOLEIRO2_PARADO, LARGURA - (int) (2.2 * img[TRAVE_DIREITA].getWidth(this)),
        (ALTURA - img[GOLEIRO2_PARADO].getHeight(this)) / 2);
    if (!optBolas)
      bola1 = new Bola(0);
    else {
      bola1 = new Bola(1);
      bola2 = new Bola(2);
    }
    add(des);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);

    // INICIA SOM DE TORCIDA
    loopTorcida = new PlaySound(torcida, true);

    // INICIA CONTADOR
    contagemProcesso();

    // NECESSARIO PARA CONSEGUIR MOVER OS DOIS GOLEIROS AO MESMO TEMPO
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Q) {
          goleiro1.cima = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
          goleiro1.baixo = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_O) {
          goleiro2.cima = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
          goleiro2.baixo = true;
        }
      }

      // NECESSARIO PARA CONSEGUIR MOVER OS DOIS GOLEIROS AO MESMO TEMPO
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Q) {
          goleiro1.cima = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
          goleiro1.baixo = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_O) {
          goleiro2.cima = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
          goleiro2.baixo = false;
        }
      }
    });
    addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        jogoAtivo = false;
        loopTorcida.clip.stop();
      }
    });

    // REDESENHA JOGO A CADA INTERACAO
    Timer timer = new Timer(25, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!inicio) {
          moveGoleiro();
          bola1.moveBola();
          if (optBolas)
            bola2.moveBola();
          des.repaint();
        }
      }
    });
    timer.start();
  }

  class Bola {
    final int MAX_SPEED = 10, MIN_SPEED = 8;
    final double MAX_ANGLE = 5 * Math.PI / 12;
    int n;
    int coordX = 0, coordY;
    double velX, velY;
    boolean started = false;

    Bola(int n) {
      this.n = n;
    }

    // n = 0, 1 bola, n = 1, bola de cima, n = 2 bola de baixo
    void iniciaBola() {
      int dirX = new Random().nextBoolean() ? 1 : -1;
      int dirY = new Random().nextBoolean() ? 1 : -1;
      if (n == 0) {
        coordX = getWidth() / 2 - img[BOLA].getWidth(JogoBase.this) / 2;
        coordY = getHeight() / 2 - img[BOLA].getHeight(JogoBase.this) / 2;
      } else if (n == 1) {
        coordX = getWidth() / 2 - img[BOLA].getWidth(JogoBase.this) / 2;
        coordY = getHeight() * 1 / 4 - img[BOLA].getHeight(JogoBase.this) / 2;
      } else {
        coordX = getWidth() / 2 - img[BOLA].getWidth(JogoBase.this) / 2;
        coordY = getHeight() * 3 / 4 - img[BOLA].getHeight(JogoBase.this) / 2;
      }

      velX = (new Random().nextInt(MAX_SPEED - MIN_SPEED) + MIN_SPEED) * dirX;
      velY = (new Random().nextInt(MAX_SPEED - MIN_SPEED) + MIN_SPEED) * dirY;
    }

    void hitTraves() {
      double nextX = coordX + velX, nextY = coordY + velY;

      // trave direita
      if (nextX + img[BOLA].getWidth(JogoBase.this) / 2 >= getSize().width - img[TRAVE_DIREITA].getWidth(JogoBase.this)
          - 30)
        if (nextY <= 30 || nextY >= (900 * ALTURA) / 920 - img[BOLA].getHeight(JogoBase.this))
          velX *= -1;
      // trave esquerda
      if (nextX + img[BOLA].getWidth(JogoBase.this) / 2 <= 20 + (63 * LARGURA) / 1400)
        if (nextY <= 30 || nextY >= (900 * ALTURA) / 920 - img[BOLA].getHeight(JogoBase.this))
          velX *= -1;
    }

    void hitLaterais() {
      if (coordY + velY + 20 >= getHeight() - img[BOLA].getHeight(JogoBase.this))
        velY *= -1;
      else if (coordY <= 0)
        velY *= -1;
    }

    // checa se bola está tocando algum dos goleiros
    // obs: esse codigo é uma abominação e deveria ser crime te-lo criado
    void hitGoleiro() {
      double yIntersect, bounceAngle = 0, speed, newVelY;
      speed = Math.sqrt(Math.pow(velX, 2) + Math.pow(velY, 2));
      // se a bola estiver a direita do campo
      if (n == 0 || n == 1) {
        if (coordX > getWidth() / 2) {
          if (hitBola1DirSup.getY() >= hitboxGoleiroDirFrente.getY()
              && hitBola1DirSup.getY() <= hitboxGoleiroDirFrente.getY() + img[goleiro2.estado].getHeight(JogoBase.this)
              && hitBola1DirSup.getX() >= hitboxGoleiroDirFrente.getX()
              && hitBola1DirSup.getX() <= hitboxGoleiroDirFrente.getX() + (int) (20. / 800 * LARGURA)) {
            yIntersect = hitBola1DirSup.getY()
                - (hitLineGoleiroDirFrente.getY1() + hitLineGoleiroDirFrente.getY2()) / 2;
            yIntersect /= img[goleiro2.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * -Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          } else if (hitBola1DirInf.getY() >= hitboxGoleiroDirFrente.getY()
              && hitBola1DirInf.getY() <= hitboxGoleiroDirFrente.getY() + img[goleiro2.estado].getHeight(JogoBase.this)
              && hitBola1DirInf.getX() >= hitboxGoleiroDirFrente.getX()
              && hitBola1DirInf.getX() <= hitboxGoleiroDirFrente.getX() + (int) (20. / 800 * LARGURA)) {
            yIntersect = hitBola1DirInf.getY()
                - (hitLineGoleiroDirFrente.getY1() + hitLineGoleiroDirFrente.getY2()) / 2;
            yIntersect /= img[goleiro2.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * -Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          }
        } else {
          if (hitBola1EsqSup.getY() >= hitboxGoleiroEsqFrente.getY()
              && hitBola1EsqSup.getY() <= hitboxGoleiroEsqFrente.getY() + img[goleiro1.estado].getHeight(JogoBase.this)
              && hitBola1EsqSup.getX() <= hitboxGoleiroEsqFrente.getX() + (int) (20. / 800 * LARGURA)
              && hitBola1EsqSup.getX() >= hitboxGoleiroEsqFrente.getX()) {
            yIntersect = hitBola1EsqSup.getY()
                - (hitLineGoleiroEsqFrente.getY1() + hitLineGoleiroEsqFrente.getY2()) / 2;
            yIntersect /= img[goleiro1.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          } else if (hitBola1EsqInf.getY() >= hitboxGoleiroEsqFrente.getY()
              && hitBola1EsqInf.getY() <= hitboxGoleiroEsqFrente.getY() + img[goleiro1.estado].getHeight(JogoBase.this)
              && hitBola1EsqInf.getX() <= hitboxGoleiroEsqFrente.getX() + (int) (20. / 800 * LARGURA)
              && hitBola1EsqInf.getX() >= hitboxGoleiroEsqFrente.getX()) {
            yIntersect = hitBola1EsqInf.getY()
                - (hitLineGoleiroEsqFrente.getY1() + hitLineGoleiroEsqFrente.getY2()) / 2;
            yIntersect /= img[goleiro1.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          }
        }
      } else {
        if (coordX > getWidth() / 2) {
          if (hitBola2DirSup.getY() >= hitboxGoleiroDirFrente.getY()
              && hitBola2DirSup.getY() <= hitboxGoleiroDirFrente.getY() + img[goleiro2.estado].getHeight(JogoBase.this)
              && hitBola2DirSup.getX() >= hitboxGoleiroDirFrente.getX()
              && hitBola2DirSup.getX() <= hitboxGoleiroDirFrente.getX() + (int) (20. / 800 * LARGURA)) {
            yIntersect = hitBola2DirSup.getY()
                - (hitLineGoleiroDirFrente.getY1() + hitLineGoleiroDirFrente.getY2()) / 2;
            yIntersect /= img[goleiro2.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * -Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          } else if (hitBola2DirInf.getY() >= hitboxGoleiroDirFrente.getY()
              && hitBola2DirInf.getY() <= hitboxGoleiroDirFrente.getY() + img[goleiro2.estado].getHeight(JogoBase.this)
              && hitBola2DirInf.getX() >= hitboxGoleiroDirFrente.getX()
              && hitBola2DirInf.getX() <= hitboxGoleiroDirFrente.getX() + (int) (20. / 800 * LARGURA)) {
            yIntersect = hitBola2DirInf.getY()
                - (hitLineGoleiroDirFrente.getY1() + hitLineGoleiroDirFrente.getY2()) / 2;
            yIntersect /= img[goleiro2.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * -Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          }
        } else {
          if (hitBola2EsqSup.getY() >= hitboxGoleiroEsqFrente.getY()
              && hitBola2EsqSup.getY() <= hitboxGoleiroEsqFrente.getY() + img[goleiro1.estado].getHeight(JogoBase.this)
              && hitBola2EsqSup.getX() <= hitboxGoleiroEsqFrente.getX() + (int) (20. / 800 * LARGURA)
              && hitBola2EsqSup.getX() >= hitboxGoleiroEsqFrente.getX()) {
            yIntersect = hitBola2EsqSup.getY()
                - (hitLineGoleiroEsqFrente.getY1() + hitLineGoleiroEsqFrente.getY2()) / 2;
            yIntersect /= img[goleiro1.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          } else if (hitBola2EsqInf.getY() >= hitboxGoleiroEsqFrente.getY()
              && hitBola2EsqInf.getY() <= hitboxGoleiroEsqFrente.getY() + img[goleiro1.estado].getHeight(JogoBase.this)
              && hitBola2EsqInf.getX() <= hitboxGoleiroEsqFrente.getX() + (int) (20. / 800 * LARGURA)
              && hitBola2EsqInf.getX() >= hitboxGoleiroEsqFrente.getX()) {
            yIntersect = hitBola2EsqInf.getY()
                - (hitLineGoleiroEsqFrente.getY1() + hitLineGoleiroEsqFrente.getY2()) / 2;
            yIntersect /= img[goleiro1.estado].getHeight(JogoBase.this) / 2;
            bounceAngle = yIntersect * MAX_ANGLE;
            velX = speed * Math.cos(bounceAngle);
            newVelY = speed * -Math.sin(bounceAngle);
            if (velY < 0 && newVelY > 0 || velY > 0 && newVelY < 0)
              velY = -newVelY;
            else
              velY = newVelY;
          }
        }
      }
    }

    // loop que move bola e executa as verificações de hits
    void moveBola() {
      hitGoleiro();
      hitLaterais();
      hitTraves();
      dentroDoGol();

      if (velY < 1 && velY > 0)
        velY = 1;

      coordX += velX;
      coordY += velY;
    }

    // checa se bola esta dentro de algum dos gols
    void dentroDoGol() {
      if (coordX <= 20 - img[BOLA].getWidth(JogoBase.this) / 2) {
        iniciaBola();
        goleiro2.pontos++;
      } else if (coordX >= getSize().width - img[TRAVE_DIREITA].getWidth(JogoBase.this) - 30
          + img[BOLA].getWidth(JogoBase.this) / 2) {
        iniciaBola();
        goleiro1.pontos++;
      }
    }
  }

  class Goleiro {
    int posicaoXInicial;
    int posicaoYInicial;
    int coordX;
    int coordY;
    int estado;
    int pontos = 0;
    boolean cima = false;
    boolean baixo = false;

    Goleiro(int estado, int posicaoXInicial, int posicaoYInicial) {
      this.estado = estado;
      this.posicaoXInicial = this.coordX = posicaoXInicial;
      this.posicaoYInicial = this.coordY = posicaoYInicial;
    }
  }

  public void moveGoleiro() {
    // MOVIMENTOS PARA O GOLEIRO 1
    if (goleiro1.cima && goleiro1.coordY >= (70 * ALTURA) / 920) {
      if (goleiro1.estado == GOLEIRO1_PARADO) {
        goleiro1.estado = GOLEIRO1_CIMA;
      } else {
        goleiro1.estado = GOLEIRO1_PARADO;
      }
      goleiro1.coordY -= 20;
    }
    if (goleiro1.baixo && goleiro1.coordY <= (745 * ALTURA) / 920) {
      if (goleiro1.estado == GOLEIRO1_PARADO) {
        goleiro1.estado = GOLEIRO1_BAIXO;
      } else {
        goleiro1.estado = GOLEIRO1_PARADO;
      }
      goleiro1.coordY += 20;
    }

    // MOVIMENTOS PARA O GOLEIRO 2
    if (goleiro2.cima && goleiro2.coordY >= (70 * ALTURA) / 920) {
      if (goleiro2.estado == GOLEIRO2_PARADO) {
        goleiro2.estado = GOLEIRO2_CIMA;
      } else {
        goleiro2.estado = GOLEIRO2_PARADO;
      }
      goleiro2.coordY -= 20;
    }
    if (goleiro2.baixo && goleiro2.coordY <= (745 * ALTURA) / 920) {
      if (goleiro2.estado == GOLEIRO2_PARADO) {
        goleiro2.estado = GOLEIRO2_BAIXO;
      } else {
        goleiro2.estado = GOLEIRO2_PARADO;
      }
      goleiro2.coordY += 20;
    }
  }

  // CONTADOR DO CONTADOR | REDESENHA A CADA INTERACAO
  void contagem(boolean inicio) {

    // NECESSITA DE UM WINDOW LISTENER NA CONTAGEM, JA QUE CONGELA O PROGRAMA
    addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        jogoAtivo = false;
        loopTorcida.clip.stop();
      }
    });

    if (inicio) {
      for (int i = 3; i > 0; i--) {
        if (!jogoAtivo) {
          comSom = false;
          return;
        }
        new PlaySound(countdownSom, false);
        des.repaint();
        valorCountdown = i;
        sleep(900);
      }
      valorCountdown = 3;
    }
  }

  // RESPOSAVEL POR TODOS OS SONS
  class PlaySound {
    Clip clip;

    PlaySound(File Sound, boolean loop) {
      if (comSom) {
        try {
          clip = AudioSystem.getClip();
          clip.open(AudioSystem.getAudioInputStream(Sound));
          clip.start();
          if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
          }
        } catch (Exception e) {
          System.out.print(e);
        }
      }
    }
  }

  // CONTADOR
  public void contagemProcesso() {
    sleep(700);
    inicio = true;
    contagem(inicio);
    inicio = false;
    des.repaint();
    new PlaySound(inicioSom, false);
  }

  // FUNCAO PAUSA
  public void sleep(int tempo) {
    try {
      TimeUnit.MILLISECONDS.sleep(tempo);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  static public void main(String[] args) {
    boolean iniciaMenu = true;
    Menu menu = new Menu(true);

    while (true) {
      if (iniciaMenu) {
        menu = new Menu();
        while (menu.menuAtivo) {
          // DO NOTHING...
        }
        iniciaMenu = false;
      } else {
        JogoBase jogo = new JogoBase(menu.resX[menu.optOpcoes[0]], menu.resY[menu.optOpcoes[0]], menu.optOpcoes[2],
            menu.optOpcoes[1]);
        while (jogo.jogoAtivo) {
          // DO NOTHING...
        }
        iniciaMenu = true;
      }
    }
  }
}
