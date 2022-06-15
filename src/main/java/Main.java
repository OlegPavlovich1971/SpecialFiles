import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.parser.ParseException;
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
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "data.csv";
        String fileName2 = "data.xml";
        String fileName3 = "data.json";
        String fileName4 = "data2.json";

        //  Создание data.csv:
//        List<String[]> list = new ArrayList<>();
//        list.add("1,John,Smith,USA,25".split(","));
//        list.add("2,Inav,Petrov,RU,23".split(","));
//        createCsvFile(fileName, list);

        //  Создание data.xml:
//        try {
//            createXmlFile(fileName2);
//        } catch (ParserConfigurationException | TransformerException e) {
//            e.printStackTrace();
//        }

        //  Код для 1-го задания:
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list2 = parseCSV(columnMapping, fileName);
        String json = listToJson(list2);
        writeString(json, fileName3);

        //  Код для 2-го задания:
        List<Employee> list3 = new ArrayList<>();
        try {
            list3 = parseXML(fileName2);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        writeString(listToJson(list3), fileName4);

        //  Код для 3-го задания:
        String json2 = readString(fileName3);
        List<Employee> list4 = new ArrayList<>();
        try {
            list4 = jsonToList(json2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Employee employee : list4) {
            System.out.println(employee);
        }

    }

    private static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> list = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        for (JsonElement element : array) {
            Employee employee = gson.fromJson(element, Employee.class);
            list.add(employee);
        }
        return list;
    }

    private static String readString(String fileName) {
        String s = "text";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String s1;
            while ((s1 = reader.readLine()) != null) {
                s = s1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
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
            Element element = (Element) node;
            String idString = element.getElementsByTagName("id").item(0).getTextContent();
            String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
            String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
            String country = element.getElementsByTagName("country").item(0).getTextContent();
            String ageString = element.getElementsByTagName("age").item(0).getTextContent();
            long id = 0;
            int age = 0;
            try {
                id = Integer.parseInt(idString);
                age = Integer.parseInt(ageString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
//            NodeList employeeChildNodes = node.getChildNodes();
//            String[] text = new String[employeeChildNodes.getLength()];
//            for (int j = 0; j < employeeChildNodes.getLength(); j++) {
//                Node node1 = employeeChildNodes.item(j);
//                text[j] = node1.getTextContent();
//            }
//            firstName = text[1];
//            lastName = text[2];
//            country = text[3];
//            try {
//                id = Integer.parseInt(text[0]);
//                age = Integer.parseInt(text[4]);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
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
