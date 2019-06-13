import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.Vector;

/* developed by YUSUF FAWZY
 * Grey Scale Version
 * December - 2013
 * */
public class VectorQuantization {
    static int oldH, oldW, NEWidth, NEWoldH;
    static int BlockH, BlockW, codeBlockLength;
    static String path = "C:\\Users\\Yusuf\\Desktop\\017.jpg";

    static int[][] CreateSquared(int[][] image) {
        if ((NEWoldH = oldH) % BlockH != 0) NEWoldH = ((oldH / BlockH) + 1) * BlockH;
        if ((NEWidth = oldW) % BlockW != 0) NEWidth = ((oldW / BlockW) + 1) * BlockW;

        //Scale by adding padding
        int col, wid;
        int[][] SquaredImage = new int[NEWoldH][NEWidth];
        for (int i = 0; i < NEWoldH; i++) {
            col = i;
            //bn2ol in al oldH al gded da tolo akbr mn bta3y al 2adeem... so ana hazwd 3aleh mn al2dem fa bagyb mn al5ana ally ablya w akrrha
            //instead of adding new oldH and its value is zero, so padding by the prev result of the same row ... so change the rwo

            for (int j = 0; j < NEWidth; j++) {
                wid = j;

                if (i+1>oldH || j+1>oldW){
                    SquaredImage[i][j] = 0;
                }
               else  SquaredImage[i][j] = image[col][wid];
            }
        }
        return SquaredImage;
    }

    static Vector <Vector <Integer>> CreateBlocks(int[][] SquaredImage) {
        Vector <Vector <Integer>> Vectors = new Vector <>();
        //han2sem al array as the 4x4 or any size of the given block
        //we are sure that the matrix height is factor of the block height.
        for (int i = 0; i < NEWoldH; i += BlockH)
            for (int j = 0; j < NEWidth; j += BlockW) {
                var v = new Vector();
                for (int x = i; x < i + BlockH; x++)
                    for (int y = j; y < j + BlockW; y++)
                        v.add(SquaredImage[x][y]); //add a value to the last element, means to this vector
                Vectors.add(v);
            }
        return Vectors;
    }

    static void WriteToFile(Vector <Vector <Integer>> QBlocks, Vector <Integer> VectorsIndices) throws IOException {

        //this text file that will be used in decompression
        var HelperFile = new ObjectOutputStream(new FileOutputStream(path.substring(0, path.lastIndexOf('.')) + ".txt"));
        HelperFile.writeInt(oldW);
        HelperFile.writeInt(oldH);
        HelperFile.writeInt(NEWidth);
        HelperFile.writeInt(NEWoldH);
        HelperFile.writeInt(BlockW);
        HelperFile.writeInt(BlockH);
        HelperFile.writeObject(VectorsIndices);
        HelperFile.writeObject(QBlocks);
        HelperFile.close();
    }

    static Vector <Integer> getMean(Vector <Vector <Integer>> Vectors) {
        var cum = new int[Vectors.elementAt(0).size()]; //size = block elements
        var nOfBlocks = Vectors.size();
        var res = new Vector <Integer>();
        for (var block : Vectors)
            for (int i = 0; i < block.size(); i++)
                cum[i] += block.elementAt(i);
        for (int i = 0; i < cum.length; i++)
            res.add(cum[i] / nOfBlocks);
        return res;
    }

    static int minDistance(Vector <Integer> x, Vector <Integer> y, int PlusOrMinus) {
        int dist = 0;
        for (int i = 0; i < x.size(); i++)
            //working with the min distance as |x-y +- 1|
            dist += Math.abs(x.get(i) - y.get(i) + PlusOrMinus);
        return dist;
    }

    private static Vector <Integer> rearrange(Vector <Vector <Integer>> Vectors, Vector <Vector <Integer>> QBlocks) {
        Vector <Integer> res = new Vector <>();
        for (Vector <Integer> vector : Vectors) {
            int min = 1000000, idx = -1, temp;
            //choose the suitable block based on the min distance
            for (int i = 1; i < QBlocks.size(); i++) {
                if ((temp = minDistance(vector, QBlocks.get(i), 0)) < min) {
                    min = temp;
                    idx = i;
                }
            }
            res.add(idx);
        }
        return res;
    }

