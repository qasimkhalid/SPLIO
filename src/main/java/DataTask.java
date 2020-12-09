import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataTask{

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> map){
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    public static <K, V extends Comparable<V> > Map.Entry<K, V> getMaxEntryInMapBasedOnValue(Map<K, V> map){
        Map.Entry<K, V> entryWithMaxValue = null;

        for (Map.Entry<K, V> currentEntry : map.entrySet()) {
            if (entryWithMaxValue == null || currentEntry.getValue().compareTo(entryWithMaxValue.getValue())> 0){
                entryWithMaxValue = currentEntry;
            }
        }
        return entryWithMaxValue;
    }

    public static Map<String, Integer> getAverage( Map<String, List> map){

        Map<String, Integer> newMap = new HashMap<>();

        for (Map.Entry<String, List> entry: map.entrySet()) {

            double average;
            double sum = 0;
            int counter = 0;

            List<Integer> value = entry.getValue();
            String key = entry.getKey();

            for(double ent : value){

                sum += ent;
                counter++;
            }

            average = sum / counter;
            newMap.put(key, (int) Math.round(average));
        }
        return newMap;
    }

    public static DataModel ReadJSONFile( String JSONString){

        JSONParser parser = new JSONParser();

        DataModel JSONdataModel = new DataModel();
        DataModel.Records records;
        DataModel.Fields fieldsValues;

        Object obj = null;
        try {
            obj = parser.parse(JSONString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject objJSON = (JSONObject) obj ;
        JSONArray arrayRecords = (JSONArray) objJSON.get("records");


        List<DataModel.Records> recordsList = new ArrayList<DataModel.Records>();

        for (int i = 0; i < arrayRecords.size(); i++) {
            records = JSONdataModel.new Records();
            records.setRecord_timestamp(((JSONObject)arrayRecords.get(i)).get("record_timestamp").toString());


            JSONObject objFields = (JSONObject) ((JSONObject)arrayRecords.get(i)).get("fields");

            fieldsValues = JSONdataModel.new Fields();

            fieldsValues.setStation_name((String) objFields.get("station_name".toString()));
            fieldsValues.setStation_code(Integer.parseInt((String) objFields.get("station_code")));

            int totAvailBike = (int) (long) ((Long) objFields.get("nbbike") + (Long) objFields.get("nbebike"));
            int totFreeDock = (int) (long) ((Long) objFields.get("nbfreeedock") + (Long) objFields.get("nbfreedock"));

            fieldsValues.setTotalAvailBike(totAvailBike);
            fieldsValues.setTotalFreeDock(totFreeDock);

            recordsList.add(records);
            records.setFields(fieldsValues);
        }
        JSONdataModel.setRecords(recordsList);

        return JSONdataModel;
    }


    public static void main( String... args ) {

        BufferedReader br = null;
        List<DataModel> dataModels = new ArrayList<DataModel>();

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader("data/velib_dataset_c271c5d8-6b77-4557-845c-3b449863bbb0.txt"));
            while ((sCurrentLine = br.readLine()) != null) {


                DataModel line = ReadJSONFile(sCurrentLine);

                Optional<DataModel.Records> record = line.getRecords().stream().findFirst();
                if(record.isPresent())
                {
                    line.setTimeStamp(record.get().get_timestamp());
                }

                dataModels.add(line);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Map<Integer, List<DataModel>> groupedByHour = dataModels.stream()
                .filter(p -> p.getTimeStamp() != null && p.getTimeStamp().getHour() != 0)
                .collect(Collectors.groupingBy(l->(l.getTimeStamp().getHour())));

        Map<Integer, Map<String, Integer>> avgBikeAvailAllTime = new HashMap();
        Map<Integer, Map<String, Integer>> avgDockFreeAllTime = new HashMap();

        Map<Integer, Map.Entry<String, Integer>> maxAvgBikeAvailStationAllTime = new HashMap();
        Map<Integer, Map.Entry<String, Integer>> maxAvgDockFreeStationAllTime = new HashMap();

        List<String> listTask = new ArrayList<>();
        listTask.add("available bikes");
        listTask.add("free docks");


        Map<String, Integer> bikeAvailTotalPerStation = new HashMap();

        for(Object key : groupedByHour.keySet()) {
            List value = groupedByHour.get(key);

            Map<String, List> stationWiseBikeAvail = new HashMap<>();
            Map<String, List> stationWiseDockFree = new HashMap<>();

            for (int i = 0; i < value.size(); i++) {

                DataModel records = (DataModel) value.get(i);
                List<DataModel.Records> recordsList = new ArrayList();
                recordsList = records.getRecords();

                for (int j = 0; j < recordsList.size(); j++) {
                    int totalBike = (recordsList.get(j).getFields().getTotalAvailBike());
                    int totalDock = (recordsList.get(j).getFields().getTotalFreeDock());
                    String stationName = recordsList.get(j).getFields().getStation_name();

                    if (stationWiseBikeAvail.get(stationName) == null) {
                        stationWiseBikeAvail.put(stationName, new ArrayList<Integer>());
                    }
                    stationWiseBikeAvail.get(stationName).add(totalBike);

                    if (stationWiseDockFree.get(stationName) == null) {
                        stationWiseDockFree.put(stationName, new ArrayList<Integer>());
                    }
                    stationWiseDockFree.get(stationName).add(totalDock);
               }
            }


//Finding out the total number of bikes available per station. Later on, it will be associated by hour like others.
            for(Map.Entry<String, List> entry : stationWiseBikeAvail.entrySet()) {
                String key2 = entry.getKey();
                for (Object value2 : entry.getValue()) {
                    if (bikeAvailTotalPerStation.get(key2) == null) {
                        bikeAvailTotalPerStation.put(key2, (Integer) value2);
                    }else{
                        bikeAvailTotalPerStation.merge(key2, (Integer) value2, ( oldValue, newValue ) -> oldValue + newValue);
                    }
                }
            }


            Map<String, Integer> avgBikeAvail = getAverage(stationWiseBikeAvail);
            Map<String, Integer> avgDockFree = getAverage(stationWiseDockFree);

            avgBikeAvailAllTime.put((Integer) key, avgBikeAvail);
            avgDockFreeAllTime.put((Integer) key,avgDockFree);

            maxAvgBikeAvailStationAllTime.put((Integer) key,getMaxEntryInMapBasedOnValue(avgBikeAvail));
            maxAvgDockFreeStationAllTime.put((Integer) key,getMaxEntryInMapBasedOnValue(avgDockFree));
        }

        List<Map<Integer, Map<String, Integer>>> listAvgAllTime = new ArrayList<>() ;

        listAvgAllTime.add(avgBikeAvailAllTime);
        listAvgAllTime.add(avgDockFreeAllTime);

        List<Map<Integer, Map.Entry<String, Integer>>> listMaxAvgAllTime = new ArrayList<>() ;

        listMaxAvgAllTime.add(maxAvgBikeAvailStationAllTime);
        listMaxAvgAllTime.add(maxAvgDockFreeStationAllTime);

        for (int i = 0; i < listMaxAvgAllTime.size(); i++) {
            int max = 0;
            int hour = 0;
            String stationName = null;
            Set set = listMaxAvgAllTime.get(i).entrySet();
            Iterator j = set.iterator();
            while(j.hasNext()) {
                Map.Entry first = (Map.Entry)j.next();
                Map.Entry second = (Map.Entry)first.getValue();
                if((int) second.getValue() > max || max == 0){
                    hour = (int) first.getKey();
                    stationName = (String) second.getKey();
                    max = (int) second.getValue();
                }
            }

            System.out.println("------");
            System.out.println("Average number of " + listTask.get(i) + " per station per day’s hour:");

           for (Map.Entry<Integer, Map<String, Integer>> entry : listAvgAllTime.get(i).entrySet()) {
                System.out.println(entry.getKey() + ":00h ---- Stations: " + entry.getValue().toString());
            }

            System.out.println("------");
            System.out.println("The station that has the most " + listTask.get(i) + " at hour:");
            System.out.println(" \"" +stationName + "\" " + "has the most number of "+ listTask.get(i) + " = " + max  + ", at " + hour + ":00h.");
            System.out.println("------");
            System.out.println();
        }

        System.out.println("Top 3 Velib’ stations with the most available bikes per day:");
        Map<String, Integer> bikeAvailTotalPerStationSorted = sortByValue((HashMap<String, Integer>) bikeAvailTotalPerStation);

        ArrayList<String> keyList = new ArrayList<String>(bikeAvailTotalPerStationSorted.keySet());
        ArrayList<Integer> valueList = new ArrayList<Integer>(bikeAvailTotalPerStationSorted.values());

        List<String> tailKeyList = keyList.subList(Math.max(keyList.size() - 3, 0), keyList.size());
        List<Integer> tailValueList = valueList.subList(Math.max(valueList.size() - 3, 0), valueList.size());
        for (int k = 0; k < tailKeyList.size() ; k++) {
            System.out.println(tailKeyList.get(k) +" with " + tailValueList.get(k)+ " bikes. ");
        }
        System.out.println("------");

    }

}


