package be.pcoppens.chaos_reverse_eng.application;

import be.pcoppens.chaos_reverse_eng.application.core.SystemDiscoverTool;
import be.pcoppens.chaos_reverse_eng.application.input.LogESBEntry;
import be.pcoppens.chaos_reverse_eng.application.input.LogGroupEntry;
import be.pcoppens.chaos_reverse_eng.application.model.EndPointEntry;
import be.pcoppens.chaos_reverse_eng.application.model.EsbService;
import be.pcoppens.chaos_reverse_eng.application.model.ServiceGroup;
import be.pcoppens.chaos_reverse_eng.application.output.vizceral.Data;
import be.pcoppens.chaos_reverse_eng.application.output.vizceral.builder.DataBuilder;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * Main class.
 * Build a representation of a system from Forem ESB log.
 * Process:
 *  1. Read the "forem.txt.csv" file
 *  2. Use the SystemDiscoverTool to build a abstraction of the system.
 *  3. Write a json Vizceral file ("../vizceral/dist/sample_data.json") that represent the discovered system.
 */
public class ProcessForemLogs {
    private static String fileName = "forem.txt.csv";
    private static String fileNameGroup = "foremApp.txt";
    private static String vizceralOutput = "vizceral/dist/sample_data.json";
    private static ServiceGroup group;
    private static ServiceGroup services;
    private static Set<EndPointEntry> fragiles;
    private static Set<EsbService> fragileSv;
    private static List<ServiceGroup> pseudoApp;
    private static Data data;

    private static void readData() throws IOException{
        group= LogGroupEntry.readGroup(new FileInputStream(fileNameGroup), "Forem", false);
        services= LogESBEntry.read(new FileInputStream(fileName), "All Clients");
    }

    private static void discover(){
        services= SystemDiscoverTool.removeSimilarEnpointEntryByService(services, 0.99f);
        fragiles= SystemDiscoverTool.getFragileEndpoint(services, 2);
        fragileSv= SystemDiscoverTool.getFragileService(services, 2);
        pseudoApp= SystemDiscoverTool.getPseudoApp(services, 3);
    }

    private static void sysOut(){
        fragiles.forEach(endPointEntry -> System.out.println(endPointEntry));
        fragileSv.forEach(sv -> System.out.println(sv));
        System.out.println("fragile endpoints: "+fragiles.size());
        System.out.println("services: "+services.getEsbServices().size());
        System.out.println("fragile services: "+fragileSv.size());
    }

    private static void visceral()throws IOException{
        data= DataBuilder.makeDataGlobal("Forem", "Views");
        DataBuilder.addToData(data, services);
        DataBuilder.addToData(data, new ServiceGroup("Fragile Client("+fragileSv.size()+")", fragileSv));
        ServiceGroup serviceGroup= new ServiceGroup("APP("+pseudoApp.size()+")");
        serviceGroup.setGroups(pseudoApp);
        DataBuilder.addAppToData(data, serviceGroup);

        Files.write(Paths.get(vizceralOutput), new JSONObject(data).toString().getBytes());
    }


    private static void process() throws IOException{
        readData();
        discover();
      //  sysOut();
        visceral();
    }

    public static void main(String args[])throws IOException {
        process();
      //  String appStr= lr.esbClient.stream().map(entry->entry.getClient()).collect(Collectors.toSet()).stream().collect( Collectors.joining( "\n" ) );
      //  Files.write(Paths.get("C:/Users/cppptr/Source/vizceral-example/src/app.txt"), appStr.getBytes());
    }
}