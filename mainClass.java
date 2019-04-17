import java.io.*;
import java.util.*;


public class mainClass {
    /*
        graph solution

        nodes list is shuffled so result may vary between iterations

     */


        //node cluster dimension
    private static final int clusterDimension = 5000;
        //seed for shuffle
        //using seed to be able to repeat result
    private static final long seed = 1234567890;

    public static void main(String args[]) {

        List<String> filePathI = new ArrayList<>();
        filePathI.add("         /a_example"); //file input
        filePathI.add("         /b_lovely_landscapes");
        filePathI.add("         /c_memorable_moments");
        filePathI.add("         /d_pet_pictures");
        filePathI.add("         /e_shiny_selfies");

        for (String k : filePathI) {
            doTheThing(k);
        }

    }

    private static void doTheThing(String k) {

        //scan the input file
        //combine vertical photos based on Node.getScore(Node A) function
            //todo better score function
            //bonus - (waste * 2) may be better?
        //returns a list with all nodes (vertical combined 2:1 into horizontal nodes

        List<Node> hNodes = getHorizontalNodesList(k);

        //make clusters
        //make path inside clusters
        //returns all path combined into a single one

        List<Node> output = getOutput(hNodes);


        //print output in input folder
        //name = old name + _out
        printOutputToFile(k, output);
    }

    private static void printOutputToFile(String k, List<Node> output) {
        try {
            outputFile(k + "_out.txt", output);
        } catch (IOException e) {
            System.out.println("error at: " + k);
        }

        System.out.println("done: " + k);
    }

    private static List<Node> getOutput(List<Node> hNodes) {
        List<Node> output = new ArrayList<>();




        //switch to while hNodes not empty?
            /*
        int totalCluster = (hNodes.size() / clusterDimension) + 1;
        for (int currentCluster = 0; currentCluster < totalCluster; currentCluster++) {
            */
         while(hNodes.size()>0){


             //make a cluster
            List<Node> clusterNodes = new ArrayList<>();
            for (int i = 0; hNodes.size() > 0 && i < clusterDimension; i++) {
                clusterNodes.add(hNodes.remove(0));
            }

                //just in case
            if (clusterNodes.size() > 1) {

                //make arc inside cluster
                //returns highest "interestScore" Arc
                Arc startingArc = makeWay(clusterNodes);

                List<Node> finalList = new ArrayList<>();

                if(startingArc!=null) {


                    getClusterPath(output, clusterNodes, startingArc, finalList);
                }
            }


        }
        return output;
    }

    private static void getClusterPath(List<Node> output, List<Node> clusterNodes, Arc startingArc, List<Node> finalList) {
        finalList.add(startingArc.A);
        finalList.add(startingArc.B);

        startingArc.deleteArc();

        clusterNodes.remove(finalList.get(0));
        clusterNodes.remove(finalList.get(1));

            //todo add lists to a List and combine them based on list extremes
            //may be overkill for a n/clusterDimension * medianScore
        output.addAll(getFinalList(finalList, clusterNodes));
    }

    private static List<Node> getHorizontalNodesList(String k) {
        tempList tempListA = readFile(k + ".txt");

        List<Node> hNodes = new ArrayList<>();
        List<Node> vNodes = new ArrayList<>();


        if(tempListA.H!=null)
            hNodes = tempListA.H;
        if(tempListA.V!=null)
            vNodes = tempListA.V;


        //combina i nodi verticali e li aggiunge alla lista degli orizzontali
        hNodes = combineV(hNodes, vNodes);
        Collections.shuffle(hNodes, new Random(seed));
        return hNodes;
    }

    private static List<Node> combineV(List<Node> H, List<Node> V) {


//        Collections.sort(V, new tagsComparator());
        while (V.size() > 1) {
            Node tempV = V.remove(0);

            Node iN = null;
            int i = Integer.MIN_VALUE;

            for (Node aV : V) {
                int tempVal = tempV.getScore(aV);
                if (tempVal > i) {
                    i = tempVal;
                    iN = aV;
                }
            }
            if (iN != null) {
                tempV.mergeNode(iN);
                V.remove(iN);
                H.add(tempV);
            }
        }
        return H;
    }

