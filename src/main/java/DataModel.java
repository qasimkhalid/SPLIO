import java.time.ZonedDateTime;
import java.util.List;


public class DataModel {

    private List<Records> records;
    private ZonedDateTime timeStamp;

//    private Integer total_bikes;
//    private String total_docks;
//    private Double average;
//
//    public Integer getTotal_bikes() {
//        return total_bikes;
//    }
//
//    public void setTotal_bikes( Integer total_bikes ) {
//        this.total_bikes = total_bikes;
//    }
//
//    public String getTotal_docks() {
//        return total_docks;
//    }
//
//    public void setTotal_docks( String total_docks ) {
//        this.total_docks = total_docks;
//    }
//
//    public Double getAverage() {
//        return average;
//    }
//
//    public void setAverage( Double average ) {
//        this.average = average;
//    }





    public List<Records> getRecords ()
    {
        return records;
    }

    public ZonedDateTime getTimeStamp ()
    {
        return timeStamp;
    }

    public void setRecords (List<Records> records)
    {
        this.records = records;
    }

    public void setTimeStamp (ZonedDateTime timeS)
    {
        this.timeStamp = timeS;
    }





    public class Records
    {
        private Fields fields;
        private String record_timestamp;
        private ZonedDateTime timeStamp;

        public Fields getFields ()
        {
            return fields;
        }

        public void setFields (Fields fields)
        {
            this.fields = fields;
        }

        public String getRecord_timestamp ()
        {
            return record_timestamp;
        }

        public ZonedDateTime get_timestamp ()
        {
            return timeStamp;
        }

        public void setRecord_timestamp (String record_timestamp)
        {
            this.record_timestamp = record_timestamp;
            this.timeStamp = ZonedDateTime.parse(record_timestamp);
        }
    }





    public class Fields
    {
        private String station_name;

        private Integer station_code;

        private Integer totalAvailBike;

        private Integer totalFreeDock;

//        private Integer nbfreeedock;
//        private Integer nbfreedock;
//        private Integer nbebike;
//        private Integer nbbike;
//        private String dist;


        public String getStation_name ()
        {
            return station_name;
        }

        public void setStation_name (String station_name)
        {
            this.station_name = station_name;
        }

        public Integer getStation_code ()
        {
            return station_code;
        }

        public void setStation_code (Integer station_code)
        {
            this.station_code = station_code;
        }

        public Integer getTotalAvailBike() {
            return totalAvailBike;
        }

        public void setTotalAvailBike( Integer totalAvailBike ) {
            this.totalAvailBike = totalAvailBike;
        }

        public Integer getTotalFreeDock() {
            return totalFreeDock;
        }

        public void setTotalFreeDock( Integer totalFreeDock ) {
            this.totalFreeDock = totalFreeDock;
        }


//        public Integer getNbebike ()
//        {
//            return nbebike;
//        }
//
//        public void setNbebike (Integer nbebike)
//        {
//            this.nbebike = nbebike;
//        }
//
//        public Integer getNbbike ()
//        {
//            return nbbike;
//        }
//
//        public void setNbbike (Integer nbbike)
//        {
//            this.nbbike = nbbike;
//        }
//        public Integer getNbfreeedock ()
//        {
//            return nbfreeedock;
//        }

//        public void setNbfreeedock (Integer nbfreeedock)
//        {
//            this.nbfreeedock = nbfreeedock;
//        }
//
//
//        public Integer getNbfreedock ()
//        {
//            return nbfreedock;
//        }
//
//        public void setNbfreedock (Integer nbfreedock)
//        {
//            this.nbfreedock = nbfreedock;
//        }


//        public String getDist ()
//        {
//            return dist;
//        }
//
//        public void setDist (String dist)
//        {
//            this.dist = dist;
//        }


    }


}
