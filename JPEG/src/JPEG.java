import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static java.lang.Math.pow;

class Tag {
    Tag(int zeros, String originalNumber, int category) {
        this.zeros = zeros;
        OriginalNumber = originalNumber;
        this.category = category;
    }

    int zeros;
    String OriginalNumber;
    int category;

    @Override
    public String toString() {
        if (OriginalNumber == "EOB") return "EOB";
        return zeros + "/" + category + "-" + OriginalNumber;
    }
}

class Category {
    String binary;
    int category;

    Category(int category, String binary) {
        this.binary = binary;
        this.category = category;
    }

}

public class JPEG {
    // HashMap<AC,Add>
    static Vector <Tag> originalTags = new Vector <>();
    static HashMap <String, Category> CategoryTable = new HashMap <>();
    static HashMap <String, String> HuffmanTable = new HashMap <String, String>();

    static Vector <String> RLE(String a) {
        int cnt = 0, i = 0;
        /*To be splitted using spaces or commas*/
        // a = a.replaceAll(" ", "");
        //var v = a.split(",");
        var v = a.split(" ");
        for (String s : v) {
            s = s.strip();
        }
        Vector <String> res = new Vector <>();
        System.out.println("TAGS GENERATED ARE ");
        while (true) {
            if (v[i].contains("0")) cnt++;
                //if a tag has a  zero and zero then it means that we achieved our EOB
            else if (v[i].contains("EOB")) {
                originalTags.add(new Tag(0, "EOB", 0));
                res.add("EOB");
                break;
            } else {
                originalTags.add(new Tag(cnt, v[i], CategoryTable.get(v[i]).category));
                System.out.println("tag is " + cnt + "/" + CategoryTable.get(v[i]).category);
                res.add(cnt + "/" + CategoryTable.get(v[i]).category);
                cnt = 0;
            }
            i++;
        }
        System.out.println("The Constructed Tags-Original Number");
        for (Tag originalTag : originalTags) {
            System.out.println("tag is " + originalTag.zeros+"/"+originalTag.OriginalNumber);
        }
        return res;
    }

    static void Initialize() {
        int cnt = 0;
        for (int i = 1, j = 1; i <= 5; i++) {
            for (int x = (int) pow(2, i) - 1; x >= j; x--) {
                var binary = Integer.toBinaryString(cnt);
                while (binary.length() < i) binary = "0" + binary;
                CategoryTable.put(String.valueOf(-1 * x), new Category(i, binary));
                cnt++;
            }
            for (; j < pow(2, i); j++) {
                var binary = Integer.toBinaryString(cnt);
                while (binary.length() < i) binary = "0" + binary;
                CategoryTable.put(String.valueOf(j), new Category(i, binary));
                cnt++;
            }
            cnt = 0;
        }
        CategoryTable.put("EOB", new Category(0, ""));

    }

    static String CreateResult() {
        String a = "";
        for (Tag originalTag : originalTags) {
            String HuffCode;
            if (originalTag.OriginalNumber == "EOB") {
                HuffCode = Huffman.Table.get("EOB");
            } else {
                HuffCode = Huffman.Table.get(originalTag.zeros + "/" + originalTag.category);
            }
            var additionalBits = CategoryTable.get(originalTag.OriginalNumber).binary;
            System.out.println(HuffCode + "/" + additionalBits);
            a += HuffCode + additionalBits;
        }
        System.out.println("THE COMPRESSED CODE IS " + a);
        return a;
    }

    static String Compress(String a) {
        Initialize();
        var tagsTOHuffman = RLE(a);
        System.out.println();
        HuffmanTable = Huffman.Compress(tagsTOHuffman);
        System.out.println();
        return CreateResult();
    }

    static HashMap <String, String> HuffmanReverse() {
        HashMap <String, String> res = new HashMap <>();
        for (Map.Entry <String, String> entry : HuffmanTable.entrySet()) {
            res.put(entry.getValue(), entry.getKey());
        }

        return res;
    }

    static HashMap <String, String> CategoryReverse() {

        HashMap <String, String> res = new HashMap <>();
        for (Map.Entry <String, Category> entry : CategoryTable.entrySet()) {
            res.put(entry.getValue().binary, entry.getKey());
        }
        return res;
    }

    static String Decompress(String a) {
        //me7tag a3ks al huffman code 34an a3rf a4t8l
        var HuffmanTableReversed = HuffmanReverse();
        var CategoryTableReversed = CategoryReverse();
        String res = "";
        String temp = "";
        System.out.println(HuffmanTableReversed);
        for (int i = 0; i < a.length(); ) {
            temp += a.charAt(i);
            if (HuffmanTableReversed.containsKey(temp)) {
                var now = HuffmanTableReversed.get(temp).split("/");
                if (HuffmanTableReversed.get(temp).equals("EOB") ){
                    res += "EOB";
                    break;
                }
                res += "0".repeat(Integer.parseInt(now[0]));
                int take = Integer.parseInt(now[1]);
                i +=1;
                String Add = a.substring(i, i + take);
                i+=take;
                res += " " + CategoryTableReversed.get(Add) + " ";
                temp = "";
            }
            else i++;
        }
        System.out.println("The Decompressed code is "+res);
        return res;

    }

    public static void main(String[] args) {
        //String a = "-2, 0,0,2, 0,0,3, 2, 0,1, 0,0,-2, 0,-1, 0,0,1, 0,0,-1, EOB";
        String a = "-2 0 0 2 0 0 3 2 0 1 0 0 -2 0 -1 0 0 1 0 0 -1 EOB";
        String compressed = Compress(a);// + that we have the HuffmanTable and the CategoryTable
        var decompressed = Decompress(compressed);
    }
}
