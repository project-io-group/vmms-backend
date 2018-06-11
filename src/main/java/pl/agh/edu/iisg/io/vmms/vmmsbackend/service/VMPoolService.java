package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import java.util.List;

public interface VMPoolService {
    List<VMPool> getVMPools();

    List<VMPool> getEnabledVMPools();

    List<VMPool> findByDescriptionContaining(String name);

    VMPool find(String shortName);

    VMPool find(Long id);

    VMPool save(VMPool vmPool);

    void delete(VMPool vmPool);
}
