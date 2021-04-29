/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> and <NAME2), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID:
 *  email address:
 *  Grader name:
 *
 *  Student 2
 *  UTEID:
 *  email address:
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private TreeMap<Integer, String> codeMap;
    private ArrayList<Integer> allBytes;
    private int headerFormat;
    private int[] frequency;
    private HuffmanTree tree;
    private boolean canCompressWithoutForcing;
    private int diff;

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work.
     *
     * @param in           is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     *                     header to use, standard count format, standard tree format, or
     *                     possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        BitInputStream scanner = new BitInputStream(in);
        this.headerFormat = headerFormat;
        frequency = new int[ALPH_SIZE];
        allBytes = new ArrayList<>(); // stores each byte read so we can re-read the file later
        int numBitsRead = 0;
        int currentByte = scanner.readBits(BITS_PER_WORD);
        myViewer.showMessage("Preprocessing started.");
        while (currentByte != -1) {
            allBytes.add(currentByte);
            frequency[currentByte]++;
            numBitsRead += BITS_PER_WORD;
            currentByte = scanner.readBits(BITS_PER_WORD);
        }
        scanner.close();
        tree = new HuffmanTree(frequency);
        codeMap = tree.getMap();
        int numBitsWritten = getNumBitsToWrite();
        diff = numBitsRead - numBitsWritten;
        canCompressWithoutForcing = diff > 0;
        int numBitsDifference = numBitsRead - numBitsWritten;
        myViewer.showMessage("Preprocessing completed. Num bits: " + numBitsDifference);
        return numBitsDifference;
    }

    /**
     * Returns the number of bits that will be written to the new Huffman file.
     */
    private int getNumBitsToWrite() {
        int numBitsWritten = 0;
        // for both magic number and STORE_COUNT or STORE_TREE constant
        final int NUM_INTS = 2;
        numBitsWritten += (BITS_PER_INT * NUM_INTS);
        // number of bits for frequencies of data
        if (headerFormat == STORE_COUNTS) {
            // count format - number of bits to store array of frequencies
            numBitsWritten += (BITS_PER_INT * IHuffConstants.ALPH_SIZE);
        } else if (headerFormat == STORE_TREE) {
            // tree format - number of bits to store the tree
            numBitsWritten += BITS_PER_INT; // size of tree
            numBitsWritten += (tree.getNumNodes() + ((BITS_PER_WORD + 1) * tree.getNumLeafNodes()));
        }

        // number of bits written for actual data
        for (int i = 0; i < frequency.length; i++) {
            if (codeMap.get(i) != null) {
                numBitsWritten += frequency[i] * codeMap.get(i).length();
            }
        }
        numBitsWritten += codeMap.get(PSEUDO_EOF).length();
        return numBitsWritten;
    }

    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     *
     * @param in    is the stream being compressed
     * @param out   is bound to a file/stream to which bits are written
     *              for the compressed file
     * @param force if this is true create the output file even if it is larger than the input file.
     *              If this is false do not create the output file if it is larger than the input
     *              file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        myViewer.showMessage("Compression started.");
        // have to check if client wants to force
        if (!canCompressWithoutForcing && !force) {
            // if you try to compress without forcing, and the file needs to be forced to
            // compress, then throw an exception
            myViewer.showError("Compressed file has " + Math.abs(diff) + " more bits than " +
                    "uncompressed file.\nSelect \"force compression\" option to compress.");
            return -1;
        } else {
            // all other cases, okay to compress
            int numWritten = 0;
            BitOutputStream bitOut = new BitOutputStream(out);
            numWritten += createHeader(bitOut, headerFormat);
            numWritten += writeData(bitOut);
            myViewer.showMessage("Compression completed. Number of bits written: " + numWritten);
            return numWritten;
        }
    }

    /**
     * Creates the header for the compressed file.
     *
     * @return the number of bits written to the header.
     */
    private int createHeader(BitOutputStream out, int headerFormat) {
        int numBitsWritten = 0;
        out.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        numBitsWritten += BITS_PER_INT;
        if (headerFormat == STORE_COUNTS) {
            myViewer.showMessage("Using counts format.");
            out.writeBits(BITS_PER_INT, STORE_COUNTS);
            numBitsWritten += BITS_PER_INT;
            for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                out.writeBits(BITS_PER_INT, frequency[i]);
                numBitsWritten += BITS_PER_INT;
            }
        } else if (headerFormat == STORE_TREE) {
            myViewer.showMessage("Using tree format.");
            out.writeBits(BITS_PER_INT, STORE_TREE);
            numBitsWritten += BITS_PER_INT;
            out.writeBits(BITS_PER_INT, tree.getSizeOfTree());
            numBitsWritten += BITS_PER_INT;
            // write out the pre order traversal of the tree
            TreeNode root = tree.getRoot();
            numBitsWritten += writeTreeHeader(out, root);
        }
        myViewer.showMessage("Header created. Number of bits written to header: " + numBitsWritten);
        return numBitsWritten;
    }

    /**
     * Writes the tree format header information, if compression algorithm is using STORE_TREE
     * constant
     *
     * @return the number of bits in the tree format header
     */
    private int writeTreeHeader(BitOutputStream out, TreeNode node) {
        int numBitsWritten = 1;
        final int leafBit = 1;
        final int internalBit = 0;
        if (node.isLeaf()) {
            out.writeBits(1, leafBit);
            out.writeBits(BITS_PER_WORD + 1, node.getValue());
            numBitsWritten += BITS_PER_WORD + 1;
        } else {
            out.writeBits(1, internalBit);
            numBitsWritten += writeTreeHeader(out, node.getLeft())
                    + writeTreeHeader(out, node.getRight());
        }
        return numBitsWritten;
    }

    /**
     * Writes the data in the compressed file using the previously generated codes. Returns the
     * number of bits written to the file.
     */
    private int writeData(BitOutputStream out) throws IOException {
        int numBitsWritten = 0;
        for (Integer s : allBytes) {
            numBitsWritten += writeCode(s, out);
        }
        // write PSEUDO-EOF code at the end
        numBitsWritten += writeCode(PSEUDO_EOF, out);
        // flushing out the output stream
        out.close();
        return numBitsWritten;
    }

    /**
     * Writes out the Huffman code to a file, given the original byte.
     *
     * @return the number of bits in that code
     */
    private int writeCode(int originalByte, BitOutputStream out) {
        int numBitsWritten = 0;
        String newCode = codeMap.get(originalByte);
        for (int i = 0; i < newCode.length(); i++) {
            char c = newCode.charAt(i);
            final char ZERO = '0';
            if (c == ZERO) {
                out.writeBits(1, 0);
            } else {
                out.writeBits(1, 1);
            }
            numBitsWritten++;
        }
        return numBitsWritten;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     *
     * @param in  is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        BitInputStream scanner = new BitInputStream(in);
        if (scanner.readBits(BITS_PER_INT) != MAGIC_NUMBER) {
            myViewer.showError("Error reading compressed file.\n" +
                    "File did not start with the huff magic number.");
            return -1;
        }
        int headerFormat = scanner.readBits(BITS_PER_INT);
        if (headerFormat == STORE_COUNTS) {
            myViewer.showMessage("Decompressing from count format header.");
            // read in ALPH_SIZE -  this gives us back the array
            frequency = new int[ALPH_SIZE];
            for (int i = 0; i < ALPH_SIZE; i++) {
                frequency[i] = scanner.readBits(BITS_PER_INT);
            }
            tree = new HuffmanTree(frequency);
        } else if (headerFormat == STORE_TREE) {
            myViewer.showMessage("Decompressing from tree format header.");
            int sizeOfTree = scanner.readBits(BITS_PER_INT);
            TreeNode rootNode = rebuildTree(scanner);
            tree = new HuffmanTree(rootNode);
        } else if (headerFormat != STORE_CUSTOM) {
            myViewer.showError("Error reading compressed file.\n" +
                    "File did not include a correct header format.");
        }
        return writeUncompressedFile(scanner, new BitOutputStream(out));
    }

    /**
     * Uses recursion to rebuild the Huffman tree from the binary representation of the
     * tree in the tree-format header. Creates TreeNodes and links them together.
     */
    private TreeNode rebuildTree(BitInputStream in) throws IOException {
        final int internalValue = -1;
        if (in.readBits(1) == 0) {
            return new TreeNode(rebuildTree(in), internalValue, rebuildTree(in));
        } else {
            //leaf node
            int data = in.readBits(BITS_PER_WORD + 1);
            return new TreeNode(data, internalValue);
        }
    }

    /**
     * Using a rebuilt Huffman Tree, writes the uncompressed data when traversing a .hf file.
     *
     * @return the number of bits for the ACTUAL DATA written to the compressed file
     */
    public int writeUncompressedFile(BitInputStream in, BitOutputStream out) throws IOException {
        boolean done = false;
        int numBitsWritten = 0;
        TreeNode node = tree.getRoot();
        final int goLeft = 0;
        final int goRight = 1;
        while (!done) {
            int bit = in.readBits(1);
            if (bit == -1) {
                myViewer.showError("Error reading compressed file. Unexpected end of input. " +
                        "No PSEUDO_EOF value present in file.");
                throw new IOException();
            } else {
                // traverses down the Huffman tree until we reach a leaf node
                if (bit == goLeft) {
                    node = node.getLeft();
                } else if (bit == goRight) {
                    node = node.getRight();
                }
                if (node.isLeaf()) {
                    if (node.getValue() == PSEUDO_EOF) {
                        done = true;
                    } else {
                        out.writeBits(BITS_PER_WORD, node.getValue());
                        numBitsWritten += BITS_PER_WORD;
                        node = tree.getRoot();
                    }
                }
            }
        }
        myViewer.showMessage("Uncompressed file written. Number of bits written: "
                + numBitsWritten);
        return numBitsWritten;
    }

    /**
     * Shows a string in the HuffViewer View area.
     */
    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
