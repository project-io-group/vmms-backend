package pl.agh.edu.iisg.io.vmms.vmmsbackend.parsers;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import java.util.ArrayList;
import java.util.Optional;

public class VMPoolCSVParser {

    private static final String DELIMINATOR = ",";
    private static final String QUOTE = "\"";
    private static final int REQUIRED_ARGS_COUNT = 5;
    private static final int ALL_ARGS_COUNT = 6;

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
        if( parsedFields.size() != REQUIRED_ARGS_COUNT && parsedFields.size() != ALL_ARGS_COUNT){
            return Optional.empty();
        }

        vmPool.setShortName(parsedFields.get(1));

        vmPool.setDisplayName(parsedFields.get(2));

        try {
            vmPool.setMaximumCount(Integer.valueOf(parsedFields.get(3)));
        } catch (NumberFormatException e){
            return Optional.empty();
        }

        vmPool.setEnabled(parsedFields.get(4).equals("true"));

        if(parsedFields.size() == ALL_ARGS_COUNT)
            vmPool.setDescription(parsedFields.get(5));
        else
            vmPool.setDescription("");


        return Optional.of(vmPool);
    }
}
