package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;

public interface VMAdminService {
    VMAdmin find(String name);
    VMAdmin find(Long id);
}
