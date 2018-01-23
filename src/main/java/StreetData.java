import java.util.HashSet;
import java.util.Set;

/**
 * Created by vgorokhov on 17.01.2018.
 */
public class StreetData {
    private String name;
    private int count = 0;
    private Set<String> classRoutes = new HashSet<String>();

    StreetData (String name){
        this.name = name;
    }

    public void printClasses(){
        for (String s: classRoutes) {
            System.out.println(s);
        }
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void addCount(){
        this.count++;
    }

    public void addSegment(String classRoute){
        classRoutes.add(classRoute);
        addCount();
    }
}
