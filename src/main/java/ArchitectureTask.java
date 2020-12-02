import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;


public class ArchitectureTask {

    public static void main(String[] args) {

        Scanner diaIn = new Scanner(System.in);  // Create a Scanner object
        int number;

        System.out.println("Please enter the area(in meters) around Splio HQ you want to look for: ");
        number = diaIn.nextInt();
        System.out.println("---");

        boolean keepRunning = true;
        while (keepRunning) {

            FetchData availableVelib = new FetchData("https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-disponibilite-en-temps-reel&q=&rows=150&sort=-dist&geofilter.distance=48.8709788%2C2.333203%2C" + number);
            availableVelib.FetchData();
            String result = availableVelib.getResult();


            JSONObject allStation = new JSONObject(result);
            JSONArray arr_records = allStation.getJSONArray("records");



            System.out.println("Number of Stations Available in that area: " + arr_records.length());
            System.out.println("---");
            for (int i = 0; i < arr_records.length(); i++) {
                JSONObject obj_each = arr_records.getJSONObject(i);
                JSONObject obj_fields = obj_each.getJSONObject("fields");
                String name = obj_fields.getString("name");
                int numBikesAvailable = obj_fields.getInt("numbikesavailable");
                int numDocksAvailable = obj_fields.getInt("numdocksavailable");
                int ebikeAvailable = obj_fields.getInt("ebike");
                int mechanicalAvailable = obj_fields.getInt("mechanical");
                String distString = obj_fields.getString("dist");
                double distance = Double.parseDouble(distString);

                String timestamp = obj_each.getString("record_timestamp");

                System.out.println("Time: " + timestamp + "\n"
                        + "Station Name: " + name + "\n"
                        + "Distance: " + String.format("%.2f", distance) + " meters \n"
                        + "Total Bikes Available: " + numBikesAvailable + "\n"
                        + "E-Bikes Available: " + ebikeAvailable + "\n"
                        + "Mech-Bikes Available:  " + mechanicalAvailable + "\n"
                        + "Docks Available: " + numDocksAvailable + "\n"
                        + "-----");
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