    static Vector <Integer> Quantize(int L, Vector <Vector <Integer>> Vectors, Vector <Vector <Integer>> QBlocks) {
        if (L == 1) {
            if (Vectors.size() > 0)
                QBlocks.add(getMean(Vectors));
            return rearrange(Vectors, QBlocks);
        }
        //here we will have a left and right ones, and add them by the least distance
        Vector <Vector <Integer>> Lefts = new Vector(), Rights = new Vector();
        //Calculate Average Vector that we will compare with each time we call the function
        Vector <Integer> mean = getMean(Vectors);
        for (var vec : Vectors) { //the original one that want to be converted
            int left = minDistance(vec, mean, 1);
            int right = minDistance(vec, mean, -1);
            if (left > right) Lefts.add(vec);
            else Rights.add(vec);
        }
        Quantize(L / 2, Rights, QBlocks);
        Quantize(L / 2, Lefts, QBlocks);
        return rearrange(Vectors, QBlocks);
    }

    public static int[][] readImage(String filePath) {

        File f = new File(filePath); //image file path

        int[][] imageMAtrix = null;

        try {
            BufferedImage img = ImageIO.read(f);
            oldW = img.getWidth();
            oldH = img.getHeight();

            imageMAtrix = new int[oldH][oldW];

            for (int y = 0; y < oldH; y++) {
                for (int x = 0; x < oldW; x++) {
                    int p = img.getRGB(x, y);
                    int a = (p >> 24) & 0xff;
                    int r = (p >> 16) & 0xff;
                    int g = (p >> 8) & 0xff;
                    int b = p & 0xff;

                    //because in gray image r=g=b  we will select r

                    imageMAtrix[y][x] = r;

                    //set new RGB value
                    p = (a << 24) | (r << 16) | (g << 8) | b;
                    img.setRGB(x, y, p);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
           return imageMAtrix;
    }

    public static void writeImage(int[][] imagePixels, String outPath) {
        int oldH = imagePixels.length;
        int oldW = imagePixels[0].length;
        BufferedImage img = new BufferedImage(oldW, oldH, BufferedImage.TYPE_3BYTE_BGR);

        for (int y = 0; y < oldH; y++) {
            for (int x = 0; x < oldW; x++) {

                int a = 255;
                int pix = imagePixels[y][x];
                int p = (a << 24) | (pix << 16) | (pix << 8) | pix;

                img.setRGB(x, y, p);

            }
        }

        File f = new File(outPath);

        try {
            ImageIO.write(img, "jpg", f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static void Compress(String Path) throws IOException {

        int[][] image = readImage(Path);

        int[][] SquaredImage = CreateSquared(image);

        Vector <Vector <Integer>> Vectors = CreateBlocks(SquaredImage), QBlocks = new Vector <>();
        ;

        Vector <Integer> VectorsIndices = Quantize(codeBlockLength, Vectors, QBlocks);

        WriteToFile(QBlocks, VectorsIndices);

    }

    static void Decompress(String Path) throws IOException, ClassNotFoundException {

        InputStream file = new FileInputStream(Path);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        oldW = input.readInt();oldH = input.readInt();
        int NEWidth = input.readInt();int NEWoldH = input.readInt();
        int BlockW = input.readInt();int BlockH = input.readInt();
        Vector <Integer> VectorsIndices = (Vector <Integer>) input.readObject();
        Vector <Vector <Integer>> QBlocks = (Vector <Vector <Integer>>) input.readObject();

        int[][] newImg = new int[NEWoldH][NEWidth];
        for (int i = 0; i < VectorsIndices.size(); i++) {
            //al3mlya al3ksya le el encoding bta3 al scaling image to match the block
            int x = i / (NEWidth / BlockW);
            int y = i % (NEWidth / BlockW);
            x *= BlockH;
            y *= BlockW;
            int v = 0;
            for (int j = x; j < x + BlockH; j++) {
                for (int k = y; k < y + BlockW; k++) {
                    newImg[j][k] = QBlocks.get(VectorsIndices.get(i)).get(v++);
                }
            }
        }
        writeImage(newImg, (Path.substring(0, path.lastIndexOf('.')) + "_Reconstructed.jpg"));
    }

    public static void main(String[] args) {
        //we need to make BlockH and others be filled here, so no need to send to function compress
        try {
            Scanner input = new Scanner(System.in); // 2 2 64
            System.out.println("Enter your Block Hieght");
            BlockH = input.nextInt();
            System.out.println("Enter your Block Width");
            BlockW = input.nextInt();
            System.out.println("Enter your CodeBookLength");
            codeBlockLength = input.nextInt();
            Compress(path);
            Decompress(path.substring(0, path.lastIndexOf('.')) + ".txt");
            System.out.println();
            System.out.println("CONVERSION IS DONE SUCCESSFULLY");
        } catch (IOException e1) {
            System.out.println("There is an error occured");
        } catch (ClassNotFoundException e1) {
            System.out.println("There is an error occured");
        }
    }
}
