public class MainMemory {
    int size;
    int blockSize;
    String memory[];

    public MainMemory(int size) {
        this.size = size;
        this.memory = new String[size];

    }
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void fillMemory() {
        for (int i = 0; i < this.memory.length; i++) {
            this.memory[i] = Integer.toHexString(i);
        }
    }
    public int getSize() {
        return size;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public String getData(int address) {
        return this.memory[address];
    }
}