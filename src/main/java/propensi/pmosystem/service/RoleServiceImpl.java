package propensi.pmosystem.service;

import propensi.pmosystem.model.RoleModel;
import propensi.pmosystem.repository.RoleDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@org.springframework.transaction.annotation.Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleDb roleDb;

    @Override
    public List<RoleModel> findAll(){ return roleDb.findAll();}
}
