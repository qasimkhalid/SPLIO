import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

public class DataTask{

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> map)
    {
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


    public static <K, V extends Comparable<V> > Map.Entry<K, V> getMaxEntryInMapBasedOnValue(Map<K, V> map)
    {
        Map.Entry<K, V> entryWithMaxValue = null;

        for (Map.Entry<K, V> currentEntry : map.entrySet()) {
            if (
                    entryWithMaxValue == null
                            || currentEntry.getValue()
                            .compareTo(entryWithMaxValue.getValue())
                            > 0) {
                entryWithMaxValue = currentEntry;
            }
        }
        return entryWithMaxValue;
    }

    public static Map<String, Integer> getAverage( Map<String, List> map){

        Map<String, Integer> newMap = new HashMap<>();

        double average;
        double sum = 0;
        int counter = 0;

        for (Map.Entry<String, List> entry: map.entrySet()) {

            //Fills the list 'value', with the values from the map entry.
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

    public static DataModel ReadJSONFile( String JSONString) {

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

        JSONObject obj_json = (JSONObject) obj ;
        JSONArray arr_records = (JSONArray) obj_json.get("records");


        List<DataModel.Records> recordsList = new ArrayList<DataModel.Records>();

        for (int i = 0; i < arr_records.size(); i++) {
            records = JSONdataModel.new Records();
            records.setRecord_timestamp(((JSONObject)arr_records.get(i)).get("record_timestamp").toString());


            JSONObject obj_fields = (JSONObject) ((JSONObject)arr_records.get(i)).get("fields");

            fieldsValues = JSONdataModel.new Fields();

            fieldsValues.setStation_name((String) obj_fields.get("station_name".toString()));
            fieldsValues.setStation_code(Integer.parseInt((String) obj_fields.get("station_code")));

            int totAvailBike = (int) (long) ((Long) obj_fields.get("nbbike") + (Long) obj_fields.get("nbebike"));
            int totFreeDock = (int) (long) ((Long) obj_fields.get("nbfreeedock") + (Long) obj_fields.get("nbfreedock"));

            fieldsValues.setTotalAvailBike(totAvailBike);
            fieldsValues.setTotalFreeDock(totFreeDock);

            recordsList.add(records);
            records.setFields(fieldsValues);
        }
        JSONdataModel.setRecords(recordsList);

        return JSONdataModel;
    }


    public static void main( String[] args ) {

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
            br.close();

            Map<Optional<Integer>, List<DataModel>> groupedByHour = dataModels.stream()
                    .filter(p -> p.getTimeStamp() != null && p.getTimeStamp().getHour() != 0)
                    .collect(Collectors.groupingBy(l->Optional.ofNullable(l.getTimeStamp().getHour())));

            Map<Integer, Map<String, Integer>> av_bikeAvailAllTime = new HashMap();
            Map<Integer, Map<String, Integer>> av_dockFreeAllTime = new HashMap();

            Map<Integer, Map.Entry<String, Integer>> maxAv_bikeAvailStationAllTime = new HashMap();
            Map<Integer, Map.Entry<String, Integer>> maxAv_dockFreeStationAllTime = new HashMap();

            List<String> listTask = new ArrayList<>() ;
            listTask.add("available bikes");
            listTask.add("free docks");


            Map<String, Integer> bikeAvailTotalPerStation = new HashMap();

            for(Object key : groupedByHour.keySet()) {
                List value = groupedByHour.get(key);
                Integer hour = Integer.parseInt((key.toString().substring(9, key.toString().length() - 1)));

                Map<String, List> stationWiseBikeAvail = new HashMap<>();
                Map<String, List> stationWiseDockFree = new HashMap<>();

                for (int i = 0; i < value.size(); i++) {

                    DataModel records = (DataModel) value.get(i);
                    List<DataModel.Records> recordsList = new ArrayList();
                    recordsList = records.getRecords();

                    for (int j = 0; j < recordsList.size(); j++) {
                        int tot_bike = (recordsList.get(j).getFields().getTotalAvailBike());
                        int tot_dock = (recordsList.get(j).getFields().getTotalFreeDock());
                        String stationName = recordsList.get(j).getFields().getStation_name();

                        if (stationWiseBikeAvail.get(stationName) == null) {
                            stationWiseBikeAvail.put(stationName, new ArrayList<Integer>());
                        }
                        stationWiseBikeAvail.get(stationName).add(tot_bike);

                        if (stationWiseDockFree.get(stationName) == null) {
                            stationWiseDockFree.put(stationName, new ArrayList<Integer>());
                        }
                        stationWiseDockFree.get(stationName).add(tot_dock);
                   }

                }

                for(Map.Entry<String, List> entry : stationWiseDockFree.entrySet()) {
                    String key2 = entry.getKey();
                    for (Object value2 : entry.getValue()) {
                        if (bikeAvailTotalPerStation.get(key2) == null) {
                            bikeAvailTotalPerStation.put(key2, (Integer) value2);
                        }
                        bikeAvailTotalPerStation.merge(key2, (Integer) value2, (oldValue, newValue) -> oldValue + newValue);
                    }
                }


                Map<String, Integer> av_BikeAvail = getAverage(stationWiseBikeAvail);
                Map<String, Integer> av_DockFree = getAverage(stationWiseDockFree);

                av_bikeAvailAllTime.put(hour,av_BikeAvail);
                av_dockFreeAllTime.put(hour,av_DockFree);

                maxAv_bikeAvailStationAllTime.put(hour,getMaxEntryInMapBasedOnValue(av_BikeAvail));
                maxAv_dockFreeStationAllTime.put(hour,getMaxEntryInMapBasedOnValue(av_DockFree));
            }
            int z = 0;
            List<Map<Integer, Map<String, Integer>>> list_av_allTime = new ArrayList<>() ;

            list_av_allTime.add(av_bikeAvailAllTime);
            list_av_allTime.add(av_dockFreeAllTime);

            List<Map<Integer, Map.Entry<String, Integer>>> list_maxAv_allTime = new ArrayList<>() ;

            list_maxAv_allTime.add(maxAv_bikeAvailStationAllTime);
            list_maxAv_allTime.add(maxAv_dockFreeStationAllTime);

            for (int i = 0; i < list_maxAv_allTime.size(); i++) {
                int max = 0;
                int hour = 0;
                String station_name = null;
                Set set = list_maxAv_allTime.get(i).entrySet();
                Iterator j = set.iterator();
                while(j.hasNext()) {
                    Map.Entry first = (Map.Entry)j.next();
                    Map.Entry second = (Map.Entry)first.getValue();
                    if((int) second.getValue() > max || max == 0){
                        hour = (int) first.getKey();
                        station_name = (String) second.getKey();
                        max = (int) second.getValue();
                    }
                }

                System.out.println("------");
                System.out.println("Average number of " + listTask.get(i) + " per station per day’s hour:");

               for (Map.Entry<Integer, Map<String, Integer>> entry : list_av_allTime.get(i).entrySet()) {
                    System.out.println(entry.getKey() + ":00h ---- Stations: " + entry.getValue().toString());
                }

                System.out.println("------");
                System.out.println("The station that has the most " + listTask.get(i) + " at hour:");
                System.out.println(" \"" +station_name + "\" " + "has the most number of "+ listTask.get(i) + " = " + max  + ", at " + hour + ":00h.");
                System.out.println("------");
                System.out.println();
            }

            System.out.println("Top 3 Velib’ stations with the most available bikes per day:");
            Map<String, Integer> bikeAvailTotalPerStation_sorted = sortByValue((HashMap<String, Integer>) bikeAvailTotalPerStation);

            ArrayList<String> keyList = new ArrayList<String>(bikeAvailTotalPerStation_sorted.keySet());
            ArrayList<Integer> valueList = new ArrayList<Integer>(bikeAvailTotalPerStation_sorted.values());

            List<String> tail_keyList = keyList.subList(Math.max(keyList.size() - 3, 0), keyList.size());
            List<Integer> tail_valueList = valueList.subList(Math.max(valueList.size() - 3, 0), valueList.size());
            for (int k = 0; k < tail_keyList.size() ; k++) {
                System.out.println(tail_keyList.get(k) +" with " + tail_valueList.get(k)+ " bikes. ");
            }
            System.out.println("------");

        }catch (IOException e) {
                e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}


