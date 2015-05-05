package puzzle;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;

public class Tree extends Group{

    
    List<List<Branch>> generations = new ArrayList<List<Branch>>();
    List<Branch> crown = new ArrayList<Branch>();
    List<Flower> flowers = new ArrayList<Flower>();
    List<Leaf> leafage = new ArrayList<Leaf>();
 
    public Tree(int depth) {
        for (int i = 0; i < depth; i++) {
            generations.add(new ArrayList<Branch>());
        }
    }
}
