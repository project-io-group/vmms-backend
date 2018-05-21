package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.VMAdminRepository;

@Service
public class VMAdminServiceImpl implements VMAdminService {

    private final VMAdminRepository vmAdminRepository;

    @Autowired
    public VMAdminServiceImpl(VMAdminRepository vmAdminRepository) {
        this.vmAdminRepository = vmAdminRepository;
    }

    @Override
    public VMAdmin find(String name) {
        return vmAdminRepository.findFirstByName(name);
    }

    @Override
    public VMAdmin find(Long id) {
        return vmAdminRepository.getOne(id);
    }

    @Override
    public void drop(){
        vmAdminRepository.deleteAll();
    }

    @Override
    public VMAdmin save(VMAdmin vmAdmin) {
        return vmAdminRepository.save(vmAdmin);
    }
}
