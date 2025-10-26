package Parser;

public class TreePrinter {

    // Recursively prints the parse tree with indentation
    public static void printTree(TreeNode node) {
        printTree(node, 0);
    }

    private static void printTree(TreeNode node, int depth) {
        if (node == null) return;

        // indentation proportional to depth
        String indent = " ".repeat(depth * 2);

        // print node data
        String type = (node.data == null) ? "null" : node.data.t.toString();
        String value = (node.data == null) ? "null" : node.data.value;
        System.out.println(indent + "─ " + type + " : " + value);

        // recursively print children
        if (node.children != null) {
            for (TreeNode child : node.children) {
                printTree(child, depth + 1);
            }
        }
    }

    // optional overload if you only have the root
    public static void printRoot(TreeNode root) {
        System.out.println("\n=== PARSE TREE ===");
        printTree(root, 0);
        System.out.println("==================\n");
    }
}
