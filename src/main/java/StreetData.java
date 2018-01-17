import java.util.HashSet;
import java.util.Set;

/**
 * Created by vgorokhov on 17.01.2018.
 */
public class StreetData {
    private long id;
    private int count = 0;
    private Set<String> classRoutes = new HashSet<String>();

    StreetData(long id){
        this.id = id;
    }

    public void addCount(){
        this.count++;
    }

    public void addSegment(String classRoute){

    }
}
