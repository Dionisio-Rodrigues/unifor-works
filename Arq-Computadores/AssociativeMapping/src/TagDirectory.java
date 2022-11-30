public class TagDirectory {
    int lines;
    int directory[];
    public TagDirectory() {}

    public void setLines(int lines) {
        this.lines = lines;
        this.directory = new int[lines];
        for (int i = 0; i < this.directory.length; i++) {
            this.directory[i] = -1;
        }
    }

    public int getTag(int line) {
        return directory[line];
    }

    public void setTag(int line, int tag) {
        this.directory[line] = tag;
    }
}