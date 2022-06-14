import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //  Создание data.csv:
//        List<String[]> list = new ArrayList<>();
//        list.add("1,John,Smith,USA,25".split(","));
//        list.add("2,Inav,Petrov,RU,23".split(","));
//        String fileName = "data.csv";
//        createCsvFile(fileName, list);

        //  Создание data.xml:
//        String fileName2 = "data.xml";
//        try {
//            createXmlFile(fileName2);
//        } catch (ParserConfigurationException | TransformerException e) {
//            e.printStackTrace();
//        }

        //  Код для 1-го задания:
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName3 = "data.csv";
        List<Employee> list2 = parseCSV(columnMapping, fileName3);
        String json = listToJson(list2);
        String fileName4 = "data.json";
        writeString(json, fileName4);

        //  Код для 2-го задания:
        List<Employee> list3 = new ArrayList<>();
        String fileName5 = "data.xml";
        try {
            list3 = parseXML(fileName5);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        String fileName6 = "data2.json";
        writeString(listToJson(list3), fileName6);

    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException,
            IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NodeList employeeChildNodes = node.getChildNodes();
            String[] text = new String[employeeChildNodes.getLength()];
            for (int j = 0; j < employeeChildNodes.getLength(); j++) {
                Node node1 = employeeChildNodes.item(j);
                text[j] = node1.getTextContent();
            }
            long id = 0;
            String firstName = text[1];
            String lastName = text[2];
            String country = text[3];
            int age = 0;
            try {
                id = Integer.parseInt(text[0]);
                age = Integer.parseInt(text[4]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            list.add(new Employee(id, firstName, lastName, country, age));
        }
        return list;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void createCsvFile(String fileName, List<String[]> list) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createXmlFile(String fileName) throws ParserConfigurationException,
            TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element staff = doc.createElement("staff");
        doc.appendChild(staff);
        Element employee = doc.createElement("employee");
        staff.appendChild(employee);
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode("1"));
        employee.appendChild(id);
        Element firstName = doc.createElement("firstName");
        firstName.appendChild(doc.createTextNode("John"));
        employee.appendChild(firstName);
        Element lastName = doc.createElement("lastName");
        lastName.appendChild(doc.createTextNode("Smith"));
        employee.appendChild(lastName);
        Element country = doc.createElement("country");
        country.appendChild(doc.createTextNode("USA"));
        employee.appendChild(country);
        Element age = doc.createElement("age");
        age.appendChild(doc.createTextNode("25"));
        employee.appendChild(age);
        Element employee2 = doc.createElement("employee");
        staff.appendChild(employee2);
        Element id2 = doc.createElement("id");
        id2.appendChild(doc.createTextNode("2"));
        employee2.appendChild(id2);
        Element firstName2 = doc.createElement("firstName");
        firstName2.appendChild(doc.createTextNode("Inav"));
        employee2.appendChild(firstName2);
        Element lastName2 = doc.createElement("lastName");
        lastName2.appendChild(doc.createTextNode("Petrov"));
        employee2.appendChild(lastName2);
        Element country2 = doc.createElement("country");
        country2.appendChild(doc.createTextNode("RU"));
        employee2.appendChild(country2);
        Element age2 = doc.createElement("age");
        age2.appendChild(doc.createTextNode("23"));
        employee2.appendChild(age2);

        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(fileName));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }
}
