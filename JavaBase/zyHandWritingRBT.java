package JavaInterview;

import scala.concurrent.java8.FuturesConvertersImpl;

import java.awt.peer.PanelPeer;

public class RedBlackTree {
    static class Node {
        int hash;
        int key;
        int val;
        Node next;
    }

    static class TreeNode {
        TreeNode parent;
        TreeNode left;
        TreeNode right;
        TreeNode prev;
        boolean red;

        TreeNode(int hash, int key, int val, Node root) {
            // pass
        }

        /**
         * Returns root of tree containing this node.
         */
        final TreeNode root() {
            for (TreeNode r = this, p; ; ) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }

/*          总结一下： 
            （1）如果插入节点的父亲是左子树
                父节点和叔叔节点不都是红色的话，不需要修改红黑树结构。    
                父节点和叔叔节点都是红色的话，进行变色：
                    父亲和叔叔都变成黑色，祖父变成红色，当前指针指向祖父节点；
                    （2）之后，if，如果x是它父亲节点的右孩子：
                                此时父亲是红，叔叔是黑，以父亲为轴做左旋，把指针指向祖父节点；
                            if，如果x是它父亲节点的左孩子：
                                此时父亲是红，叔叔是黑，以祖父节点为轴右旋：
                                    先把父亲变黑，祖父变红，以祖父节点做右旋。
            （3）如果插入节点的父亲是右子树（此时全部反过来）
                父亲和叔叔都是红色，把父亲和叔叔变黑，祖父变红；
                （4）之后，if，如果x是它父亲节点的左孩子：
                                此时父亲是红，叔叔是黑，以父亲节点为轴左旋，思路与第一部分相同，但是调用的是右旋方法；
                            if，如果x是它父亲节点的右孩子：
                                父亲变黑，祖父变红，以祖父节点右旋，但是调用左旋方法。                
 */

        static TreeNode balananceInsertion(TreeNode root, TreeNode x) {
            x.red = true;
            for (TreeNode xp, xpp, xppl, xppr; ; ) {
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                /* 插入节点的父亲节点是黑色，或者是父亲节点已经是根节点了，则不需要调整 */
                else if (!xp.red || (xpp = xp.parent) == null) {
                    return root;
                }
                /* 如果插入节点的父亲是左子树 */
                if (xp == (xppl = xpp.left)) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xp.red = false;
                        xppr.red = false;
                        xpp.red = true;
                        /* 当前指针指向祖父节点 */
                        x = xpp;
                    } else {
                        /* 父亲节点是红色，叔叔节点是黑色 */
                        /* 当前节点是它父亲节点的右孩子,以父亲节点为轴左旋 */
                        if (x == xp.right) {
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        /* 左旋完之后，如果父亲节点不为空，开始考虑右旋 */
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                /* 如果插入节点的父亲节点是右子树 */
                else {
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        /* 当前节点是它父亲节点的左孩子，以父亲节点为轴左旋，调用右旋方法 */
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = ((xp = x.parent) == null) ? null : xp.parent;
                        }
                        /* 左旋完之后，如果父亲不为空，开始考虑右旋,调用左旋方法*/
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        /*
        pp                       pp
         \                       \
         p                       r
        / \        --->         /  \
       l   r                   p    rr
          / \                 / \
        rl   rr              l  rl
        */
        /*将rl设置为p的右孩子；将rl的父节点设置为p；
        将r的父节点设置为pp；将pp的左节点或右节点设置为r；
        将r的左节点设置为p；将p的父节点设置为r；
        */
        /* zy handwritting rotateLeft method */
        private static TreeNode rotateLeft(TreeNode root, TreeNode p) {
            /*r：旋转节点的右节点； pp：旋转节点的父节点； rl：旋转节点的右节点的左节点。*/
            TreeNode r, pp, rl;
            /*旋转节点非空，并且旋转节点的右孩子非空*/
            if (p != null && (r = p.right) != null) {
                /*将p的右节点设置为p的右节点的左节点 -》 将rl设置为p的右孩子*/
                if ( (rl = p.right = r.left) != null) {
                    /*将rl的父节点设置为p*/
                    rl.parent = p;
                }
                /*将r的父亲节点设置为p的父亲节点，如果是空的话，染成黑色*/
                if ( (pp = r.parent = p.parent) == null) {
                    (root = r).red = false;
                }
                /*判断p是pp的左节点还是右节点*/
                else if (pp.left == p) {
                  pp.left = r;
                } else {
                    pp.right = r;
                }
                /*最后把r的左节点设置为p；把p的父亲节点设置为r*/
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        /*
        pp                   pp
        \                     \
        p      --->           l
       / \                   / \
      l   r                 ll  p
     / \                       / \
    ll lr                     lr  r
        将lr设置为p的左孩子；将lr的父亲节点设置为p；
        将l的父亲节点设置为pp；将pp的左节点或右节点设置为l；
        将l的右孩子设置为p；将p的父亲节点设置为l；
        */
        private static TreeNode rotateRight(TreeNode root, TreeNode p) {
            TreeNode l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ( (lr = p.left = l.left) != null ) {
                    lr.parent = p;
                }
                if ( (pp = l.parent = p.parent ) == null) {
                    (root = l).red = false;
                } else if (pp.right == p) {
                    pp.right = l;
                } else {
                    pp.left = l;
                }
                l.right = p;
                p.parent = l;
            }
            return root;
        }
    }
}
