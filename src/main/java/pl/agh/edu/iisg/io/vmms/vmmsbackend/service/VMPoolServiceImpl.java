package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.UserRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.VMPoolRepository;

import java.util.List;

@Service
public class VMPoolServiceImpl implements VMPoolService{

    @Autowired
    private VMPoolRepository vmPoolRepository;

    @Override
    public List<VMPool> getVMPools() {
        return vmPoolRepository.findAll();
    }

    @Override
    public List<VMPool> findByDisplayNameContaining(String displayName) {
        return vmPoolRepository.findAllByDisplayNameContaining(displayName);
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
}
