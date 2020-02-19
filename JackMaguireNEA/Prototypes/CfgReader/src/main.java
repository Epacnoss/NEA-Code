public class main {

    public static void main(String[] args) {
        CfgReader hm = new CfgReader("https://raw.githubusercontent.com/Epacnoss/NEAAssets/master/Tests/CfgReader/tower.cfg");

        String name = hm.get("playerSees", "name").toString();

        int cost = Integer.parseInt(hm.get("compSees", "cost").toString());
        int upgrade = Integer.parseInt(hm.get("compSees", "upgrade").toString());

        int costPlusUpgrade =  cost + upgrade;

        System.out.println("Cost Plus Upgrade for " + name + " = " + costPlusUpgrade);

    }

}
