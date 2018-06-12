package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.VMPoolImportFileException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.parsers.VMPoolCSVParser;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.VMPoolRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
public class VMPoolServiceImpl implements VMPoolService {

    private final VMPoolRepository vmPoolRepository;

    @Autowired
    public VMPoolServiceImpl(VMPoolRepository vmPoolRepository) {
        this.vmPoolRepository = vmPoolRepository;
    }

    @Override
    public List<VMPool> getVMPools() {
        return vmPoolRepository.findAll();
    }

    @Override
    public List<VMPool> getEnabledVMPools() {
        return vmPoolRepository.findAllByEnabled(true);
    }

    @Override
    public List<VMPool> findByDescriptionContaining(String displayName) {
        return vmPoolRepository.findAllByDescriptionContaining(displayName);
    }

    @Override
    public VMPool find(String shortName) {
        return vmPoolRepository.findFirstByShortName(shortName);
    }

    @Override
    public VMPool find(Long id) {
        return vmPoolRepository.getOne(id);
    }

    @Override
    public VMPool save(VMPool vmPool) {
        return vmPoolRepository.save(vmPool);
    }

    @Override
    public void delete(VMPool vmPool) {
        vmPoolRepository.delete(vmPool);
    }

    @Override
    public int updateVmPools(MultipartFile file, boolean replace) throws VMPoolImportFileException {
        int vmPoolsNumber = 0;
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(file.getInputStream()));
            VMPoolCSVParser parser = new VMPoolCSVParser();

            List<VMPool> vmPools = getVMPools();

            StringBuilder errors = new StringBuilder();
            int lines = 0;

            while ((line = bufferedReader.readLine()) != null) {
                lines++;
                Optional<VMPool> o = parser.parseLine(line);
                if (o.isPresent()) {
                    VMPool newPool = o.get();
                    VMPool oldPool = find(newPool.getShortName());
                    try {
                        if (processVmPool(vmPools, newPool, oldPool)) {
                            vmPoolsNumber++;
                        }
                    } catch (VMPoolImportFileException e){
                        errors.append(String.format("Error in line %d: %s\n", lines, e.getMessage()));
                    }
                } else {
                    errors.append(String.format("Error parsing line %d\n", lines));
                }
            }

            if (replace) {
                for (VMPool deleted : vmPools) {
                    if (deleted.getReservations().size() > 0) {
                        errors.append(String.format("Can't delete pool %s with trailing reservations!\n", deleted.getDisplayName()));
                    }
                    delete(deleted);
                }
            }
            if(!errors.toString().isEmpty()){
                throw new VMPoolImportFileException(errors.toString());
            }

        } catch (IOException e) {
            throw new VMPoolImportFileException("Failed to open the file!");
        }
        return vmPoolsNumber;
    }

    private boolean processVmPool(List<VMPool> vmPools, VMPool newPool, VMPool oldPool) throws VMPoolImportFileException {
        if (oldPool != null) {
            vmPools.remove(oldPool);
            boolean changed = false;
            if (!oldPool.getDisplayName().equals(newPool.getDisplayName())) {
                oldPool.setDisplayName(newPool.getDisplayName());
                changed = true;
            }
            if (!oldPool.getMaximumCount().equals(newPool.getMaximumCount())) {
                boolean anyMatch = oldPool.getReservations().stream().anyMatch(r -> r.getMachinesNumber() > newPool.getMaximumCount());
                if (anyMatch) {
                    throw new VMPoolImportFileException(newPool.getDisplayName() + " - can not decrease pool count with trailing reservations!");
                }
                oldPool.setMaximumCount(newPool.getMaximumCount());
                changed = true;
            }
            if (!oldPool.getEnabled().equals(newPool.getEnabled())) {
                if (!newPool.getEnabled() && oldPool.getReservations().size() > 0) {
                    throw new VMPoolImportFileException(newPool.getDisplayName() + " - can not disable pool with trailing reservations!");
                }
                oldPool.setEnabled(newPool.getEnabled());
                changed = true;
            }
            if (!oldPool.getDescription().equals(newPool.getDescription())) {
                oldPool.setDescription(newPool.getDescription());
                changed = true;
            }
            if (changed) {
                save(oldPool);
                return true;
            }
            return false;
        } else {
            save(newPool);
            return true;
        }
    }
}
