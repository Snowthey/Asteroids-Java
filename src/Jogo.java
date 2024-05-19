import javax.swing.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Jogo extends JPanel implements Runnable{

    public static final int LARGURA_TELA = 1300;
    public static final int ALTURA_TELA = 750;
    private static final int TEMPO_TOTAL = 60; // Tempo total em segundos
    private int combustivel = TEMPO_TOTAL;
    private static final double VELOCIDADE_TIRO = 5.0;
    private int contadorTempo;
    private JProgressBar barraCombustivel;
    public static Semaphore Mutex;
    private int score; // Adicione a variável score
    public static final int INTERVALO = 200;
    public static final int NUMERO_ASTEROIDES = 5; // Número de asteroides a serem criados
    private ArrayList<Asteroids> asteroides; // Lista para armazenar os asteroides
    public static final String NOME_FONTE = "Ink Free";
    public boolean GameOver = false;
    Nave objNave;
    Asteroids objAsteroids;
    Tiro objTiro;
    private boolean jogoAtivo = true;
    private ArrayList<Tiro> tiros; // Array para armazenar os tiros

    public Jogo(){
        asteroides = new ArrayList<>();
        setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        setBackground(Color.BLACK);
        setFocusable(true);
        objNave = new Nave(LARGURA_TELA / 2, ALTURA_TELA / 2, 50, 50, "C:/Users/sherl/IdeaProjects/Asteroids/src/teste.png");
        objAsteroids = new Asteroids();
//        objAsteroids.CriarNovaPosicao();
        addKeyListener(new InterrupcaoTeclado(objNave));
        score = 0;
        adicionarMeteoros();
        GameOver = false;
        Mutex = new Semaphore(1);
        tiros = new ArrayList<>();
        contadorTempo = TEMPO_TOTAL;

        barraCombustivel = new JProgressBar();
        barraCombustivel.setMinimum(0); // Valor mínimo da barra de progresso
        barraCombustivel.setMaximum(TEMPO_TOTAL); // Valor máximo da barra de progresso (tempo total)
        barraCombustivel.setValue(TEMPO_TOTAL); // Valor inicial da barra de progresso (tempo total)
        setLayout(new FlowLayout(FlowLayout.LEFT)); //
        JLabel rotuloCombustivel = new JLabel("Combustível");
        rotuloCombustivel.setForeground(Color.WHITE);

        add(rotuloCombustivel);

        // Adicione a barra de combustível ao painel
        add(barraCombustivel);

        criarAsteroides();
        new Thread(this).start();
        new Thread(() -> {
            while (jogoAtivo) {
                try {
                    dispararTiro();
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (combustivel > 0) {
                try {
                    Thread.sleep(1000); // Espera 1 segundo
                    combustivel--; // Decrementa o combustível
                    contadorTempo--; // Atualiza o contador de tempo
                    barraCombustivel.setValue(combustivel); // Atualiza a barra de progresso
                    repaint(); // Redesenha a tela para atualizar a barra de combustível
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Quando o combustível acabar, defina o GameOver como verdadeiro
            GameOver = true;
            // Exiba a pontuação total do jogador

        }).start();


    }

    private void tocarSomTiro() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/tiro.wav"));
        clip.open(inputStream);
        clip.start();
    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
            try{
                desenharTela(g);
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e){
                e.printStackTrace();
            }

            if (!tiros.isEmpty()) {
                for (Tiro tiro : tiros) {
                    tiro.desenhar(g);
                }
            }

            objNave.draw((Graphics2D) g);
            for (Asteroids asteroide : asteroides) {
                asteroide.Desenhar(g); // Chama o método para desenhar cada asteroide
            }


    }

    public void desenharTela(Graphics g) throws LineUnavailableException, IOException, UnsupportedAudioFileException{
        if(GameOver == false){
            g.setColor(Color.white);
            g.setFont(new Font(NOME_FONTE, Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String texto = "Pontos: " + score;
            g.drawString(texto, (LARGURA_TELA - metrics.stringWidth(texto)) / 2, g.getFont().getSize());
            g.setColor(Color.GREEN);

        } else{
            fimDeJogo(g);
        }
    }

    private int calcularPontuacao() {
        return score;
    }

    public void fimDeJogo(Graphics g) throws LineUnavailableException, IOException, UnsupportedAudioFileException{
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 75));
        FontMetrics fonteFinal = getFontMetrics(g.getFont());
        g.drawString("Fim do Jogo. \n ", (LARGURA_TELA - fonteFinal.stringWidth("Fim de Jogo")) / 2, ALTURA_TELA / 2);
        g.drawString("Pontuação:" + calcularPontuacao(), (LARGURA_TELA - fonteFinal.stringWidth("Fim de Jogo")) / 2, ALTURA_TELA / 4);

        jogoAtivo = false;

    }


    public void run() {
        while (true) {
            for (Asteroids asteroide : asteroides) {
                asteroide.atualizarPosicao();
            }

            try {
                verificarColisoes(); // Verificar colisões antes de atualizar a tela
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
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


    }

    private void criarAsteroides() {
        int numeroMaximoAsteroides = Math.min(NUMERO_ASTEROIDES, 5); // Define o máximo de 5 asteroides
        for (int i = 0; i < numeroMaximoAsteroides; i++) {
            Asteroids asteroide = new Asteroids();
            asteroide.criarNovoAsteroide(); // Define uma nova posição aleatória para o asteroide
            asteroides.add(asteroide);
        }
    }

    private void adicionarMeteoros() {
        while (asteroides.size() < 5) {
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
        tocarSomTiro(); // Toca o som de tiro

    }


    private void moverTiros() {
        ArrayList<Tiro> tirosParaRemover = new ArrayList<>();

        for (Tiro tiro : tiros) {
            tiro.mover();

            // Verifica se o tiro ultrapassou os limites da tela
            if (tiro.getY() < 0 || tiro.getY() > ALTURA_TELA) {
                tirosParaRemover.add(tiro);
            }
        }

        tiros.removeAll(tirosParaRemover);
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
                        incrementarPontuacao(); // Incrementa a pontuação quando um meteoro é destruído (ajuste conforme necessário)
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
        score += 10; // Incrementa a pontuação em 10 unidades a cada meteoro destruído (ajuste conforme necessário)
    }

}
