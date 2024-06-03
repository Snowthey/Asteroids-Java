import java.awt.*;

public class Tiro {
    private int x;
    private int y;
    private double velocidadeX;
    private double velocidadeY;
    private static final double VELOCIDADE_TIRO = 8.0; 
    public static int largura;
    public static int altura;
    private Color cor;

    public Tiro(int x, int y, double anguloRadianos) {
        this.x = x;
        this.y = y;
        this.velocidadeX = Math.sin(anguloRadianos) * VELOCIDADE_TIRO; 
        this.velocidadeY = -Math.cos(anguloRadianos) * VELOCIDADE_TIRO; 
        this.largura = 3;
        this.altura = 10;
        this.cor = Color.RED;
    }

    public void mover() {
        x += velocidadeX;
        y += velocidadeY;
    }

    public void desenhar(Graphics g) {
        g.setColor(cor);
        g.fillRect(x, y, largura, altura);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
