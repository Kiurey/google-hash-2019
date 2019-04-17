import java.util.Comparator;

public class Arc {


    Node A;
    Node B;

    int interestFactor = 0;


    public Arc(Node one, Node two, int interest){

        this.A = one;
        this.B = two;

        interestFactor = interest;

        A.addArc(this);
        B.addArc(this);

    }

    public Node returnOther(Node nodeOne){
        if(A.equals(nodeOne)){
            return B;
        } else {
            return A;
        }
    }
  /*
    private int getInterestFactor(){

        int temp = getCommonTags();

        interestFactor =    Math.min(interestFactor,
                            Math.min(A.getTagSize() - temp, B.getTagSize() - temp));

    }

    private int getCommonTags(){
            int num = 0;
            for (String x : A.tags){
                if (B.tags.contains(x))
                    num++;
            }
            return num;
    }
    */
    public void deleteArc(){
        A.removeArc(this);
        B.removeArc(this);
    }






}
