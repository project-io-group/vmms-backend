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
import java.io.IOException;
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

                List<VMPool> vmPools = vmPoolService.getVMPools();

                while ((line = bufferedReader.readLine()) != null) {
                    Optional<VMPool> o = parser.parseLine(line);
                    if (o.isPresent()) {
                        System.out.println("parsed");
                        VMPool newPool = o.get();
                        VMPool found = vmPoolService.find(newPool.getShortName());
                        try {
                            if (found != null) {
                                vmPools.remove(found);
                                boolean changed = false;
                                if (!found.getDisplayName().equals(newPool.getDisplayName())) {
                                    found.setDisplayName(newPool.getDisplayName());
                                    changed = true;
                                }
                                if (!found.getMaximumCount().equals(newPool.getMaximumCount())) {
                                    boolean anyMatch = found.getReservations().stream().anyMatch(r -> r.getMachinesNumber() > newPool.getMaximumCount());
                                    if (anyMatch) {
                                        throw new VMPoolImportFileException("Can not decrease pool count with trailing reservations!");
                                    }
                                    found.setMaximumCount(newPool.getMaximumCount());
                                    changed = true;
                                }
                                if (!found.getEnabled().equals(newPool.getEnabled())) {
                                    if (!newPool.getEnabled() && found.getReservations().size() > 0) {
                                        throw new VMPoolImportFileException("Can not disable pool with trailing reservations!");
                                    }
                                    found.setEnabled(newPool.getEnabled());
                                    changed = true;
                                }
                                if (!found.getDescription().equals(newPool.getDescription())) {
                                    found.setDescription(newPool.getDescription());
                                    changed = true;
                                }
                                if (changed) {
                                    vmPoolService.save(found);
                                }
                            } else {
                                vmPoolService.save(newPool);
                            }
                        } catch (Exception e) {
                            String details = "";
                            if (e.getMessage().contains("constraint [uk_")) {
                                details = " - Unique key";
                            } else if (e.getMessage().contains("constraint [fk_")) {
                                details = " - Foreign key";
                            }
                            throw new VMPoolImportFileException("Constraint not met" + details);
                        }
                        vmPoolsNumber++;
                    } else {
                        throw new VMPoolImportFileException("Failed to parse the file!");
                    }
                }

                for (VMPool deleted : vmPools) {
                    if (deleted.getReservations().size() > 0) {
                        throw new VMPoolImportFileException("Can not delete pool with trailing reservations!");
                    }
                    vmPoolService.delete(deleted);
                }

            } catch (IOException e) {
                throw new VMPoolImportFileException("Failed to open the file!");
            }
        }

        return "Created " + vmPoolsNumber + " virtual machine pools.";
    }
}
