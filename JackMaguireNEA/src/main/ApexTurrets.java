/*
Copyright 2020 Jack Maguire

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package main;

import javax.swing.*;

import static main.main.lvl;

public class ApexTurrets {


    public static void main(String[] args) { //main method
        if(JOptionPane.showConfirmDialog(null, "Would you like to play a game of Apex Turrets?", "Apex Turrets?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) != JOptionPane.YES_OPTION)
            System.exit(0);

        Object[] lvls = {1, 2, 3};
        Object result = JOptionPane.showInputDialog(null, "Which level would you like to play?", "Level?", JOptionPane.INFORMATION_MESSAGE, null, lvls, 0);
        if(result == null)
            System.exit(0);

        boolean again = lvl(Integer.parseInt(result.toString()));
        if(!again)
            System.exit(0);
        else
            main(null);
    }

}
