import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class CfgReader {

    private ArrayList<String> moduleNames;
    private ArrayList<HashMap<String, Object>> hashMaps;
    private BufferedReader reader;

    public CfgReader(String fn) {
        moduleNames = new ArrayList<>();
        hashMaps = new ArrayList<>();
        try {
            URL url = new URL(fn);
            URLConnection connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        read();
    }

    private void read () {
        String currentLine = "";

        String thisModuleName = "";
        HashMap<String, Object> currentModule = new HashMap<>();

        String propertyName = "";
        String propertyValue = "";

        int equalsLocation = 0;

        try {
            while ((currentLine = reader.readLine()) != null) {
                char[] chars = currentLine.toCharArray();

                if(currentLine.contains("module"))
                {
                    for (int i = 7; i < chars.length - 2; i++) { //Start at 7 to get the name, and end two before not to get the end space or the starting bracket
                        thisModuleName += chars[i] + "";
                    }
                } else if (currentLine.contains("}"))
                {
                    moduleNames.add(thisModuleName);
                    hashMaps.add(((HashMap<String, Object>) currentModule.clone()));

                    thisModuleName = "";
                    currentModule.clear();
                } else if (currentLine.contains("="))
                {
                    equalsLocation = currentLine.indexOf("=");

                    boolean isInt = false;
                    boolean isStr = false;

                    for (int i = 4; i < chars.length; i++) { //Begin at 4 to not include whitespace
                        if(i == equalsLocation)
                            continue;
                        else if (i == equalsLocation + 1)
                        {
                            if(chars[i] == 's')
                                isStr = true;
                            else if (chars[i] == 'i')
                                isInt = true;
                        }
                        else if (i < equalsLocation)
                            propertyName += chars[i];
                        else if (i > equalsLocation + 1)
                            propertyValue += chars[i];
                    }

                    if(isInt)
                    {
                        int value = Integer.parseInt(propertyValue);
                        currentModule.put(propertyName, value);
                    }else if (isStr)
                        currentModule.put(propertyName, propertyValue);


                    propertyName = "";
                    propertyValue = "";
                    equalsLocation = 0;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object get (String module, String property) {
        int moduleIndex = moduleNames.indexOf(module);
        HashMap<String, Object> moduleToGet = hashMaps.get(moduleIndex);
        String propValue = moduleToGet.get(property).toString();

        return propValue;
    }

}
