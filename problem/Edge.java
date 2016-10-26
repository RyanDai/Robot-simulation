package problem;


public class Edge {

    public final ArmConfig target;

    public Edge(ArmConfig targetNode){
        target = targetNode;

    }


	public String toString(){
        return target.toString();
    }
}
