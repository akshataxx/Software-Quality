/*****************************************************************************************************
 Student: Daniel Beiers
 UID: c3039134
 Course: SENG4430 Software Quality
 Assessment: Assignment 2
 ****************************************************************************************************/
package com.SENG4430.Fan;
import com.SENG4430.MetricsList;
import spoon.Launcher;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/*********************************************************************************************************
 This class is the metrics collection, Spoon execution point and metrics string builder.
 From here the main application can call the comments collection class and return the
 metric as a string in the format required of the main application.
 If the format changes, this class can be modified to change how the results are processed,
 allowing for scalability and abstraction.
 *********************************************************************************************************/

public class FanInOutList extends MetricsList {

    private final FanInOutChk fanInOutChk;

    public FanInOutList(String[] args) {
        fanInOutChk = new FanInOutChk();
    }

    /**
     * Runs the analysis
     */
    @Override
    public void execute(Launcher launcher) {
        fanInOutChk.FanInOutChkLauncher(launcher);
    }


    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        //jsonBuilder.append("\t\t},\n");
        jsonBuilder.append("\n");

        // Add the Plain Text Credentials object to the JSON string
        LinkedList<Map.Entry<String, LinkedList<Map<String, Integer>>>> fanInOut = fanInOutChk.getFanInOut();
        if (fanInOut.isEmpty()) {
            jsonBuilder.append("Fan In and Out\": \"No classes to traverse\"\n");
        } else {
            jsonBuilder.append("Fan In and Out: \n");
            String className;
            for (Map.Entry<String, LinkedList<Map<String, Integer>>> entry : fanInOut) {
                className = entry.getKey();
                jsonBuilder.append("Class - \"").append(className).append("\": \n");
                int select = 0;
                for(Map<String, Integer> methodEntry: entry.getValue()){
                    Set<String> methods = methodEntry.keySet();
                    if(select==0){
                        jsonBuilder.append("\t Fan In: (MethodName:Value) \n");
                        select++;
                    }else{
                        jsonBuilder.append("\n\t Fan Out: (MethodName:Value) \n");
                    }
                    int iter=0;
                    for(String method:methods){
                        jsonBuilder.append("\t\t").append(method).append(":").append(methodEntry.get(method)).append("\t");
                        iter++;
                        if(iter%5==0){
                            jsonBuilder.append("\n");
                        }
                    }
                    jsonBuilder.append("\n");
                }
                jsonBuilder.append("\n");
            }
        }
        return jsonBuilder.toString();
    }


}