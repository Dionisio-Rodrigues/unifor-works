import java.lang.Math;

public class Main {
    public static void main(String[] args) {
        int memorySize = (int) Math.pow(2, 16);
        int cacheSize = (int) Math.pow(2, 10);
        int blockSize = (int) Math.pow(2, 3);
        CPU cpu = new CPU(memorySize, blockSize);
        MainMemory mainMemory = new MainMemory(memorySize);
        mainMemory.setBlockSize(blockSize);
        mainMemory.fillMemory();
        Cache cache = new Cache(cacheSize, blockSize);
        TagDirectory tagDirectory = new TagDirectory();
        tagDirectory.setLines(cache.getSize() / mainMemory.getBlockSize());
        CacheController cacheController = new CacheController(mainMemory, cache, tagDirectory);
        for (int i=0; i < 100; i++) {
            cpu.setPC();
            cpu.setEAX(cacheController.getData(cpu.getPC()));
            System.out.println(String.format("EAX value: %s", cpu.getEAX()));
        }
    }
}