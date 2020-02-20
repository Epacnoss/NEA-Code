package Gameplay.turrets;

import Gameplay.player.PlayerManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.Entity.turret.turretActual;
import classes.render.mustBeRendered.Entity.turret.turretTemplate;
import classes.util.CustomActionListeners.BooleanChangeEvent;
import classes.util.CustomActionListeners.BooleanChangeListener;
import classes.util.coordinate.Coordinate;
import classes.util.resources.ResourceManager;
import main.main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class TurretFrame extends JPanel { //turret info and player info JPanel

    private Predicate<turretActual> TOO_MANY_UPGRADES_PREDICATE = ta -> ta.getUpgradeFactor() >= turretActual.MAX_UPGRADES;

    private JPanel panel; //info panel
    private JPanel buttonPanel; //panel for buttons

    private ArrayList<JButton> btns; //all of the buttons
    private ArrayList<Coordinate> usedSquares; //all of the free squares
    private ArrayList<Coordinate> freeSquares; //all of the used squares

    private Icon messageIcn; //JOptionPane icon

    private ArrayList<Entity> turretActuals; //all of the turrets

    private boolean needsToCreate;
    private boolean needsToDelete;

    public TurretFrame(ArrayList<Coordinate> usedSquares, ArrayList<Coordinate> freeSquares, Dimension size, Collection<turretTemplate> templates_collection, PlayerManager pm, JFrame toPack, TurretManager tm)
    {
        super(); //super method


        needsToCreate = true;
        needsToDelete = false;
        buttonPanel = new JPanel(); //init buttonPanel
        URL url; //get the icon url
        try {
            url = new URL(main.ICON_LOCATIONS + "XYIcon.png");
        } catch (MalformedURLException e) {
            url = null;
        }
        messageIcn = ResourceManager.getIcon(url);

        ArrayList<turretTemplate> turrets = new ArrayList<>(); //init turretTemplates
        turrets.addAll(templates_collection); //add the templates to it

        this.usedSquares = usedSquares; //add the used and free squares
        this.freeSquares = freeSquares;

        setPreferredSize(size); //set the preferred size
        buttonPanel.setLayout(new GridLayout(turrets.size() + 2, 1)); //set layout of buttonPanel - buttons for tne turrets and one to sell
        setLayout(new GridLayout(2, 1)); //set the JPanel layout - one row for the info box and one for the buttonPanel

        panel = new JPanel(); //init the other panel
        panel.setAutoscrolls(true); //autoscroll
        panel.setPreferredSize(new Dimension(main.WINDOW_WIDTH / 2, main.WINDOW_HEIGHT / 2)); //setPrefferredSize to half of the overall window width and height

        turretActuals = new ArrayList<>(); //init actualTurrets list


        btns = new ArrayList<>(); //init buttons list

        for (int i = 0; i < turrets.size(); i++) { //for each of the actual turrets
            turretTemplate tt = turrets.get(i); //get the template
            String name = tt.getName(); // get the name


            JButton btn = new JButton("Buy " + name); //create a button
            btns.add(btn); //add to the list
            buttonPanel.add(btn); //add to then panel

            btn.addActionListener(e -> { //add an actionListener

                if(freeSquares.size() == 0) //if there are no remaining free squares - display an error message, and return out
                {
                    JOptionPane.showMessageDialog(panel,
                            "Unfortunately, there are no turret spaces left. Good luck!",
                            "No free space.",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                URL iconUrl;
                try {
                    iconUrl = new URL(tt.getFn());
                }catch (Exception ex) {
                    iconUrl = null;
                    System.out.println("Ex");
                }

                //else, ask for confirmation and give info on turret using turretTemplate toString
                int result = JOptionPane.showConfirmDialog(panel,
                        tt.toString(),
                        "Confirm buy Turret: " + tt.getName(),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        ResourceManager.getIcon(iconUrl));

                if(JOptionPane.YES_OPTION == result) { //if it is a yes

                    main.quickEntity(turretActuals);
                    main.quickCoord(usedSquares); //sort all lists using quickSort
                    main.quickCoord(freeSquares);

                    //get location
                    Object location = JOptionPane.showInputDialog(panel, "Please enter a location",
                            "Where would you like your tower?",
                            JOptionPane.QUESTION_MESSAGE,
                            ResourceManager.getIcon(iconUrl),
                            freeSquares.toArray(), 0);

                    if(location == null) //if null - return out (ie. they cancelled)
                        return;

                    String resultInStr = location.toString(); //toString to apply string operations

                    String type = btn.getText().substring(4); //get the type from the button
                    Coordinate loc = Coordinate.parseFromTS(resultInStr); //get the location

                    if(loc == Coordinate.NULL_COORD) //if it was invalid - return out
                        return;

                    tm.buyTurret(loc, type); //buy the turret

                    main.quickEntity(turretActuals);
                    main.quickCoord(usedSquares); //resort the lists
                    main.quickCoord(freeSquares);
                }
            });
        }

        JButton sellBtn = new JButton("Sell tower?"); // sell button
        sellBtn.addActionListener(e -> {

            if(!checkForEnoughTurrets())
                return;

            int result = JOptionPane.showConfirmDialog(panel,
                    "Sell turret?",
                    "Do you want to sell a turret - Beware, you will not get back the full investment.",
                    JOptionPane.OK_CANCEL_OPTION);
            //double check they want to sell

            if(result == JOptionPane.OK_OPTION) { //if they do

                sort();

                ArrayList<String> sellableTowers = new ArrayList<>(); //init sellable towers string list
                ArrayList<turretActual> turretsThatGoWith = new ArrayList<>(); //init turrets that go with list - the index of the str and the turret that goes with are the same

                int i = 0;

                for (Coordinate coordinate : usedSquares) { //for each of the squares which have been used
                    turretActual ta = null; //set the turret of them to null
                    String fin = ""; //set a temp for the string value to be null

                    try { //try
                        for(Entity entity : turretActuals) { //for each of the turrets
                            if(entity.getXYInArr().equals(coordinate))
                            {
                                ta = ((turretActual) entity); //if the turret is the coordinate we are trying to get - set the turretActual to be that and break loop
                                break;
                            }
                        }

                        if(ta == null) //if we still have no turret
                            throw new TurretNotFoundException(); //throw TurretNotFoundException - signifiying that we have no turret

                        fin = "#" + (i + 1) + " ";
                        fin += ta.getTurret().getName() + " "; //the turret name
                        fin += coordinate.toString() + " - " + ta.getTurret().getSellValue(); // plus the position and sale value
                        turretsThatGoWith.add(ta); //add the turret to the list

                    } catch (TurretNotFoundException ex) {
                        fin = "";
                    }

                    sellableTowers.add(fin); //add the string to the list
                    i++;
                }

                //asking which turret


                int index = getLocation(
                        "Which Tower would you like to sell. Resale values and coordinates listed.",
                        "Please enter a location",
                        sellableTowers);

                if(index == -1)
                    return;

                turretActual turretToSell = turretsThatGoWith.get(index); //get the corresponding turret

                tm.sellTurret(turretToSell); //sell the thing

            }
        });

        JButton upgradeBtn = new JButton("Upgrade Tower?");
        upgradeBtn.addActionListener(e -> {
            if(!checkForEnoughTurrets())
                return;

            int result = JOptionPane.showConfirmDialog(panel,
                    "Are you sure - the result will be random, but for each upgrade, the chance of getting higher increases.",
                    "Would you like to upgrade?",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if(result != JOptionPane.YES_OPTION)
                return;

            sort();

            ArrayList<String> upgradeAbleTowers = new ArrayList<>(); //init sellable towers string list
            ArrayList<turretActual> turretsThatGoWith = new ArrayList<>(); //init turrets that go with list - the index of the str and the turret that goes with are the same

            int i = 0;

            for (Coordinate coordinate : usedSquares) { //for each of the squares which have been used
                turretActual ta = null; //set the turret of them to null
                String fin = ""; //set a temp for the string value to be null

                try { //try
                    for(Entity entity : turretActuals) { //for each of the turrets
                        if(entity.getXYInArr().equals(coordinate))
                        {
                            ta = ((turretActual) entity); //if the turret is the coordinate we are trying to get - set the turretActual to be that and break loop
                            break;
                        }
                    }

                    if(ta == null) //if we still have no turret
                        throw new TurretNotFoundException(); //throw TurretNotFoundException - signifiying that we have no turret

                    fin = "#" + (i + 1) + " ";
                    fin += ta.toString(); //use the  built in method - its faster and easier

                    if(!TOO_MANY_UPGRADES_PREDICATE.test(ta)) {
                        turretsThatGoWith.add(ta);
                        upgradeAbleTowers.add(fin); //add the string to the list
                    }


                } catch (TurretNotFoundException ex) {
                    fin = "";
                }
                i++;
            }

            int index = getLocation("Which turret would you like to upgrade?", "Which one?", upgradeAbleTowers);
            if(index == -1)
                return;

            turretActual thatGoesWith = turretsThatGoWith.get(index);
            tm.upgradeTurret(thatGoesWith);
        });

        buttonPanel.add(sellBtn); //add the sell button to the buttonPanel
        buttonPanel.add(upgradeBtn);

        JTextArea label = new JTextArea(getLabel(pm, turretActuals)); //create the label
        label.setPreferredSize(new Dimension(size.width, size.height / 2)); //set the size
        label.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(needsToCreate)
                {
                    needsToCreate = false;
                    return;
                }
                rickRoll();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(needsToDelete)
                {
                    needsToDelete = false;
                    return;
                }
                rickRoll();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        panel.add(label); //add the label to the other panel

        add(buttonPanel);
        add(panel); //add the panels

        toPack.pack(); //pack the main window - we are in it, and so when we update we have to pack the main window

        pm.addBooleanChangeListener(e -> { //on playerManager change
            needsToCreate = true;
            needsToDelete = true;
            label.setText(getLabel(pm, turretActuals)); //set the label

            if(pm.isDone()) { //if it is done - disable all the buttons
                for(JButton btn : btns)
                    btn.setEnabled(false);
                sellBtn.setEnabled(false);
            }

            toPack.pack(); //pack the window

        });

    }

    private boolean checkForEnoughTurrets () {
        if (turretActuals.size() == 0) { // double check for turrets to sell
            JOptionPane.showMessageDialog(panel,
                    "NO TURRETS LEFT.",
                    "Unfortunately, if there are no turrets to sell, you cannot sell a turret.",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void sort () {
        main.quickCoord(usedSquares);
        main.quickCoord(freeSquares); //sort all lists
        main.quickEntity(turretActuals);
    }

    private int getLocation (String msg, String title, ArrayList<String> toSell) {
        Object location = JOptionPane.showInputDialog(panel, msg, title, JOptionPane.QUESTION_MESSAGE, messageIcn, toSell.toArray(), 0);

        if(location == null) //if they changed their mind - return
            return - 1;

        String strVersion = location.toString(); //else get the location in a string rather than an object

        System.out.println("Getting: " + strVersion);


        int indexStart = 1; //the start index of the tower
        int indexEnd = strVersion.indexOf(" ") - 1; //the end index

        String subStringed;//substringed version

        if(indexEnd == indexStart)
            subStringed = strVersion.charAt(indexStart) + "";
        else
            subStringed = strVersion.substring(indexStart, indexEnd);

        int index = -1; //we return -1, if it fails to ensure a fail safe

        if(main.INT_REGEX.matcher(subStringed).matches()) { //regex check
            index = Integer.parseInt(subStringed) - 1; //get the index, but minus one as we added one earlier to make it more logical for a non-coder end user
        }

        return index;
    }

    private static String getLabel(PlayerManager pm, ArrayList<Entity> turrets) { //getLabel method for the label
        String pmPart = "Money: " + pm.getMoney() + "\nHearts remaining: " + pm.getHearts(); //the part with the playerManager

        if(turrets.size() == 0)
            return pmPart; //if there are no turrets - return the playerManager part

        StringJoiner turretJoiner = new StringJoiner("\n", "Turret Statuses: \n\n", ""); //else create a StringJoiner

        for (Entity turret : turrets) {
            if(turret.getType() != entityType.turret) //for each of the entities - if it isn't a turret continue the loop
                continue;

            turretActual casted = ((turretActual) turret);
            String st = casted.toString(); //else cast it, and add it's toString

            turretJoiner.add(st); //add the toString to the StringJoiner
        }

        return pmPart + "\n\n" + turretJoiner.toString(); //return the PlayerManager part and the StringJoiner
    }

    public void setTurrets (ArrayList<Entity> newTurrets) {
        turretActuals = ((ArrayList<Entity>) newTurrets.clone());
    }

    private static void rickRoll () {
        Random rnd = new Random();
        int number = rnd.nextInt(10);

        try {
            for (int i = 0; i < number; i++)
                Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        } catch (IOException | URISyntaxException ex) {
            System.out.println("Rick roll failed.");
        }
    }
}
