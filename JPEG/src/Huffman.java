import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

class Node implements Comparable <Node> {
    public String s;
    public int p;
    public Node left;
    public Node right;

    public Node(String c, int p, Node l, Node r) {
        this.s = c;
        this.p = p;
        left = l;
        right = r;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(p, o.p);
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }


}

class Compressed {
    public String compressed;
    public String table ;

    Compressed(String compressed,String t) {
        this.table = t;
        this.compressed = compressed;
    }

    @Override
    public String toString() {
        String a = "The Compressed text is " + compressed;
        a+="\nThe code Table is "+table;
        return a;
    }
}

public class Huffman {
    /* Compress Methods */
    static HashMap<String,String> Table = new HashMap <>();
    public static HashMap<String,String>  Compress(Vector<String> a) {
        //return type isn't string as we need to return the string + table of codes we generated so can decode
        var p = CreateProb(a); //P is a vector that have the probabilities
        var PrQu = new PriorityQueue <Node>();
        //Getting the chars inserted into the priority queue
        for (Map.Entry <String, Integer> element : p.entrySet()) {
            PrQu.add(new Node(element.getKey(), element.getValue(), null, null));
        }
        //reordering and making trees
        while (PrQu.size() != 1) {
            var x = PrQu.poll();
            var y = PrQu.poll();
            PrQu.add(new Node("0", x.p + y.p, x, y));
        }

        var Tree = new HashMap <String, String>();
        //===============Tree has the value of the code================//

        var root = PrQu.peek();
        System.out.println("THE HUFFMAN TABLE IS ");
        RecTable(root, ""); //entirely edits the "Table"
        return Table;
    }


    static void RecTable(Node n, String a) {
        if (n.isLeaf()) {Table.put( n.s ,a);}
        else {
            RecTable(n.right, a + '1');
            if (n.right.isLeaf()) {
                System.out.println(n.right.s + "  " + a + '1');
                Table.put( n.right.s ,a+"1");
            }
            RecTable(n.left, a + '0');
            if (n.left.isLeaf()) {
                System.out.println(n.left.s + "  " + a + '0');
                Table.put( n.left.s ,a+"0");
            }
        }
    }

    public static HashMap<String,Integer> CreateProb(Vector<String> a) {
        //3ayzen n7sb kam mra mtkrra al klma dy
        //han4t8l 3l asas in fe msln map lkol string bt7utlo l7tta dy
        HashMap<String,Integer> probability = new HashMap <>();
        for (String s : a) {
            if (probability.containsKey(s)){
                probability.replace(s,probability.get(s)+1);
            }
            else
                probability.put(s,1);
        }

        return probability;
    }
}