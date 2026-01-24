package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            CSVReader reader = new CSVReader(new StringReader(csvString));
            java.util.List<String[]> rows = reader.readAll();
            
            String[] header = rows.get(0); // first row
            java.util.List<String[]> dataRows = rows.subList(1, rows.size());
        
            JsonArray colHeadings = new JsonArray();
            for (String h : header) {
                colHeadings.add(h);
            }
            
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            for (String[] row : dataRows) {
                prodNums.add(row[0]); // ProdNum

                JsonArray rowData = new JsonArray();
                rowData.add(row[1]);                         // Title
                rowData.add(Integer.parseInt(row[2]));       // Season (int)
                rowData.add(Integer.parseInt(row[3]));       // Episode (int)
                rowData.add(row[4]);                         // Stardate
                rowData.add(row[5]);                         // OriginalAirdate
                rowData.add(row[6]);                         // RemasteredAirdate

                data.add(rowData);
            }
            
            JsonObject root = new JsonObject();
            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);
            
            result = root.toJson();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            JsonObject root = Jsoner.deserialize(jsonString, new JsonObject());
            
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray data = (JsonArray) root.get("Data");
            
            java.util.List<String[]> rows = new java.util.ArrayList<>();
            
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i <colHeadings.size(); i++) {
                header[i] = (String) colHeadings.get(i);
            }
            rows.add(header);
            
            for (int i = 0; i < prodNums.size(); i++) {
                String prodNum = (String) prodNums.get(i);
                JsonArray rowData = (JsonArray) data.get(i);

                String[] row = new String[7];
                row[0] = prodNum;
                row[1] = (String) rowData.get(0); // Title

                int season = ((Number) rowData.get(1)).intValue();
                int episode = ((Number) rowData.get(2)).intValue();

                row[2] = String.format("%d", season);        // Season (no leading zero)
                row[3] = String.format("%02d", episode);     // Episode (with leading zero)

                row[4] = (String) rowData.get(3); // Stardate
                row[5] = (String) rowData.get(4); // OriginalAirdate
                row[6] = (String) rowData.get(5); // RemasteredAirdate

                rows.add(row);
            }
            
            java.io.StringWriter sw = new java.io.StringWriter();
            CSVWriter writer = new CSVWriter(sw);
            writer.writeAll(rows);
            writer.close();
            
            result = sw.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
