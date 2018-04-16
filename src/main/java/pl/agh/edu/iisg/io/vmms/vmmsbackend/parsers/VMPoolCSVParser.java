package pl.agh.edu.iisg.io.vmms.vmmsbackend.parsers;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import java.util.Optional;

public class VMPoolCSVParser {

    private static final String DELIMINATOR = ",";
    private static final String QUOTE = "\"";
    private static final int ARGS_NUMBER = 5;

    public VMPool parseLine(String line){

        String[] fields = line.split(QUOTE+DELIMINATOR+QUOTE);

        if(fields.length != ARGS_NUMBER)
            return null;

        VMPool vmPool = new VMPool();
        vmPool.setShortName(fields[0].substring(1));
        vmPool.setDisplayName(fields[1]);
        vmPool.setMaximumCount(Integer.valueOf(fields[2]));
        vmPool.setEnabled(fields[3].equals("true"));
        vmPool.setDescription(fields[4]);
        return vmPool;
    }
}
