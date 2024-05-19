import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;

public class Nave {
    private static int x;
    private static int y;
    private int velocidadeX, velocidadeY;
    private int angulo;
    private static int altura;
    private static int largura;
    private static final double VELOCIDADE = 2.0; // Ajuste conforme necessário
    private static final double ACELERACAO = 0.5; // Ajuste conforme necessário
    private static final double DESACELERACAO = 1; // Ajuste conforme necessário
    private static final double VELOCIDADE_MAXIMA = 10.0;
    private static final int ANGULO_DE_GIRO = 10;
    private BufferedImage imagem;
    private boolean exploding = false;
    private boolean explosionPlayed = false;
    private int explosionFrame = 0;
    private int explosionFrames[] = {0, 1, 2, 3, 4, 5}; // assuming you have 6 explosion frames
    private long explosionStartTime = 0;
    private boolean acelerando = false;



    public Nave(int x, int y, int largura, int altura, String imagePath) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.velocidadeX = 0;
        this.velocidadeY = 0;
        this.angulo = 0;
        this.exploding = false;

        try {
            BufferedImage imagemOriginal = ImageIO.read(new File(imagePath));
            this.imagem = redimensionarImagem(imagemOriginal, largura, altura);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (exploding) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - explosionStartTime > 50) { // 50ms per frame
                explosionStartTime = currentTime;
                explosionFrame = (explosionFrame + 1) % explosionFrames.length;
                if (explosionFrame == 0) {
                    exploding = false;
                    explosionPlayed = false;
                }
            }
        }
        // Mantém a direção (ângulo) constante enquanto se move
        double anguloRadianos = Math.toRadians(angulo);
        double velocidadeX = Math.cos(anguloRadianos) * VELOCIDADE;
        double velocidadeY = Math.sin(anguloRadianos) * VELOCIDADE;

        // Atualiza a posição da nave com base na velocidade
        x += velocidadeX;
        y += velocidadeY;

        if (x < 0) x = 0;
        if (x + largura > Jogo.LARGURA_TELA) x = Jogo.LARGURA_TELA - largura;
        if (y < 0) y = 0;
        if (y + altura > Jogo.ALTURA_TELA) y = Jogo.ALTURA_TELA - altura;

    }


    public void acelerar() {
        double anguloRadianos = Math.toRadians(angulo);
        double aceleracaoX = Math.cos(anguloRadianos) * ACELERACAO;
        double aceleracaoY = Math.sin(anguloRadianos) * ACELERACAO;

        // Limita a velocidade máxima
        if (Math.sqrt(velocidadeX * velocidadeX + velocidadeY * velocidadeY) < VELOCIDADE_MAXIMA) {
            velocidadeX += aceleracaoX;
            velocidadeY += aceleracaoY;
        }

    }

    public void desacelerar() {
        double anguloRadianos = Math.toRadians(angulo);
        double desaceleracaoX = Math.cos(anguloRadianos) * DESACELERACAO;
        double desaceleracaoY = Math.sin(anguloRadianos) * DESACELERACAO;

        // Reduz a velocidade gradualmente
        velocidadeX -= desaceleracaoX;
        velocidadeY -= desaceleracaoY;
    }

    public void girarDireita() {
        angulo += ANGULO_DE_GIRO;
    }

    public void girarEsquerda() {
        angulo -= ANGULO_DE_GIRO;
    }

    public void draw(Graphics2D g2d) {
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(Math.toRadians(angulo), largura / 2, altura / 2); // Rotaciona em torno do centro da imagem
        g2d.drawImage(imagem, transform, null);
    }

    public static int getX() {
        return x;
    }

    public static int getY() {
        return y;
    }

    public static int getLargura(){
        return largura;
    }

    public static int getAltura(){
        return altura;
    }

    public int getAngulo() {
        return angulo;
    }



    private BufferedImage redimensionarImagem(BufferedImage imagemOriginal, int largura, int altura) {
        BufferedImage imagemRedimensionada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagemRedimensionada.createGraphics();
        g2d.drawImage(imagemOriginal.getScaledInstance(largura, altura, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();
        return imagemRedimensionada;
    }

    private boolean soundPlayed = false;

    public boolean colidiuComAsteroide(Asteroids asteroide) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        int naveX = this.x + this.largura / 2;
        int naveY = this.y + this.altura / 2;
        int asteroideX = asteroide.getX() + Asteroids.LARGURA_ASTEROIDE / 2;
        int asteroideY = asteroide.getY() + Asteroids.ALTURA_ASTEROIDE / 2;
        int distancia = (int) Math.sqrt(Math.pow(naveX - asteroideX, 2) + Math.pow(naveY - asteroideY, 2));
        if (distancia <= this.largura / 2 + Asteroids.LARGURA_ASTEROIDE / 2) {
            exploding = true;
            if (!soundPlayed) {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/grito.wav"));
                clip.open(inputStream);
                clip.start();
                soundPlayed = true;
            }
        }
        return distancia <= this.largura / 2 + Asteroids.LARGURA_ASTEROIDE / 2;
    }

}