import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GetStringArrFromWeb {

    public static String[] WellWhatElse (String fn)  throws Exception {
        URL url = new URL(fn);
        URLConnection connor = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connor.getInputStream()));

        ArrayList<String> temp = new ArrayList<>();

        String current = "";
        while((current = reader.readLine()) != null) {
            temp.add(current);
        }

        String[] fin = new String[temp.size()];

        for (int i = 0; i < temp.size(); i++) {
            fin[i] = temp.get(i);
        }

        return fin;

    }

}
