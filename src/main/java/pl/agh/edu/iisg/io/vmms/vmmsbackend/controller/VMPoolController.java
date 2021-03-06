package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.VMPoolImportFileException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMPoolService;

import java.util.List;

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
            vmPoolsNumber = updateVmPools(file, false);
        }

        return "Updated " + vmPoolsNumber + " virtual machine pools.";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String importFromFile(@RequestParam("file") MultipartFile file) throws HttpException {

        int vmPoolsNumber = 0;
        if (!file.isEmpty()) {
            vmPoolsNumber = updateVmPools(file, true);
        }

        return "Created " + vmPoolsNumber + " virtual machine pools.";
    }

    private int updateVmPools(@RequestParam("file") MultipartFile file, boolean replace) throws VMPoolImportFileException {
        return vmPoolService.updateVmPools(file, replace);
    }

}
