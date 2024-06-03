import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Nave {
    private int x;
    private int y;
    private double velocidadeX, velocidadeY;
    private int angulo; 
    private int altura;
    private int largura;
    private static final double ACELERACAO = 0.8;
    private static final double DESACELERACAO = 0.10;
    private static final double VELOCIDADE_MAXIMA = 8.0;
    private static final int ANGULO_DE_GIRO = 10;
    private BufferedImage imagem;
    private boolean exploding = false;
    private boolean explosionPlayed = false;
    private int explosionFrame = 0;
    private int explosionFrames[] = {0, 1, 2, 3, 4, 5}; 
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
            if (currentTime - explosionStartTime > 50) { 
                explosionStartTime = currentTime;
                explosionFrame = (explosionFrame + 1) % explosionFrames.length;
                if (explosionFrame == 0) {
                    exploding = false;
                    explosionPlayed = false;
                }
            }
            return;
        }

        
        x += velocidadeX;
        y += velocidadeY;

        
        if (x < 0) x = 0;
        if (x + largura > Jogo.LARGURA_TELA) x = Jogo.LARGURA_TELA - largura;
        if (y < 0) y = 0;
        if (y + altura > Jogo.ALTURA_TELA) y = Jogo.ALTURA_TELA - altura;

        
        if (!acelerando) {
            velocidadeX *= (1 - DESACELERACAO);
            velocidadeY *= (1 - DESACELERACAO);
        }
    }

    public void acelerar() {
        double anguloRadianos = Math.toRadians(angulo);
        double aceleracaoX = Math.sin(anguloRadianos) * ACELERACAO; 
        double aceleracaoY = -Math.cos(anguloRadianos) * ACELERACAO; 

        
        if (Math.sqrt(velocidadeX * velocidadeX + velocidadeY * velocidadeY) < VELOCIDADE_MAXIMA) {
            velocidadeX += aceleracaoX;
            velocidadeY += aceleracaoY;
        }

        acelerando = true;
    }

    public void desacelerar() {
        acelerando = false;
    }

    public void girarDireita() {
        angulo += ANGULO_DE_GIRO;
    }

    public void girarEsquerda() {
        angulo -= ANGULO_DE_GIRO;
    }

    public void draw(Graphics2D g2d) {
        AffineTransform transform = new AffineTransform();
        transform.translate(x + largura / 2, y + altura / 2);
        transform.rotate(Math.toRadians(angulo));
        transform.translate(-largura / 2, -altura / 2);
        g2d.drawImage(imagem, transform, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLargura(){
        return largura;
    }

    public int getAltura(){
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
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-15.0f);
                clip.start();
                soundPlayed = true;
            }
        }
        return distancia <= this.largura / 2 + Asteroids.LARGURA_ASTEROIDE / 2;
    }

}
