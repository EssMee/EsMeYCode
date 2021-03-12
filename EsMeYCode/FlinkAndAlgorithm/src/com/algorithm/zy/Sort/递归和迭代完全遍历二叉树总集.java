import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class 递归和迭代完全遍历二叉树总集 {
    /* 递归方式-前序遍历*/
    public List<Integer> preorderWithRecrusive(TreeNode node) {
        List<Integer> res = new ArrayList<>();
        preorderWithRecrusive(node, res);
        return res;
    }

    public List<Integer> inOrderWithRecrusive(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        inOrderWithRecrusive(root, res);
        return res;
    }

    private void inOrderWithRecrusive(TreeNode root, List<Integer> res) {
        if (root == null) return;
        while (root != null) {
            inOrderWithRecrusive(root.left);
            res.add(root.val);
            inOrderWithRecrusive(root.right);
        }
    }

    public void preorderWithRecrusive(TreeNode root, List<Integer> res) {
        if (root == null) {
            return;
        }
        while (root != null) {
            res.add(root.val);
            preorderWithRecrusive(root.left, res);
            preorderWithRecrusive(root.right, res);
        }
    }

    /*迭代方式的前序遍历-root，root。left，root。right
     * 思路类似，把左子树查到底，同时将遍历过的节点推入栈。
     * 当到叶子节点的时候，此时要把节点从栈顶往外pop，直到某个节点有右孩子了，
     * 再进行类似的操作。
     * */
    public List<Integer> preOrderWithIteration(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        TreeNode node = root;
        while (!stack.isEmpty() || node != null) {
            /*此处相当于一直进行左子树的遍历，一直把左子节点压入栈中*/
            while (node != null) {
                res.add(node.val);
                stack.push(node);
                node = node.left;
            }
            /*当左子树遍历完了，此时root为空的时候，需要从栈里面pop出元素，pop出的第一个元素
              就是左子树的第一个叶子节点。*/
            node = stack.pop();
            /*然后拿到该叶子节点的右孩子。如果还是空，那么继续pop，否则的话，将该叶子
              节点的右孩子作为新的root，进行右子树的遍历。
            如果右孩子又有左孩子和右孩子，那么就重复以上过程。*/
            node = node.right;
        }
        return res;
    }

    /*TODO 不会写*/
/*
    public List<Integer> inorderWithIteration(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        TreeNode node = root;
        if (node == null) return res;
        Deque<TreeNode> stack = new LinkedList<>();
        while (!stack.isEmpty() || node != null) {
            node = node.left;
            res.add(node.val);
            stack.push(node);
        }

    }
}
*/

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }

        TreeNode() {
        }
    }
}
