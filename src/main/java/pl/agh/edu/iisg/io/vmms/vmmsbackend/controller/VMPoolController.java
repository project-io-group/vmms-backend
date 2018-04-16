package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.VMPoolImportFileException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.parsers.VMPoolCSVParser;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMPoolService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/vm")
public class VMPoolController {

    private final VMPoolService vmPoolService;

    @Autowired
    public VMPoolController(VMPoolService vmPoolService) {
        this.vmPoolService = vmPoolService;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public List<VMPool> getAllVMPools() {
        return vmPoolService.getVMPools();
    }

    @RequestMapping(path = "/enabled", method = RequestMethod.GET)
    public List<VMPool> getEnabledVMPools() {
        return vmPoolService.getEnabledVMPools();
    }

    @RequestMapping(path = "/tag/{tag}", method = RequestMethod.GET)
    public List<VMPool> getVMPoolsByTag(@PathVariable String tag) {
        return vmPoolService.findByDescriptionContaining(tag);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String importFromFile(@RequestParam("file") MultipartFile file) throws HttpException {

        int vmPoolsNumber = 0;
        if (!file.isEmpty()) {
            try {
                String line;
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(file.getInputStream()));

                VMPoolCSVParser parser = new VMPoolCSVParser();

                while ((line = bufferedReader.readLine()) != null) {
                    Optional<VMPool> o = parser.parseLine(line);
                    System.out.println("parsed");
                    if(o.isPresent()) {
                        System.out.println("parsed");
                        vmPoolService.save(o.get());
                        vmPoolsNumber++;
                    }
                    else {
                        throw new VMPoolImportFileException("Failed to parse the file!");
                    }
                }

            } catch (Exception e) {
                throw new VMPoolImportFileException("Failed to open the file!");
            }
        }

        return "Created " + vmPoolsNumber + " virtual machine pools.";
    }
}