    private static Arc makeWay(List<Node> nodes) {
        Arc bestArc = null;
        Arc currentArc = null;
        int bestScore = 0;
        Node jNode = null;
        Node iNode = null;


        //Collections.sort(nodes, new tagsComparator());

        int nodNum = nodes.size();


        for (int i = 0; i < nodNum - 1; i++) {

            iNode = nodes.get(i);
            //for (int j = i + 1; j < nodNum; j++) {

            for (int j = i + 1; j < nodNum; j++) {
                jNode = nodes.get(j);

                int currentSize = iNode.getInterestFactor(jNode);

                if (currentSize > 0) {
                    currentArc = new Arc(iNode, jNode, currentSize);

                }
                if (currentSize > bestScore) {
                    bestArc = currentArc;
                }
            }
        }


        return bestArc;
    }

    private static List<Node> getFinalList(List<Node> finalList, List<Node> nodes) {

        Node leftNode = finalList.get(0);
        Node rightNode = finalList.get(finalList.size() - 1);

        Arc leftMax = leftNode.getMaxArc();
        Arc rightMax = rightNode.getMaxArc();


        while (nodes.size() > 0 && (leftMax != null || rightMax != null)) {
            if (leftMax != null && (rightMax == null || leftMax.interestFactor > rightMax.interestFactor)) {
                Node temp = leftMax.returnOther(leftNode);

                leftNode.iRemovedThis();

                nodes.remove(temp);
                leftNode = temp;

                finalList.add(0, leftNode);
            } else {
                Node temp = rightMax.returnOther(rightNode);

                rightNode.iRemovedThis();
                nodes.remove(temp);
                rightNode = temp;

                finalList.add(rightNode);


            }


            leftMax = leftNode.getMaxArc();
            rightMax = rightNode.getMaxArc();
            if (leftMax != null && leftMax.equals(rightMax)) {
                leftMax.deleteArc();
                leftMax = leftNode.getMaxArc();
                rightMax = rightNode.getMaxArc();
            }

            if(rightMax!= null && leftMax!=null) {
                if (leftMax.returnOther(leftNode).equals(rightNode)) {
                    leftMax.deleteArc();
                    leftMax = leftNode.getMaxArc();
                }
                if (rightMax.returnOther(rightNode).equals(leftNode)) {
                    rightMax.deleteArc();
                    rightMax = rightNode.getMaxArc();
                }
            }
        }

        while (nodes.size() > 1 && nodes.get(0).getMaxArc() == null) {
            nodes.remove(0);
        }


        if (nodes.size() < 2) {

            return finalList;

        } else {

            List<Node> tempList = new ArrayList<>();
            Node tempN = nodes.remove(0);

            tempList.add(tempN);

            Arc tempA = tempN.getMaxArc();
            Node tempNB = tempA.returnOther(tempN);

            nodes.remove(tempNB);
            tempList.add(tempNB);

            tempA.deleteArc();


            finalList.addAll(getFinalList(tempList, nodes));
            return finalList;
        }


    }

    private static tempList readFile(String fileDir) {
        List<Node> hor = new ArrayList<Node>();
        List<Node> ver = new ArrayList<Node>();

        //metti qui il file
        File fileLoc = new File(fileDir);
        try {
            BufferedReader buffR = new BufferedReader(new FileReader(fileLoc));


            String st;
            st = buffR.readLine();
            try {
                String[] splited = st.split("\\s+");
                int currentP = 0;

                while ((st = buffR.readLine()) != null) {


                    splited = st.split("\\s+");
                    int tagNum = Integer.parseInt(splited[1]);
                    //String[] tempTag = new String[Integer.parseInt(splited[1])];
                    List<String> tempList = new ArrayList<>();

                    for (int i = 0; i < tagNum; i++) {
                        //tempTag[i] = splited[i+2];
                        tempList.add(splited[i + 2]);

                    }
                    if (splited[0].equals("H")) {
                        hor.add(new Node(currentP, 'H', tagNum, tempList));
                    } else {
                        ver.add(new Node(currentP, 'V', tagNum, tempList));
                    }
                    currentP++;
                }

               tempList AA = new tempList();
                AA.H = hor;
                AA.V = ver;

                return AA;


            } catch (Exception e) {
                System.out.println("file sbagliato");
            }
        } catch (Exception e) {
            System.out.println("boh");
        }

        return null;
    }

    private static void outputFile(String dir, List<Node> nodes) throws IOException {
        PrintWriter out = new PrintWriter(dir);


        out.println(nodes.size());
        for (Node x : nodes) {
            if (x.secondId != -1) {
                out.println(x.id + " " + x.secondId);
            } else {
                out.println(x.id);
            }
        }
        out.close();
    }
}

class tagsComparator implements Comparator<Node> {
    @Override
    public int compare(Node a, Node b) {
        return a.numTags < b.numTags ? 1 : a.numTags == b.numTags ? 0 : -1;
    }
}
