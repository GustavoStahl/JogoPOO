import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.imageio.*;

class Menu extends JFrame {
    Image img[] = new Image[2];
    Desenha des = new Desenha();
    Timer t;

    final int JOGAR = 0;
    final int OPT = 1;
    final int SAIR = 2;
    final int MENU_IMG = 0;
    final int OPT_IMG = 1;
    final int CONTROLES_IMG = 2;
    final int RESOLUCAO = 0;
    final int MODO = 1;
    final int SOM = 2;
    final int CONTROLES = 3;
    final int CANCELAR = 4;
    final int SALVAR = 5;
    final int VOLTAR = 0;
    final int RES1 = 0;
    final int RES2 = 1;
    final int RES3 = 2;
    final int RES4 = 3;
    final int MODO1 = 0;
    final int MODO2 = 1;
    final int SOM_OFF = 0;
    final int SOM_ON = 1;

    volatile boolean menuAtivo = true;

    int ALTURA;
    int LARGURA;

    // Controla o botão ativo
    int selected = JOGAR;
    int selectedOpt = RESOLUCAO;
    int currScreen = MENU_IMG;
    int[] lastOpt = new int[3];
    // Guarda as opções no formato {Resolução, Modo de Jogo, Som}
    int[] optOpcoes = new int[] { RES1, MODO1, SOM_ON };
    String[] optResolutions = { "800×600", "960×720", "1024×768", "1280×960" };
    int[] resX = { 800, 960, 1024, 1280 };
    int[] resY = { 600, 720, 768, 960 };

    class Desenha extends JPanel {
        // Botões do menu principal
        Rectangle jogarBtn;
        String jogarTxt = "JOGAR";

        Rectangle sairBtn;
        String sairTxt = "SAIR";

        Rectangle optBtn;
        String optTxt = "OPÇÕES";

        // Componentes do menu de opções
        Rectangle salvarBtn;
        String salvarTxt = "SALVAR";

        Rectangle cancelarBtn;
        String cancelarTxt = "CANCELAR";

        Rectangle optMenuArea;
        String optMenuAreaTxt = "OPÇÕES";

        Rectangle ctrlInnerArea;

        Rectangle optResolution;
        String optResolutionTxt = "RESOLUÇÃO";

        Rectangle optSound;
        String optSoundTxt = "SOM";
        String[] optSoundToggleTxt = { "DESATIVADO", "ATIVADO" };

        Rectangle optMode;
        String optModeTxt = "MODO";
        String[] optModesTxt = { "1x BOLA", "2x BOLAS" };

        Rectangle optControles;
        String optControlesTxt = "CONTROLES";

        // Componentes do menu de controles
        Rectangle voltarBtn;
        String voltarTxt = "VOLTAR";

        Font font;

