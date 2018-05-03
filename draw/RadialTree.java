package draw;

import model.MappedTreeStructure;
import model.Node;

public class RadialTree {

    public static MappedTreeStructure processTree(MappedTreeStructure tree) {
        calculateAngles(tree, 360,0);
        return tree;
    }

    public static void calculateAngles(MappedTreeStructure tree, double alpha, double beta) {
        // the depth of v in T
        Node root = tree.getRoot();
        int depthOfNode = Node.getDepth(root);
        double theta = alpha;
        // radius for the concentric circle levels

    }



//        // initialize
//        int treeDepth = Node.getTreeDepth(tree.getRoot());
//        Node root = tree.getRoot();
//        root.setAngle(0);
//        root.setTangentLimit(Math.PI);
//        root.setLowerBisector(-(Math.PI));
//        root.setHigherBisector(Math.PI);
//
//        for (int depth = 1; depth < treeDepth + 1; depth++) {
//            double dDepth = (double) depth;
//            Node pPreviousNonleafNode = null;
//            Node pFirstNonleafNode = null;
//            Node pPreviousParent = null;
//            int indexInCurrentParent = 0;
//            double tTangentLimit = Math.acos(dDepth / dDepth + 1.0);
//            // iterate through all children
//            for ( int i = 0; i < )
//        }
//    }
}


//        int indexInCurrentParent = 0;
//        double dTangentLimt = acos( dDepth / (dDepth + 1.0) );
//        for (int i = 0; i < GetNodesInDepth(depth).size(); i++)
//        {
//        Node* pCurrentNode = GetNodesInDepth(depth).at(i);
//        Node* pParent = pCurrentNode->GetParent();
//        if (pParent != pPreviousParent)
//        {
//        pPreviousParent = pParent;
//        indexInCurrentParent = 0;
//        }
//        // (GetMaxChildAngle() - GetMinChildAngle()) / GetChildCount()
//        double angleSpace = pParent->GetAngleSpace();
//        pCurrentNode->SetAngle(angleSpace * (indexInCurrentParent + 0.5));
//        pCurrentNode->SetTangentLimit(dTangentLimt);
//        if (pCurrentNode->IsParent())
//        {
//        if (!pPreviousNonleafNode)
//        {
//        pFirstNonleafNode = pCurrentNode;
//        }
//        else
//        {
//        double dBisector = (pPreviousNonleafNode->GetAngle() + pCurrentNode->GetAngle()) / 2.0;
//        pPreviousNonleafNode->SetHigherBisector(dBisector);
//        pCurrentNode->SetLowerBisector(dBisector);
//        }
//        pPreviousNonleafNode = pCurrentNode;
//        }
//        indexInCurrentParent++;
//        }
//        if (pPreviousNonleafNode && pFirstNonleafNode)
//        {
//        if (pPreviousNonleafNode == pFirstNonleafNode)
//        {
//        double dAngle = pFirstNonleafNode->GetAngle();
//        pFirstNonleafNode->SetLowerBisector(dAngle - PI);
//        pFirstNonleafNode->SetHigherBisector(dAngle + PI);
//        }
//        else
//        {
//        double dBisector = PI + (pPreviousNonleafNode->GetAngle() + pFirstNonleafNode->GetAngle()) / 2.0;
//        pFirstNonleafNode->SetLowerBisector(dBisector);
//        pPreviousNonleafNode->SetHigherBisector(dBisector);
//        }
//        }
//        }
//        }
//
//        void Tree::CalculatePositions()
//        {
//        for (int depth = 0; depth < GetDepth() + 1; depth++)
//        {
//        double redius = SPACING * depth;
//        for (int i = 0; i < GetNodesInDepth(depth).size(); i++)
//        {
//        Node* pCurrentNode = GetNodesInDepth(depth).at(i);
//        double angle = pCurrentNode->GetAngle();
//        pCurrentNode->SetXRadial(redius * qCos(angle) + MIDDLE(m_nWidth));
//        pCurrentNode->SetYRadial(redius * qSin(angle) + MIDDLE(m_nHeight));
//        }
//        }
//        }
//
//        void Tree::CalculateLayout ()
//        {
//        CalculateAngles();
//        CalculatePositions();
//        }
//
//        double Node::GetAngleSpace()
//        {
//        return (GetMaxChildAngle() - GetMinChildAngle()) / GetChildCount();
//        }
