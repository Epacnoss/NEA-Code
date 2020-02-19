import java.util.Scanner;

public class Censorer {

    public static void main(String[] args) {
        try {
            System.out.println("Welcome to the Censoerer..");


            Scanner reader = new Scanner(System.in);

            while(true) {
                System.out.println("Type wd");
                String input = reader.nextLine();

                System.out.println(censorWord(input));
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String censorWord (String wd) throws Exception {
        String[] naughty = GetStringArrFromWeb.WellWhatElse("https://raw.githubusercontent.com/RobertJGabriel/Google-profanity-words/master/list.txt");


        boolean flag = false;

        for (String s : naughty) {
            if(s.charAt(0) != wd.charAt(0))
                continue;

            if(s.equals(wd))
                flag = true;
        }

        if(flag) {

            String[] letters = getCharArrayAsString(wd);
            
            wd = wd.toLowerCase();

            String newWd = "";

            int mod = ((int) Math.floor(Math.sqrt(letters.length))) + 1;

            for (int i = 0; i < letters.length; i++) {
                if(i % mod == 0)
                    newWd += letters[i];
                else
                    newWd += '*';
            }

            return newWd;
        }else
            return wd;
    }


    private static String[] getCharArrayAsString (String wd) {
        char[] chars = wd.toCharArray();
        String[] fin = new String[chars.length];

        for (int i = 0; i < chars.length; i++) {
            fin[i] = chars[i] + "";
        }

        return fin;
    }

}
