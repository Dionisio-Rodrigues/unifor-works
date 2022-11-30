import java.util.Random;
public class CPU {
    int blockSize;
    int PC;
    String EAX;
    int size;

    public CPU(int size, int blockSize) {
        this.size = size;
        this.blockSize = blockSize;
    }

    public void setPC() {
        Random rdm = new Random();
        this.PC = rdm.nextInt(this.size - 1);
    }

    public void setEAX(String val) { this.EAX = val; }
    public int getPC() {
        return this.PC;
    }
    public String getEAX() { return EAX; }
}