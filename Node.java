import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import static java.util.stream.Collectors.toList;

public class Node {


        //for other versions
    public static final int delta = 15;

    public int id = 0;
    public int secondId = -1;
    public char orientation;
    public int numTags = 0;
    public List<String> tags;

    public List<Arc> archi = new ArrayList<Arc>();


    public Node(int id, char orientation, int numTags, List<String> tags) {
        this.id = id;
        this.orientation = orientation;
        this.numTags = numTags;
        this.tags = tags;
    }


    public void mergeNode(Node x) {
        if (x.orientation == 'H' || this.orientation == 'H') {
            System.out.println("Mona non fondere roba orizzontale!!!!");
            return;
        }

        this.secondId = x.id;

        for (String t : x.tags) {
            if (!this.tags.contains(t))
                this.tags.add(t);
        }

        this.numTags = this.tags.size();
        this.orientation = 'H';

    }

    public int getCommonTags(Node nod) {
        int num = 0;
        for (String x : nod.tags) {
            if (tags.contains(x))
                num++;
        }
        return num;
    }

    public int getInterestFactor(Node nod) {
        int commTags = this.getCommonTags(nod);
        return Math.min((tags.size() - commTags), Math.min(commTags, (nod.tags.size() - commTags)));
    }

    public void addArc(Arc A) {
        this.archi.add(A);
    }

    public void removeArc(Arc A) {
        this.archi.remove(A);
    }

    public void iRemovedThis() {
      //  test.deleteArc();

        while(archi.size()>0){
            archi.get(0).deleteArc();
        }
    }




    public Arc getMaxArc(){
        if(hasArc()){
            int temp = archi.indexOf(testMax());
            return archi.get(temp);
        }
        return null;
    }
    private Arc testMax(){
        Arc max = archi.get(0);
            for(Arc a : archi){
                if(a.interestFactor>max.interestFactor){
                    max = a;
                }
            }

        return max;
    }
/*
    public Arc getMaxArc() {
        if(hasArc()) {
            if (archi.size() > delta) {
                List<Arc> tempArcs = new ArrayList<>();
                for (int i = 0; i < delta; i++) {
                    tempArcs.add(archi.remove(0));
                }

                Collections.sort(tempArcs, new interestComparator());

                for (int j = delta / 2; j >= 0; j++) {
                    archi.add(tempArcs.remove(j));
                }

                archi.addAll(tempArcs);

            } else {
                Collections.sort(archi, new interestComparator());
            }


            return archi.get(0);
        } else {
            return null;
        }
    }

*/
    public boolean hasArc(){
        return archi.size() > 0;
    }

    public int getScore(Node compareWith){
        int waste = this.getCommonTags(compareWith);
        int bonus = compareWith.numTags - waste;

            //todo find better formula
        //return bonus - (waste * 2);
        return bonus;
    }


}
class interestComparator implements Comparator<Arc> {
    @Override
    public int compare(Arc a, Arc b) {
        return a.interestFactor > b.interestFactor ? -1 : a.interestFactor == b.interestFactor ? 0 : 1;
    }
}