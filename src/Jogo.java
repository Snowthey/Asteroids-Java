import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class Jogo extends JPanel implements Runnable {

    public static final int LARGURA_TELA = 1300;
    public static final int ALTURA_TELA = 750;
    private static final int TEMPO_TOTAL = 60;
    private int combustivel = TEMPO_TOTAL;
    private int contadorTempo;
    private JProgressBar barraCombustivel;
    public static Semaphore Mutex;
    private int score;
    public static final int NUMERO_ASTEROIDES = 10;
    private ArrayList<Asteroids> asteroides;
    public static final String NOME_FONTE = "Ink Free";
    public boolean GameOver = false;
    private Nave objNave;
    private Asteroids objAsteroids;
    private boolean jogoAtivo = true;
    private ArrayList<Tiro> tiros;

    public Jogo() {
        asteroides = new ArrayList<>();
        setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        setBackground(Color.BLACK);
        setFocusable(true);
        String caminhoImagem = "/res/teste.png"; // O caminho deve começar com "/"
        objNave = new Nave(LARGURA_TELA / 2, ALTURA_TELA / 2, 50, 50, caminhoImagem);
        objAsteroids = new Asteroids();
        addKeyListener(new InterrupcaoTeclado(objNave));
        score = 0;
        adicionarMeteoros();
        GameOver = false;
        Mutex = new Semaphore(1);
        tiros = new ArrayList<>();
        contadorTempo = TEMPO_TOTAL;
        Recursos.carregarRecursos();  // Pré-carregue todos os recursos

        JButton reiniciarJogoButton = new JButton("Reiniciar Jogo");
        reiniciarJogoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarJogo();
            }
        });
        add(reiniciarJogoButton);

        barraCombustivel = new JProgressBar();
        barraCombustivel.setMinimum(0);
        barraCombustivel.setMaximum(TEMPO_TOTAL);
        barraCombustivel.setValue(TEMPO_TOTAL);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel rotuloCombustivel = new JLabel("Combustível");
        rotuloCombustivel.setForeground(Color.WHITE);

        add(rotuloCombustivel);
        add(barraCombustivel);

        criarAsteroides();
        new Thread(this).start();
        new Thread(() -> {
            while (jogoAtivo) {
                try {
                    dispararTiro();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (combustivel > 0) {
                try {
                    Thread.sleep(1000);
                    combustivel--;
                    contadorTempo--;
                    barraCombustivel.setValue(combustivel);
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            GameOver = true;
        }).start();

    }

    private void reiniciarJogo() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.dispose();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Jogo novoJogo = new Jogo();
                JFrame newFrame = new JFrame("Asteroids");
                newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                newFrame.getContentPane().add(novoJogo);
                newFrame.pack();
                newFrame.setLocationRelativeTo(null);
                newFrame.setVisible(true);

                new Thread(novoJogo).start();
            }
        });
    }


    private void tocarSomTiro() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/tiro.wav"));
        clip.open(inputStream);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-10.0f);
        clip.start();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        try {
            desenharTela(g);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        if (!tiros.isEmpty()) {
            ArrayList<Tiro> tirosCopy = new ArrayList<>(tiros);
            for (Tiro tiro : tirosCopy) {
                tiro.desenhar(g);
            }
        }

        objNave.draw((Graphics2D) g);


        ArrayList<Asteroids> asteroidesCopy = new ArrayList<>(asteroides);
        for (Asteroids asteroide : asteroidesCopy) {
            asteroide.Desenhar(g);
        }
    }


    public void desenharTela(Graphics g) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        if (!GameOver) {
            g.setColor(Color.white);
            g.setFont(new Font(NOME_FONTE, Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String texto = "Pontos: " + score;
            g.drawString(texto, (LARGURA_TELA - metrics.stringWidth(texto)) / 2, g.getFont().getSize());
            g.setColor(Color.GREEN);
        } else {
            fimDeJogo(g);
        }
    }

    private int calcularPontuacao() {
        return score;
    }

    public void fimDeJogo(Graphics g) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 75));
        FontMetrics fonteFinal = getFontMetrics(g.getFont());
        g.drawString("Fim do Jogo", (LARGURA_TELA - fonteFinal.stringWidth("Fim de Jogo")) / 2, ALTURA_TELA / 2);
        g.drawString("Pontuação: " + calcularPontuacao(), (LARGURA_TELA - fonteFinal.stringWidth("Fim de Jogo")) / 2, ALTURA_TELA / 4);
        jogoAtivo = false;

    }

    public void run() {
        while (true) {
            for (Asteroids asteroide : asteroides) {
                asteroide.atualizarPosicao();
            }

            try {
                verificarColisoes();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }

            moverTiros();
            atualizar();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void atualizar() {
        objNave.update();
        objAsteroids.Atualizar();
    }

    private void criarAsteroides() {
        int numeroMaximoAsteroides = Math.min(NUMERO_ASTEROIDES, 10);
        for (int i = 0; i < numeroMaximoAsteroides; i++) {
            Asteroids asteroide = new Asteroids();
            asteroide.criarNovoAsteroide();
            asteroides.add(asteroide);
        }
    }

    private void adicionarMeteoros() {
        while (asteroides.size() < 15) {
            Asteroids asteroide = new Asteroids();
            asteroide.criarNovoAsteroide();
            asteroides.add(asteroide);
        }
    }

    public void dispararTiro() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        int tiroX = objNave.getX() + objNave.getLargura() / 2 - Tiro.largura / 2;
        int tiroY = objNave.getY() - Tiro.altura;
        double anguloRadianos = Math.toRadians(objNave.getAngulo());
        Tiro tiro = new Tiro(tiroX, tiroY, anguloRadianos);
        tiros.add(tiro);
        tocarSomTiro();
    }

    private void moverTiros() {
        ArrayList<Tiro> tirosParaRemover = new ArrayList<>();

        for (Tiro tiro : tiros) {
            tiro.mover();


            if (tiro.getY() < 0 || tiro.getY() > ALTURA_TELA && tiro.getX() < 0 || tiro.getX() > LARGURA_TELA) {
                tirosParaRemover.add(tiro);
            }
        }

        tiros.remove(tirosParaRemover);
    }

    private void verificarColisoes() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        ArrayList<Asteroids> asteroidesCopia = new ArrayList<>(asteroides);
        for (Asteroids asteroide : asteroidesCopia) {
            if (objNave.colidiuComAsteroide(asteroide)) {
                GameOver = true;
                break;
            }
            synchronized (asteroides) {
                Iterator<Tiro> iteratorTiros = tiros.iterator();
                while (iteratorTiros.hasNext()) {
                    Tiro tiro = iteratorTiros.next();
                    if (asteroide.Colisao(tiro.getX(), tiro.getY())) {
                        iteratorTiros.remove();
                        asteroides.remove(asteroide);
                        incrementarPontuacao();
                        break;
                    }
                }
            }
        }

        if (asteroides.size() < 5) {
            adicionarMeteoros();
        }
    }

    private void incrementarPontuacao() {
        score += 10;
    }
}
