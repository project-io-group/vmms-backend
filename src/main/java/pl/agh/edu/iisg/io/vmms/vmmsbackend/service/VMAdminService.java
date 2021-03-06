package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;

import java.util.List;

public interface VMAdminService {
    VMAdmin find(String name);
    VMAdmin find(Long id);
    void drop();
    VMAdmin save(VMAdmin vmAdmin);
    List<VMAdmin> getVmAdmins();
}