        Desenha() {
            try {
                setPreferredSize(new Dimension(800, 600));
                // lê imagens de fundo
                img[0] = ImageIO.read(new File("Imagens/campo-blur.png"));
                img[1] = ImageIO.read(new File("Imagens/rede-blur.png"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // AntiAliasing para o texto
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            if (currScreen == MENU_IMG)
                g.drawImage(img[MENU_IMG], 0, 0, getSize().width, getSize().height, this);
            else if (currScreen == OPT_IMG || currScreen == CONTROLES_IMG)
                g.drawImage(img[OPT_IMG], 0, 0, getSize().width, getSize().height, this);
            g.setFont(font);

            // Tamanho dos botões, pode ser alterado para uma
            // proporção do tamanho da tela ao invés de um valor fixo
            int w = 300, h = 100;
            // Cria os botões e define a fonte dos textos
            jogarBtn = new Rectangle(getSize().width / 2 - w / 2, getSize().height * 2 / 8 - h / 2, w, h);
            optBtn = new Rectangle(getSize().width / 2 - w / 2, getSize().height * 4 / 8 - h / 2, w, h);
            sairBtn = new Rectangle(getSize().width / 2 - w / 2, getSize().height * 6 / 8 - h / 2, w, h);
            salvarBtn = new Rectangle(getSize().width * 3 / 4 - w / 2, getSize().height * 7 / 8 - h / 2, w, h);
            cancelarBtn = new Rectangle(getSize().width / 4 - w / 2, getSize().height * 7 / 8 - h / 2, w, h);
            optMenuArea = new Rectangle(getSize().width / 4 - w / 2, getSize().height / 2 - h * 2 + 10,
                    w + getSize().width / 2, h * 3);
            ctrlInnerArea = new Rectangle(getSize().width / 4 - w / 2 + 5, getSize().height / 2 - h * 2 + 15,
                    w + getSize().width / 2 - 10, h * 3 - 10);
            w = optMenuArea.getSize().width - 20;
            h = optMenuArea.getSize().height / 4 - 20;
            optResolution = new Rectangle((int) optMenuArea.getCenterX() - w / 2, (int) optMenuArea.getY() + 15, w, h);
            optMode = new Rectangle((int) optMenuArea.getCenterX() - w / 2, (int) optMenuArea.getY() + 30 + h, w, h);
            optSound = new Rectangle((int) optMenuArea.getCenterX() - w / 2, (int) optMenuArea.getY() + 45 + 2 * h, w,
                    h);
            optControles = new Rectangle((int) optMenuArea.getCenterX() - w / 2, (int) optMenuArea.getY() + 60 + 3 * h,
                    w, h);
            voltarBtn = new Rectangle((int) optMenuArea.getCenterX() - w / 2, getSize().height * 7 / 8 - h / 2, w, h);

            font = new Font("Roboto", Font.PLAIN, 50);

            // Cores utilizadas na interface grafica
            Color btnUnselected = new Color(211, 38, 38);
            Color btnSelected = new Color(121, 215, 15);
            Color txtColor = new Color(237, 244, 242);
            Color outlineColor = new Color(221, 221, 221);
            Color bgAreaColor = new Color(136, 136, 136);

            int strWidth, strHeight;
            // Desenha os componentes do menu
            if (currScreen == MENU_IMG) {
                g.setColor(btnUnselected);
                if (selected == JOGAR)
                    g.setColor(btnSelected);
                g2d.fill(jogarBtn);

                g.setColor(btnUnselected);
                if (selected == OPT)
                    g.setColor(btnSelected);
                g2d.fill(optBtn);

                g.setColor(btnUnselected);
                if (selected == SAIR)
                    g.setColor(btnSelected);
                g2d.fill(sairBtn);

                // Outline dos botões
                g.setColor(outlineColor);
                g2d.draw(jogarBtn);
                g2d.draw(sairBtn);
                g2d.draw(optBtn);

                // Escreve os textos do menu
                strWidth = g.getFontMetrics(font).stringWidth(jogarTxt);
                strHeight = g.getFontMetrics(font).getHeight();
                g.setColor(txtColor);
                g.drawString(jogarTxt, (int) (jogarBtn.getX() + jogarBtn.getWidth() / 2 - strWidth / 2),
                        (int) (jogarBtn.getY() + jogarBtn.getHeight() / 2 + strHeight / 4));

                strWidth = g.getFontMetrics(font).stringWidth(sairTxt);
                g.drawString(sairTxt, (int) (sairBtn.getX() + sairBtn.getWidth() / 2 - strWidth / 2),
                        (int) (sairBtn.getY() + sairBtn.getHeight() / 2 + strHeight / 4 + 5));

                strWidth = g.getFontMetrics(font).stringWidth(optTxt);
                g.drawString(optTxt, (int) (optBtn.getX() + optBtn.getWidth() / 2 - strWidth / 2),
                        (int) (optBtn.getY() + optBtn.getHeight() / 2 + strHeight / 4 + 5));
            }

            // Desenha os componentes da tela de opções
            if (currScreen == OPT_IMG) {
                g.setColor(btnUnselected);
                if (selectedOpt == CANCELAR)
                    g.setColor(btnSelected);
                g2d.fill(cancelarBtn);

                g.setColor(btnUnselected);
                if (selectedOpt == SALVAR)
                    g.setColor(btnSelected);
                g2d.fill(salvarBtn);

                g.setColor(bgAreaColor);
                g2d.fill(optMenuArea);

                g2d.setColor(btnUnselected);
                if (selectedOpt == RESOLUCAO)
                    g.setColor(btnSelected);
                g2d.fill(optResolution);

                g2d.setColor(btnUnselected);
                if (selectedOpt == MODO)
                    g.setColor(btnSelected);
                g2d.fill(optMode);

                g2d.setColor(btnUnselected);
                if (selectedOpt == SOM)
                    g.setColor(btnSelected);
                g2d.fill(optSound);

                g2d.setColor(btnUnselected);
                if (selectedOpt == CONTROLES)
                    g.setColor(btnSelected);
                g2d.fill(optControles);

                g.setColor(outlineColor);
                g2d.draw(optMenuArea);
                g2d.draw(salvarBtn);
                g2d.draw(cancelarBtn);
                g2d.draw(optResolution);
                g2d.draw(optMode);
                g2d.draw(optSound);
                g2d.draw(optControles);

                // Desenha textos
                strWidth = g.getFontMetrics(font).stringWidth(cancelarTxt);
                strHeight = g.getFontMetrics(font).getHeight();
                g.setColor(txtColor);
                g.drawString(cancelarTxt, (int) (cancelarBtn.getX() + cancelarBtn.getWidth() / 2 - strWidth / 2),
                        (int) (cancelarBtn.getY() + cancelarBtn.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth(optResolutionTxt);
                g.drawString(optResolutionTxt, (int) (optResolution.getX()) + 10,
                        (int) (optResolution.getY() + optResolution.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth(optResolutions[optOpcoes[0]]);
                g.drawString(optResolutions[optOpcoes[0]],
                        (int) optResolution.getX() + optResolution.getSize().width - strWidth - 10,
                        (int) (optResolution.getY() + optResolution.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth(optModeTxt);
                g.drawString(optModeTxt, (int) (optMode.getX()) + 10,
                        (int) (optMode.getY() + optMode.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth(optSoundToggleTxt[optOpcoes[2]]);
                g.drawString(optSoundToggleTxt[optOpcoes[2]],
                        (int) optSound.getX() + optSound.getSize().width - strWidth - 10,
                        (int) (optSound.getY() + optSound.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth(optSoundTxt);
                g.drawString(optSoundTxt, (int) (optSound.getX()) + 10,
                        (int) (optSound.getY() + optSound.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth(optModesTxt[optOpcoes[1]]);
                g.drawString(optModesTxt[optOpcoes[1]], (int) optMode.getX() + optMode.getSize().width - strWidth - 10,
                        (int) (optMode.getY() + optMode.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth(salvarTxt);
                g.drawString(salvarTxt, (int) (salvarBtn.getX() + salvarBtn.getWidth() / 2 - strWidth / 2),
                        (int) (salvarBtn.getY() + salvarBtn.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth(optControlesTxt);
                g.drawString(optControlesTxt, (int) (optControles.getX() + optControles.getWidth() / 2 - strWidth / 2),
                        (int) (optControles.getY() + optControles.getHeight() / 2 + strHeight / 3));
            }

            if (currScreen == CONTROLES_IMG) {
                g.setColor(bgAreaColor);
                g2d.fill(optMenuArea);

                g.setColor(btnUnselected);
                g2d.fill(ctrlInnerArea);

                g2d.setColor(btnSelected);
                g2d.fill(voltarBtn);

                g2d.setColor(outlineColor);
                g2d.draw(voltarBtn);

                g2d.setColor(txtColor);
                strWidth = g.getFontMetrics(font).stringWidth(voltarTxt);
                strHeight = g.getFontMetrics(font).getHeight();
                g.drawString(voltarTxt, (int) (voltarBtn.getX() + voltarBtn.getWidth() / 2 - strWidth / 2),
                        (int) (voltarBtn.getY() + voltarBtn.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth("JOGADOR 1");
                g.drawString("JOGADOR 1", (int) (optResolution.getX()) + 10,
                        (int) (optResolution.getY() + optResolution.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth("Q, W");
                g.drawString("Q, W", (int) optResolution.getX() + optResolution.getSize().width - strWidth - 10,
                        (int) (optResolution.getY() + optResolution.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth("JOGADOR 2");
                g.drawString("JOGADOR 2", (int) (optMode.getX()) + 10,
                        (int) (optMode.getY() + optMode.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth("O, P");
                g.drawString("O, P", (int) optMode.getX() + optMode.getSize().width - strWidth - 10,
                        (int) (optMode.getY() + optMode.getHeight() / 2 + strHeight / 3));

                strWidth = g.getFontMetrics(font).stringWidth("ENCERRAR PARTIDA");
                g.drawString("SAIR DA PARTIDA", (int) (optSound.getX()) + 10,
                        (int) (optSound.getY() + optSound.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth("X");
                g.drawString("X", (int) optSound.getX() + optSound.getSize().width - strWidth - 10,
                        (int) (optSound.getY() + optSound.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth("PAUSAR");
                g.drawString("PAUSAR", (int) (optControles.getX() + 10),
                        (int) (optControles.getY() + optControles.getHeight() / 2 + strHeight / 3));
                strWidth = g.getFontMetrics(font).stringWidth("ENTER");
                g.drawString("ENTER", (int) optControles.getX() + optControles.getSize().width - strWidth - 10,
                        (int) (optControles.getY() + optControles.getHeight() / 2 + strHeight / 3));

            }

            g.setColor(txtColor);
            strHeight = g.getFontMetrics(font).getHeight();
            strWidth = g.getFontMetrics(font).stringWidth("FUTEBOL EXTREME DOIDERA");
            g.drawString("FUTEBOL EXTREME DOIDERA", (int) (getWidth() / 2 - strWidth / 2), (int) (50 + strHeight / 4));

            Toolkit.getDefaultToolkit().sync();
        }
    }

    // Muda botão selecionado
    void selectUp() {
        if (selected > JOGAR)
            selected--;
        des.repaint();
    }

    // Igual o acima, mas na direçao contrária
    void selectDown() {
        if (selected < SAIR)
            selected++;
        des.repaint();
    }

    void toggleOpt(int i) {
        if (optOpcoes[i] == 1) {
            optOpcoes[i] = 0;
            return;
        }
        optOpcoes[i] = 1;
    }

    void changeResolution(String opt) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        if (opt == "UP") {
            if (optOpcoes[0] < optResolutions.length - 1 && resX[optOpcoes[0] + 1] < width)
                optOpcoes[0]++;
            else
                optOpcoes[0] = 0;
        } else if (opt == "DOWN") {
            if (optOpcoes[0] == 0)
                while (optOpcoes[0] < optResolutions.length - 1 && resX[optOpcoes[0] + 1] < width)
                    optOpcoes[0]++;
            else
                optOpcoes[0]--;
        }
        // setSize(new Dimension(resX[optOpcoes[0]],resY[optOpcoes[0]]));
    }

    // Altera o botão ativo na tela de opções
    void selectOptMenu(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                if (selectedOpt != SALVAR && selectedOpt != CANCELAR)
                    selectedOpt++;
                break;
            case KeyEvent.VK_UP:
                if (selectedOpt == SALVAR)
                    selectedOpt = SOM;
                else if (selectedOpt != RESOLUCAO)
                    selectedOpt--;
                break;
            case KeyEvent.VK_RIGHT:
                if (selectedOpt == CANCELAR)
                    selectedOpt = SALVAR;
                else if (selectedOpt == SOM)
                    toggleOpt(2);
                else if (selectedOpt == RESOLUCAO)
                    changeResolution("UP");
                else if (selectedOpt == MODO)
                    toggleOpt(1);
                break;
            case KeyEvent.VK_LEFT:
                if (selectedOpt == SALVAR)
                    selectedOpt = CANCELAR;
                else if (selectedOpt == SOM)
                    toggleOpt(2);
                else if (selectedOpt == RESOLUCAO)
                    changeResolution("DOWN");
                else if (selectedOpt == MODO)
                    toggleOpt(1);
                break;
            case KeyEvent.VK_ENTER:
                if (selectedOpt == SOM)
                    toggleOpt(2);
                else if (selectedOpt == RESOLUCAO)
                    changeResolution("UP");
                else if (selectedOpt == MODO)
                    toggleOpt(1);
                else if (selectedOpt == CANCELAR) {
                    System.arraycopy(lastOpt, 0, optOpcoes, 0, lastOpt.length);
                    des.repaint();
                    currScreen = MENU_IMG;
                } else if (selectedOpt == CONTROLES)
                    currScreen = CONTROLES_IMG;
                else {
                    setSize(new Dimension(resX[optOpcoes[0]], resY[optOpcoes[0]]));
                    des.repaint();
                    currScreen = MENU_IMG;
                }
                break;
        }

        des.repaint();
    }

    void mouseClickedOptMenu(Point p) {
        if (des.optResolution.contains(p)) {
            changeResolution("UP");
        } else if (des.optMode.contains(p)) {
            toggleOpt(1);
        } else if (des.optSound.contains(p)) {
            toggleOpt(2);
        } else if (des.optControles.contains(p)) {
            currScreen = CONTROLES_IMG;
        } else if (des.cancelarBtn.contains(p)) {
            System.arraycopy(lastOpt, 0, optOpcoes, 0, lastOpt.length);
            des.repaint();
            currScreen = MENU_IMG;
        } else if (des.salvarBtn.contains(p)) {
            setSize(new Dimension(resX[optOpcoes[0]], resY[optOpcoes[0]]));
            des.repaint();
            currScreen = MENU_IMG;
        }

        des.repaint();
    }

    void mouseClickedCtrlMenu(Point p) {
        if (des.voltarBtn.contains(p)) {
            currScreen = OPT_IMG;
            des.repaint();
        }
    }

    void mouseMoveOptMenu(Point p) {
        if (des.cancelarBtn.contains(p))
            selectedOpt = CANCELAR;
        else if (des.salvarBtn.contains(p))
            selectedOpt = SALVAR;
        else if (des.optResolution.contains(p))
            selectedOpt = RESOLUCAO;
        else if (des.optSound.contains(p))
            selectedOpt = SOM;
        else if (des.optMode.contains(p))
            selectedOpt = MODO;
        else if (des.optControles.contains(p))
            selectedOpt = CONTROLES;
        des.repaint();
    }

    Menu(boolean inicia) {
        if (inicia) {
            dispose();
        }
    }

    Menu() {
        super("FUTEBOL EXTREME DOIDERA");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Lida com ações do teclado
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (currScreen == MENU_IMG) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_DOWN:
                            selectDown();
                            break;
                        case KeyEvent.VK_UP:
                            selectUp();
                            break;
                        case KeyEvent.VK_ENTER:
                            switch (selected) {
                                case JOGAR:
                                    menuAtivo = false;
                                    dispose();
                                    break;
                                case OPT:
                                    System.arraycopy(optOpcoes, 0, lastOpt, 0, optOpcoes.length);
                                    currScreen = OPT_IMG;
                                    des.repaint();
                                    break;
                                case SAIR:
                                    System.exit(0);
                            }
                    }
                } else if (currScreen == OPT_IMG) {
                    selectOptMenu(e);
                } else if (currScreen == CONTROLES_IMG) {
                    currScreen = OPT_IMG;
                    des.repaint();
                }
            }
        });

        // Verifica se o clique do mouse está contido em algum botao
        // e executa a ação correspondente
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                // Por algum mistério o "contain" dos botões está
                // com 30px de diferença do ponto retornado, a função
                // abaixo corrige isso.
                p.translate(0, -30);
                // TODO: MUDAR PARA SWITCH CASE
                if (currScreen == MENU_IMG) {
                    if (des.jogarBtn.contains(p)) {
                        menuAtivo = false;
                        dispose();
                        // iniciar jogo
                    } else if (des.optBtn.contains(p)) {
                        // tela de opções
                        currScreen = OPT_IMG;
                        des.repaint();
                    } else if (des.sairBtn.contains(p))
                        System.exit(0);
                } else if (currScreen == OPT_IMG) {
                    mouseClickedOptMenu(p);
                } else if (currScreen == CONTROLES_IMG) {
                    mouseClickedCtrlMenu(p);
                }
            }
        });

        // Altera botão ativo conforme a posição do mouse
        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                p.translate(0, -30);
                if (currScreen == MENU_IMG) {
                    if (des.jogarBtn.contains(p)) {
                        selected = JOGAR;
                        des.repaint();
                    } else if (des.optBtn.contains(p)) {
                        selected = OPT;
                        des.repaint();
                    } else if (des.sairBtn.contains(p)) {
                        selected = SAIR;
                        des.repaint();
                    }
                } else if (currScreen == OPT_IMG) {
                    mouseMoveOptMenu(p);
                }
            }
        });
        des.repaint();
        t = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // System.out.println(selected);
                // selectDown();
                // selectUp();
            }
        });
        t.start();

        setResizable(false);
        add(des);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    static public void main(String[] args) {
        Menu f = new Menu();

    }
}