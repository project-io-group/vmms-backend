package pl.agh.edu.iisg.io.vmms.vmmsbackend.parsers;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import java.util.ArrayList;
import java.util.Optional;

public class VMPoolCSVParser {

    private static final String DELIMINATOR = ",";
    private static final String QUOTE = "\"";
    private static final int ARG_NUM = 5;

    public Optional<VMPool> parseLine(String line) {

        System.out.println(line);

        VMPool vmPool = new VMPool();
        String[] fields = line.split(QUOTE);
        ArrayList<String> parsedFields = new ArrayList<>();
        for(String field: fields){
            if(!field.equals(DELIMINATOR)){
                if(field.startsWith(DELIMINATOR)){
                    parsedFields.add(field.substring(1, field.length()-1));
                }
                else{
                    parsedFields.add(field);
                }
            }
        }
        if( parsedFields.size() != ARG_NUM && parsedFields.size() != ARG_NUM+1 ){
            return Optional.empty();
        }

        vmPool.setShortName(parsedFields.get(1));

        vmPool.setDisplayName(parsedFields.get(2));

        vmPool.setMaximumCount(Integer.valueOf(parsedFields.get(3)));

        vmPool.setEnabled(parsedFields.get(4).equals("true"));

        if(parsedFields.size() > ARG_NUM)
            vmPool.setDescription(parsedFields.get(5));
        else
            vmPool.setDescription("");


        return Optional.of(vmPool);
    }
}
