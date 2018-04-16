package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption.VMPoolImportFileException;
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

    @Autowired
    VMPoolService vmPoolService;

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
                    VMPool vmPool = Optional
                            .ofNullable(parser.parseLine(line))
                            .orElseThrow(() -> new VMPoolImportFileException("Failed to parse the file!"));
                    vmPoolService.save(vmPool);
                    vmPoolsNumber++;
                }

            } catch (Exception e) {
                throw new VMPoolImportFileException("Failed to open the file!");
            }
        }
        return "Created " + vmPoolsNumber + "virtual machine pools.";
    }
}
