/*  Student information for assignment:
 *
 *  On OUR honor, Jake Medina and Thomas Moore,
 *  this programming assignment is OUR own work
 *  and WE have not provided this code to any other student.
 *
 *  Number of slip days used: 0
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID: jrm7784
 *  email address: jakemedina@utexas.edu
 *  Grader name: Skyler V.
 *  Section number: 52260
 *
 *  Student 2
 *  UTEID: tcm2448
 *  email address: tmooretcm@utexas.edu
 *
 */

import java.util.TreeMap;

/**
 * Represents a HuffmanTree created from a list of frequency values.
 */
public class HuffmanTree implements IHuffConstants {
    private TreeNode root;
    private int numNodes;
    private int numLeafNodes;

    /**
     * Creates a new HuffmanTree given a root node.
     */
    public HuffmanTree(TreeNode root) {
        this.root = root;
    }

    /**
     * Creates and builds a new Huffman tree based on a frequency map that represents the number
     * of occurrences of each 8-bit code in a file.
     *
     * @param frequency map of code frequencies
     */
    public HuffmanTree(int[] frequency) {
        if (frequency == null) {
            throw new IllegalArgumentException("Can't create a HuffmanTree with no frequencies.");
        }

        // create the priority queue and insert all elements
        HuffmanPriority<TreeNode> pQueue = new HuffmanPriority<>();
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] > 0) {
                pQueue.add(new TreeNode(i, frequency[i]));
            }
        }
        // add the pseudo-eof node into the queue
        TreeNode eofNode = new TreeNode(PSEUDO_EOF, 1);
        pQueue.add(eofNode);
        numNodes += pQueue.size();
        numLeafNodes += pQueue.size();
        final int NODES_TO_ADD = 2;
        final int ARBITRARY_PARENT_VALUE = -1;
        // merge the nodes in the queue into one big tree
        while (pQueue.size() >= NODES_TO_ADD) {
            TreeNode leftChild = pQueue.remove();
            TreeNode rightChild = pQueue.remove();
            TreeNode parent = new TreeNode(leftChild, ARBITRARY_PARENT_VALUE, rightChild);
            pQueue.add(parent);
            numNodes++;
        }
        // now there is one node left in the queue, this should be the root of our tree
        root = pQueue.remove();
    }

    /**
     * Generates and returns a Map that associates 8-bit integer chunks to Huffman codes.
     *
     * @return a TreeMap which maps bits to Huffman codes
     */
    public TreeMap<Integer, String> getMap() {
        TreeMap<Integer, String> result = new TreeMap<>();
        StringBuilder huffCode = new StringBuilder();
        mapHelper(root, result, huffCode);
        return result;
    }

    /**
     * Recursive method for traversing the Huffman tree and finding the Huffman codes for their
     * corresponding 8-bit chunks.
     *
     * @param n      the node to check
     * @param result the resulting treemap which will map bits to Huffman codes
     * @param code   the Huffman code to place in result
     */
    private void mapHelper(TreeNode n, TreeMap<Integer, String> result, StringBuilder code) {
        if (n.isLeaf()) {
            //store the code in the map
            result.put(n.getValue(), code.toString());
        } else {
            // go left in the tree, recurse with a code 0
            code.append("0");
            mapHelper(n.getLeft(), result, code);
            code.deleteCharAt(code.length() - 1);

            // go right in the tree, recurse with a code 1
            code.append("1");
            mapHelper(n.getRight(), result, code);
            code.deleteCharAt(code.length() - 1);
        }
    }

    /**
     * Returns a reference to the root of the tree.
     *
     * @return the node of the root of the tree
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * Returns the size of the tree in bits.
     *
     * @return
     */
    public int getSizeOfTree() {
        return sizeHelper(root, 0);
    }

    /**
     * Returns the number of nodes present in this tree.
     *
     * @return the total number of nodes in the tree
     */
    public int getNumNodes() {
        return numNodes;
    }

    /**
     * Returns the number of leaf nodes in this tree.
     *
     * @return the number of leaf nodes in the tree
     */
    public int getNumLeafNodes() {
        return numLeafNodes;
    }

    /**
     * Recursive method which helps find the size of the tree.
     *
     * @param node the node to check
     * @param size the current size of the tree
     * @return the total size of the tree
     */
    private int sizeHelper(TreeNode node, int size) {
        // read any node adds one to size
        // read any leaf adds 9 to size
        if (node != null) {
            size += 1;
            if (node.isLeaf()) {
                size += IHuffConstants.BITS_PER_WORD + 1;
            } else {
                // not a leaf, still have to check the left and right subtrees
                size += sizeHelper(node.getLeft(), 0) + sizeHelper(node.getRight(), 0);
            }
        }
        return size;
    }

}
