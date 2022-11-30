/*
  Nome: Aluno 1;
  Nome: Aluno 2;
*/


import java.lang.Math;
import java.util.Arrays;
public class CacheController {
    private static final int k_way = 2;
    int tagSize;
    int numberOfLines;
    int offsetSize;
    int setSize;
    int addressMask;
    int tagMask;
    int setMask;
    int offsetMask;
    int[] nextLine;
    int numberOfSets;
    MainMemory mainMemory;
    Cache cache;
    TagDirectory tagDirectory;

    public CacheController(MainMemory mainMemory, Cache cache, TagDirectory tagDirectory) {
        this.mainMemory = mainMemory;
        this.cache = cache;
        this.tagDirectory = tagDirectory;

        /*
            k_way: É uma constante que contém o número de linhas em cada conjunto;
            tagSize: Número de bits para indexar tag;
            setSize: Número de bits para indexar conjunto;
            offsetSize: Número de bits para indexar offset;
            numberOfLines: Número de linhas na Cache;
            numberOfSets: Número de conjuntos na Cache;
            nextLine: controle FIFO da próxima inserção na cache (1 valor por conjunto da cache);

             Obs: Utilize os métodos mainMemory.getSize(), cache.getSize() e mainMemory.getBlockSize() para
                  obter os tamanhos da memória principal, cache e do offset respectivamente
        */

        this.offsetSize = calcLog2(mainMemory.getBlockSize());
        this.numberOfLines = cache.getSize()/mainMemory.getBlockSize();
        this.numberOfSets = cache.getSize()/k_way*mainMemory.getBlockSize();
        this.tagSize = calcLog2(mainMemory.getSize()/numberOfSets*mainMemory.getBlockSize());
        this.setSize = calcLog2(numberOfSets);
        this.nextLine = new int[this.numberOfSets];
        /*
            Nessa parte, você irá chamar os métodos abaixo para inicializar as máscaras de bits apropriadas.
            Essas máscaras serão úteis para obter os bits referentes às tags, linhas,  offsets e endereço
            propriamente dito.
        */

        this.setTagMask();
        this.setSetMask();
        this.setOffsetMask();
        this.setAddressMask();

    }

    public int calcLog2(int value) {
        /*
            Calcula o logarítmo na base 2 de valor e retorna o valor inteiro apropriado
        */
        return (int) (Math.log(value) / Math.log(2));
    }

    public int getTag(int address) {
        /*
           Obtém o valor do inteiro obtido dos bits da tag
        */
        address = address & tagMask;

        return address >> this.offsetSize+this.setSize;
    }

    public int getSet(int address) {
        /*
           Obtém o valor do inteiro obtido dos bits de offset
        */
        address = address & setMask;

        return address >> this.offsetSize;
    }

    public int getOffset(int address) {
        /*
           Obtém o valor do inteiro obtido dos bits de offset
        */
        address = address & offsetMask;

        return address;
    }

    public void setTagMask() {
        /*
           Calcula a máscara de bits da tag.
           E.g. tamanho do endereço 6 bits, tamanho da tag 2 bits:
           máscara de tag: 110000
        */
        int mask = 0;
        for(int i =0 ; i < this.tagSize; i++){
            mask <<= 1;
            mask++;
        }

        this.tagMask = mask << this.offsetSize+this.setSize;
    }

    public void setSetMask() {
        /*
           Calcula a máscara de bits do conjunto (setMask).
           E.g. tamanho do endereço 6 bits, tamanho do offset 2 bits e conjunto 1 bit:
           máscara de conjunto: 000100
        */
        int mask = 0;
        for(int i =0 ; i < this.setSize; i++){
            mask <<= 1;
            mask++;
        }

        this.tagMask = mask << this.offsetSize;
    }

    public void setOffsetMask() {
        /*
           Calcula a máscara de bits do offset.
           E.g. tamanho do endereço 6 bits, tamanho do offset 2 bits:
           máscara de tag: 000011
        */
        int mask = 0;
        for(int i =0 ; i < this.offsetSize; i++){
            mask <<= 1;
            mask++;
        }

        this.offsetMask = mask;
    }

    public void setAddressMask() {
        /*
           Calcula a máscara de bits de endereço.
           E.g. Estamos utilizando int (32 bits), tamanho do endereço 6 bits:
           máscara de endereço: 000000000000000000000000111111
        */
        int mask = 0;
        for(int i =0 ; i < 6; i++){
            mask <<= 1;
            mask++;
        }

        this.addressMask = mask;
    }

    public String getData(int address) {
        int tag = getTag(address);
        int set = getSet(address);
        System.out.println(String.format("Address %s, tag %s, set %s",
                                         Integer.toBinaryString(address), Integer.toBinaryString(tag),
                                         Integer.toBinaryString(set)));
        /*
            setLine contém o índice da linha do Diretório de Tags na qual devo comparar com a
            tag encontrada no endereço solicitado. Como dentro de cada conjunto, o mapeamento
            funciona como se fosse associativo, devemos iterar nas linhas do conjunto.
            Assim, para cada linha (line) de um conjunto, devemos fazer a concatenação:
            setLine = set|Line.
        */
        int setLine;
        for (int line = 0; line < k_way; line++) {
            setLine = set|line;
            if (this.tagDirectory.getTag(setLine) == tag) {
                System.out.println("Tag Hit!");
                System.out.println(String.format("Tag directory [%s|%s == %s]:%d == tag:%d",Integer.toBinaryString(set), Integer.toBinaryString(line),setLine, this.tagDirectory.getTag(setLine), tag));
                System.out.println(String.format("Cache line %d: %s", setLine, Arrays.toString(cache.getLine(setLine))));
                return cache.getLine(setLine)[getOffset(address)];
            }
        }
        System.out.println("Tag Miss!");
        System.out.println(String.format("Set %d", set));
        /*
          Nessa parte, como a tag não foi encontrada no dirertório de tags, você deve
          copiar os valores do bloco apropriado da memória principal para a linha correta da
          memória cache (MAPEAMENTO ASSOCIATIVO POR CONJUNTOS!!!) e atualizar o diretório de tags.
          Lembre-se de utilizar a variável nextLine para controlar a substituição com
          política FIFO. A variável nextLine é um array de índices, um por conjunto, que a linha de um
          conjunto que será substituida.
        */
        
        int setTagDirectory = tag % numberOfSets;
        setLine = set|nextLine[setTagDirectory];
        tagDirectory.setTag(setLine, tag);
        
        int adressBase = tag<<offsetSize+setSize;
        for(int i = 0; i < mainMemory.getBlockSize(); i++){
            cache.setCache(setLine, getOffset(adressBase+i), mainMemory.getData(adressBase+i));
        }

        nextLine[setTagDirectory]++;
        nextLine[setTagDirectory] %= k_way;


        System.out.println(String.format("Mapping address %d (and neighbors)...", address));
        System.out.println(String.format("New cache line %d: %s", setLine, Arrays.toString(cache.getLine(setLine))));
        return cache.getLine(setLine)[getOffset(address)];
    }
}
