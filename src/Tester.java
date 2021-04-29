//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.TreeMap;
//
//public class Tester {
//
//    public static void main(String[] args) throws IOException {
//        freqTests();
////        huffmanTreeTests();
//    }
//
//    private static void freqTests() throws IOException {
//
//        final IHuffProcessor testItem = new SimpleHuffProcessor();
//        File eerie = new File("smallTxt.txt");
//        final InputStream in = new BufferedInputStream(new FileInputStream(eerie));
//        final File expectedOutFile = new File(eerie.getPath() + ".hf");
////        final File outFile = File.createTempFile("A11Test-uncp-" + eerie.getName() + "-", null);
////        outFile.deleteOnExit();
//        final OutputStream out = new BufferedOutputStream(new FileOutputStream(expectedOutFile));
//        System.out.println("predicted number of bits written: ");
//        System.out.println("preprocessCompress returns " + testItem.preprocessCompress(in,
//                IHuffConstants.STORE_TREE));
//        System.out.println("compress returns " + testItem.compress(in, out, true));
//    }
//
//    private static void priorityQueueTests() {
//        HuffmanPriority<Integer> pQueue = new HuffmanPriority<>();
//
//        //test 1 - inserting at index 0
//        pQueue.add(1);
//        pQueue.add(0);
//        System.out.println(pQueue);
//
//        //test 2 - inserting after all smaller items
//        pQueue.clear();
//        pQueue.add(1);
//        pQueue.add(2);
//        pQueue.add(2);
//        pQueue.add(2);
//        pQueue.add(4);
//        pQueue.add(4);
//
//        pQueue.add(3);
//        System.out.println(pQueue);
//
//        //test 3
//        System.out.println(pQueue.contains(3));
//
//        System.out.println(pQueue.peek());
//
//        pQueue.clear();
//        System.out.println(pQueue.contains(2));
//
//        pQueue.clear();
//        pQueue.add(1);
//        pQueue.add(2);
//        pQueue.add(2);
//        pQueue.add(2);
//        pQueue.add(4);
//        pQueue.add(4);
//
//        pQueue.add(3);
//        System.out.println(pQueue.remove());
//        System.out.println(pQueue);
//    }
//
//    private static void huffmanTreeTests() {
//        int[] frequencyOf = new int[255];
//        frequencyOf['E'] = 1;
//        frequencyOf['i'] = 1;
//        frequencyOf['k'] = 1;
//        frequencyOf['l'] = 1;
//        frequencyOf['y'] = 1;
//        frequencyOf['.'] = 1;
//        frequencyOf[' '] = 4;
//        frequencyOf['e'] = 8;
//        frequencyOf['a'] = 2;
//        frequencyOf['n'] = 2;
//        frequencyOf['r'] = 2;
//        frequencyOf['s'] = 2;
//
//        HuffmanTree tree = new HuffmanTree(frequencyOf);
//        TreeMap<Integer, String> huffMap = tree.getMap();
////        System.out.println(huffMap);
//        tree.printTree();
//        System.out.println(huffMap);
//        System.out.println(tree.getSizeOfTree());
//    }
//
//
//
//}
