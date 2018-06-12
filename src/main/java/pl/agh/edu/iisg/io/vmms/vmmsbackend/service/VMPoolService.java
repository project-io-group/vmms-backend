package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.web.multipart.MultipartFile;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.controller.VMPoolController;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.VMPoolImportFileException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.parsers.VMPoolCSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

public interface VMPoolService {
    List<VMPool> getVMPools();

    List<VMPool> getEnabledVMPools();

    List<VMPool> findByDescriptionContaining(String name);

    VMPool find(String shortName);

    VMPool find(Long id);

    VMPool save(VMPool vmPool);

    void delete(VMPool vmPool);

    int updateVmPools(MultipartFile file, boolean replace) throws VMPoolImportFileException;
}
