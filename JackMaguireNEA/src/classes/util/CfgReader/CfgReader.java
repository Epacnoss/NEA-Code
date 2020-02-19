package classes.util.CfgReader;

import main.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class CfgReader { //config file reader

    //example config file - enemy, used comment lines to distinguish from actual config file
    //NB: Attribute and Property are used interchangeably

    /*
    module playerSees { //module - a section in the file
        name=sSkeleton //composition of attribute - $(VARIABLE_NAME)=$(TYPE)$(VALUE) - variable name is the name of the attribute - type is either s, d, or i, signifiying string, double or int and the value is the value
        speed=s3 tiles per second
        info=sThis skeleton is faster than he seems, but doesn't have very much health.
        heartsLost=sIf he gets past the defences, he costs 1 life.
        hp=i15
    }
    module compSees {
        spd=i12
        heartsLost=i1
        initial=sB
        hp=i15
        moneyBack=i3
    }

     */

    private static final Pattern PROPERTY_REGEX = Pattern.compile("^ {4}[a-zA-Z0-9 \\._\\-,'!@£$%^&*()¡€#¢∞§¶•ªº<>]+=([ids])[a-zA-Z0-9 \\._\\-,'!@£$%^&*()¡€#¢∞§¶•ªº<>]+"); //wide array of characters for edge cases
    private static final Pattern MODULE_START_REGEX = Pattern.compile("^module [a-zA-Z0-9 \\._\\-,'!@£$%^&*()¡€#¢∞§¶•ªº<>]+ \\{");

    private ArrayList<String> moduleNames; //the names of all the modules
    private ArrayList<HashMap<String, Object>> hashMaps; //all of the modules in an arraylist
    private BufferedReader reader; //the reader for the file
    private String fn; //the file name

    public CfgReader(String fn) {
        this.fn = fn; //set the fileName

        moduleNames = new ArrayList<>(); //init arrayLists
        hashMaps = new ArrayList<>();
        try {
            URL url = new URL(fn);
            URLConnection connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //start the bufferedReader
        } catch (Exception e) {
            e.printStackTrace();
        }

        read(); //set up the hashMaps
    }

    public CfgReader clone () { //cloning method
        return new CfgReader(fn);
    }

    private void read () { //read the config
        String currentLine = ""; //the contents of the currentLine

        String thisModuleName = ""; //the currentModule name
        HashMap<String, Object> currentModule = new HashMap<>(); //the currentModule

        String propertyName = ""; //the current property name
        String propertyValue = ""; //the current property value

        int equalsLocation; //the location of the equals sign

        try {
            while ((currentLine = reader.readLine()) != null) {//while we still can read more

                if(currentLine == "")
                    continue;


                char[] chars = currentLine.toCharArray(); //get the current line in chars

                if(MODULE_START_REGEX.matcher(currentLine).matches()) //if the current line is the start of a module
                {
                    for (int i = 7; i < chars.length - 2; i++)
                    { //Start at 7 to get the name, and end two before not to get the end space or the starting bracket
                        thisModuleName += chars[i] + ""; //add the module name
                    }

                }
                else if (currentLine.contains("}")) //or if the line is the end of a module
                {
                    moduleNames.add(thisModuleName); //add the name
                    hashMaps.add(((HashMap<String, Object>) currentModule.clone())); //add the currentModule (cloned - to avoid confusion when currentModule is cleared)

                    thisModuleName = ""; //reset name
                    currentModule.clear(); //reset currentModule
                }
                else if (PROPERTY_REGEX.matcher(currentLine).matches()) //or if this line is a standard property
                {
                    equalsLocation = currentLine.indexOf("="); //set the equals location

                    boolean isInt = false; //booleans for if it is an int,
                    boolean isStr = false; //a string,
                    boolean isDbl = false; //or a double

                    for (int i = 4; i < chars.length; i++)
                    { //Begin at 4 to not include whitespace
                        if(i == equalsLocation) //if the currentcharacter is the equalsSign - continue
                            continue;
                        else if (i == equalsLocation + 1) //or if we are one character ahead
                        {
                            if(chars[i] == 's')
                                isStr = true;
                            else if (chars[i] == 'i')
                                isInt = true;
                            else if (chars[i] == 'd') //set the booleans dependent on the first character after
                                isDbl = true;
                        }
                        else if (i < equalsLocation)
                            propertyName += chars[i]; //if the current location is before the equals or it is the property name, add the character to the property name
                        else if (i > equalsLocation + 1)
                            propertyValue += chars[i]; //or if after the equals and the type, then
                    }

                    if(isInt) //if it is an int
                    {
                        if(!main.INT_REGEX.matcher(propertyValue).matches())
                            return;

                        int value = Integer.parseInt(propertyValue); //get the value from the property value
                        currentModule.put(propertyName, value); //put it in the currentModule
                    }
                    else if (isStr) //if it is a string
                        currentModule.put(propertyName, propertyValue); //put it in
                    else if (isDbl) //if it is a double
                    {
                        if(!main.DBL_REGEX.matcher(propertyValue).matches())
                            return;

                        double value = Double.parseDouble(propertyValue); //get the value
                        currentModule.put(propertyName, value); //put it in
                    }


                    propertyName = "";
                    propertyValue = ""; //reset property name and value
                }
            }

            reader.close(); //close bufferedReader
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object get (String module, String property) { //get a specific property
        if(!moduleNames.contains(module))
            return null;

        String propValue = getModule(module).get(property).toString();

        return propValue;
    }

    public HashMap<String, Object> getModule (String module) { // get a whole module
        if(!moduleNames.contains(module))
            return null;

        int modIndex = moduleNames.indexOf(module);
        HashMap<String, Object> modToGet = hashMaps.get(modIndex);
        return ((HashMap<String, Object>) modToGet.clone());
    }

}
