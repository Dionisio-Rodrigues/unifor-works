public class Cache {
    int lines;
    int size;
    String cache[][];

    public Cache(int size, int blockSize) {
        this.size = size;
        this.lines = size / blockSize;
        this.cache = new String[this.lines][blockSize];
    }

    public String[] getLine(int line) {
        return this.cache[line];
    }

    public int getSize() {
        return this.size;
    }

    public void setCache(int line, int offset, String value) {
        this.cache[line][offset] = value;
    }
}