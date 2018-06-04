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


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String updateFromFile(@RequestParam("file") MultipartFile file) throws HttpException {
        int vmPoolsNumber = 0;
        if (!file.isEmpty()) {
            vmPoolsNumber = updateVmPools(file, vmPoolsNumber, false);
        }

        return "Updated " + vmPoolsNumber + " virtual machine pools.";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String importFromFile(@RequestParam("file") MultipartFile file) throws HttpException {

        int vmPoolsNumber = 0;
        if (!file.isEmpty()) {
            vmPoolsNumber = updateVmPools(file, vmPoolsNumber, true);
        }

        return "Created " + vmPoolsNumber + " virtual machine pools.";
    }

    private int updateVmPools(@RequestParam("file") MultipartFile file, int vmPoolsNumber, boolean replace) throws VMPoolImportFileException {
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(file.getInputStream()));
            VMPoolCSVParser parser = new VMPoolCSVParser();

            List<VMPool> vmPools = vmPoolService.getVMPools();

            StringBuilder errors = new StringBuilder();
            int lines = 0;

            while ((line = bufferedReader.readLine()) != null) {
                lines++;
                Optional<VMPool> o = parser.parseLine(line);
                if (o.isPresent()) {
                    VMPool newPool = o.get();
                    VMPool found = vmPoolService.find(newPool.getShortName());
                    try {
                        processVmPool(vmPools, newPool, found);
                    } catch (VMPoolImportFileException e){
                        errors.append("Error in line ").append(lines).append(": ").append(e.getMessage()).append("\n");
                    }
                    vmPoolsNumber++;
                } else {
                    errors.append("Error parsing line ").append(lines).append("\n");
                }
            }

            if (replace) {
                for (VMPool deleted : vmPools) {
                    if (deleted.getReservations().size() > 0) {
                        errors.append("Can not delete pool ").append(deleted.getDisplayName()).append(" with trailing reservations!\n");
                    }
                    vmPoolService.delete(deleted);
                }
            }
            if(errors.length() != 0){
                throw new VMPoolImportFileException(errors.toString());
            }

        } catch (IOException e) {
            throw new VMPoolImportFileException("Failed to open the file!");
        }
        return vmPoolsNumber;
    }

    private void processVmPool(List<VMPool> vmPools, VMPool newPool, VMPool found) throws VMPoolImportFileException {
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
                    throw new VMPoolImportFileException(newPool.getDisplayName() + " - can not decrease pool count with trailing reservations!");
                }
                found.setMaximumCount(newPool.getMaximumCount());
                changed = true;
            }
            if (!found.getEnabled().equals(newPool.getEnabled())) {
                if (!newPool.getEnabled() && found.getReservations().size() > 0) {
                    throw new VMPoolImportFileException(newPool.getDisplayName() + " - can not disable pool with trailing reservations!");
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
    }
}
