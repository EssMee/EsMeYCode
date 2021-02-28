package com.algorithm.zy.Sort;

public class BST<Key extends Comparable<Key>, Value> {
    private class Node {
        private Key key;
        private Value value;
        private Node left;
        private Node right;
        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
            this.left = this.right = null;
        }
    }
    /*根节点*/
    private Node root;
    /*节点个数*/
    private int count;
    public BST() {
        this.root = null;
        this.count = 0;
    }

/*    public method*/
    /*该BST是否为空*/
    public boolean isEmpty(){return count  == 0;}
    /*该BST的节点数*/
    public int size(){return count;}
    /*向二分搜索树中插入一个key,value的新节点*/
    public void insert(Key key, Value value) {
        root = insert(root, key, value);
    }
    /*该BST是否包含某个key*/
    public boolean containsKey(Key key) {
        return containsKey(root,key);
    }
   /* 在该BST中查找某个key的value*/
    public Value search(Key key) {
        if (containsKey(key)) {
            return search(root, key);
        }
        return null;
    }




    /*    private method*/
    /*向以node为根的二分搜索树中插入一个key，value的新节点*/
    private Node insert(Node node, Key key, Value value) {
        if (node == null) {
            count ++;
            return new Node(key,value);
        }
        if (key.compareTo(node.key) == 0) {
            node.value = value;
        } else if (key.compareTo(node.key) > 0) {
            /*新的节点需要往以node为根的左子树去插入*/
            node.left = insert(node.left, key, value);
        } else {
            /*新的节点需要往以node为根的右子树去插入*/
            node.right = insert(node.right,key,value);
        }
        /*返回插入完成之后的二叉搜索树*/
        return node;
    }

    /*在以node为根的BST中查找是否存在key的节点*/
    private boolean containsKey(Node node, Key key) {
        if (key == null || node == null) {return false;}
        if (key.compareTo(node.key) ==0) {return true;}
        /*去右子树中继续查找*/
        else if (key.compareTo(node.key) > 0) {
            return containsKey(node.right, key);
        } else {
            /*去左子树继续查找*/
            return containsKey(node.left, key);
        }
    }
/*    在以node为根的BST中查找某个key的value*/
    private Value search(Node root, Key key) {
        if (root == null || key == null) return null;
        if (key.compareTo(root.key) == 0) {
            return root.value;
        } else if (key.compareTo(root.key) > 0) {
            /*去右子树中继续查找*/
            return search(root.right, key);
        } else {
            return search(root.left,key);
        }
    }

    /*TEST*/
    public static void main(String[] args) {
        int N = 1000000;
        Integer[] arr = new Integer[N];
        for (int i = 0; i < N; i++) {
            arr[i] = i;
        }
        /*打乱数组顺序*/
        for (int i = 0; i < N; i++) {
            int pos = (int) Math.random() * (i + 1);
            Integer t = arr[pos];
            arr[pos] = arr[i];
            arr[i] = t;
        }
        BST<Integer, String> bst = new BST<>();
        for (int i = 0; i < N; i++) {
            bst.insert(new Integer(arr[i]), Integer.toString(arr[i]));
        }
        for (int i = 0; i < 2 * N; i++) {
            String res = bst.search(new Integer(i));
            if (i < N) {
                assert res.equals(Integer.toString(i));
            } else {
                assert res == null;
            }
        }
    }
}
